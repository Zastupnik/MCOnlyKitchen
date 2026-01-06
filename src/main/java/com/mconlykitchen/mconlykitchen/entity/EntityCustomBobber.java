package com.mconlykitchen.mconlykitchen.entity;

import com.mconlykitchen.mconlykitchen.config.ModConfig;
import com.mconlykitchen.mconlykitchen.network.NetworkHandler;
import com.mconlykitchen.mconlykitchen.network.PacketShowBiteAnimation;
import com.mconlykitchen.mconlykitchen.util.FishTierSystem;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityCustomBobber extends Entity {

    private EntityPlayer owner;
    private int rodTier; // теперь это сырой tier 0..6

    // Физика
    private double motionX;
    private double motionY;
    private double motionZ;
    private boolean inGround;

    // Состояние
    private int ticksInWater;
    private int timeUntilBite;
    private boolean waitingForBite;
    private boolean fishBiting;
    private int fishTier;

    public EntityCustomBobber(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
    }

    public EntityCustomBobber(World world, EntityPlayer player, int tier) {
        this(world);
        this.owner = player;
        this.rodTier = (tier < 0 ? 0 : tier > 6 ? 6 : tier); // сохраняем сырой tier 0..6

        this.setLocationAndAngles(
                player.posX,
                player.posY + player.getEyeHeight() - 0.1,
                player.posZ,
                player.rotationYaw,
                player.rotationPitch
        );

        float f = 0.4F;
        this.motionX = -MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI)
                * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI) * f;
        this.motionZ = MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI)
                * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI) * f;
        this.motionY = -MathHelper.sin(rotationPitch / 180.0F * (float)Math.PI) * f;

        this.setThrowableHeading(motionX, motionY, motionZ, 1.5F, 1.0F);
    }

    @Override
    protected void entityInit() {}

    public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy) {
        float f = MathHelper.sqrt_double(x * x + y * y + z * z);
        x /= f; y /= f; z /= f;

        x += this.rand.nextGaussian() * 0.0075 * inaccuracy;
        y += this.rand.nextGaussian() * 0.0075 * inaccuracy;
        z += this.rand.nextGaussian() * 0.0075 * inaccuracy;

        x *= velocity; y *= velocity; z *= velocity;

        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (owner == null || owner.isDead || !owner.isEntityAlive()) {
            this.setDead();
            return;
        }

        double distance = owner.getDistanceSqToEntity(this);
        if (distance > 1024.0) {
            this.setDead();
            return;
        }

        if (!inGround) {
            updateMotion();
        }

        checkInLiquid();
    }

    private void updateMotion() {
        this.motionY -= 0.03;
        this.motionX *= 0.98;
        this.motionY *= 0.98;
        this.motionZ *= 0.98;

        this.moveEntity(motionX, motionY, motionZ);

        int x = MathHelper.floor_double(posX);
        int y = MathHelper.floor_double(posY);
        int z = MathHelper.floor_double(posZ);

        Block block = worldObj.getBlock(x, y, z);
        if (!block.isAir(worldObj, x, y, z)) {
            AxisAlignedBB aabb = block.getCollisionBoundingBoxFromPool(worldObj, x, y, z);
            if (aabb != null && aabb.isVecInside(Vec3.createVectorHelper(posX, posY, posZ))) {
                inGround = true;
                motionX = motionY = motionZ = 0;
            }
        }
    }

    private void checkInLiquid() {
        int x = MathHelper.floor_double(posX);
        int y = MathHelper.floor_double(posY);
        int z = MathHelper.floor_double(posZ);

        Block block = worldObj.getBlock(x, y, z);
        Material material = block.getMaterial();

        boolean inWater = material == Material.water;
        boolean inLava  = material == Material.lava;

        // Поплавок работает только в воде или лаве
        if ((inWater || inLava) && !worldObj.isRemote) {
            ticksInWater++;

            // Сопротивление жидкости
            motionX *= 0.8;
            motionY *= 0.8;
            motionZ *= 0.8;

            // Инициализация таймера поклёвки
            if (!waitingForBite && ticksInWater == 1) {
                waitingForBite = true;
                int seconds = ModConfig.getBiteTimeByTier(rodTier); // напрямую по tier 0..6
                timeUntilBite = Math.max(1, seconds) * 20; // секунды → тики
            }

            // Проверка на поклёвку
            if (waitingForBite && ticksInWater >= timeUntilBite && !fishBiting) {
                fishBiting = true;
                fishTier = FishTierSystem.getRandomFishTier();

                if (owner instanceof EntityPlayerMP) {
                    boolean isLavaEnv = inLava || worldObj.provider.isHellWorld;
                    NetworkHandler.INSTANCE.sendTo(
                            new PacketShowBiteAnimation(rodTier, isLavaEnv, fishTier, getEntityId()),
                            (EntityPlayerMP) owner
                    );
                }

                worldObj.playSoundAtEntity(this, "random.splash", 0.25F,
                        1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
            }

        } else {
            // Вышли из жидкости — сброс состояния
            ticksInWater = 0;
            waitingForBite = false;
        }
    }

    public void retrieveBobber() {
        if (!worldObj.isRemote) {
            this.setDead();
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {}

    public EntityPlayer getOwner() { return owner; }
    public int getRodTier() { return rodTier; } // теперь возвращает сырой tier 0..6
    public int getFishTier() { return fishTier; }
    public boolean isFishBiting() { return fishBiting; }
}

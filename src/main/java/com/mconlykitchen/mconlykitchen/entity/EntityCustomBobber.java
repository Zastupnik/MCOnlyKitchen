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

/**
 * Кастомный поплавок - полностью своя механика!
 * Не наследуется от EntityFishHook, чтобы избежать проблем с angler
 */
public class EntityCustomBobber extends Entity {

    private EntityPlayer owner;
    private int rodTier;

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
        this.rodTier = tier;

        // Устанавливаем начальную позицию
        this.setLocationAndAngles(
                player.posX,
                player.posY + player.getEyeHeight() - 0.1,
                player.posZ,
                player.rotationYaw,
                player.rotationPitch
        );

        // Бросаем поплавок
        float f = 0.4F;
        this.motionX = -MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI)
                * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI) * f;
        this.motionZ = MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI)
                * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI) * f;
        this.motionY = -MathHelper.sin(rotationPitch / 180.0F * (float)Math.PI) * f;

        this.setThrowableHeading(motionX, motionY, motionZ, 1.5F, 1.0F);
    }

    @Override
    protected void entityInit() {
        // Инициализация data watcher если нужно
    }

    public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy) {
        float f = MathHelper.sqrt_double(x * x + y * y + z * z);
        x /= f;
        y /= f;
        z /= f;

        x += this.rand.nextGaussian() * 0.0075 * inaccuracy;
        y += this.rand.nextGaussian() * 0.0075 * inaccuracy;
        z += this.rand.nextGaussian() * 0.0075 * inaccuracy;

        x *= velocity;
        y *= velocity;
        z *= velocity;

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

        // Проверяем расстояние до игрока
        double distance = owner.getDistanceSqToEntity(this);
        if (distance > 1024.0) { // 32 блока
            this.setDead();
            return;
        }

        // Движение поплавка
        if (!inGround) {
            updateMotion();
        }

        // Проверка на воду/лаву
        checkInLiquid();
    }

    private void updateMotion() {
        // Гравитация
        this.motionY -= 0.03;

        // Сопротивление воздуха
        this.motionX *= 0.98;
        this.motionY *= 0.98;
        this.motionZ *= 0.98;

        // Обновляем позицию
        this.moveEntity(motionX, motionY, motionZ);

        // Проверяем коллизию с блоками
        int x = MathHelper.floor_double(posX);
        int y = MathHelper.floor_double(posY);
        int z = MathHelper.floor_double(posZ);

        Block block = worldObj.getBlock(x, y, z);
        if (!block.isAir(worldObj, x, y, z)) {
            AxisAlignedBB aabb = block.getCollisionBoundingBoxFromPool(worldObj, x, y, z);
            if (aabb != null && aabb.isVecInside(Vec3.createVectorHelper(posX, posY, posZ))) {
                inGround = true;
                motionX = 0;
                motionY = 0;
                motionZ = 0;
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
        boolean inLava = material == Material.lava;

        if ((inWater || inLava) && !worldObj.isRemote) {
            // Поплавок в жидкости
            ticksInWater++;

            // Применяем сопротивление жидкости
            motionX *= 0.8;
            motionY *= 0.8;
            motionZ *= 0.8;

            if (!waitingForBite && !fishBiting) {
                // Определяем время до поклёвки
                boolean isLava = inLava || worldObj.provider.isHellWorld;
                timeUntilBite = ModConfig.getBiteTime(rodTier, isLava) * 20; // секунды в тики
                waitingForBite = true;
            }

            if (waitingForBite && ticksInWater >= timeUntilBite && !fishBiting) {
                fishBiting = true;
                fishTier = FishTierSystem.getRandomFishTier();

                // Только сервер отправляет пакет
                if (owner instanceof EntityPlayerMP) {
                    boolean isLava = inLava || worldObj.provider.isHellWorld;
                    NetworkHandler.INSTANCE.sendTo(
                            new PacketShowBiteAnimation(rodTier, isLava, fishTier, getEntityId()),
                            (EntityPlayerMP) owner
                    );
                }

                worldObj.playSoundAtEntity(this, "random.splash", 0.25F,
                        1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
            }
            if (waitingForBite && ticksInWater >= timeUntilBite && !fishBiting) {
                fishBiting = true;
                fishTier = FishTierSystem.getRandomFishTier();

                // Только сервер отправляет пакет
                if (owner instanceof EntityPlayerMP) {
                    boolean isLava = inLava || worldObj.provider.isHellWorld;
                    NetworkHandler.INSTANCE.sendTo(
                            new PacketShowBiteAnimation(rodTier, isLava, fishTier, getEntityId()),
                            (EntityPlayerMP) owner
                    );
                }

                worldObj.playSoundAtEntity(this, "random.splash", 0.25F,
                        1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
            }
        } else {
            // Вышел из воды
            ticksInWater = 0;
            waitingForBite = false;
        }
    }

    /**
     * Вытащить удочку
     */
    public void retrieveBobber() {
        if (!worldObj.isRemote && fishBiting) {
            // Открываем GUI мини-игры
            if (owner instanceof EntityPlayerMP) {
                boolean isLava = worldObj.provider.isHellWorld;
                // Пакет будет отправлен после нажатия пробела на клиенте
            }
        }

        this.setDead();
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        // Не нужно сохранять
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        // Не нужно сохранять
    }

    public EntityPlayer getOwner() {
        return owner;
    }

    public int getRodTier() {
        return rodTier;
    }

    public int getFishTier() {
        return fishTier;
    }

    public boolean isFishBiting() {
        return fishBiting;
    }
}
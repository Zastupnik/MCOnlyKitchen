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

    /* ===================== STATE MACHINE ===================== */
    public static enum State {
        FLYING,
        IN_WATER,
        BITE_ANIMATION,
        MINI_GAME,
        FINISHED
    }

    private State state = State.FLYING;

    public State getState() { return state; }

    private void setState(State newState) { this.state = newState; }

    public void startMiniGame() {
        if (state != State.BITE_ANIMATION) return;
        setState(State.MINI_GAME);
    }

    /* ===================== FIELDS ===================== */
    private EntityPlayer owner;
    private int rodTier;

    private boolean inGround;
    private int ticksInWater;
    private int timeUntilBite;
    private boolean waitingForBite;
    private boolean fishBiting;
    private int fishTier;

    private boolean wasInWaterOnce = false;

    /* ===================== CONSTRUCTORS ===================== */
    public EntityCustomBobber(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
    }

    public EntityCustomBobber(World world, EntityPlayer player, int tier) {
        this(world);
        this.owner = player;
        this.rodTier = MathHelper.clamp_int(tier, 0, 6);
        setState(State.FLYING);

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

    @Override protected void entityInit() {}
    public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy) {
        float f = MathHelper.sqrt_double(x * x + y * y + z * z);
        x /= f; y /= f; z /= f;

        x += this.rand.nextGaussian() * 0.0075D * inaccuracy;
        y += this.rand.nextGaussian() * 0.0075D * inaccuracy;
        z += this.rand.nextGaussian() * 0.0075D * inaccuracy;

        x *= velocity; y *= velocity; z *= velocity;

        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    /* ===================== UPDATE ===================== */
    @Override
    public void onUpdate() {
        super.onUpdate();

        if (owner == null || owner.isDead) {
            setDead();
            return;
        }

        if (owner.getDistanceSqToEntity(this) > 1024.0) {
            setDead();
            return;
        }

        if (!inGround && state == State.FLYING) updateMotion();

        checkInLiquid();
    }

    private void updateMotion() {
        motionY -= 0.03;
        motionX *= 0.98;
        motionY *= 0.98;
        motionZ *= 0.98;

        moveEntity(motionX, motionY, motionZ);

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

    /* ===================== WATER / LAVA LOGIC ===================== */
    private void checkInLiquid() {
        int x = MathHelper.floor_double(posX);
        int y = MathHelper.floor_double(posY);
        int z = MathHelper.floor_double(posZ);

        Block block = worldObj.getBlock(x, y, z);
        Material material = block.getMaterial();

        boolean inWater = material == Material.water;
        boolean inLava = material == Material.lava;

        if ((inWater || inLava) && !worldObj.isRemote) {

            wasInWaterOnce = true;

            if (state == State.FLYING) setState(State.IN_WATER);

            ticksInWater++;
            motionX *= 0.8; motionY *= 0.8; motionZ *= 0.8;

            if (!waitingForBite && ticksInWater == 1) {
                waitingForBite = true;
                timeUntilBite = Math.max(1, ModConfig.getBiteTimeByTier(rodTier)) * 20;
            }

            if (waitingForBite && ticksInWater >= timeUntilBite && !fishBiting) {
                triggerBiteAnimation(inLava);
            }

        } else {
            ticksInWater = 0;
            waitingForBite = false;
        }
    }

    /* ===================== BITE ANIMATION TRIGGER ===================== */
    private void triggerBiteAnimation(boolean inLava) {
        fishBiting = true;
        fishTier = FishTierSystem.getRandomFishTier();
        setState(State.BITE_ANIMATION);

        // отправка клиенту
        if (owner instanceof EntityPlayerMP) {
            NetworkHandler.INSTANCE.sendTo(
                    new PacketShowBiteAnimation(
                            rodTier,
                            inLava || worldObj.provider.isHellWorld,
                            fishTier,
                            getEntityId()
                    ),
                    (EntityPlayerMP) owner
            );
        }
    }

    /* ===================== FINISH ===================== */
    public void retrieveBobber() {
        setState(State.FINISHED);
        if (!worldObj.isRemote) setDead();
    }

    @Override protected void readEntityFromNBT(NBTTagCompound tag) {}
    @Override protected void writeEntityToNBT(NBTTagCompound tag) {}

    public EntityPlayer getOwner() { return owner; }
    public int getRodTier() { return rodTier; }
    public int getFishTier() { return fishTier; }
    public boolean wasInWater() { return wasInWaterOnce; }
}

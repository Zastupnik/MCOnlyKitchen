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

    public enum State {
        WAITING_FOR_BITE,
        BITE_ANIMATION,
        MINI_GAME_STARTED,
        FINISHED
    }

    private final EntityPlayer owner;
    private final int rodTier;

    private boolean inGround;
    private State state = State.WAITING_FOR_BITE;

    private int biteTimer = -1;
    private int fishTier;

    // Задержка после мини-игры (чтобы игрок не прыгал)
    private int postGameDelay = 0;

    // Для совместимости с FishingSessionManager
    private boolean dead = false;

    public EntityCustomBobber(World world) {
        super(world);
        this.owner = null;
        this.rodTier = 0;
        setSize(0.25F, 0.25F);
    }

    public EntityCustomBobber(World world, EntityPlayer player, int tier) {
        super(world);
        this.owner = player;
        this.rodTier = MathHelper.clamp_int(tier, 0, 6);
        setSize(0.25F, 0.25F);

        setLocationAndAngles(
                player.posX,
                player.posY + player.getEyeHeight() - 0.1,
                player.posZ,
                player.rotationYaw,
                player.rotationPitch
        );

        float f = 0.4F;
        motionX = -MathHelper.sin(rotationYaw * (float)Math.PI / 180F)
                * MathHelper.cos(rotationPitch * (float)Math.PI / 180F) * f;
        motionZ =  MathHelper.cos(rotationYaw * (float)Math.PI / 180F)
                * MathHelper.cos(rotationPitch * (float)Math.PI / 180F) * f;
        motionY = -MathHelper.sin(rotationPitch * (float)Math.PI / 180F) * f;

        setHeading(motionX, motionY, motionZ, 1.5F, 1.0F);
    }

    public void setHeading(double x, double y, double z, float velocity, float inaccuracy) {
        float f = MathHelper.sqrt_double(x*x + y*y + z*z);
        x /= f; y /= f; z /= f;

        x += rand.nextGaussian() * 0.0075 * inaccuracy;
        y += rand.nextGaussian() * 0.0075 * inaccuracy;
        z += rand.nextGaussian() * 0.0075 * inaccuracy;

        x *= velocity; y *= velocity; z *= velocity;

        motionX = x;
        motionY = y;
        motionZ = z;
    }

    @Override
    protected void entityInit() {}

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (owner == null || !owner.isEntityAlive()) { setDead(); return; }
        if (owner.getDistanceSqToEntity(this) > 1024D) { setDead(); return; }

        if (!inGround) updateMotion();
        handleBiteLogic();

        if (postGameDelay > 0) postGameDelay--;
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
        AxisAlignedBB aabb = block.getCollisionBoundingBoxFromPool(worldObj, x, y, z);

        if (aabb != null && aabb.isVecInside(Vec3.createVectorHelper(posX, posY, posZ))) {
            inGround = true;
            motionX = motionY = motionZ = 0;
        }
    }

    private void handleBiteLogic() {
        int x = MathHelper.floor_double(posX);
        int y = MathHelper.floor_double(posY);
        int z = MathHelper.floor_double(posZ);

        Material mat = worldObj.getBlock(x, y, z).getMaterial();
        boolean validLiquid = mat == Material.water || mat == Material.lava;

        if (!validLiquid || worldObj.isRemote) return;

        switch (state) {
            case WAITING_FOR_BITE:
                if (biteTimer < 0) { biteTimer = ModConfig.getBiteTimeByTier(rodTier) * 20; return; }
                biteTimer--;
                if (biteTimer <= 0) {
                    fishTier = FishTierSystem.getRandomFishTier();
                    state = State.BITE_ANIMATION;

                    if (owner instanceof EntityPlayerMP) {
                        boolean lava = mat == Material.lava || worldObj.provider.isHellWorld;
                        NetworkHandler.INSTANCE.sendTo(
                                new PacketShowBiteAnimation(rodTier, lava, fishTier, getEntityId()),
                                (EntityPlayerMP) owner
                        );
                    }

                    worldObj.playSoundAtEntity(this, "random.splash", 0.25F, 1.0F);
                    postGameDelay = 10;
                }
                break;

            case BITE_ANIMATION:
                // Ждём PacketSpacePressed → startMiniGame()
                break;

            case MINI_GAME_STARTED:
                // Здесь можно добавить прогресс-бар таймер
                break;

            case FINISHED:
                setDead();
                break;
        }
    }

    public void startMiniGame() {
        if (state == State.BITE_ANIMATION) {
            state = State.MINI_GAME_STARTED;
        }
    }

    public void retrieveBobber() {
        state = State.FINISHED;
        setDead(); // ← КРИТИЧЕСКИ ВАЖНО
    }


    public boolean isDeadCustom() { return dead || super.isDead; }

    @Override
    public void setDead() { dead = true; super.setDead(); }

    @Override protected void readEntityFromNBT(NBTTagCompound tag) {}
    @Override protected void writeEntityToNBT(NBTTagCompound tag) {}

    public EntityPlayer getOwner() { return owner; }
    public int getRodTier() { return rodTier; }
    public int getFishTier() { return fishTier; }
    public State getState() { return state; }
    public int getPostGameDelay() { return postGameDelay; }
}

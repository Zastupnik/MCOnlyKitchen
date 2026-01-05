package com.mconlykitchen.mconlykitchen.entity;

import com.mconlykitchen.mconlykitchen.network.NetworkHandler;
import com.mconlykitchen.mconlykitchen.network.PacketOpenFishingGUI;
import com.mconlykitchen.mconlykitchen.util.FishTierSystem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class EntityCustomFishHook extends Entity {

    private final int rodTier;
    private final EntityPlayerMP owner;
    private int ticksInWater = 0;
    private int timeUntilBite = 0;
    private boolean waitingForBite = false;

    // Конструктор для Forge
    public EntityCustomFishHook(World world) {
        super(world);
        this.rodTier = 0;
        this.owner = null;
    }

    // Конструктор для игрока
    public EntityCustomFishHook(World world, EntityPlayerMP player, int tier) {
        super(world);
        this.rodTier = tier;
        this.owner = player;

        this.setPosition(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        this.timeUntilBite = 50 + this.rand.nextInt(50); // поклёвка через 50-100 тиков

// Мгновенно открываем GUI
        boolean lavaFishing = world.provider.isHellWorld;
        boolean gotChest = rand.nextInt(100) < 25;
        boolean golden = gotChest && rand.nextInt(100) < 10;

// Определяем Tier рыбы (пример)
        int fishTier = 1 + rand.nextInt(Math.min(rodTier + 1, 5));

        NetworkHandler.INSTANCE.sendTo(
                new PacketOpenFishingGUI(rodTier, lavaFishing, gotChest, golden, fishTier),
                player
        );


    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (owner == null || owner.isDead) {
            this.setDead();
            return;
        }

        boolean inWater = this.worldObj.getBlock((int) posX, (int) posY, (int) posZ).getMaterial().isLiquid();
        boolean inLava = inWater && this.worldObj.provider.isHellWorld;

        if (inWater || inLava) {
            ticksInWater++;
            if (!waitingForBite) waitingForBite = true;
        } else {
            ticksInWater = 0;
            waitingForBite = false;
        }

        // Простое падение, если не в воде
        if (!inWater && !inLava) {
            this.motionX *= 0.9;
            this.motionY -= 0.03;
            this.motionZ *= 0.9;
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
        }
    }

    @Override
    protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound tag) {}

    @Override
    protected void writeEntityToNBT(net.minecraft.nbt.NBTTagCompound tag) {}

    public int getRodTier() { return rodTier; }
}

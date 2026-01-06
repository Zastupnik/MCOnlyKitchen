package com.mconlykitchen.mconlykitchen.entity;

import com.mconlykitchen.mconlykitchen.network.NetworkHandler;
import com.mconlykitchen.mconlykitchen.network.PacketOpenFishingGUI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.world.World;

public class EntityCustomFishHook extends EntityFishHook {

    private boolean guiOpened = false;
    private final int rodTier;

    // ОБЯЗАТЕЛЬНО для Forge
    public EntityCustomFishHook(World world) {
        super(world);
        this.rodTier = 0;
    }

    // Используется при забросе
    public EntityCustomFishHook(World world, EntityPlayer player, int tier) {
        super(world, player);
        this.rodTier = tier;

        // КРИТИЧНО: иначе ванила ломается
        player.fishEntity = this;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (worldObj.isRemote) return;

        // field_146042_b = игрок (angler в нормальных версиях)
        if (!(this.field_146042_b instanceof EntityPlayerMP)) return;

        // ВАНИЛЬНЫЙ МОМЕНТ ПОКЛЁВКИ
        if (this.ticksCatchable > 0 && !guiOpened) {
            guiOpened = true;

            EntityPlayerMP player = (EntityPlayerMP) this.field_146042_b;
            boolean isLava = worldObj.provider.isHellWorld;

            NetworkHandler.INSTANCE.sendTo(
                    new PacketOpenFishingGUI(rodTier, isLava),
                    player
            );
        }
    }
}

package com.mconlykitchen.mconlykitchen.fishing;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class FishingSession {

    private final EntityPlayerMP player;
    private final int rodTier;
    private final boolean isLava;
    private boolean finished = false;

    public FishingSession(EntityPlayerMP player, int rodTier, boolean isLava) {
        this.player = player;
        this.rodTier = rodTier;
        this.isLava = isLava;
    }

    public void finish(boolean gotChest, boolean isGolden) {
        if (finished) return;
        finished = true;

        ItemStack loot;

        if (gotChest) {
            loot = isGolden
                    ? LootHelper.getGoldenChestLoot(player)
                    : LootHelper.getChestLoot(player);
        } else {
            loot = LootHelper.getFishLoot(player, rodTier, isLava);
        }

        if (loot != null) {
            spawnLoot(loot);
        }

        damageRod();
    }

    private void spawnLoot(ItemStack stack) {
        World world = player.worldObj;

        EntityItem item = new EntityItem(
                world,
                player.posX,
                player.posY + 0.5,
                player.posZ,
                stack
        );
        item.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld(item);
    }

    private void damageRod() {
        ItemStack rod = player.getHeldItem();
        if (rod != null) {
            rod.damageItem(1, player);
        }
    }
}

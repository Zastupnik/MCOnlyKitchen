package com.mconlykitchen.mconlykitchen.fishing;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class LootHelper {

    public static ItemStack getFishLoot(EntityPlayerMP player, int rodTier, boolean isLava) {
        // простой пример: обычная рыба или лавовая рыба
        if (isLava) {
            return new ItemStack(Items.magma_cream);
        } else {
            return new ItemStack(Items.fish, 1, 0); // обычная рыба
        }
    }

    public static ItemStack getChestLoot(EntityPlayerMP player) {
        // простой сундук: немного золота
        return new ItemStack(Items.gold_ingot, 3);
    }

    public static ItemStack getGoldenChestLoot(EntityPlayerMP player) {
        // золотой сундук: редкий предмет
        return new ItemStack(Items.diamond, 1);
    }
}

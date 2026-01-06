package com.mconlykitchen.mconlykitchen.util;

import com.mconlykitchen.mconlykitchen.config.ModConfig;
import com.mconlykitchen.mconlykitchen.init.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Random;

public class FishingLootHelper {

    private static final Random RAND = new Random();

    /** Получить рыбу или предмет из водоёма */
    public static ItemStack getFishLoot(boolean isLava, int fishTier) {
        if (isLava) {
            return getNetherFish(fishTier);
        } else {
            // 90% шанс на рыбу, 10% на предмет
            if (RAND.nextInt(100) < 90) {
                return getOverworldFish(fishTier);
            } else {
                return getOverworldItem();
            }
        }
    }

    public static ItemStack getChestLoot(boolean isGolden) {
        if (isGolden) {
            return ModConfig.getGoldenChestLoot();
        } else {
            return ModConfig.getNormalChestLoot();
        }
    }


    /** Обычная рыба с определённым тиром */
    private static ItemStack getOverworldFish(int tier) {
        Item fish = FishTierSystem.getFishByTier(tier, false);
        if (fish == null) {
            return new ItemStack(Items.fish, 1, 0);
        }
        return new ItemStack(fish, 1);
    }

    /** Адская рыба с определённым тиром */
    private static ItemStack getNetherFish(int tier) {
        Item fish = FishTierSystem.getFishByTier(tier, true);
        if (fish == null) {
            return new ItemStack(Items.fish, 1, 0);
        }
        return new ItemStack(fish, 1);
    }

    /** Обычный предмет */
    private static ItemStack getOverworldItem() {
        Item[] items = ModItems.getAllItems();
        if (items.length == 0) {
            return new ItemStack(Items.dye, 1, 0);
        }
        Item selectedItem = items[RAND.nextInt(items.length)];
        return new ItemStack(selectedItem, 1);
    }

    /** Лут из сундука через конфиг */
    private static ItemStack getChestLootFromConfig(List<ModConfig.LootEntry> lootTable) {
        if (lootTable == null || lootTable.isEmpty()) {
            return new ItemStack(Items.saddle);
        }

        int totalWeight = 0;
        for (ModConfig.LootEntry e : lootTable) totalWeight += e.weight;

        int roll = RAND.nextInt(totalWeight);
        for (ModConfig.LootEntry e : lootTable) {
            roll -= e.weight;
            if (roll < 0) {
                return e.getStack(RAND);
            }
        }

        return new ItemStack(Items.saddle);
    }
}

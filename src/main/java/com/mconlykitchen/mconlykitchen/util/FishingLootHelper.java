package com.mconlykitchen.mconlykitchen.util;

import com.mconlykitchen.mconlykitchen.init.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import java.util.Random;

public class FishingLootHelper {

    private static final Random RAND = new Random();

    /**
     * Получить рыбу или предмет из водоёма
     */
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

    /**
     * Получить лут из сундука
     */
    public static ItemStack getChestLoot(boolean isGolden) {
        if (isGolden) {
            return getGoldenChestLoot();
        } else {
            return getNormalChestLoot();
        }
    }

    /**
     * Обычная рыба с определённым тиром
     */
    private static ItemStack getOverworldFish(int tier) {
        Item fish = FishTierSystem.getFishByTier(tier, false);

        if (fish == null) {
            return new ItemStack(Items.fish, 1, 0);
        }

        return new ItemStack(fish, 1);
    }

    /**
     * Адская рыба с определённым тиром
     */
    private static ItemStack getNetherFish(int tier) {
        Item fish = FishTierSystem.getFishByTier(tier, true);

        if (fish == null) {
            return new ItemStack(Items.fish, 1, 0);
        }

        return new ItemStack(fish, 1);
    }

    /**
     * Обычный предмет
     */
    private static ItemStack getOverworldItem() {
        Item[] items = ModItems.getAllItems();
        if (items.length == 0) {
            return new ItemStack(Items.dye, 1, 0);
        }

        Item selectedItem = items[RAND.nextInt(items.length)];
        return new ItemStack(selectedItem, 1);
    }

    /**
     * Лут из обычного сундука
     */
    private static ItemStack getNormalChestLoot() {
        int roll = RAND.nextInt(100);

        if (roll < 25) {
            // Алмазы (1-3)
            return new ItemStack(Items.diamond, 1 + RAND.nextInt(3));
        } else if (roll < 45) {
            // Золото (2-5)
            return new ItemStack(Items.gold_ingot, 2 + RAND.nextInt(4));
        } else if (roll < 65) {
            // Изумруды (1-3)
            return new ItemStack(Items.emerald, 1 + RAND.nextInt(3));
        } else if (roll < 80) {
            // Жемчуг эндера (2-5)
            return new ItemStack(Items.ender_pearl, 2 + RAND.nextInt(4));
        } else if (roll < 90) {
            // Зачарованная книга
            return new ItemStack(Items.enchanted_book);
        } else {
            // Седло
            return new ItemStack(Items.saddle);
        }
    }

    /**
     * Лут из золотого сундука (лучше)
     */
    private static ItemStack getGoldenChestLoot() {
        int roll = RAND.nextInt(100);

        if (roll < 35) {
            // Алмазы (4-8)
            return new ItemStack(Items.diamond, 4 + RAND.nextInt(5));
        } else if (roll < 55) {
            // Изумруды (4-7)
            return new ItemStack(Items.emerald, 4 + RAND.nextInt(4));
        } else if (roll < 70) {
            // Звезда незера (1)
            return new ItemStack(Items.nether_star, 1);
        } else if (roll < 85) {
            // Зачарованная книга (с хорошими зачарованиями)
            // TODO: добавить мощные зачарования
            return new ItemStack(Items.enchanted_book);
        } else if (roll < 92) {
            // Именная бирка
            return new ItemStack(Items.name_tag);
        } else {
            // Музыкальная пластинка (редкая)
            int record = 2256 + RAND.nextInt(12); // записи от 2256 до 2267
            return new ItemStack(Item.getItemById(record));
        }
    }
}
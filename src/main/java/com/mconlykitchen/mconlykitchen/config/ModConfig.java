package com.mconlykitchen.mconlykitchen.config;

import com.mconlykitchen.mconlykitchen.MCOnlyKitchen;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModConfig {

    private static Configuration config;

    // ===== КАТЕГОРИИ =====
    private static final String CATEGORY_RODS = "fishing_rods";
    private static final String CATEGORY_CHESTS = "treasure_chests";
    private static final String CATEGORY_LOOT = "treasure_chest_loot";
    private static final String CATEGORY_GAMEPLAY = "gameplay";

    // ===== ГЕЙМПЛЕЙ =====
    public static boolean enableChestAnimation = true;
    public static boolean enableSounds = true;
    public static int minigameFailTime = 180; // в тиках

    // ===== ВРЕМЯ ПОКЛЁВКИ (0..6) =====
    public static int[] biteTimesByTier = {60, 30, 15, 60, 30, 15, 5};

    // ===== ШАНСЫ СУНДУКОВ (РАЗДЕЛЬНО: БЛОК vs МИНИ-ИГРА, 0..6) =====
    public static int[] blockChestChanceByTier       = {5, 7, 10, 12, 15, 18, 22};
    public static int[] blockGoldenChestChanceByTier = {2, 3, 4, 6, 8, 10, 12};

    public static int[] miniChestChanceByTier        = {5, 8, 12, 15, 20, 25, 100};
    public static int[] miniGoldenChestChanceByTier  = {3, 4, 6, 8, 10, 12, 15};

    // ===== ЛУТ =====
    private static final List<LootEntry> NORMAL_LOOT = new ArrayList<LootEntry>();
    private static final List<LootEntry> GOLDEN_LOOT = new ArrayList<LootEntry>();
    private static final Random RAND = new Random();

    public static class LootEntry {
        public final Item item;
        public final int minCount;
        public final int maxCount;
        public final int weight;

        public LootEntry(Item item, int minCount, int maxCount, int weight) {
            this.item = item;
            this.minCount = minCount;
            this.maxCount = maxCount;
            this.weight = weight;
        }

        public ItemStack getStack(Random rand) {
            int count = minCount + rand.nextInt(maxCount - minCount + 1);
            return new ItemStack(item, count);
        }
    }

    // ===== INIT =====
    public static void init(File file) {
        config = new Configuration(file);

        try {
            config.load();

            // --- геймплей ---
            enableChestAnimation = config.getBoolean(
                    "enableChestAnimation",
                    CATEGORY_GAMEPLAY,
                    true,
                    "Показывать анимацию сундука"
            );

            enableSounds = config.getBoolean(
                    "enableSounds",
                    CATEGORY_GAMEPLAY,
                    true,
                    "Включить звуки рыбалки"
            );

            minigameFailTime = config.getInt(
                    "minigameFailTime",
                    CATEGORY_GAMEPLAY,
                    180,
                    60,
                    600,
                    "Время до провала мини-игры (в тиках)"
            );

            // --- шансы сундуков: блоковый механизм ---
            blockChestChanceByTier = config.get(
                    CATEGORY_CHESTS,
                    "blockChestChanceByTier",
                    new int[]{5, 7, 10, 12, 15, 18, 22},
                    "Шанс появления сундука в механизме блока по тиру (0..6), %"
            ).getIntList();

            blockGoldenChestChanceByTier = config.get(
                    CATEGORY_CHESTS,
                    "blockGoldenChestChanceByTier",
                    new int[]{2, 3, 4, 6, 8, 10, 12},
                    "Шанс золотого сундука в механизме блока по тиру (0..6), %"
            ).getIntList();

            // --- шансы сундуков: мини-игра ---
            miniChestChanceByTier = config.get(
                    CATEGORY_CHESTS,
                    "miniChestChanceByTier",
                    new int[]{5, 8, 12, 15, 20, 25, 100},
                    "Шанс появления сундука в мини-игре по тиру (0..6), %"
            ).getIntList();

            miniGoldenChestChanceByTier = config.get(
                    CATEGORY_CHESTS,
                    "miniGoldenChestChanceByTier",
                    new int[]{3, 4, 6, 8, 10, 12, 100},
                    "Шанс золотого сундука в мини-игре по тиру (0..6), %"
            ).getIntList();

            // Санити: ровно 7 значений
            blockChestChanceByTier       = ensure7(blockChestChanceByTier, 5);
            blockGoldenChestChanceByTier = ensure7(blockGoldenChestChanceByTier, 2);
            miniChestChanceByTier        = ensure7(miniChestChanceByTier, 5);
            miniGoldenChestChanceByTier  = ensure7(miniGoldenChestChanceByTier, 3);

            loadLoot();

            // Санити biteTimesByTier
            if (biteTimesByTier == null || biteTimesByTier.length != 7) {
                MCOnlyKitchen.LOGGER.warn("biteTimesByTier повреждены, восстановление по умолчанию.");
                biteTimesByTier = new int[]{60, 30, 15, 60, 30, 15, 5};
            }

        } catch (Exception e) {
            MCOnlyKitchen.LOGGER.error("Ошибка загрузки конфига", e);
        } finally {
            if (config.hasChanged()) config.save();
        }
    }

    // ===== ЗАГРУЗКА ЛУТА =====
    private static void loadLoot() {
        NORMAL_LOOT.clear();
        GOLDEN_LOOT.clear();

        String[] normal = config.get(
                CATEGORY_LOOT,
                "normalChest",
                new String[]{
                        "minecraft:iron_ingot@1-3@40",
                        "minecraft:gold_ingot@2-5@30",
                        "minecraft:diamond@1-2@15"
                }
        ).getStringList();

        String[] golden = config.get(
                CATEGORY_LOOT,
                "goldenChest",
                new String[]{
                        "minecraft:iron_ingot@1-3@40",
                        "minecraft:gold_ingot@2-5@30",
                        "minecraft:diamond@1-2@15"
                }
        ).getStringList();

        parseLoot(normal, NORMAL_LOOT);
        parseLoot(golden, GOLDEN_LOOT);
    }

    // ===== УТИЛИТЫ =====
    private static int[] ensure7(int[] arr, int def) {
        if (arr == null || arr.length != 7) {
            int[] r = new int[7];
            for (int i = 0; i < 7; i++) r[i] = def;
            return r;
        }
        return arr;
    }

    private static int clampTierIndex(int tier) {
        if (tier < 0) return 0;
        if (tier > 6) return 6;
        return tier;
    }

    public static int getBiteTimeByTier(int tier) {
        int idx = clampTierIndex(tier);
        return biteTimesByTier[idx];
    }

    private static void parseLoot(String[] data, List<LootEntry> list) {
        for (String s : data) {
            try {
                String[] p = s.split("@");
                String[] id = p[0].split(":");

                Item item = GameRegistry.findItem(id[0], id[1]);

                String[] range = p[1].split("-");
                int minCount = Integer.parseInt(range[0]);
                int maxCount = range.length > 1 ? Integer.parseInt(range[1]) : minCount;

                int weight = Integer.parseInt(p[2]);

                if (item != null && weight > 0) {
                    list.add(new LootEntry(item, minCount, maxCount, weight));
                }
            } catch (Exception e) {
                MCOnlyKitchen.LOGGER.error("Ошибка лута: " + s);
            }
        }
    }

    // ===== ВЫПАДЕНИЕ =====
    public static ItemStack getChestLoot(boolean isGoldenChest) {
        return isGoldenChest ? getGoldenChestLoot() : getNormalChestLoot();
    }

    public static ItemStack getNormalChestLoot() {
        return rollLootTable(NORMAL_LOOT);
    }

    public static ItemStack getGoldenChestLoot() {
        return rollLootTable(GOLDEN_LOOT);
    }

    private static ItemStack rollLootTable(List<LootEntry> loot) {
        if (loot.isEmpty()) return null;

        int totalWeight = 0;
        for (LootEntry e : loot) totalWeight += e.weight;

        int roll = RAND.nextInt(totalWeight);
        for (LootEntry e : loot) {
            roll -= e.weight;
            if (roll < 0) return e.getStack(RAND);
        }
        return null;
    }

    // ===== API: РАЗДЕЛЬНАЯ ЛОГИКА ШАНСОВ =====
    // Для механизма блока
    public static int getBlockChestChanceByTier(int tier) {
        return blockChestChanceByTier[clampTierIndex(tier)];
    }
    public static int getBlockGoldenChestChanceByTier(int tier) {
        return blockGoldenChestChanceByTier[clampTierIndex(tier)];
    }

    // Для мини-игры
    public static int getMiniChestChanceByTier(int tier) {
        return miniChestChanceByTier[clampTierIndex(tier)];
    }
    public static int getMiniGoldenChestChanceByTier(int tier) {
        return miniGoldenChestChanceByTier[clampTierIndex(tier)];
    }
}

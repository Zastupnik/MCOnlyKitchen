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

    // ===== ШАНСЫ =====
    public static int chestChanceBasic = 5;
    public static int goldenChestChanceBasic = 10;

    // ===== ГЕЙМПЛЕЙ =====
    public static boolean enableChestAnimation = true;
    public static boolean enableSounds = true;
    public static int minigameFailTime = 180;

    // ===== ЛУТ =====
    private static final List<LootEntry> NORMAL_LOOT = new ArrayList<LootEntry>();
    private static final List<LootEntry> GOLDEN_LOOT = new ArrayList<LootEntry>();
    private static final Random RAND = new Random();

    public static class LootEntry {
        public final ItemStack stack;
        public final int weight;

        public LootEntry(ItemStack stack, int weight) {
            this.stack = stack;
            this.weight = weight;
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

            loadLoot();

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
                        "minecraft:iron_ingot@3@40",
                        "minecraft:gold_ingot@2@30",
                        "minecraft:diamond@1@15"
                }
        ).getStringList();

        String[] golden = config.get(
                CATEGORY_LOOT,
                "goldenChest",
                new String[]{
                        "minecraft:diamond@3@40",
                        "minecraft:emerald@4@30",
                        "minecraft:nether_star@1@5"
                }
        ).getStringList();

        parseLoot(normal, NORMAL_LOOT);
        parseLoot(golden, GOLDEN_LOOT);
    }
    // ===== ВРЕМЯ ПОКЛЁВКИ =====
    public static int[] biteTimes = {60, 30, 15, 5}; // секунды, по типам удочек

    public static int getBiteTime(int rodTier, boolean isNether) {
        // rodTier 0..3 или 0..4, зависит от вашей маппинга
        if (rodTier < 0 || rodTier >= biteTimes.length) return biteTimes[0];
        return biteTimes[rodTier];
    }

    private static void parseLoot(String[] data, List<LootEntry> list) {
        for (String s : data) {
            try {
                String[] p = s.split("@");
                String[] id = p[0].split(":");

                Item item = GameRegistry.findItem(id[0], id[1]);
                int count = Integer.parseInt(p[1]);
                int weight = Integer.parseInt(p[2]);

                if (item != null && weight > 0) {
                    list.add(new LootEntry(new ItemStack(item, count), weight));
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
            if (roll < 0) return e.stack.copy();
        }
        return null;
    }

    // ===== API =====
    public static int getChestChance(int rodTier, boolean isNether) {
        // TODO: добавить бонусы от уровня удочки и модулей блока
        return chestChanceBasic;
    }

    public static int getGoldenChestChance(int rodTier, boolean isNether) {
        // TODO: добавить бонусы от уровня удочки и модулей блока
        return goldenChestChanceBasic;
    }
}

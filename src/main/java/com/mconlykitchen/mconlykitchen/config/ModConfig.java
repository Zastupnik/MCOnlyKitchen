package com.mconlykitchen.mconlykitchen.config;

import com.mconlykitchen.mconlykitchen.MCOnlyKitchen;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModConfig {

    private static Configuration config;
    private static final Random rand = new Random();

    // Категории
    private static final String CATEGORY_RODS = "fishing_rods";
    private static final String CATEGORY_CHESTS = "treasure_chests";
    private static final String CATEGORY_GAMEPLAY = "gameplay";

    // Настройки удочек
    public static float difficultyBasic = 1.0f;
    public static float difficultyImproved = 0.8f;
    public static float difficultyAdvanced = 0.6f;
    public static float difficultyNetherBasic = 1.0f;
    public static float difficultyNetherImproved = 0.8f;
    public static float difficultyNetherAdvanced = 0.6f;
    public static float difficultyUniversal = 0.4f;

    // Шансы сундуков
    public static int chestChanceBasic = 100;
    public static int chestChanceImproved = 100;
    public static int chestChanceAdvanced = 100;
    public static int chestChanceNetherBasic = 5;
    public static int chestChanceNetherImproved = 8;
    public static int chestChanceNetherAdvanced = 12;
    public static int chestChanceUniversal = 100;

    // Шансы золотого сундука
    public static int goldenChestChanceBasic = 10;
    public static int goldenChestChanceImproved = 15;
    public static int goldenChestChanceAdvanced = 25;
    public static int goldenChestChanceNetherBasic = 10;
    public static int goldenChestChanceNetherImproved = 15;
    public static int goldenChestChanceNetherAdvanced = 25;
    public static int goldenChestChanceUniversal = 40;

    // Геймплей
    public static boolean enableChestAnimation = true;
    public static boolean enableSounds = true;
    public static int minigameFailTime = 180;

    // Лут сундуков
    private static final List<ItemStack> chestLootList = new ArrayList<ItemStack>();
    private static final List<ItemStack> goldenChestLootList = new ArrayList<ItemStack>();

    public static void init(File configFile) {
        config = new Configuration(configFile);

        try {
            config.load();

            // Сложность удочек
            difficultyBasic = config.getFloat("basicRodDifficulty", CATEGORY_RODS, 1.0f, 0.0f, 2.0f, "Сложность базовой удочки");
            difficultyImproved = config.getFloat("improvedRodDifficulty", CATEGORY_RODS, 0.8f, 0.0f, 2.0f, "Сложность улучшенной удочки");
            difficultyAdvanced = config.getFloat("advancedRodDifficulty", CATEGORY_RODS, 0.6f, 0.0f, 2.0f, "Сложность продвинутой удочки");
            difficultyNetherBasic = config.getFloat("netherBasicRodDifficulty", CATEGORY_RODS, 1.0f, 0.0f, 2.0f, "Сложность базовой адской удочки");
            difficultyNetherImproved = config.getFloat("netherImprovedRodDifficulty", CATEGORY_RODS, 0.8f, 0.0f, 2.0f, "Сложность улучшенной адской удочки");
            difficultyNetherAdvanced = config.getFloat("netherAdvancedRodDifficulty", CATEGORY_RODS, 0.6f, 0.0f, 2.0f, "Сложность продвинутой адской удочки");
            difficultyUniversal = config.getFloat("universalRodDifficulty", CATEGORY_RODS, 0.4f, 0.0f, 2.0f, "Сложность универсальной удочки");

            // Шансы сундуков
// Шансы сундуков
            chestChanceBasic = config.getInt("basicRodChestChance", CATEGORY_CHESTS, 5, 0, 100, "Шанс сундука для базовой удочки");
            chestChanceImproved = config.getInt("improvedRodChestChance", CATEGORY_CHESTS, 8, 0, 100, "Шанс сундука для улучшенной удочки");
            chestChanceAdvanced = config.getInt("advancedRodChestChance", CATEGORY_CHESTS, 12, 0, 100, "Шанс сундука для продвинутой удочки");
            chestChanceNetherBasic = config.getInt("netherBasicRodChestChance", CATEGORY_CHESTS, 5, 0, 100, "Шанс сундука для базовой адской удочки");
            chestChanceNetherImproved = config.getInt("netherImprovedRodChestChance", CATEGORY_CHESTS, 8, 0, 100, "Шанс сундука для улучшенной адской удочки");
            chestChanceNetherAdvanced = config.getInt("netherAdvancedRodChestChance", CATEGORY_CHESTS, 12, 0, 100, "Шанс сундука для продвинутой адской удочки");
            chestChanceUniversal = config.getInt("universalRodChestChance", CATEGORY_CHESTS, 15, 0, 100, "Шанс сундука для универсальной удочки");
            // Шансы золотого сундука
            goldenChestChanceBasic = config.getInt("basicRodGoldenChestChance", CATEGORY_CHESTS, 10, 0, 100, "Шанс золотого сундука для базовой удочки");
            goldenChestChanceImproved = config.getInt("improvedRodGoldenChestChance", CATEGORY_CHESTS, 15, 0, 100, "Шанс золотого сундука для улучшенной удочки");
            goldenChestChanceAdvanced = config.getInt("advancedRodGoldenChestChance", CATEGORY_CHESTS, 25, 0, 100, "Шанс золотого сундука для продвинутой удочки");
            goldenChestChanceNetherBasic = config.getInt("netherBasicRodGoldenChestChance", CATEGORY_CHESTS, 10, 0, 100, "Шанс золотого сундука для базовой адской удочки");
            goldenChestChanceNetherImproved = config.getInt("netherImprovedRodGoldenChestChance", CATEGORY_CHESTS, 15, 0, 100, "Шанс золотого сундука для улучшенной адской удочки");
            goldenChestChanceNetherAdvanced = config.getInt("netherAdvancedRodGoldenChestChance", CATEGORY_CHESTS, 25, 0, 100, "Шанс золотого сундука для продвинутой адской удочки");
            goldenChestChanceUniversal = config.getInt("universalRodGoldenChestChance", CATEGORY_CHESTS, 40, 0, 100, "Шанс золотого сундука для универсальной удочки");

            // Геймплей
            enableChestAnimation = config.getBoolean("enableChestAnimation", CATEGORY_GAMEPLAY, true, "Анимация сундука");
            enableSounds = config.getBoolean("enableSounds", CATEGORY_GAMEPLAY, true, "Звуки рыбалки");
            minigameFailTime = config.getInt("minigameFailTime", CATEGORY_GAMEPLAY, 180, 60, 600, "Время до провала мини-игры");

            // Лут сундуков (редактируемые списки в конфиге; формат: modid:item*count)
            String[] chestLootStrings = config.getStringList(
                    "basicChestLoot", CATEGORY_CHESTS,
                    new String[]{"minecraft:iron_ingot*3", "minecraft:gold_ingot*2", "minecraft:fish*1"},
                    "Список предметов для обычного сундука (modid:item*count)"
            );

            String[] goldenChestLootStrings = config.getStringList(
                    "goldenChestLoot", CATEGORY_CHESTS,
                    new String[]{"minecraft:diamond", "minecraft:emerald", "minecraft:golden_apple"},
                    "Список предметов для золотого сундука (modid:item*count)"
            );

            parseLoot(chestLootStrings, chestLootList);
            parseLoot(goldenChestLootStrings, goldenChestLootList);

        } catch (Exception e) {
            MCOnlyKitchen.LOGGER.error("Ошибка загрузки конфига", e);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    // Парсер для 1.7.10: GameRegistry.findItem(modid, name)
    private static void parseLoot(String[] entries, List<ItemStack> target) {
        target.clear();
        for (String s : entries) {
            try {
                String[] countSplit = s.split("\\*");
                String id = countSplit[0];                // modid:item
                int count = (countSplit.length > 1) ? Integer.parseInt(countSplit[1]) : 1;

                String[] nameSplit = id.split(":");
                String modid = (nameSplit.length > 1) ? nameSplit[0] : "minecraft";
                String itemName = (nameSplit.length > 1) ? nameSplit[1] : nameSplit[0];

                Item item = GameRegistry.findItem(modid, itemName);
                if (item != null) {
                    target.add(new ItemStack(item, Math.max(1, count)));
                } else {
                    MCOnlyKitchen.LOGGER.warn("Не найден предмет в реестре 1.7.10: " + modid + ":" + itemName);
                }
            } catch (Exception e) {
                MCOnlyKitchen.LOGGER.warn("Ошибка парсинга записи лута: " + s, e);
            }
        }
    }

    // Сложность по типу удочки и измерению
    public static float getDifficulty(int rodTier, boolean isNether) {
        if (rodTier == 4) return difficultyUniversal;

        if (isNether) {
            switch (rodTier) {
                case 1: return difficultyNetherBasic;
                case 2: return difficultyNetherImproved;
                case 3: return difficultyNetherAdvanced;
                default: return 1.0f;
            }
        } else {
            switch (rodTier) {
                case 0: return difficultyBasic;
                case 1: return difficultyImproved;
                case 2: return difficultyAdvanced;
                default: return 1.0f;
            }
        }
    }

    // Шанс появления сундука
    public static int getChestChance(int rodTier, boolean isNether) {
        if (rodTier == 4) return chestChanceUniversal;

        if (isNether) {
            switch (rodTier) {
                case 1: return chestChanceNetherBasic;
                case 2: return chestChanceNetherImproved;
                case 3: return chestChanceNetherAdvanced;
                default: return 0;
            }
        } else {
            switch (rodTier) {
                case 0: return chestChanceBasic;
                case 1: return chestChanceImproved;
                case 2: return chestChanceAdvanced;
                default: return 0;
            }
        }
    }

    // Шанс, что сундук будет золотым
    public static int getGoldenChestChance(int rodTier, boolean isNether) {
        if (rodTier == 4) return goldenChestChanceUniversal;

        if (isNether) {
            switch (rodTier) {
                case 1: return goldenChestChanceNetherBasic;
                case 2: return goldenChestChanceNetherImproved;
                case 3: return goldenChestChanceNetherAdvanced;
                default: return 0;
            }
        } else {
            switch (rodTier) {
                case 0: return goldenChestChanceBasic;
                case 1: return goldenChestChanceImproved;
                case 2: return goldenChestChanceAdvanced;
                default: return 0;
            }
        }
    }

    // Получение предмета награды из конфигурации
    public static ItemStack getChestLoot(boolean golden) {
        List<ItemStack> list = golden ? goldenChestLootList : chestLootList;
        if (list.isEmpty()) {
            // Надёжный fallback для 1.7.10
            return new ItemStack(Items.stick);
        }
        return list.get(rand.nextInt(list.size()));
    }
}

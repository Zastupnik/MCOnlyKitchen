package com.mconlykitchen.mconlykitchen.config;

import com.mconlykitchen.mconlykitchen.MCOnlyKitchen;
import net.minecraftforge.common.config.Configuration;
import java.io.File;

public class ModConfig {

    private static Configuration config;

    // Категории
    private static final String CATEGORY_RODS = "fishing_rods";
    private static final String CATEGORY_CHESTS = "treasure_chests";
    private static final String CATEGORY_GAMEPLAY = "gameplay";

    // Настройки удочек - сложность (0.0 = легко, 1.0 = сложно)
    public static float difficultyBasic = 1.0f;
    public static float difficultyImproved = 0.8f;
    public static float difficultyAdvanced = 0.6f;
    public static float difficultyNetherBasic = 1.0f;
    public static float difficultyNetherImproved = 0.8f;
    public static float difficultyNetherAdvanced = 0.6f;
    public static float difficultyUniversal = 0.4f;

    // Шансы на сундуки (в процентах)
    public static int chestChanceBasic = 5;
    public static int chestChanceImproved = 8;
    public static int chestChanceAdvanced = 12;
    public static int chestChanceNetherBasic = 5;
    public static int chestChanceNetherImproved = 8;
    public static int chestChanceNetherAdvanced = 12;
    public static int chestChanceUniversal = 15;

    // Шансы на золотой сундук (в процентах от обычного сундука)
    public static int goldenChestChanceBasic = 10;
    public static int goldenChestChanceImproved = 15;
    public static int goldenChestChanceAdvanced = 25;
    public static int goldenChestChanceNetherBasic = 10;
    public static int goldenChestChanceNetherImproved = 15;
    public static int goldenChestChanceNetherAdvanced = 25;
    public static int goldenChestChanceUniversal = 40;

    // Настройки геймплея
    public static boolean enableChestAnimation = true;
    public static boolean enableSounds = true;
    public static int minigameFailTime = 180; // тики до провала (180 = 9 секунд)

    public static void init(File configFile) {
        config = new Configuration(configFile);

        try {
            config.load();

            // Сложность удочек
            difficultyBasic = config.getFloat("basicRodDifficulty", CATEGORY_RODS, 1.0f, 0.0f, 2.0f,
                    "Сложность базовой удочки (0.0 = очень легко, 1.0 = нормально, 2.0 = очень сложно)");
            difficultyImproved = config.getFloat("improvedRodDifficulty", CATEGORY_RODS, 0.8f, 0.0f, 2.0f,
                    "Сложность улучшенной удочки");
            difficultyAdvanced = config.getFloat("advancedRodDifficulty", CATEGORY_RODS, 0.6f, 0.0f, 2.0f,
                    "Сложность продвинутой удочки");
            difficultyNetherBasic = config.getFloat("netherBasicRodDifficulty", CATEGORY_RODS, 1.0f, 0.0f, 2.0f,
                    "Сложность базовой адской удочки");
            difficultyNetherImproved = config.getFloat("netherImprovedRodDifficulty", CATEGORY_RODS, 0.8f, 0.0f, 2.0f,
                    "Сложность улучшенной адской удочки");
            difficultyNetherAdvanced = config.getFloat("netherAdvancedRodDifficulty", CATEGORY_RODS, 0.6f, 0.0f, 2.0f,
                    "Сложность продвинутой адской удочки");
            difficultyUniversal = config.getFloat("universalRodDifficulty", CATEGORY_RODS, 0.4f, 0.0f, 2.0f,
                    "Сложность универсальной удочки");

            // Шансы на сундуки
            chestChanceBasic = config.getInt("basicRodChestChance", CATEGORY_CHESTS, 5, 0, 100,
                    "Шанс получить сундук с базовой удочкой (%)");
            chestChanceImproved = config.getInt("improvedRodChestChance", CATEGORY_CHESTS, 8, 0, 100,
                    "Шанс получить сундук с улучшенной удочкой (%)");
            chestChanceAdvanced = config.getInt("advancedRodChestChance", CATEGORY_CHESTS, 12, 0, 100,
                    "Шанс получить сундук с продвинутой удочкой (%)");
            chestChanceNetherBasic = config.getInt("netherBasicRodChestChance", CATEGORY_CHESTS, 5, 0, 100,
                    "Шанс получить сундук с базовой адской удочкой (%)");
            chestChanceNetherImproved = config.getInt("netherImprovedRodChestChance", CATEGORY_CHESTS, 8, 0, 100,
                    "Шанс получить сундук с улучшенной адской удочкой (%)");
            chestChanceNetherAdvanced = config.getInt("netherAdvancedRodChestChance", CATEGORY_CHESTS, 12, 0, 100,
                    "Шанс получить сундук с продвинутой адской удочкой (%)");
            chestChanceUniversal = config.getInt("universalRodChestChance", CATEGORY_CHESTS, 15, 0, 100,
                    "Шанс получить сундук с универсальной удочкой (%)");

            // Шансы на золотой сундук
            goldenChestChanceBasic = config.getInt("basicRodGoldenChestChance", CATEGORY_CHESTS, 10, 0, 100,
                    "Шанс что сундук будет золотым с базовой удочкой (% от шанса сундука)");
            goldenChestChanceImproved = config.getInt("improvedRodGoldenChestChance", CATEGORY_CHESTS, 15, 0, 100,
                    "Шанс что сундук будет золотым с улучшенной удочкой (% от шанса сундука)");
            goldenChestChanceAdvanced = config.getInt("advancedRodGoldenChestChance", CATEGORY_CHESTS, 25, 0, 100,
                    "Шанс что сундук будет золотым с продвинутой удочкой (% от шанса сундука)");
            goldenChestChanceNetherBasic = config.getInt("netherBasicRodGoldenChestChance", CATEGORY_CHESTS, 10, 0, 100,
                    "Шанс что сундук будет золотым с базовой адской удочкой (% от шанса сундука)");
            goldenChestChanceNetherImproved = config.getInt("netherImprovedRodGoldenChestChance", CATEGORY_CHESTS, 15, 0, 100,
                    "Шанс что сундук будет золотым с улучшенной адской удочкой (% от шанса сундука)");
            goldenChestChanceNetherAdvanced = config.getInt("netherAdvancedRodGoldenChestChance", CATEGORY_CHESTS, 25, 0, 100,
                    "Шанс что сундук будет золотым с продвинутой адской удочкой (% от шанса сундука)");
            goldenChestChanceUniversal = config.getInt("universalRodGoldenChestChance", CATEGORY_CHESTS, 40, 0, 100,
                    "Шанс что сундук будет золотым с универсальной удочкой (% от шанса сундука)");

            // Геймплей
            enableChestAnimation = config.getBoolean("enableChestAnimation", CATEGORY_GAMEPLAY, true,
                    "Показывать анимацию открытия сундука");
            enableSounds = config.getBoolean("enableSounds", CATEGORY_GAMEPLAY, true,
                    "Включить звуки рыбалки");
            minigameFailTime = config.getInt("minigameFailTime", CATEGORY_GAMEPLAY, 180, 60, 600,
                    "Время до провала мини-игры в тиках (20 тиков = 1 секунда)");

        } catch (Exception e) {
            MCOnlyKitchen.LOGGER.error("Ошибка загрузки конфига", e);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    public static float getDifficulty(int rodTier, boolean isNether) {
        if (rodTier == 6) return difficultyUniversal; // универсальная

        if (isNether) {
            switch (rodTier) {
                case 3: return difficultyNetherBasic;
                case 4: return difficultyNetherImproved;
                case 5: return difficultyNetherAdvanced;
            }
        } else {
            switch (rodTier) {
                case 0: return difficultyBasic;
                case 1: return difficultyImproved;
                case 2: return difficultyAdvanced;
            }
        }

        return 1.0f;
    }

    public static int getChestChance(int rodTier, boolean isNether) {
        if (rodTier == 6) return chestChanceUniversal;

        if (isNether) {
            switch (rodTier) {
                case 3: return chestChanceNetherBasic;
                case 4: return chestChanceNetherImproved;
                case 5: return chestChanceNetherAdvanced;
            }
        } else {
            switch (rodTier) {
                case 0: return chestChanceBasic;
                case 1: return chestChanceImproved;
                case 2: return chestChanceAdvanced;
            }
        }

        return 5;
    }

    public static int getGoldenChestChance(int rodTier, boolean isNether) {
        if (rodTier == 6) return goldenChestChanceUniversal;

        if (isNether) {
            switch (rodTier) {
                case 3: return goldenChestChanceNetherBasic;
                case 4: return goldenChestChanceNetherImproved;
                case 5: return goldenChestChanceNetherAdvanced;
            }
        } else {
            switch (rodTier) {
                case 0: return goldenChestChanceBasic;
                case 1: return goldenChestChanceImproved;
                case 2: return goldenChestChanceAdvanced;
            }
        }

        return 10;
    }
}
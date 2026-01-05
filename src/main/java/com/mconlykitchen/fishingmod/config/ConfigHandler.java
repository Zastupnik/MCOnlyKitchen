package com.mconlykitchen.fishingmod.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ConfigHandler {

    // параметры сложности
    public static float fishingDifficulty;
    public static float netherFishingDifficulty;

    // сундуки
    public static List<String> chestLoot;
    public static List<String> netherChestLoot;
    public static float chestLootChance;
    public static float netherChestLootChance;
    public static float goldenChestChance;

    private static Configuration config;

    public static void init(File configFile) {
        config = new Configuration(configFile);
        load();
    }

    private static void load() {
        try {
            config.load();

            // сложность
            fishingDifficulty = (float) config.get("difficulty", "fishingDifficulty", 1.0,
                    "Сложность рыбалки (обычный мир)").getDouble(1.0);
            netherFishingDifficulty = (float) config.get("difficulty", "netherFishingDifficulty", 1.5,
                    "Сложность рыбалки (Ад)").getDouble(1.5);

            // сундуки
            chestLootChance = (float) config.get("chest", "chestLootChance", 0.2,
                    "Шанс выпадения сундука в обычном мире").getDouble(0.2);
            netherChestLootChance = (float) config.get("chest", "netherChestLootChance", 0.1,
                    "Шанс выпадения сундука в Аду").getDouble(0.1);
            goldenChestChance = (float) config.get("chest", "goldenChestChance", 0.05,
                    "Шанс выпадения золотого сундука").getDouble(0.05);

            // списки лута сундуков
            String[] chestLootArray = config.get("chest", "chestLoot",
                    new String[]{"diamond", "iron_ingot", "gold_ingot"},
                    "Список предметов для сундука (обычный мир)").getStringList();
            chestLoot = Arrays.asList(chestLootArray);

            String[] netherChestLootArray = config.get("chest", "netherChestLoot",
                    new String[]{"nether_star", "blaze_rod"},
                    "Список предметов для сундука (Ад)").getStringList();
            netherChestLoot = Arrays.asList(netherChestLootArray);

        } catch (Exception e) {
            System.out.println("Ошибка загрузки конфига рыбалки: " + e);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}

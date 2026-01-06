package com.mconlykitchen.mconlykitchen.util;

import com.mconlykitchen.mconlykitchen.init.ModItems;
import net.minecraft.item.Item;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Система тиров рыбы:
 * Tier 1 - Обычная (40% шанс) - Лёгкая
 * Tier 2 - Необычная (30% шанс) - Средняя
 * Tier 3 - Редкая (20% шанс) - Нормальная
 * Tier 4 - Эпическая (8% шанс) - Сложная
 * Tier 5 - Легендарная (2% шанс) - Очень сложная
 */
public class FishTierSystem {

    private static final Random RAND = new Random();

    // Обычный мир
    private static final List<Item> TIER1_OVERWORLD = new ArrayList<>();
    private static final List<Item> TIER2_OVERWORLD = new ArrayList<>();
    private static final List<Item> TIER3_OVERWORLD = new ArrayList<>();
    private static final List<Item> TIER4_OVERWORLD = new ArrayList<>();
    private static final List<Item> TIER5_OVERWORLD = new ArrayList<>();

    // Незер
    private static final List<Item> TIER1_NETHER = new ArrayList<>();
    private static final List<Item> TIER2_NETHER = new ArrayList<>();
    private static final List<Item> TIER3_NETHER = new ArrayList<>();
    private static final List<Item> TIER4_NETHER = new ArrayList<>();
    private static final List<Item> TIER5_NETHER = new ArrayList<>();

    /**
     * Инициализация тиров рыбы
     */
    public static void init() {
        // ОБЫЧНЫЙ МИР
        // Tier 1 (7 рыб)
        TIER1_OVERWORLD.add(ModItems.fish_sardine);
        TIER1_OVERWORLD.add(ModItems.fish_anchovy);
        TIER1_OVERWORLD.add(ModItems.fish_herring);
        TIER1_OVERWORLD.add(ModItems.fish_perch);
        TIER1_OVERWORLD.add(ModItems.fish_crab);
        TIER1_OVERWORLD.add(ModItems.fish_squid);
        TIER1_OVERWORLD.add(ModItems.fish_mackerel);

        // Tier 2 (7 рыб)
        TIER2_OVERWORLD.add(ModItems.fish_cod);
        TIER2_OVERWORLD.add(ModItems.fish_bass);
        TIER2_OVERWORLD.add(ModItems.fish_flounder);
        TIER2_OVERWORLD.add(ModItems.fish_catfish);
        TIER2_OVERWORLD.add(ModItems.fish_tilapia);
        TIER2_OVERWORLD.add(ModItems.fish_eel);
        TIER2_OVERWORLD.add(ModItems.fish_octopus);

        // Tier 3 (7 рыб)
        TIER3_OVERWORLD.add(ModItems.fish_salmon);
        TIER3_OVERWORLD.add(ModItems.fish_trout);
        TIER3_OVERWORLD.add(ModItems.fish_pike);
        TIER3_OVERWORLD.add(ModItems.fish_carp);
        TIER3_OVERWORLD.add(ModItems.fish_halibut);
        TIER3_OVERWORLD.add(ModItems.fish_snapper);
        TIER3_OVERWORLD.add(ModItems.fish_grouper);

        // Tier 4 (4 рыбы)
        TIER4_OVERWORLD.add(ModItems.fish_tuna);
        TIER4_OVERWORLD.add(ModItems.fish_sturgeon);
        TIER4_OVERWORLD.add(ModItems.fish_barracuda);
        TIER4_OVERWORLD.add(ModItems.fish_lobster);

        // Tier 5 (3 рыбы - легендарные)
        TIER5_OVERWORLD.add(ModItems.fish_swordfish);
        TIER5_OVERWORLD.add(ModItems.fish_marlin);
        TIER5_OVERWORLD.add(ModItems.fish_shark);

        // НЕЗЕР
        // Tier 1 (5 рыб)
        TIER1_NETHER.add(ModItems.nether_fish_ember);
        TIER1_NETHER.add(ModItems.nether_fish_basalt);
        TIER1_NETHER.add(ModItems.nether_fish_crimson);
        TIER1_NETHER.add(ModItems.nether_fish_warped);
        TIER1_NETHER.add(ModItems.nether_fish_soul);

        // Tier 2 (5 рыб)
        TIER2_NETHER.add(ModItems.nether_fish_magma);
        TIER2_NETHER.add(ModItems.nether_fish_flame);
        TIER2_NETHER.add(ModItems.nether_fish_volcanic);
        TIER2_NETHER.add(ModItems.nether_fish_blackstone);
        TIER2_NETHER.add(ModItems.nether_fish_piglin);

        // Tier 3 (5 рыб)
        TIER3_NETHER.add(ModItems.nether_fish_infernal);
        TIER3_NETHER.add(ModItems.nether_fish_blaze);
        TIER3_NETHER.add(ModItems.nether_fish_hellfire);
        TIER3_NETHER.add(ModItems.nether_fish_hoglin);
        TIER3_NETHER.add(ModItems.nether_fish_strider);

        // Tier 4 (3 рыбы)
        TIER4_NETHER.add(ModItems.nether_fish_ghast);
        TIER4_NETHER.add(ModItems.nether_fish_obsidian);
        TIER4_NETHER.add(ModItems.nether_fish_netherite);

        // Tier 5 (2 рыбы - легендарные)
        TIER5_NETHER.add(ModItems.nether_fish_wither);
        TIER5_NETHER.add(ModItems.nether_fish_void);
    }

    /**
     * Получить случайный тир рыбы с учётом вероятностей
     */
    public static int getRandomFishTier() {
        int roll = RAND.nextInt(100);

        if (roll < 40) return 1;      // 40%
        if (roll < 70) return 2;      // 30%
        if (roll < 90) return 3;      // 20%
        if (roll < 98) return 4;      // 8%
        return 5;                     // 2%
    }

    /**
     * Получить рыбу определённого тира
     */
    public static Item getFishByTier(int tier, boolean isNether) {
        List<Item> tierList = getTierList(tier, isNether);

        if (tierList.isEmpty()) {
            // Резерв - вернуть обычную рыбу
            return isNether ? ModItems.nether_fish_magma : ModItems.fish_cod;
        }

        return tierList.get(RAND.nextInt(tierList.size()));
    }

    /**
     * Получить список рыб по тиру
     */
    private static List<Item> getTierList(int tier, boolean isNether) {
        if (isNether) {
            switch (tier) {
                case 1: return TIER1_NETHER;
                case 2: return TIER2_NETHER;
                case 3: return TIER3_NETHER;
                case 4: return TIER4_NETHER;
                case 5: return TIER5_NETHER;
            }
        } else {
            switch (tier) {
                case 1: return TIER1_OVERWORLD;
                case 2: return TIER2_OVERWORLD;
                case 3: return TIER3_OVERWORLD;
                case 4: return TIER4_OVERWORLD;
                case 5: return TIER5_OVERWORLD;
            }
        }
        return new ArrayList<>();
    }

    /**
     * Получить название тира для локализации
     */
    public static String getTierName(int tier) {
        switch (tier) {
            case 1: return "tier.common";
            case 2: return "tier.uncommon";
            case 3: return "tier.rare";
            case 4: return "tier.epic";
            case 5: return "tier.legendary";
            default: return "tier.unknown";
        }
    }

    /**
     * Получить цвет тира
     */
    public static int getTierColor(int tier) {
        switch (tier) {
            case 1: return 0xFFFFFFFF; // Белый
            case 2: return 0xFF55FF55; // Зелёный
            case 3: return 0xFF5555FF; // Синий
            case 4: return 0xFFAA00AA; // Фиолетовый
            case 5: return 0xFFFFAA00; // Оранжевый
            default: return 0xFFFFFFFF;
        }
    }
}
package com.mconlykitchen.mconlykitchen.util;

import com.mconlykitchen.mconlykitchen.init.ModItems;
import net.minecraft.item.Item;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Система тиров рыбы (3 уровня):
 * Tier 1 - Обычная (простая) ~65%
 * Tier 2 - Средняя (реже) ~25%
 * Tier 3 - Топовая (очень редкая) ~10%
 */
public class FishTierSystem {

    private static final Random RAND = new Random();

    // Обычный мир
    private static final List<Item> TIER1_OVERWORLD = new ArrayList<>();
    private static final List<Item> TIER2_OVERWORLD = new ArrayList<>();
    private static final List<Item> TIER3_OVERWORLD = new ArrayList<>();

    // Незер
    private static final List<Item> TIER1_NETHER = new ArrayList<>();
    private static final List<Item> TIER2_NETHER = new ArrayList<>();
    private static final List<Item> TIER3_NETHER = new ArrayList<>();

    /** Инициализация тиров рыбы */
    public static void init() {
        // ОБЫЧНЫЙ МИР
        // Tier 1 (простая, много видов)
        TIER1_OVERWORLD.add(ModItems.fish_sardine);
        TIER1_OVERWORLD.add(ModItems.fish_anchovy);
        TIER1_OVERWORLD.add(ModItems.fish_herring);
        TIER1_OVERWORLD.add(ModItems.fish_perch);
        TIER1_OVERWORLD.add(ModItems.fish_crab);
        TIER1_OVERWORLD.add(ModItems.fish_squid);
        TIER1_OVERWORLD.add(ModItems.fish_mackerel);
        TIER1_OVERWORLD.add(ModItems.fish_cod);
        TIER1_OVERWORLD.add(ModItems.fish_bass);
        TIER1_OVERWORLD.add(ModItems.fish_flounder);
        TIER1_OVERWORLD.add(ModItems.fish_catfish);
        TIER1_OVERWORLD.add(ModItems.fish_tilapia);

        // Tier 2 (средняя, меньше видов)
        TIER2_OVERWORLD.add(ModItems.fish_eel);
        TIER2_OVERWORLD.add(ModItems.fish_octopus);
        TIER2_OVERWORLD.add(ModItems.fish_salmon);
        TIER2_OVERWORLD.add(ModItems.fish_trout);
        TIER2_OVERWORLD.add(ModItems.fish_pike);
        TIER2_OVERWORLD.add(ModItems.fish_carp);

        // Tier 3 (топовые, всего 3)
        TIER3_OVERWORLD.add(ModItems.fish_swordfish);
        TIER3_OVERWORLD.add(ModItems.fish_marlin);
        TIER3_OVERWORLD.add(ModItems.fish_shark);

        // НЕЗЕР
        // Tier 1
        TIER1_NETHER.add(ModItems.nether_fish_ember);
        TIER1_NETHER.add(ModItems.nether_fish_basalt);
        TIER1_NETHER.add(ModItems.nether_fish_crimson);
        TIER1_NETHER.add(ModItems.nether_fish_warped);
        TIER1_NETHER.add(ModItems.nether_fish_soul);
        TIER1_NETHER.add(ModItems.nether_fish_magma);
        TIER1_NETHER.add(ModItems.nether_fish_flame);

        // Tier 2
        TIER2_NETHER.add(ModItems.nether_fish_volcanic);
        TIER2_NETHER.add(ModItems.nether_fish_blackstone);
        TIER2_NETHER.add(ModItems.nether_fish_piglin);
        TIER2_NETHER.add(ModItems.nether_fish_infernal);
        TIER2_NETHER.add(ModItems.nether_fish_blaze);

        // Tier 3 (топовые, всего 2)
        TIER3_NETHER.add(ModItems.nether_fish_wither);
        TIER3_NETHER.add(ModItems.nether_fish_void);
    }

    /** Получить случайный тир рыбы с учётом вероятностей */
    public static int getRandomFishTier() {
        int roll = RAND.nextInt(100);

        if (roll < 65) return 1;   // 65% шанс — обычные
        if (roll < 90) return 2;   // 25% шанс — средние
        return 3;                  // 10% шанс — топовые
    }

    /** Получить рыбу определённого тира */
    public static Item getFishByTier(int tier, boolean isNether) {
        List<Item> tierList = getTierList(tier, isNether);

        if (tierList.isEmpty()) {
            return isNether ? ModItems.nether_fish_magma : ModItems.fish_cod;
        }

        return tierList.get(RAND.nextInt(tierList.size()));
    }

    /** Получить список рыб по тиру */
    private static List<Item> getTierList(int tier, boolean isNether) {
        if (isNether) {
            switch (tier) {
                case 1: return TIER1_NETHER;
                case 2: return TIER2_NETHER;
                case 3: return TIER3_NETHER;
            }
        } else {
            switch (tier) {
                case 1: return TIER1_OVERWORLD;
                case 2: return TIER2_OVERWORLD;
                case 3: return TIER3_OVERWORLD;
            }
        }
        return new ArrayList<>();
    }

    /** Получить название тира для локализации */
    public static String getTierName(int tier) {
        switch (tier) {
            case 1: return "tier.common";
            case 2: return "tier.uncommon";
            case 3: return "tier.rare";
            default: return "tier.unknown";
        }
    }

    /** Получить цвет тира */
    public static int getTierColor(int tier) {
        switch (tier) {
            case 1: return 0xFFFFFFFF; // Белый
            case 2: return 0xFF55FF55; // Зелёный
            case 3: return 0xFF5555FF; // Синий
            default: return 0xFFFFFFFF;
        }
    }
}

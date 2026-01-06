package com.mconlykitchen.mconlykitchen.init;

import com.mconlykitchen.mconlykitchen.item.ItemModFish;
import com.mconlykitchen.mconlykitchen.item.ItemModItem;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import java.util.ArrayList;
import java.util.List;

public class ModItems {

    // Списки для хранения предметов
    private static final List<Item> OVERWORLD_FISH = new ArrayList<>();
    private static final List<Item> NETHER_FISH = new ArrayList<>();
    private static final List<Item> MISC_ITEMS = new ArrayList<>();

    // Обычные рыбы (28 штук - добавишь свои названия)
    public static Item fish_salmon;
    public static Item fish_cod;
    public static Item fish_trout;
    public static Item fish_bass;
    public static Item fish_perch;
    public static Item fish_pike;
    public static Item fish_carp;
    public static Item fish_tuna;
    public static Item fish_mackerel;
    public static Item fish_herring;
    public static Item fish_sardine;
    public static Item fish_anchovy;
    public static Item fish_catfish;
    public static Item fish_sturgeon;
    public static Item fish_tilapia;
    public static Item fish_flounder;
    public static Item fish_halibut;
    public static Item fish_snapper;
    public static Item fish_grouper;
    public static Item fish_barracuda;
    public static Item fish_swordfish;
    public static Item fish_marlin;
    public static Item fish_shark;
    public static Item fish_eel;
    public static Item fish_octopus;
    public static Item fish_squid;
    public static Item fish_crab;
    public static Item fish_lobster;

    // Адские рыбы (20 штук - добавишь свои названия)
    public static Item nether_fish_magma;
    public static Item nether_fish_soul;
    public static Item nether_fish_infernal;
    public static Item nether_fish_blaze;
    public static Item nether_fish_ember;
    public static Item nether_fish_flame;
    public static Item nether_fish_volcanic;
    public static Item nether_fish_hellfire;
    public static Item nether_fish_crimson;
    public static Item nether_fish_warped;
    public static Item nether_fish_ghast;
    public static Item nether_fish_wither;
    public static Item nether_fish_obsidian;
    public static Item nether_fish_netherite;
    public static Item nether_fish_basalt;
    public static Item nether_fish_blackstone;
    public static Item nether_fish_piglin;
    public static Item nether_fish_hoglin;
    public static Item nether_fish_strider;
    public static Item nether_fish_void;

    // Предметы (6 штук - добавишь свои названия)
    public static Item item_pearl;
    public static Item item_shell;
    public static Item item_coral;
    public static Item item_starfish;
    public static Item item_seaweed;
    public static Item item_amber;

    public static void init() {
        // Регистрация обычных рыб
        fish_salmon = registerFish("fish_salmon", false);
        fish_cod = registerFish("fish_cod", false);
        fish_trout = registerFish("fish_trout", false);
        fish_bass = registerFish("fish_bass", false);
        fish_perch = registerFish("fish_perch", false);
        fish_pike = registerFish("fish_pike", false);
        fish_carp = registerFish("fish_carp", false);
        fish_tuna = registerFish("fish_tuna", false);
        fish_mackerel = registerFish("fish_mackerel", false);
        fish_herring = registerFish("fish_herring", false);
        fish_sardine = registerFish("fish_sardine", false);
        fish_anchovy = registerFish("fish_anchovy", false);
        fish_catfish = registerFish("fish_catfish", false);
        fish_sturgeon = registerFish("fish_sturgeon", false);
        fish_tilapia = registerFish("fish_tilapia", false);
        fish_flounder = registerFish("fish_flounder", false);
        fish_halibut = registerFish("fish_halibut", false);
        fish_snapper = registerFish("fish_snapper", false);
        fish_grouper = registerFish("fish_grouper", false);
        fish_barracuda = registerFish("fish_barracuda", false);
        fish_swordfish = registerFish("fish_swordfish", false);
        fish_marlin = registerFish("fish_marlin", false);
        fish_shark = registerFish("fish_shark", false);
        fish_eel = registerFish("fish_eel", false);
        fish_octopus = registerFish("fish_octopus", false);
        fish_squid = registerFish("fish_squid", false);
        fish_crab = registerFish("fish_crab", false);
        fish_lobster = registerFish("fish_lobster", false);

        // Регистрация адских рыб
        nether_fish_magma = registerFish("nether_fish_magma", true);
        nether_fish_soul = registerFish("nether_fish_soul", true);
        nether_fish_infernal = registerFish("nether_fish_infernal", true);
        nether_fish_blaze = registerFish("nether_fish_blaze", true);
        nether_fish_ember = registerFish("nether_fish_ember", true);
        nether_fish_flame = registerFish("nether_fish_flame", true);
        nether_fish_volcanic = registerFish("nether_fish_volcanic", true);
        nether_fish_hellfire = registerFish("nether_fish_hellfire", true);
        nether_fish_crimson = registerFish("nether_fish_crimson", true);
        nether_fish_warped = registerFish("nether_fish_warped", true);
        nether_fish_ghast = registerFish("nether_fish_ghast", true);
        nether_fish_wither = registerFish("nether_fish_wither", true);
        nether_fish_obsidian = registerFish("nether_fish_obsidian", true);
        nether_fish_netherite = registerFish("nether_fish_netherite", true);
        nether_fish_basalt = registerFish("nether_fish_basalt", true);
        nether_fish_blackstone = registerFish("nether_fish_blackstone", true);
        nether_fish_piglin = registerFish("nether_fish_piglin", true);
        nether_fish_hoglin = registerFish("nether_fish_hoglin", true);
        nether_fish_strider = registerFish("nether_fish_strider", true);
        nether_fish_void = registerFish("nether_fish_void", true);

        // Регистрация предметов
        item_pearl = registerItem("item_pearl");
        item_shell = registerItem("item_shell");
        item_coral = registerItem("item_coral");
        item_starfish = registerItem("item_starfish");
        item_seaweed = registerItem("item_seaweed");
        item_amber = registerItem("item_amber");
    }

    private static Item registerFish(String name, boolean isNether) {
        ItemModFish fish = new ItemModFish(name, isNether);
        GameRegistry.registerItem(fish, name);

        if (isNether) {
            NETHER_FISH.add(fish);
        } else {
            OVERWORLD_FISH.add(fish);
        }

        return fish;
    }

    private static Item registerItem(String name) {
        ItemModItem item = new ItemModItem(name);
        GameRegistry.registerItem(item, name);
        MISC_ITEMS.add(item);
        return item;
    }

    // Геттеры для лут-системы
    public static Item[] getAllOverworldFish() {
        return OVERWORLD_FISH.toArray(new Item[0]);
    }

    public static Item[] getAllNetherFish() {
        return NETHER_FISH.toArray(new Item[0]);
    }

    public static Item[] getAllItems() {
        return MISC_ITEMS.toArray(new Item[0]);
    }
}
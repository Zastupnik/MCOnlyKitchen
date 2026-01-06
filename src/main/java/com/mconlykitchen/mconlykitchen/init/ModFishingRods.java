package com.mconlykitchen.mconlykitchen.init;

import com.mconlykitchen.mconlykitchen.item.ItemCustomFishingRod;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModFishingRods {

    // Обычные удочки
    public static ItemCustomFishingRod basicRod;
    public static ItemCustomFishingRod improvedRod;
    public static ItemCustomFishingRod advancedRod;

    // Адские удочки
    public static ItemCustomFishingRod netherBasicRod;
    public static ItemCustomFishingRod netherImprovedRod;
    public static ItemCustomFishingRod netherAdvancedRod;

    // Универсальная
    public static ItemCustomFishingRod universalRod;

    public static void init() {
        // Обычные удочки (tier 0–2)
        basicRod     = registerRod("basic_fishing_rod", 0, false);     // 60 сек
        improvedRod  = registerRod("improved_fishing_rod", 1, false);  // 30 сек
        advancedRod  = registerRod("advanced_fishing_rod", 2, false);  // 15 сек

        // Адские удочки (tier 3–5)
        netherBasicRod     = registerRod("nether_basic_fishing_rod", 3, true);   // 60 сек
        netherImprovedRod  = registerRod("nether_improved_fishing_rod", 4, true); // 30 сек
        netherAdvancedRod  = registerRod("nether_advanced_fishing_rod", 5, true); // 15 сек

        // Универсальная (tier 6)
        universalRod = registerRod("universal_fishing_rod", 6, true); // 5 сек, работает везде
    }

    private static ItemCustomFishingRod registerRod(String name, int tier, boolean canUseInNether) {
        ItemCustomFishingRod rod = new ItemCustomFishingRod(name, tier, canUseInNether);
        GameRegistry.registerItem(rod, name);
        return rod;
    }
}

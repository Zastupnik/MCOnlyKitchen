package com.mconlykitchen.mconlykitchen.init;

import com.mconlykitchen.mconlykitchen.MCOnlyKitchen;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModSounds {

    // Имена звуков
    public static final String REEL_FAST = MCOnlyKitchen.MODID + ":reel_fast";
    public static final String REEL_SLOW = MCOnlyKitchen.MODID + ":reel_slow";
    public static final String REEL_CREAK = MCOnlyKitchen.MODID + ":reel_creak";
    public static final String COMPLETE = MCOnlyKitchen.MODID + ":complete";
    public static final String FISH_ESCAPE = MCOnlyKitchen.MODID + ":fish_escape";
    public static final String CHEST_GET = MCOnlyKitchen.MODID + ":chest_get";
    public static final String OPEN_CHEST = MCOnlyKitchen.MODID + ":open_chest";
    public static final String OPEN_CHEST_GOLDEN = MCOnlyKitchen.MODID + ":open_chest_golden";

    public static void init() {
        // В 1.7.10 звуки регистрируются через sounds.json
        // Этот метод оставляем для будущих звуковых эффектов
        MCOnlyKitchen.LOGGER.info("Sound system initialized");
    }
}
package com.mconlykitchen.fishingmod.sound;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ModSounds {

    // имена звуков (должны совпадать с sounds.json)
    public static final String REEL_FAST = "fishingmod:reel_fast";
    public static final String REEL_SLOW = "fishingmod:reel_slow";
    public static final String REEL_CREAK = "fishingmod:reel_creak";
    public static final String HIT = "fishingmod:hit";
    public static final String PERFECT = "fishingmod:perfect";
    public static final String ESCAPE = "fishingmod:fish_escape";
    public static final String OPEN_CHEST = "fishingmod:open_chest";
    public static final String OPEN_CHEST_GOLDEN = "fishingmod:open_chest_golden";

    /** Вызов звука в мире */
    public static void play(World world, EntityPlayer player, String sound) {
        world.playSound(
                player.posX,
                player.posY,
                player.posZ,
                sound,
                1.0F, // громкость
                1.0F, // тон
                false
        );
    }

    /** Регистрация звуков (в 1.7.10 фактически через sounds.json) */
    public static void registerSounds() {
        // В 1.7.10 звуки регистрируются через ресурсный файл sounds.json,
        // поэтому здесь можно оставить пусто или использовать для отладки.
    }
}

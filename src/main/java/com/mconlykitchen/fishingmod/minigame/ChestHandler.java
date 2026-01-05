package com.mconlykitchen.fishingmod.minigame;

import com.mconlykitchen.fishingmod.config.ConfigHandler;
import com.mconlykitchen.fishingmod.minigame.LootHandler;
import net.minecraft.entity.player.EntityPlayer;

public class ChestHandler {

    private final boolean lava;       // режим (обычный/адский)
    private float appearTicks;        // анимация появления
    private float progress;           // прогресс удержания
    private boolean opened;           // сундук открыт

    public ChestHandler(boolean lava) {
        this.lava = lava;
        this.appearTicks = 0;
        this.progress = 0;
        this.opened = false;
    }

    /** Обновление состояния сундука */
    public void update(boolean overlap) {
        // анимация появления
        if (appearTicks < 20) {
            appearTicks++;
        }

        // если индикатор игрока пересекается с сундуком — растёт прогресс
        if (overlap) {
            progress += 0.02F;
        } else {
            progress -= 0.01F;
        }

        if (progress < 0) progress = 0;
        if (progress > 1.0F) {
            opened = true;
        }
    }

    /** Выдать лут игроку */
    public void giveLoot(EntityPlayer player) {
        if (!opened) return;

        LootHandler.giveChestLoot(player, lava);
        player.addChatMessage(new net.minecraft.util.ChatComponentText(
                lava ? "Ты открыл адский сундук!" : "Ты открыл сундук!"
        ));
    }

    // геттеры для GUI
    public float getAppearScale() {
        return Math.min(1.0F, appearTicks / 20.0F);
    }

    public float getProgress() {
        return progress;
    }

    public boolean isOpened() {
        return opened;
    }
}
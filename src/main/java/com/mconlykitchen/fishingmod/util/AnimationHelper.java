package com.mconlykitchen.fishingmod.util;

import net.minecraft.client.gui.Gui;

public class AnimationHelper {

    /** Анимация появления (scale от 0 до 1) */
    public static float getAppearScale(int ticks, int maxTicks) {
        return Math.min(1.0F, (float) ticks / (float) maxTicks);
    }

    /** Тряска (смещение X/Y) */
    public static int getShakeOffset(int ticks, int amplitude) {
        return (ticks % 4 < 2 ? amplitude : -amplitude);
    }

    /** Плавное появление текста (альфа от 0 до 255) */
    public static int getFadeAlpha(int ticks, int maxTicks) {
        float ratio = Math.min(1.0F, (float) ticks / (float) maxTicks);
        return (int) (255 * ratio);
    }

    /** Прогресс‑бар (ширина от 0 до maxWidth) */
    public static int getProgressWidth(float progress, int maxWidth) {
        return (int) (progress * maxWidth);
    }

    /** Отрисовка простого прогресс‑бара */
    public static void drawProgressBar(Gui gui, int x, int y, int width, int height, float progress, int color) {
        int progWidth = getProgressWidth(progress, width);
        gui.drawRect(x, y, x + progWidth, y + height, color);
    }
}

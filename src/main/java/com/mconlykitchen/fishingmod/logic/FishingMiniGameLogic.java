package com.mconlykitchen.fishingmod.logic;

import java.util.Random;

public class FishingMiniGameLogic {

    // Размеры элементов
    private static final int DEFAULT_BAR_SIZE = 32;
    private static final int FISH_HEIGHT = 15;

    private final int top;
    private final int bottom;

    private int barSize;
    private float barY;
    private float barVelocity = 0;

    private float fishY;
    private float fishVelocity = 0;
    private float fishTarget;
    private int fishTimer = 0;

    private float progress = 0;
    private int failBuffer = 0;

    private final Random rand = new Random();

    public FishingMiniGameLogic(int top, int bottom) {
        this.top = top;
        this.bottom = bottom;
        this.barSize = DEFAULT_BAR_SIZE;

        barY = (top + bottom - barSize) / 2f;
        fishY = (top + bottom - FISH_HEIGHT) / 2f;
        fishTarget = fishY;
    }

    /* ---------- INPUT ---------- */

    public void press() {
        barVelocity = -1.5f; // прыжок вверх (мгновенная установка скорости)
    }

    /* ---------- UPDATE ---------- */

    public void tick(boolean lava) {
        updateBar();
        updateFish(lava);
        updateProgress();
    }

    private void updateBar() {
        // Гравитация и трение
        barVelocity += 0.5f;   // гравитация
        barVelocity *= 0.92f;  // трение
        barY += barVelocity;

        clampBar();
    }

    private void updateFish(boolean lava) {
        // Смена цели раз в N тиков
        if (fishTimer-- <= 0) {
            int range = bottom - top - FISH_HEIGHT;
            fishTarget = top + rand.nextInt(Math.max(1, range));
            fishTimer = lava ? 15 + rand.nextInt(15) : 25 + rand.nextInt(25);
        }

        float maxSpeed = lava ? 2.0f : 1.5f;
        float acceleration = lava ? 0.08f : 0.06f;
        float delta = fishTarget - fishY;

        // Плавное движение к цели
        fishVelocity += delta * acceleration;
        fishVelocity = clamp(fishVelocity, -maxSpeed, maxSpeed);
        fishVelocity *= 0.95f; // замедление

        fishY += fishVelocity;
        clampFish();
    }

    private void updateProgress() {
        boolean overlap = isOverlap();

        if (overlap) {
            progress += 2.5f; // рост при совпадении
            if (failBuffer > 0) failBuffer--;
        } else {
            progress -= 0.8f; // падение при промахе
            failBuffer++;
        }

        progress = clamp(progress, 0, 100);
    }

    /* ---------- LIMITS ---------- */

    private void clampBar() {
        if (barY < top) {
            barY = top;
            barVelocity = 0;
        }
        if (barY + barSize > bottom) {
            barY = bottom - barSize;
            barVelocity = 0;
        }
    }

    private void clampFish() {
        if (fishY < top) fishY = top;
        if (fishY + FISH_HEIGHT > bottom) fishY = bottom - FISH_HEIGHT;
    }

    private float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }

    /* ---------- GETTERS ---------- */

    public int getBarY() {
        return (int) barY;
    }

    public int getBarSize() {
        return barSize;
    }

    public int getFishY() {
        return (int) fishY;
    }

    public int getProgress() {
        return (int) progress;
    }

    public boolean isOverlap() {
        return !(barY + barSize < fishY || barY > fishY + FISH_HEIGHT);
    }

    public boolean isFailed() {
        return failBuffer > 180;
    }

    public boolean isSuccess() {
        return progress >= 100;
    }
}
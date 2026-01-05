package com.mconlykitchen.mconlykitchen.logic;

import com.mconlykitchen.mconlykitchen.config.ModConfig;
import java.util.Random;

public class FishingMiniGameLogic {

    private static final int DEFAULT_BAR_SIZE = 32;
    private static final int FISH_HEIGHT = 15;
    private static final int CHEST_HEIGHT = 13;

    private final int top;
    private final int bottom;
    private final float difficultyMultiplier;
    private final boolean isLava;
    private final int rodTier;
    private final int fishTier; // новое: тир пойманной рыбы

    private int barSize;
    private float barY;
    private float barVelocity = 0;

    private float fishY;
    private float fishVelocity = 0;
    private float fishTarget;
    private int fishTimer = 0;

    // Сундук
    private boolean chestVisible = false;
    private float chestY;
    private float chestProgress = 0;
    private boolean isGoldenChest = false;

    private float progress = 0;
    private int failBuffer = 0;

    private final Random rand = new Random();

    public FishingMiniGameLogic(int top, int bottom, int rodTier, boolean isLava, int fishTier) {
        this.top = top;
        this.bottom = bottom;
        this.rodTier = rodTier;
        this.isLava = isLava;
        this.fishTier = fishTier;
        this.difficultyMultiplier = ModConfig.getDifficulty(rodTier, isLava) * getFishDifficultyMultiplier(fishTier);

        // Размер бобера зависит от сложности
        this.barSize = (int)(DEFAULT_BAR_SIZE / Math.max(0.3f, difficultyMultiplier));
        this.barSize = Math.max(20, Math.min(60, barSize));

        barY = (top + bottom - barSize) / 2f;
        fishY = (top + bottom - FISH_HEIGHT) / 2f;
        fishTarget = fishY;

        // Проверяем шанс на появление сундука
        int chestChance = ModConfig.getChestChance(rodTier, isLava);
        if (rand.nextInt(100) < chestChance) {
            chestVisible = true;
            chestY = top + rand.nextInt(bottom - top - CHEST_HEIGHT);

            int goldenChance = ModConfig.getGoldenChestChance(rodTier, isLava);
            isGoldenChest = rand.nextInt(100) < goldenChance;
        }
    }

    /**
     * Множитель сложности в зависимости от тира рыбы
     */
    private float getFishDifficultyMultiplier(int tier) {
        switch (tier) {
            case 1: return 0.6f;  // Очень легко
            case 2: return 0.8f;  // Легко
            case 3: return 1.0f;  // Нормально
            case 4: return 1.3f;  // Сложно
            case 5: return 1.7f;  // Очень сложно (легендарные)
            default: return 1.0f;
        }
    }

    public void press() {
        barVelocity = -1.8f; // увеличил силу прыжка
    }

    public void tick(boolean lava) {
        updateBar();
        updateFish();
        updateChest();
        updateProgress();
    }

    private void updateBar() {
        barVelocity += 0.45f; // уменьшил гравитацию
        barVelocity *= 0.93f; // увеличил трение
        barY += barVelocity;
        clampBar();
    }

    private void updateFish() {
        if (fishTimer-- <= 0) {
            int range = bottom - top - FISH_HEIGHT;
            fishTarget = top + rand.nextInt(Math.max(1, range));

            // Время смены направления зависит от сложности и тира
            int baseTime = isLava ? 20 : 30;
            int variance = isLava ? 20 : 30;
            fishTimer = (int)((baseTime + rand.nextInt(variance)) * difficultyMultiplier);
        }

        // Скорость рыбы зависит от тира
        float maxSpeed = (isLava ? 1.8f : 1.3f) * difficultyMultiplier;
        float acceleration = (isLava ? 0.06f : 0.05f) * difficultyMultiplier;
        float delta = fishTarget - fishY;

        fishVelocity += delta * acceleration;
        fishVelocity = clamp(fishVelocity, -maxSpeed, maxSpeed);
        fishVelocity *= 0.96f; // больше трения для рыбы

        fishY += fishVelocity;
        clampFish();
    }

    private void updateChest() {
        if (!chestVisible) return;

        boolean onChest = isBobberOnChest();

        if (onChest) {
            chestProgress += 2.0f; // быстрее наполнение
            if (chestProgress >= 100.0f) {
                chestProgress = 100.0f;
            }
        } else {
            chestProgress -= 0.3f; // медленнее убывание
            if (chestProgress < 0) {
                chestProgress = 0;
            }
        }
    }

    private void updateProgress() {
        boolean overlap = isOverlap();

        if (overlap) {
            // Прогресс растёт быстрее с хорошей удочкой
            float progressGain = 3.5f / Math.max(0.4f, difficultyMultiplier);
            progress += progressGain;
            if (failBuffer > 0) failBuffer--;
        } else {
            // Прогресс падает медленнее
            float progressLoss = 0.6f * Math.max(0.5f, difficultyMultiplier);
            progress -= progressLoss;
            failBuffer++;
        }

        progress = clamp(progress, 0, 100);
    }

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

    public boolean isChestVisible() {
        return chestVisible;
    }

    public int getChestY() {
        return (int) chestY;
    }

    public float getChestProgress() {
        return chestProgress / 100.0f;
    }

    public boolean isGoldenChest() {
        return isGoldenChest;
    }

    public boolean hasChestReward() {
        return chestVisible && chestProgress >= 100.0f;
    }

    public boolean isOverlap() {
        return !(barY + barSize < fishY || barY > fishY + FISH_HEIGHT);
    }

    public boolean isBobberOnChest() {
        if (!chestVisible) return false;
        return !(barY + barSize < chestY || barY > chestY + CHEST_HEIGHT);
    }

    public boolean isBobberOnFish() {
        return isOverlap();
    }

    public boolean isFailed() {
        return failBuffer > ModConfig.minigameFailTime;
    }

    public boolean isSuccess() {
        return progress >= 100;
    }

    public int getFishTier() {
        return fishTier;
    }
}
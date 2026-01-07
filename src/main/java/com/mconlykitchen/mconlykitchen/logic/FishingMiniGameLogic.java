package com.mconlykitchen.mconlykitchen.logic;

import com.mconlykitchen.mconlykitchen.config.ModConfig;
import java.util.Random;

public class FishingMiniGameLogic {

    private static final int DEFAULT_BAR_SIZE = 32;
    private static final int FISH_HEIGHT = 15;
    private static final int CHEST_HEIGHT = 13;

    private final int top;
    private final int bottom;

    private final boolean isLava;
    private final boolean isNether;
    private final int rodTier;   // 0..6
    private final int fishTier;  // 1..3

    private final float difficultyMultiplier;

    private int barSize;
    private float barY;
    private float barVelocity;

    private float fishY;
    private float fishVelocity;
    private float fishTargetY;
    private int fishTimer;

    private boolean chestVisible;
    private boolean chestCollected;
    private float chestY;
    private float chestProgress;
    private boolean isGoldenChest;

    private float progress;
    private int failBuffer;
    private boolean finishedByChest = false;

    private static final float CENTER_BONUS_RADIUS = 4f;

    private final Random rand = new Random();

    public FishingMiniGameLogic(int top, int bottom, int rodTier, boolean isLava, int fishTier) {
        this.top = top;
        this.bottom = bottom;
        this.rodTier = (rodTier < 0 ? 0 : Math.min(rodTier, 6));
        this.isLava = isLava;
        this.isNether = (net.minecraft.client.Minecraft.getMinecraft().theWorld != null
                && net.minecraft.client.Minecraft.getMinecraft().theWorld.provider.isHellWorld);
        this.fishTier = clampInt(fishTier, 1, 3);

        float fishMul = getFishDifficultyMultiplier(this.fishTier);
        float envMul  = isLava ? 1.20f : 1.00f;
        float rodMul  = getRodEaseMultiplier(this.rodTier);
        this.difficultyMultiplier = fishMul * envMul * rodMul;

        this.barSize = clampInt((int) (DEFAULT_BAR_SIZE / Math.max(0.40f, difficultyMultiplier)),
                20, Math.max(22, bottom - top - 4));

        this.barY = midY() - barSize / 2.0f;
        this.barVelocity = 0f;

        this.fishY = midY() - FISH_HEIGHT / 2.0f;
        this.fishVelocity = 0f;
        this.fishTargetY = fishY;
        this.fishTimer = 0;

        initChest();

        this.progress = 0f;
        this.failBuffer = 0;
    }

    private float getRodEaseMultiplier(int tier) {
        switch (tier) {
            case 6: return 0.70f; // универсальная
            case 2: case 5: return 0.85f; // продвинутая
            case 1: case 4: return 0.92f; // улучшенная
            case 0: case 3: default: return 1.00f; // базовая
        }
    }

    private float getFishDifficultyMultiplier(int tier) {
        switch (tier) {
            case 1: return 0.80f;
            case 2: return 1.00f;
            case 3: return 1.30f;
            default: return 1.00f;
        }
    }

    private void initChest() {
        // Берём шанс сундука для мини‑игры
        int chestChance = clampInt(ModConfig.getMiniChestChanceByTier(rodTier), 0, 100);
        this.chestVisible = (chestChance >= 100) || (rand.nextInt(100) < chestChance);
        this.chestCollected = false;

        if (chestVisible) {
            int range = Math.max(1, bottom - top - CHEST_HEIGHT);
            this.chestY = top + rand.nextInt(range);

            // Берём шанс золотого сундука для мини‑игры
            int goldenChance = clampInt(ModConfig.getMiniGoldenChestChanceByTier(rodTier), 0, 100);
            this.isGoldenChest = (goldenChance >= 100) || (rand.nextInt(100) < goldenChance);
        } else {
            this.chestY = midY() - CHEST_HEIGHT / 2.0f;
            this.isGoldenChest = false;
        }

        this.chestProgress = 0f;
    }


    public void press() {
        float jump;
        switch (rodTier) {
            case 6: jump = 1.6f + 0.15f * 3; break;
            case 2: case 5: jump = 1.6f + 0.15f * 2; break;
            case 1: case 4: jump = 1.6f + 0.15f * 1; break;
            case 0: case 3: default: jump = 1.6f; break;
        }
        barVelocity -= jump;
    }

    public void tick() {
        updateBar();
        updateFish();
        updateChest();
        updateProgress();
        // ВНИМАНИЕ: здесь нет endGame(), нет задержек и нет отправки пакетов
    }

    private void updateBar() {
        float gravity = isLava ? 0.42f : 0.48f;
        float friction = 0.93f;
        float maxSpeed = 2.6f;

        barVelocity += gravity;
        barVelocity *= friction;
        barVelocity = clamp(barVelocity, -maxSpeed, maxSpeed);

        barY += barVelocity;
        clampBar();
    }

    private void updateFish() {
        if (fishTimer <= 0) {
            int range = Math.max(1, bottom - top - FISH_HEIGHT);
            fishTargetY = top + rand.nextInt(range);

            int base = isLava ? 16 : 24;
            int var  = isLava ? 16 : 24;

            float timeMul = Math.max(0.60f, 1.25f / difficultyMultiplier);
            fishTimer = (int) ((base + rand.nextInt(var)) * timeMul);
        } else {
            fishTimer--;
        }

        float delta = fishTargetY - fishY;
        float accel = (isLava ? 0.060f : 0.052f) * difficultyMultiplier;
        float maxSpeed = (isLava ? 1.85f : 1.45f) * difficultyMultiplier;

        fishVelocity += delta * accel;
        fishVelocity = clamp(fishVelocity, -maxSpeed, maxSpeed);
        fishVelocity *= 0.965f;

        fishY += fishVelocity;
        clampFish();
    }

    private int chestSpawnGraceTicks = 8;

    private void updateChest() {
        if (!chestVisible || chestCollected) return;
        if (chestSpawnGraceTicks > 0) {
            chestSpawnGraceTicks--;
            return;
        }

        if (isBobberOnChest()) {
            float speed;
            switch (rodTier) {
                case 6:  speed = 5.0f; break;
                case 2:
                case 5:  speed = 3.5f; break;
                case 1:
                case 4:  speed = 2.8f; break;
                case 0:
                case 3:
                default: speed = 2.2f; break;
            }

            chestProgress += speed;

            if (chestProgress >= 100f) {
                chestProgress = 100f;
                chestCollected = true;
                chestVisible = false;
                finishedByChest = true;
            }
        }
    }


    private void updateProgress() {

        if (finishedByChest) return;

        boolean overlap = isOverlap();

        if (overlap) {
            float fishCenter = fishY + FISH_HEIGHT / 2f;
            float barCenter  = barY + barSize / 2f;
            float dist       = Math.abs(barCenter - fishCenter);

            float tightness = 1f - clamp(dist / CENTER_BONUS_RADIUS, 0f, 1f);

            float baseGain = 1.0f / Math.max(0.6f, difficultyMultiplier);
            float rodBonus =
                    (rodTier == 6 ? 0.30f :
                            (rodTier == 2 || rodTier == 5) ? 0.20f :
                                    (rodTier == 1 || rodTier == 4) ? 0.10f :
                                            (rodTier == 0 || rodTier == 3) ? 0.05f : 0.00f);

            progress += baseGain * (1.0f + 0.3f * tightness) + rodBonus;

            if (failBuffer > 0) failBuffer--;

        } else {
            float baseLoss = 0.9f * Math.max(0.6f, difficultyMultiplier);
            float rodEaseLoss =
                    (rodTier == 6 ? 0.75f :
                            (rodTier == 2 || rodTier == 5) ? 0.85f :
                                    1.00f);

            progress -= baseLoss * rodEaseLoss;
            failBuffer++;
        }

        progress = clamp(progress, 0f, 100f);
    }


    private void clampBar() {
        if (barY < top) {
            barY = top;
            if (barVelocity < 0) barVelocity = 0;
        }
        if (barY + barSize > bottom) {
            barY = bottom - barSize;
            if (barVelocity > 0) barVelocity = 0;
        }
    }

    private void clampFish() {
        if (fishY < top) fishY = top;
        if (fishY > bottom - FISH_HEIGHT) fishY = bottom - FISH_HEIGHT;
    }

    private float midY() {
        return (top + bottom) / 2.0f;
    }

    // Утилиты
    private float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
    private int clampInt(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    // Геттеры/проверки
    public int getBarY() { return (int) barY; }
    public int getBarSize() { return barSize; }
    public int getFishY() { return (int) fishY; }
    public int getProgress() { return (int) progress; }

    public boolean isChestVisible() { return chestVisible; }
    public int getChestY() { return (int) chestY; }
    public float getChestProgress() { return chestProgress / 100.0f; }
    public boolean isGoldenChest() { return isGoldenChest; }

    public boolean hasChestReward() { return chestCollected; }

    public boolean isOverlap() {
        return !(barY + barSize <= fishY || barY >= fishY + FISH_HEIGHT);
    }

    public boolean isBobberOnChest() {
        if (!chestVisible) return false;
        return !(barY + barSize <= chestY || barY >= chestY + CHEST_HEIGHT);
    }

    public boolean isBobberOnFish() { return isOverlap(); }

    public boolean isFailed() {
        int baseFail = Math.max(10, ModConfig.minigameFailTime);
        int rodBufferBonus =
                (rodTier == 6 ? 8 :
                        (rodTier == 2 || rodTier == 5) ? 4 : 0);
        return failBuffer > (baseFail + rodBufferBonus);
    }

    public boolean isSuccess() { return progress >= 100f; }

    public int getFishTier() { return fishTier; }

    public boolean isFinishedByChest() {
        return finishedByChest;
    }
}

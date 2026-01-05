package com.mconlykitchen.fishingmod.minigame;

import com.mconlykitchen.fishingmod.config.ConfigHandler;
import com.mconlykitchen.fishingmod.minigame.ChestHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.Random;

public class FishingMinigame {

    private final EntityPlayer player;
    private final World world;
    private final boolean lava; // режим (обычный/адский)
    private final Random rand;

    // позиции
    private int fishY;
    private int barY;

    // прогресс
    private int progress;
    private int failBuffer;
    private boolean finished;

    // сундук
    private ChestHandler chest;

    public FishingMinigame(EntityPlayer player, World world, boolean lava) {
        this.player = player;
        this.world = world;
        this.lava = lava;
        this.rand = new Random();

        this.fishY = 80;
        this.barY = 90;
        this.progress = 0;
        this.failBuffer = 0;
        this.finished = false;

        // шанс появления сундука
        float chance = lava ? ConfigHandler.netherChestLootChance : ConfigHandler.chestLootChance;
        if (rand.nextFloat() < chance) {
            this.chest = new ChestHandler(lava);
        }
    }

    /** Обновление состояния мини-игры */
    public void update() {
        if (finished) return;

        // движение индикатора игрока (падает вниз)
        barY += 1;
        if (barY + 30 > 188) barY = 188 - 30;

        // движение рыбы
        int drift = rand.nextBoolean() ? 1 : -1;
        float difficulty = lava ? ConfigHandler.netherFishingDifficulty : ConfigHandler.fishingDifficulty;
        fishY += drift * difficulty;
        if (fishY < 28) fishY = 28;
        if (fishY + 15 > 188) fishY = 188 - 15;

        // проверка пересечения индикатора и рыбы
        boolean overlap = !(barY + 30 < fishY || barY > fishY + 15);

        if (overlap) {
            progress += 2;
            if (failBuffer > 0) failBuffer--;
        } else {
            progress -= 1;
            failBuffer++;
        }

        if (progress < 0) progress = 0;
        if (progress > 100) {
            onSuccess();
        }

        if (failBuffer > 160) {
            onFail();
        }

        // сундук
        if (chest != null) {
            chest.update(overlap);
            if (chest.isOpened()) {
                chest.giveLoot(player);
            }
        }
    }

    private void onSuccess() {
        finished = true;
        player.addChatMessage(new net.minecraft.util.ChatComponentText("Ты поймал рыбу!"));
        // TODO: вызвать LootHandler для выдачи рыбы
    }

    private void onFail() {
        finished = true;
        player.addChatMessage(new net.minecraft.util.ChatComponentText("Рыба сорвалась."));
    }

    // геттеры для GUI
    public int getFishY() { return fishY; }
    public int getBarY() { return barY; }
    public int getProgress() { return progress; }
    public ChestHandler getChest() { return chest; }
    public boolean isFinished() { return finished; }
}

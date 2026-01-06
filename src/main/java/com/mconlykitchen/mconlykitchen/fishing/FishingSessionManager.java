package com.mconlykitchen.mconlykitchen.fishing;

import com.mconlykitchen.mconlykitchen.config.ModConfig;
import com.mconlykitchen.mconlykitchen.util.FishingLootHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class FishingSessionManager {

    // Одна активная сессия на игрока (по UUID)
    private static final Map<UUID, FishingSession> sessions = new HashMap<UUID, FishingSession>();

    // Таймаут «висящей» сессии, если клиент не завершил мини‑игру
    private static final long SESSION_TIMEOUT = 60000L; // 60 сек

    // Удаление просроченных сессий (зови периодически с сервера)
    public static void removeExpiredSessions() {
        Iterator<Map.Entry<UUID, FishingSession>> it = sessions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, FishingSession> e = it.next();
            if (e.getValue().isExpired()) {
                it.remove();
            }
        }
    }

    public static void startSession(net.minecraft.entity.player.EntityPlayerMP player, int rodTier, boolean isLava, int fishTier) {
        java.util.UUID id = player.getUniqueID();
        FishingSession prev = sessions.remove(id);
        if (prev != null) {
            // закрываем старую сессию без награды
        }
        sessions.put(id, new FishingSession(player, rodTier, isLava, fishTier));
    }

    public static boolean finishSession(net.minecraft.entity.player.EntityPlayerMP player, boolean success, boolean gotChest, boolean isGoldenChest) {
        java.util.UUID id = player.getUniqueID();
        FishingSession session = sessions.remove(id);
        if (session == null || session.isExpired() || session.getPlayer() != player) {
            return false;
        }

        long nowTicks = player.worldObj.getTotalWorldTime();
        player.getEntityData().setLong("FishingPostLockUntil", nowTicks + 40L);

        if (!success) return true;

        player.motionX = player.motionY = player.motionZ = 0.0D;
        player.velocityChanged = true;

        net.minecraft.item.ItemStack fish = com.mconlykitchen.mconlykitchen.util.FishingLootHelper.getFishLoot(session.isLava(), session.getFishTier());
        if (fish != null && fish.getItem() != null) {
            giveRewardToPlayer(player, fish);
        }

        if (gotChest) {
            net.minecraft.item.ItemStack chestLoot = isGoldenChest
                    ? com.mconlykitchen.mconlykitchen.config.ModConfig.getGoldenChestLoot()
                    : com.mconlykitchen.mconlykitchen.config.ModConfig.getNormalChestLoot();
            if (chestLoot != null && chestLoot.getItem() != null) {
                giveRewardToPlayer(player, chestLoot);
                player.getEntityData().setTag("LastChestLoot", chestLoot.writeToNBT(new net.minecraft.nbt.NBTTagCompound()));
            }
        }
        return true;
    }



    // Есть ли активная сессия у игрока
    public static boolean hasSession(EntityPlayerMP player) {
        return sessions.containsKey(player.getUniqueID());
    }

    // Сбросить сессию вручную (если требуется)
    public static void clearSession(EntityPlayerMP player) {
        sessions.remove(player.getUniqueID());
    }

    // Выдача награды игроку (инвентарь или дроп)
    private static void giveRewardToPlayer(EntityPlayerMP player, ItemStack loot) {
        ItemStack stack = loot.copy();
        if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropPlayerItemWithRandomChoice(stack, false);
            player.addChatMessage(new ChatComponentText(
                    EnumChatFormatting.YELLOW + "Инвентарь полон! Предмет выпал на землю."
            ));
        }
        player.inventoryContainer.detectAndSendChanges();

        // Износ текущей удочки
        ItemStack heldItem = player.getHeldItem();
        if (heldItem != null && heldItem.getItem() != null) {
            heldItem.damageItem(1, player);
        }
    }

    // Данные одной сессии
    private static class FishingSession {
        private final EntityPlayerMP player;
        private final int rodTier;
        private final boolean isLava;
        private final int fishTier;
        private final long startMillis;

        public FishingSession(EntityPlayerMP player, int rodTier, boolean isLava, int fishTier) {
            this.player = player;
            this.rodTier = rodTier;
            this.isLava = isLava;
            this.fishTier = fishTier;
            this.startMillis = System.currentTimeMillis();
        }

        public EntityPlayerMP getPlayer() { return player; }
        public int getRodTier() { return rodTier; }
        public boolean isLava() { return isLava; }
        public int getFishTier() { return fishTier; }

        public boolean isExpired() {
            return System.currentTimeMillis() - startMillis > SESSION_TIMEOUT;
        }
    }
}

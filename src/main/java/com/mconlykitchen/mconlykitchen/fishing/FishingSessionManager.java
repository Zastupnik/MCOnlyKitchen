package com.mconlykitchen.mconlykitchen.fishing;

import com.mconlykitchen.mconlykitchen.util.FishingLootHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Управляет сессиями рыбалки для защиты от читеров
 */
public class FishingSessionManager {

    private static final Map<UUID, FishingSession> sessions = new HashMap<UUID, FishingSession>();
    private static final long SESSION_TIMEOUT = 60000L; // 60 секунд (в миллисекундах)

    /**
     * Создать новую сессию рыбалки
     */
    public static void startSession(EntityPlayerMP player, int rodTier, boolean isLava, int fishTier) {
        UUID playerId = player.getUniqueID();

        // Удаляем старую сессию если есть
        if (sessions.containsKey(playerId)) {
            sessions.remove(playerId);
        }

        sessions.put(playerId, new FishingSession(player, rodTier, isLava, fishTier));
    }

    /**
     * Завершить сессию и выдать награду
     */
    public static boolean finishSession(EntityPlayerMP player, boolean success, boolean gotChest, boolean isGoldenChest) {
        UUID playerId = player.getUniqueID();
        FishingSession session = sessions.get(playerId);

        // Проверка: есть ли активная сессия?
        if (session == null) {
            // Читер пытается получить награду без сессии
            return false;
        }

        // Проверка: не истекла ли сессия?
        if (session.isExpired()) {
            sessions.remove(playerId);
            return false;
        }

        // Проверка: тот же игрок?
        if (!session.getPlayer().equals(player)) {
            sessions.remove(playerId);
            return false;
        }

        // Удаляем сессию
        sessions.remove(playerId);

        // Если неудача - ничего не даём
        if (!success) {
            return true;
        }

        // Выдаём награду
        ItemStack loot;
        if (gotChest) {
            loot = FishingLootHelper.getChestLoot(isGoldenChest);
        } else {
            // Используем fishTier из сессии (защита от читеров!)
            loot = FishingLootHelper.getFishLoot(session.isLava(), session.getFishTier());
        }

        if (loot == null || loot.getItem() == null) {
            return true;
        }

        // Добавляем в инвентарь
        if (!player.inventory.addItemStackToInventory(loot.copy())) {
            player.dropPlayerItemWithRandomChoice(loot, false);
            player.addChatMessage(new ChatComponentText(
                    EnumChatFormatting.YELLOW + "Инвентарь полон! Предмет выпал на землю."
            ));
        }

        // Синхронизация
        player.inventoryContainer.detectAndSendChanges();

        // Урон удочке
        ItemStack heldItem = player.getHeldItem();
        if (heldItem != null && heldItem.getItem() != null) {
            heldItem.damageItem(1, player);
        }

        return true;
    }

    /**
     * Проверить наличие активной сессии
     */
    public static boolean hasSession(EntityPlayerMP player) {
        return sessions.containsKey(player.getUniqueID());
    }

    /**
     * Очистить сессию игрока (при дисконекте)
     */
    public static void clearSession(EntityPlayerMP player) {
        sessions.remove(player.getUniqueID());
    }

    /**
     * Очистить все истёкшие сессии (вызывать периодически)
     */
    public static void cleanupExpiredSessions() {
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<UUID, FishingSession>> iterator = sessions.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, FishingSession> entry = iterator.next();
            if (entry.getValue().isExpired()) {
                iterator.remove();
            }
        }
    }

    /**
     * Класс сессии рыбалки
     */
    private static class FishingSession {
        private final EntityPlayerMP player;
        private final int rodTier;
        private final boolean isLava;
        private final int fishTier;
        private final long startTime;

        public FishingSession(EntityPlayerMP player, int rodTier, boolean isLava, int fishTier) {
            this.player = player;
            this.rodTier = rodTier;
            this.isLava = isLava;
            this.fishTier = fishTier;
            this.startTime = System.currentTimeMillis();
        }

        public EntityPlayerMP getPlayer() {
            return player;
        }

        public int getRodTier() {
            return rodTier;
        }

        public boolean isLava() {
            return isLava;
        }

        public int getFishTier() {
            return fishTier;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - startTime > SESSION_TIMEOUT;
        }
    }
}
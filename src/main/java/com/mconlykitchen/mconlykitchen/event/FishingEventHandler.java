package com.mconlykitchen.mconlykitchen.event;

import com.mconlykitchen.mconlykitchen.fishing.FishingSessionManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Обработчик событий для системы рыбалки
 */
public class FishingEventHandler {

    private int cleanupTimer = 0;
    private static final int CLEANUP_INTERVAL = 1200; // 60 секунд (20 тиков = 1 сек)

    /**
     * Очистка сессии при выходе игрока
     */
    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            FishingSessionManager.clearSession((EntityPlayerMP) event.player);
        }
    }

    /**
     * Периодическая очистка истёкших сессий
     */
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            cleanupTimer++;

            if (cleanupTimer >= CLEANUP_INTERVAL) {
                cleanupTimer = 0;
                // теперь чистим только истёкшие сессии
                FishingSessionManager.removeExpiredSessions();
            }
        }
    }
}

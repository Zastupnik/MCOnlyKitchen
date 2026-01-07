package com.mconlykitchen.mconlykitchen.fishing;

import com.mconlykitchen.mconlykitchen.entity.EntityCustomBobber;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FishingSessionManager {

    private static final Map<EntityPlayer, SessionData> activeSessions = new HashMap<>();

    private static class SessionData {
        EntityCustomBobber bobber;
        long startTime;

        SessionData(EntityCustomBobber bobber, long startTime) {
            this.bobber = bobber;
            this.startTime = startTime;
        }
    }

    public static boolean hasActiveSession(EntityPlayer player) {
        return activeSessions.containsKey(player);
    }

    public static EntityCustomBobber getActiveBobber(EntityPlayer player) {
        SessionData data = activeSessions.get(player);
        if (data == null) return null;
        if (data.bobber.isDeadCustom()) {
            endSession(player);
            return null;
        }
        return data.bobber;
    }

    public static boolean startSession(EntityPlayer player, EntityCustomBobber bobber) {
        if (hasActiveSession(player)) return false;
        activeSessions.put(player, new SessionData(bobber, player.worldObj.getTotalWorldTime()));
        return true;
    }

    public static void endSession(EntityPlayer player) {
        SessionData data = activeSessions.remove(player);
        if (data != null && !data.bobber.isDeadCustom()) {
            data.bobber.retrieveBobber(); // завершает поплавок
        }
    }

    // Таймаут сессий (если поплавок завис)
    public static void removeExpiredSessions() {
        Iterator<Map.Entry<EntityPlayer, SessionData>> it = activeSessions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<EntityPlayer, SessionData> entry = it.next();
            SessionData data = entry.getValue();
            if (data.bobber.isDeadCustom()) {
                it.remove();
            }
        }
    }

    // Генерация рыбы
    public static ItemStack generateFish(int fishTier) {
        // сюда ваша логика по fishTier
        return null;
    }

    // Генерация сундуков
    public static ItemStack[] generateChestContents(boolean golden) {
        // сюда ваша логика сундука
        return new ItemStack[0];
    }
}

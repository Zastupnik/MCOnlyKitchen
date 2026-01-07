package com.mconlykitchen.mconlykitchen.fishing;

import com.mconlykitchen.mconlykitchen.entity.EntityCustomBobber;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class FishingSessionManager {

    private static final Map<UUID, SessionData> activeSessions = new HashMap<>();

    private static class SessionData {
        final EntityCustomBobber bobber;
        final long startTime;

        SessionData(EntityCustomBobber bobber, long startTime) {
            this.bobber = bobber;
            this.startTime = startTime;
        }
    }

    /* ===================== PUBLIC API ===================== */

    public static boolean hasActiveSession(EntityPlayer player) {
        return activeSessions.containsKey(player.getUniqueID());
    }

    public static EntityCustomBobber getActiveBobber(EntityPlayer player) {
        SessionData data = activeSessions.get(player.getUniqueID());
        if (data == null) return null;

        if (isBobberInvalid(data.bobber)) {
            forceEndSession(player);
            return null;
        }

        return data.bobber;
    }

    public static boolean startSession(EntityPlayer player, EntityCustomBobber bobber) {
        UUID id = player.getUniqueID();

        if (activeSessions.containsKey(id)) {
            forceEndSession(player);
        }

        activeSessions.put(id, new SessionData(bobber, player.worldObj.getTotalWorldTime()));
        return true;
    }

    public static void endSession(EntityPlayer player) {
        UUID id = player.getUniqueID();
        SessionData data = activeSessions.remove(id);

        if (data != null && !isBobberInvalid(data.bobber)) {
            data.bobber.retrieveBobber();
        }
    }

    /** ЖЁСТКИЙ аварийный сброс (использовать при рассинхроне) */
    public static void forceEndSession(EntityPlayer player) {
        UUID id = player.getUniqueID();
        SessionData data = activeSessions.remove(id);

        if (data != null && data.bobber != null) {
            data.bobber.retrieveBobber(); // ✔ корректное завершение
        }
    }


    /* ===================== CLEANUP ===================== */

    /** Чистка зависших сессий */
    public static void removeExpiredSessions() {
        Iterator<Map.Entry<UUID, SessionData>> it = activeSessions.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<UUID, SessionData> entry = it.next();
            SessionData data = entry.getValue();

            if (isBobberInvalid(data.bobber)) {
                it.remove();
            }
        }
    }

    private static boolean isBobberInvalid(EntityCustomBobber bobber) {
        return bobber == null || bobber.isDead || bobber.worldObj == null;
    }

    /* ===================== LOOT ===================== */

    public static ItemStack generateFish(int fishTier) {
        // TODO: твоя логика
        return null;
    }

    public static ItemStack[] generateChestContents(boolean golden) {
        // TODO: твоя логика
        return new ItemStack[0];
    }
}

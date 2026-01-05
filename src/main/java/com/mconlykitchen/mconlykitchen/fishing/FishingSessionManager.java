package com.mconlykitchen.mconlykitchen.fishing;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import com.mconlykitchen.mconlykitchen.network.PacketFishingInput;

public class FishingSessionManager {

    private static final Map<String, FishingSession> sessions = new HashMap<>();

    // Создать сессию
    public static void startSession(EntityPlayerMP player, int rodTier, boolean isLava) {
        sessions.put(player.getCommandSenderName(), new FishingSession(player, rodTier, isLava));
    }

    // Обработать пакет от игрока
    public static void handleFishingInput(EntityPlayerMP player, PacketFishingInput packet) {
        FishingSession session = sessions.get(player.getCommandSenderName());
        if (session == null) return;

        boolean gotChest = packet.hasChest();
        boolean isGolden = packet.isGoldenChest();

        session.finish(gotChest, isGolden);
        sessions.remove(player.getCommandSenderName());
    }

    public static boolean hasSession(EntityPlayerMP player) {
        return sessions.containsKey(player.getCommandSenderName());
    }
}

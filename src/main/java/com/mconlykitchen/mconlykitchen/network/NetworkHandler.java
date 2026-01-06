package com.mconlykitchen.mconlykitchen.network;

import com.mconlykitchen.mconlykitchen.MCOnlyKitchen;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class NetworkHandler {

    public static final SimpleNetworkWrapper INSTANCE =
            NetworkRegistry.INSTANCE.newSimpleChannel(MCOnlyKitchen.MODID);

    private static int packetId = 0;

    public static void init() {
        // Открытие мини‑игры (сервер → клиент)
        INSTANCE.registerMessage(
                new PacketOpenFishingGUI.Handler(),
                PacketOpenFishingGUI.class,
                packetId++,
                Side.CLIENT
        );

        // Результат мини‑игры (клиент → сервер)
        INSTANCE.registerMessage(
                new PacketFishingResult.Handler(),
                PacketFishingResult.class,
                packetId++,
                Side.SERVER
        );

        // Анимация поклёвки (сервер → клиент)
        INSTANCE.registerMessage(
                new PacketShowBiteAnimation.Handler(),
                PacketShowBiteAnimation.class,
                packetId++,
                Side.CLIENT
        );

        // Нажатие пробела (клиент → сервер)
        INSTANCE.registerMessage(
                new PacketSpacePressed.Handler(),
                PacketSpacePressed.class,
                packetId++,
                Side.SERVER
        );
    }
}

package com.mconlykitchen.mconlykitchen.network;

import com.mconlykitchen.mconlykitchen.MCOnlyKitchen;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class NetworkHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(MCOnlyKitchen.MODID);

    private static int packetId = 0;

    public static void init() {
        // Анимация поклёвки (сервер -> клиент)
        INSTANCE.registerMessage(
                PacketShowBiteAnimation.Handler.class,
                PacketShowBiteAnimation.class,
                packetId++,
                Side.CLIENT
        );

        // Нажатие пробела (клиент -> сервер)
        INSTANCE.registerMessage(
                PacketSpacePressed.Handler.class,
                PacketSpacePressed.class,
                packetId++,
                Side.SERVER
        );

        // Открытие GUI мини-игры (сервер -> клиент)
        // НЕ НУЖЕН! GUI открывается локально после нажатия пробела

        // Результат мини-игры (клиент -> сервер)
        INSTANCE.registerMessage(
                PacketFishingResult.Handler.class,
                PacketFishingResult.class,
                packetId++,
                Side.SERVER
        );
    }
}
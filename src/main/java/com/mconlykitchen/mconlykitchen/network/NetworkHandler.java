package com.mconlykitchen.mconlykitchen.network;

import com.mconlykitchen.mconlykitchen.MCOnlyKitchen;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public final class NetworkHandler {

    public static final SimpleNetworkWrapper INSTANCE =
            NetworkRegistry.INSTANCE.newSimpleChannel(MCOnlyKitchen.MODID);

    private static int id = 0;

    public static void init() {
        // Сервер -> клиент (открытие GUI)
        INSTANCE.registerMessage(PacketOpenFishingGUI.Handler.class, PacketOpenFishingGUI.class, id++, Side.CLIENT);
        // Клиент -> сервер (результат мини-игры)
        INSTANCE.registerMessage(PacketFishingResult.Handler.class, PacketFishingResult.class, id++, Side.SERVER);
    }

    private NetworkHandler() {}
}

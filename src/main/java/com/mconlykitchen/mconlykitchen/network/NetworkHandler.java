package com.mconlykitchen.mconlykitchen.network;

import com.mconlykitchen.mconlykitchen.MCOnlyKitchen;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class NetworkHandler {

    public static final SimpleNetworkWrapper INSTANCE =
            NetworkRegistry.INSTANCE.newSimpleChannel(MCOnlyKitchen.MODID);

    private static int packetId = 0;

    public static void init() {
        // ================= СЕРВЕРНЫЕ пакеты =================
        INSTANCE.registerMessage(
                PacketFishingResult.Handler.class,
                PacketFishingResult.class,
                packetId++,
                Side.SERVER
        );

        INSTANCE.registerMessage(
                PacketSpacePressed.Handler.class,
                PacketSpacePressed.class,
                packetId++,
                Side.SERVER
        );

        // ================= КЛИЕНТСКИЕ пакеты =================
        if (FMLCommonHandler.instance().getSide().isClient()) {
            INSTANCE.registerMessage(
                    PacketOpenFishingGUI.Handler.class,
                    PacketOpenFishingGUI.class,
                    packetId++,
                    Side.CLIENT
            );

            INSTANCE.registerMessage(
                    PacketShowBiteAnimation.Handler.class,
                    PacketShowBiteAnimation.class,
                    packetId++,
                    Side.CLIENT
            );
        }
    }
}

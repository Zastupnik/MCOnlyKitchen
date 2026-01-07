package com.mconlykitchen.mconlykitchen.proxy;

import com.mconlykitchen.mconlykitchen.client.ClientEventHandler;
import com.mconlykitchen.mconlykitchen.client.render.RenderCustomBobber;
import com.mconlykitchen.mconlykitchen.entity.EntityCustomBobber;
import com.mconlykitchen.mconlykitchen.network.NetworkHandler;
import com.mconlykitchen.mconlykitchen.network.PacketShowBiteAnimation;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        // Регистрация рендера поплавка
        RenderingRegistry.registerEntityRenderingHandler(
                EntityCustomBobber.class,
                new RenderCustomBobber()
        );

        // Клиентские эвенты
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(new ClientEventHandler());

        // ✅ Регистрация клиентских пакетов только на клиенте
        int packetId = 0;
        NetworkHandler.INSTANCE.registerMessage(
                PacketShowBiteAnimation.Handler.class,
                PacketShowBiteAnimation.class,
                packetId++,
                cpw.mods.fml.relauncher.Side.CLIENT
        );
    }
}

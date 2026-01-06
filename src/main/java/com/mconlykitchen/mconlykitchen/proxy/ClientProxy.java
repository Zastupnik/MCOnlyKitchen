package com.mconlykitchen.mconlykitchen.proxy;

import com.mconlykitchen.mconlykitchen.client.ClientEventHandler;
import com.mconlykitchen.mconlykitchen.client.render.RenderCustomBobber;
import com.mconlykitchen.mconlykitchen.entity.EntityCustomBobber;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        // Регистрируем клиентские события
        ClientEventHandler clientEvents = new ClientEventHandler();
        FMLCommonHandler.instance().bus().register(clientEvents);
        MinecraftForge.EVENT_BUS.register(clientEvents);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        // Регистрируем рендер кастомного поплавка
        RenderingRegistry.registerEntityRenderingHandler(
                EntityCustomBobber.class,
                new RenderCustomBobber()
        );
    }
}

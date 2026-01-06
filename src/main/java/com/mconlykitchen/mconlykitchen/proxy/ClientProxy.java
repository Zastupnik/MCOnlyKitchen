package com.mconlykitchen.mconlykitchen.proxy;

import com.mconlykitchen.mconlykitchen.client.ClientEventHandler;
import com.mconlykitchen.mconlykitchen.client.render.RenderCustomBobber;
import com.mconlykitchen.mconlykitchen.entity.EntityCustomBobber;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(cpw.mods.fml.common.event.FMLInitializationEvent event) {
        super.init(event);

        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
                com.mconlykitchen.mconlykitchen.entity.EntityCustomBobber.class,
                new com.mconlykitchen.mconlykitchen.client.render.RenderCustomBobber()
        );

        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new com.mconlykitchen.mconlykitchen.client.ClientEventHandler());
        cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(new com.mconlykitchen.mconlykitchen.client.ClientEventHandler());
    }

}

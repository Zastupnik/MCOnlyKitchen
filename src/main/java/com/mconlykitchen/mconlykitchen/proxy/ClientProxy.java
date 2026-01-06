// ClientProxy.java
package com.mconlykitchen.mconlykitchen.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        // Клиентская инициализация (рендеры и т.д.)
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        // Клиентская инициализация
    }
}
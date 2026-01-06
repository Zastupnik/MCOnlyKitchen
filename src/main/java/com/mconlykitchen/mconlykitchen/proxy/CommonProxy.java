// CommonProxy.java
package com.mconlykitchen.mconlykitchen.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import com.mconlykitchen.mconlykitchen.MCOnlyKitchen;
import com.mconlykitchen.mconlykitchen.entity.EntityCustomFishHook;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        // Регистрация entity
        EntityRegistry.registerModEntity(
                EntityCustomFishHook.class,
                "custom_fish_hook",
                0, // уникальный ID
                MCOnlyKitchen.instance,
                64, // trackingRange
                10, // updateFrequency
                true // sendVelocityUpdates
        );
    }

    public void init(FMLInitializationEvent event) {
        // Общая инициализация
    }

    public void postInit(FMLPostInitializationEvent event) {
        // Пост-инициализация
    }
}
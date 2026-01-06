// CommonProxy.java
package com.mconlykitchen.mconlykitchen.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import com.mconlykitchen.mconlykitchen.MCOnlyKitchen;
import com.mconlykitchen.mconlykitchen.entity.EntityCustomBobber;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        // Регистрация кастомного поплавка
        EntityRegistry.registerModEntity(
                EntityCustomBobber.class,
                "custom_bobber",
                0, // уникальный ID для мода
                MCOnlyKitchen.instance,
                64, // trackingRange
                10, // updateFrequency (тиков между пакетами обновления)
                true // sendVelocityUpdates
        );
    }

    public void init() {}

    public void init(FMLInitializationEvent event) {
        // Общая инициализация (пока пусто)
    }

    public void postInit(FMLPostInitializationEvent event) {
        // Пост-инициализация
    }
}

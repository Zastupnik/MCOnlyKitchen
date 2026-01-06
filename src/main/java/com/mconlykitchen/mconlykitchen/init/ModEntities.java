package com.mconlykitchen.mconlykitchen.init;

import com.mconlykitchen.mconlykitchen.MCOnlyKitchen;
import com.mconlykitchen.mconlykitchen.entity.EntityCustomBobber;
import cpw.mods.fml.common.registry.EntityRegistry;

public class ModEntities {

    public static void init() {
        // Регистрируем поплавок
        EntityRegistry.registerModEntity(
                EntityCustomBobber.class,   // класс сущности
                "CustomBobber",             // имя
                0,                          // ID внутри мода
                MCOnlyKitchen.instance,     // основной мод-объект
                64,                         // радиус отслеживания
                10,                         // частота обновления
                true                        // отправлять скорость
        );
    }
}

package com.mconlykitchen.mconlykitchen.init;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.common.MinecraftForge;

public class ModEvents {

    public static void init() {
        ModEvents events = new ModEvents();

        // Регистрация обработчиков событий
        FMLCommonHandler.instance().bus().register(events);
        MinecraftForge.EVENT_BUS.register(events);
    }

    // Пример: можно добавить события для отслеживания рыбалки
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        // Действия при входе игрока (если нужно)
    }
}
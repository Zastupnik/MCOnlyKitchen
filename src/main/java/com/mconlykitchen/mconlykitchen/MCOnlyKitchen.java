package com.mconlykitchen.mconlykitchen;

import com.mconlykitchen.mconlykitchen.config.ModConfig;
import com.mconlykitchen.mconlykitchen.init.*;
import com.mconlykitchen.mconlykitchen.network.NetworkHandler;
import com.mconlykitchen.mconlykitchen.proxy.CommonProxy;
import com.mconlykitchen.mconlykitchen.util.FishTierSystem;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = MCOnlyKitchen.MODID,
        name = MCOnlyKitchen.NAME,
        version = MCOnlyKitchen.VERSION,
        acceptedMinecraftVersions = "[1.7.10]"
)
public class MCOnlyKitchen {

    public static final String MODID = "mconlykitchen";
    public static final String NAME = "MCOnlyKitchen";
    public static final String VERSION = "1.0.0";

    @Mod.Instance(MODID)
    public static MCOnlyKitchen instance;

    @SidedProxy(
            clientSide = "com.mconlykitchen.mconlykitchen.proxy.ClientProxy",
            serverSide = "com.mconlykitchen.mconlykitchen.proxy.CommonProxy"
    )
    public static CommonProxy proxy;

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("MCOnlyKitchen PreInitialization");

        // Загрузка конфига
        ModConfig.init(event.getSuggestedConfigurationFile());

        // Регистрация предметов
        ModItems.init();
        ModFishingRods.init();

        // Инициализация системы тиров
        FishTierSystem.init();
        LOGGER.info("Fish tier system initialized");

        // Регистрация звуков
        ModSounds.init();

        // Инициализация сети
        NetworkHandler.init();

        // Прокси инициализация
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("MCOnlyKitchen Initialization");

        // Регистрация событий
        ModEvents.init();

        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        LOGGER.info("MCOnlyKitchen PostInitialization");

        proxy.postInit(event);
    }
}
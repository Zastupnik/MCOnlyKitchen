package com.mconlykitchen.fishingmod;

import com.mconlykitchen.fishingmod.items.ItemFishingRod;
import com.mconlykitchen.fishingmod.items.ItemNetherFishingRod;
import com.mconlykitchen.fishingmod.network.PacketFishingResult;
import com.mconlykitchen.fishingmod.events.FishingEventHandler;
import com.mconlykitchen.fishingmod.config.ConfigHandler;
import com.mconlykitchen.fishingmod.network.PacketStartFishing;
import com.mconlykitchen.fishingmod.sound.ModSounds;
import com.mconlykitchen.fishingmod.gui.FishingGuiHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.item.Item;

@Mod(modid = FishingMod.MODID, name = "Fishing Mod", version = "1.0")
public class FishingMod {

    public static final String MODID = "fishingmod";

    @Mod.Instance(FishingMod.MODID)
    public static FishingMod instance;

    public static SimpleNetworkWrapper CHANNEL;

    // предметы
    public static Item fishingRod;
    public static Item netherFishingRod;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // конфиг
        ConfigHandler.init(event.getSuggestedConfigurationFile());

        // предметы
        fishingRod = new ItemFishingRod()
                .setUnlocalizedName("fishingRod")
                .setTextureName(MODID + ":fishing_rod");
        netherFishingRod = new ItemNetherFishingRod()
                .setUnlocalizedName("netherFishingRod")
                .setTextureName(MODID + ":nether_fishing_rod");

        GameRegistry.registerItem(fishingRod, "fishingRod");
        GameRegistry.registerItem(netherFishingRod, "netherFishingRod");
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // События
        MinecraftForge.EVENT_BUS.register(new FishingEventHandler());

        // Сетевой канал
        CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

        // Регистрируем пакеты с уникальными ID
        CHANNEL.registerMessage(PacketStartFishing.Handler.class, PacketStartFishing.class, 0, Side.CLIENT);
        CHANNEL.registerMessage(PacketFishingResult.Handler.class, PacketFishingResult.class, 1, Side.SERVER);

        // GUI handler
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new FishingGuiHandler());

        // звуки
        ModSounds.registerSounds();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        // Дополнительные интеграции при необходимости
    }
}

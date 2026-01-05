package com.mconlykitchen.fishingmod.events;

import com.mconlykitchen.fishingmod.FishingMod;
import com.mconlykitchen.fishingmod.items.ItemFishingRod;
import com.mconlykitchen.fishingmod.items.ItemNetherFishingRod;
import com.mconlykitchen.fishingmod.gui.GuiFishingMiniGame;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class FishingEventHandler {

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent event) {
        EntityPlayer player = event.entityPlayer;
        World world = player.worldObj;

        if (player == null || world.isRemote) return;

        ItemStack held = player.getHeldItem();
        if (held == null) return;

        // проверка типа удочки
        boolean isFishingRod = held.getItem() instanceof ItemFishingRod;
        boolean isNetherRod = held.getItem() instanceof ItemNetherFishingRod;
        if (!isFishingRod && !isNetherRod) return;

        // координаты блока под игроком
        int bx = (int) player.posX;
        int by = (int) player.posY - 1;
        int bz = (int) player.posZ;
        Block block = world.getBlock(bx, by, bz);

        // проверка на воду/лаву
        boolean isLiquid = block == Blocks.water || block == Blocks.flowing_water ||
                block == Blocks.lava || block == Blocks.flowing_lava;
        if (!isLiquid) return;

        // запуск GUI через GuiHandler
        player.openGui(FishingMod.instance, GuiFishingMiniGame.GUI_ID, world, bx, by, bz);
    }
}

package com.mconlykitchen.fishingmod.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class FishingGuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return null; // мини-игра только на клиенте
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        boolean lava = world.getBlock(x, y, z) == net.minecraft.init.Blocks.lava || player.dimension == -1;
        return new GuiFishingMiniGame(lava);
    }
}

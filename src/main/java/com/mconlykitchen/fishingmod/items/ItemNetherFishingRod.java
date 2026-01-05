package com.mconlykitchen.fishingmod.items;

import com.mconlykitchen.fishingmod.FishingMod;
import com.mconlykitchen.fishingmod.network.PacketStartFishing;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemNetherFishingRod extends Item {

    public ItemNetherFishingRod() {
        this.setMaxStackSize(1);
        this.setMaxDamage(128); // прочность выше, чем у обычной
        this.setCreativeTab(CreativeTabs.tabMisc); // чтобы предмет был виден в креативе
        this.setUnlocalizedName("netherFishingRod"); // имя для lang-файла
        this.setTextureName(FishingMod.MODID + ":nether_fishing_rod"); // путь к текстуре
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            // адская удочка всегда запускает lava-режим
            boolean lava = true;

            // отправляем пакет на клиент для запуска GUI
            FishingMod.CHANNEL.sendTo(
                    new PacketStartFishing(lava),
                    (net.minecraft.entity.player.EntityPlayerMP) player
            );
        }
        return stack;
    }
}

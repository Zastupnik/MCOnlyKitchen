package com.mconlykitchen.fishingmod.items;

import com.mconlykitchen.fishingmod.FishingMod;
import com.mconlykitchen.fishingmod.network.PacketStartFishing;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFishingRod extends Item {

    public ItemFishingRod() {
        this.setMaxStackSize(1);
        this.setMaxDamage(64); // прочность удочки
        this.setCreativeTab(CreativeTabs.tabMisc); // чтобы предмет был виден в креативе
        this.setUnlocalizedName("fishingRod");     // имя для lang-файла
        this.setTextureName(FishingMod.MODID + ":fishing_rod"); // путь к текстуре
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            // проверка условий для режима lava
            boolean lava = false;
            int bx = (int) player.posX;
            int by = (int) player.posY - 1;
            int bz = (int) player.posZ;

            if (world.getBlock(bx, by, bz) == net.minecraft.init.Blocks.lava) {
                lava = true;
            }
            if (player.dimension == -1) {
                lava = true;
            }

            // отправляем пакет на клиент для запуска GUI
            FishingMod.CHANNEL.sendTo(
                    new PacketStartFishing(lava),
                    (net.minecraft.entity.player.EntityPlayerMP) player
            );
        }
        return stack;
    }
}

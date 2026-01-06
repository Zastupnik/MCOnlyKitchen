package com.mconlykitchen.mconlykitchen.item;

import com.mconlykitchen.mconlykitchen.MCOnlyKitchen;
import com.mconlykitchen.mconlykitchen.init.ModCreativeTab;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemModFish extends ItemFood {

    private final String fishName;
    private final boolean isNether;

    public ItemModFish(String name, boolean isNether) {
        super(2, 0.3F, false); // 2 голода, 0.3 насыщения

        this.fishName = name;
        this.isNether = isNether;

        this.setCreativeTab(ModCreativeTab.INSTANCE);
        this.setUnlocalizedName(MCOnlyKitchen.MODID + "." + name);
        this.setMaxStackSize(64);

        // Адские рыбы дают огнестойкость при поедании сырыми
        if (isNether) {
            this.setAlwaysEdible();
        }
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);

        // Адские рыбы дают краткую огнестойкость (10 секунд)
        if (isNether && !world.isRemote) {
            player.addPotionEffect(new net.minecraft.potion.PotionEffect(
                    net.minecraft.potion.Potion.fireResistance.id,
                    200, // 10 секунд
                    0
            ));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(MCOnlyKitchen.MODID + ":" + fishName);
    }

    public boolean isNetherFish() {
        return isNether;
    }
}
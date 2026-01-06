package com.mconlykitchen.mconlykitchen.init;

import com.mconlykitchen.mconlykitchen.MCOnlyKitchen;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ModCreativeTab extends CreativeTabs {

    public static final ModCreativeTab INSTANCE = new ModCreativeTab();

    private ModCreativeTab() {
        super(MCOnlyKitchen.MODID);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem() {
        // Иконка таба - универсальная удочка
        return ModFishingRods.universalRod;
    }

    @Override
    public String getTranslatedTabLabel() {
        return "MCOnlyKitchen";
    }
}
package com.mconlykitchen.mconlykitchen.item;

import com.mconlykitchen.mconlykitchen.MCOnlyKitchen;
import com.mconlykitchen.mconlykitchen.config.ModConfig;
import com.mconlykitchen.mconlykitchen.entity.EntityCustomBobber;
import com.mconlykitchen.mconlykitchen.init.ModCreativeTab;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.List;

public class ItemCustomFishingRod extends Item {

    private final String rodName;
    private final int tier;
    private final boolean canUseInNether;

    public ItemCustomFishingRod(String name, int tier, boolean canUseInNether) {
        this.rodName = name;
        this.tier = tier;
        this.canUseInNether = canUseInNether;

        this.setMaxStackSize(1);
        this.setMaxDamage(tier == 6 ? 128 : 64);
        this.setCreativeTab(ModCreativeTab.INSTANCE);
        this.setUnlocalizedName(MCOnlyKitchen.MODID + "." + name);
    }

    private static int mapRodTier(int tier) {
        if (tier == 6) return 3; // последний индекс для конфиг-массива biteTimes
        if (tier >= 3 && tier <= 5) return tier - 2;
        return tier;
    }

    private static boolean mapIsNether(int tier) {
        return (tier >= 3 && tier <= 5);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        boolean inNether = player.worldObj.provider.isHellWorld;

        if (!canUseInNether && inNether) {
            if (world.isRemote) player.addChatMessage(
                    new ChatComponentText(EnumChatFormatting.RED + "Эта удочка не работает в Незере!"));
            return stack;
        }
        if (mapIsNether(tier) && !inNether) {
            if (world.isRemote) player.addChatMessage(
                    new ChatComponentText(EnumChatFormatting.RED + "Эта удочка работает только в Незере!"));
            return stack;
        }

        if (!world.isRemote) {
            EntityCustomBobber bobber = new EntityCustomBobber(world, player, tier);
            world.spawnEntityInWorld(bobber);
        }

        world.playSoundAtEntity(player, "random.bow", 0.5F,
                0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        player.swingItem();

        // Обновляем время использования
        setLastUsedTime(stack, player.worldObj.getTotalWorldTime());

        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);

        // rodTier 0..3: 0-базовая,1-улучшенная,2-продвинутая,3-универсальная
        int rodTier = mapRodTier(this.tier);

        // Шансы и время всегда берём из конфига для этих 4 типов
        boolean isNether = mapIsNether(this.tier);

        int chestChance = ModConfig.getChestChance(rodTier, isNether);
        int goldenChance = ModConfig.getGoldenChestChance(rodTier, isNether);
        int biteTime = ModConfig.getBiteTime(rodTier, isNether); // секунды
        long cooldownTicks = biteTime * 20L;

        list.add(EnumChatFormatting.GOLD + "Шанс сундука: " + chestChance + "%");
        list.add(EnumChatFormatting.YELLOW + "Шанс золотого: " + goldenChance + "%");
        list.add(EnumChatFormatting.GRAY + "Время поклёвки: " + biteTime + " сек.");

        if (player != null && player.worldObj != null) {
            long currentTime = player.worldObj.getTotalWorldTime();
            long lastUsed = getLastUsedTime(stack);

            if (currentTime - lastUsed < cooldownTicks) {
                long remainingSec = (cooldownTicks - (currentTime - lastUsed)) / 20;
                list.add(EnumChatFormatting.RED + "Осталось: " + remainingSec + " сек.");
            }
        }
    }


    private long getLastUsedTime(ItemStack stack) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        return stack.getTagCompound().getLong("LastUsed");
    }

    private void setLastUsedTime(ItemStack stack, long time) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setLong("LastUsed", time);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(MCOnlyKitchen.MODID + ":" + rodName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }

    public int getTier() {
        return tier;
    }

    public boolean canUseInNether() {
        return canUseInNether;
    }
}

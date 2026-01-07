package com.mconlykitchen.mconlykitchen.item;

import com.mconlykitchen.mconlykitchen.MCOnlyKitchen;
import com.mconlykitchen.mconlykitchen.config.ModConfig;
import com.mconlykitchen.mconlykitchen.entity.EntityCustomBobber;
import com.mconlykitchen.mconlykitchen.fishing.FishingSessionManager;
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

    public ItemCustomFishingRod(String name, int tier) {
        this.rodName = name;
        this.tier = tier;

        setMaxStackSize(1);
        setMaxDamage(tier == 6 ? 128 : 64);
        setCreativeTab(ModCreativeTab.INSTANCE);
        setUnlocalizedName(MCOnlyKitchen.MODID + "." + name);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        boolean inNether = world.provider.isHellWorld;

        // Проверка работы удочки по миру
        if ((tier <= 2 && inNether) || (tier >= 3 && tier <= 5 && !inNether)) {
            if (world.isRemote)
                player.addChatMessage(new ChatComponentText(
                        EnumChatFormatting.RED + (inNether ? "Эта удочка работает только в обычном мире!" :
                                "Эта удочка работает только в Незере!")
                ));
            return stack;
        }

        // Проверка cooldown
        if (!world.isRemote) {
            long last = getLastUsedTime(stack);
            long diff = world.getTotalWorldTime() - last;
            long cd = ModConfig.getBiteTimeByTier(tier) * 20L;
            if (diff < cd) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED +
                        "Удочка ещё отдыхает: " + ((cd - diff) / 20) + " сек."));
                return stack;
            }
        }

        // Проверка блока под игроком / место попадания поплавка
        int bx = (int) Math.floor(player.posX);
        int by = (int) Math.floor(player.posY + 0.1);
        int bz = (int) Math.floor(player.posZ);

        if (!world.isRemote) {
            if (!world.getBlock(bx, by, bz).getMaterial().isLiquid()) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Бросайте удочку только в воду или лаву!"));
                return stack;
            }
        }

        if (!world.isRemote) {
            // Проверяем, нет ли активной сессии
            if (FishingSessionManager.hasActiveSession(player)) return stack;

            // Создаём поплавок
            EntityCustomBobber bobber = new EntityCustomBobber(world, player, tier);

            // Стартуем сессию
            FishingSessionManager.startSession(player, bobber);

            world.spawnEntityInWorld(bobber);
            setLastUsedTime(stack, world.getTotalWorldTime());
        }

        return stack;
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
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean adv) {
        int biteTime = ModConfig.getBiteTimeByTier(tier);
        list.add(EnumChatFormatting.GRAY + "Время поклёвки: " + biteTime + " сек.");
        list.add(EnumChatFormatting.GOLD + "Шанс сундука: " + ModConfig.getMiniChestChanceByTier(tier) + "%");
        list.add(EnumChatFormatting.YELLOW + "Шанс золотого сундука: " + ModConfig.getMiniGoldenChestChanceByTier(tier) + "%");

        if (player != null && player.worldObj != null) {
            long last = getLastUsedTime(stack);
            long diff = player.worldObj.getTotalWorldTime() - last;
            long cd = biteTime * 20L;
            if (diff < cd)
                list.add(EnumChatFormatting.RED + "Осталось: " + ((cd - diff) / 20) + " сек.");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        itemIcon = reg.registerIcon(MCOnlyKitchen.MODID + ":" + rodName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFull3D() { return true; }

    public int getTier() { return tier; }
}

package com.mconlykitchen.mconlykitchen.item;

import com.mconlykitchen.mconlykitchen.MCOnlyKitchen;
import com.mconlykitchen.mconlykitchen.config.ModConfig;
import com.mconlykitchen.mconlykitchen.entity.EntityCustomFishHook;
import com.mconlykitchen.mconlykitchen.init.ModCreativeTab;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

    // Кулдауны в секундах
    private static final int[] COOLDOWNS = {
            60,  // tier 0: базовая - 60 сек
            30,  // tier 1: улучшенная - 30 сек
            15,  // tier 2: продвинутая - 15 сек
            60,  // tier 3: адская базовая - 60 сек
            30,  // tier 4: адская улучшенная - 30 сек
            15,  // tier 5: адская продвинутая - 15 сек
            5    // tier 6: универсальная - 5 сек
    };

    public ItemCustomFishingRod(String name, int tier, boolean canUseInNether) {
        this.rodName = name;
        this.tier = tier;
        this.canUseInNether = canUseInNether;

        this.setMaxStackSize(1);
        this.setMaxDamage(tier == 6 ? 128 : 64);
        this.setCreativeTab(ModCreativeTab.INSTANCE);
        this.setUnlocalizedName(MCOnlyKitchen.MODID + "." + name);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        long currentTime = world.getTotalWorldTime();
        long lastUsed = getLastUsedTime(stack);
        long cooldownTicks = COOLDOWNS[tier] * 20L;

        if (currentTime - lastUsed < cooldownTicks) {
            if (world.isRemote) {
                long remainingSec = (cooldownTicks - (currentTime - lastUsed)) / 20;
                player.addChatMessage(new ChatComponentText(
                        EnumChatFormatting.RED + "Кулдаун: " + remainingSec + " сек."
                ));
            }
            return stack;
        }

        if (!canUseInNether && player.worldObj.provider.isHellWorld ||
                tier >= 3 && tier <= 5 && !player.worldObj.provider.isHellWorld) {
            if (world.isRemote) return stack;
            return stack;
        }

        if (player.fishEntity != null) {
            player.swingItem();
        } else {
            world.playSoundAtEntity(player, "random.bow", 0.5F,
                    0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

            if (!world.isRemote && player instanceof EntityPlayerMP) {
                EntityCustomFishHook hook = new EntityCustomFishHook(world, (EntityPlayerMP) player, tier);
                world.spawnEntityInWorld(hook);
                setLastUsedTime(stack, currentTime);
            }
            player.swingItem();
        }

        return stack;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);

        // Шансы на сундуки
        int chestChance = ModConfig.getChestChance(tier, tier >= 3 && tier <= 5);
        int goldenChance = ModConfig.getGoldenChestChance(tier, tier >= 3 && tier <= 5);

        list.add(EnumChatFormatting.GOLD + "Шанс сундука: " + chestChance + "%");
        list.add(EnumChatFormatting.YELLOW + "Шанс золотого: " + goldenChance + "%");
        list.add(EnumChatFormatting.GRAY + "Кулдаун: " + COOLDOWNS[tier] + " сек.");

        // Показываем оставшийся кулдаун
        if (player != null && player.worldObj != null) {
            long currentTime = player.worldObj.getTotalWorldTime();
            long lastUsed = getLastUsedTime(stack);
            long cooldownTicks = COOLDOWNS[tier] * 20L;

            if (currentTime - lastUsed < cooldownTicks) {
                long remainingSec = (cooldownTicks - (currentTime - lastUsed)) / 20;
                list.add(EnumChatFormatting.RED + "Кулдаун: " + remainingSec + " сек.");
            }
        }
    }

    /**
     * Получить время последнего использования
     */
    private long getLastUsedTime(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound().getLong("LastUsed");
    }

    /**
     * Установить время последнего использования
     */
    private void setLastUsedTime(ItemStack stack, long time) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
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
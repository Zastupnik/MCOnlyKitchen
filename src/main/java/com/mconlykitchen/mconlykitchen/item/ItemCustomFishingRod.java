package com.mconlykitchen.mconlykitchen.item;

import com.mconlykitchen.mconlykitchen.MCOnlyKitchen;
import com.mconlykitchen.mconlykitchen.config.ModConfig;
import com.mconlykitchen.mconlykitchen.entity.EntityCustomFishHook;
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

    private static final int[] COOLDOWNS = {60, 30, 15, 60, 30, 15, 5};

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
        if (tier == 6) return 4;
        if (tier >= 3 && tier <= 5) return tier - 2;
        return tier;
    }

    private static boolean mapIsNether(int tier) {
        if (tier == 6) return false;
        return (tier >= 3 && tier <= 5);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        // Проверка кулдауна
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

        // Проверка измерения
        boolean inNether = player.worldObj.provider.isHellWorld;

        if (!canUseInNether && inNether) {
            if (world.isRemote) {
                player.addChatMessage(new ChatComponentText(
                        EnumChatFormatting.RED + "Эта удочка не работает в Незере!"
                ));
            }
            return stack;
        }

        if ((tier >= 3 && tier <= 5) && !inNether) {
            if (world.isRemote) {
                player.addChatMessage(new ChatComponentText(
                        EnumChatFormatting.RED + "Эта удочка работает только в Незере!"
                ));
            }
            return stack;
        }

        // Проверяем есть ли уже крючок в мире
        if (player.fishEntity != null) {
            // Вытаскиваем существующий крючок
            int damage = player.fishEntity.func_146034_e();
            stack.damageItem(damage, player);
            world.playSoundAtEntity(player, "random.bow", 0.5F,
                    0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        } else {
            // Кидаем новый крючок
            if (!world.isRemote) {
                EntityCustomFishHook hook = new EntityCustomFishHook(world, player, tier);
                world.spawnEntityInWorld(hook);
                setLastUsedTime(stack, currentTime);
            }

            world.playSoundAtEntity(player, "random.bow", 0.5F,
                    0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        }

        player.swingItem();
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);

        int rodTier = mapRodTier(this.tier);
        boolean isNether = mapIsNether(this.tier);

        int chestChance = ModConfig.getChestChance(rodTier, isNether);
        int goldenChance = ModConfig.getGoldenChestChance(rodTier, isNether);

        list.add(EnumChatFormatting.GOLD + "Шанс сундука: " + chestChance + "%");
        list.add(EnumChatFormatting.YELLOW + "Шанс золотого: " + goldenChance + "%");
        list.add(EnumChatFormatting.GRAY + "Кулдаун: " + COOLDOWNS[this.tier] + " сек.");

        if (player != null && player.worldObj != null) {
            long currentTime = player.worldObj.getTotalWorldTime();
            long lastUsed = getLastUsedTime(stack);
            long cooldownTicks = COOLDOWNS[tier] * 20L;

            if (currentTime - lastUsed < cooldownTicks) {
                long remainingSec = (cooldownTicks - (currentTime - lastUsed)) / 20;
                list.add(EnumChatFormatting.RED + "Осталось: " + remainingSec + " сек.");
            }
        }
    }

    private long getLastUsedTime(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound().getLong("LastUsed");
    }

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
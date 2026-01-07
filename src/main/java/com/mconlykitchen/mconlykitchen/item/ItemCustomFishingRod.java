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

    public ItemCustomFishingRod(String name, int tier) {
        this.rodName = name;
        this.tier = tier;

        this.setMaxStackSize(1);
        this.setMaxDamage(tier == 6 ? 128 : 64);
        this.setCreativeTab(ModCreativeTab.INSTANCE);
        this.setUnlocalizedName(MCOnlyKitchen.MODID + "." + name);
    }

    private boolean canUseHere(boolean inNether) {
        if (tier <= 2) return !inNether;
        if (tier <= 5) return inNether;
        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        boolean inNether = player.worldObj.provider.isHellWorld;

        // Проверка мира
        if (!canUseHere(inNether)) {
            if (world.isRemote) {
                player.addChatMessage(new ChatComponentText(
                        EnumChatFormatting.RED + (inNether
                                ? "Эта удочка работает только в обычном мире!"
                                : "Эта удочка работает только в Незере!")
                ));
            }
            return stack;
        }

        if (!world.isRemote && player instanceof EntityPlayerMP) {
            EntityPlayerMP mp = (EntityPlayerMP) player;

            // GUI-lock
            if (mp.getEntityData().getBoolean("FishingLock")) {
                return stack;
            }

            // Пост-лок
            long until = mp.getEntityData().getLong("FishingPostLockUntil");
            if (until > 0 && player.worldObj.getTotalWorldTime() < until) {
                return stack;
            } else if (until > 0) {
                mp.getEntityData().setLong("FishingPostLockUntil", 0L);
            }

            // Активная сессия
            if (com.mconlykitchen.mconlykitchen.fishing.FishingSessionManager.hasActiveSession(mp)) {
                return stack;
            }

            // НОВОЕ: Удаляем старый поплавок если есть
            EntityCustomBobber oldBobber = findExistingBobber(world, player);
            if (oldBobber != null) {
                // Проверяем был ли поплавок в воде
                if (!oldBobber.wasInWater()) {
                    player.addChatMessage(new ChatComponentText(
                            EnumChatFormatting.RED + "Бросайте удочку только в воду!"
                    ));
                }
                oldBobber.setDead();
            }

            EntityCustomBobber bobber = new EntityCustomBobber(world, player, this.tier);
            world.spawnEntityInWorld(bobber);

            FishingSessionManager.startSession(player, bobber);
        }

        // Звук и анимация
        world.playSoundAtEntity(player, "random.bow", 0.5F,
                0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        player.swingItem();

        // ИСПРАВЛЕНО: таймер обновляется ВСЕГДА
        setLastUsedTime(stack, player.worldObj.getTotalWorldTime());

        return stack;
    }

    /**
     * Найти существующий поплавок игрока
     */
    private EntityCustomBobber findExistingBobber(World world, EntityPlayer player) {
        for (Object obj : world.loadedEntityList) {
            if (obj instanceof EntityCustomBobber) {
                EntityCustomBobber bobber = (EntityCustomBobber) obj;
                if (bobber.getOwner() == player) {
                    return bobber;
                }
            }
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);

        int biteTime = ModConfig.getBiteTimeByTier(this.tier);
        long cooldownTicks = biteTime * 20L;

        list.add(EnumChatFormatting.GOLD + "Шанс сундука: "
                + ModConfig.getMiniChestChanceByTier(this.tier) + "%");
        list.add(EnumChatFormatting.YELLOW + "Шанс золотого: "
                + ModConfig.getMiniGoldenChestChanceByTier(this.tier) + "%");
        list.add(EnumChatFormatting.GRAY + "Время поклёвки: " + biteTime + " сек.");

        if (player != null && player.worldObj != null) {
            long currentTime = player.worldObj.getTotalWorldTime();
            long lastUsed = getLastUsedTime(stack);

            // ИСПРАВЛЕНО: показываем таймер правильно
            if (lastUsed > 0 && currentTime - lastUsed < cooldownTicks) {
                long remainingSec = (cooldownTicks - (currentTime - lastUsed)) / 20;
                list.add(EnumChatFormatting.RED + "Поклёвка через: " + remainingSec + " сек.");
            }
        }
    }

    private long getLastUsedTime(ItemStack stack) {
        if (!stack.hasTagCompound()) return 0;
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
}
package com.mconlykitchen.mconlykitchen.item;

import com.mconlykitchen.mconlykitchen.MCOnlyKitchen;
import com.mconlykitchen.mconlykitchen.config.ModConfig;
import com.mconlykitchen.mconlykitchen.entity.EntityCustomBobber;
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
    private final int tier;            // 0..6
    private final boolean canUseInNether; // оставляем, но сама логика ниже — по tier

    public ItemCustomFishingRod(String name, int tier, boolean canUseInNether) {
        this.rodName = name;
        this.tier = tier;
        this.canUseInNether = canUseInNether;

        this.setMaxStackSize(1);
        this.setMaxDamage(tier == 6 ? 128 : 64); // универсальная (tier=6) прочнее
        this.setCreativeTab(ModCreativeTab.INSTANCE);
        this.setUnlocalizedName(MCOnlyKitchen.MODID + "." + name);
    }

    // Мировая проверка без маппингов
    private boolean canUseHere(boolean inNether) {
        if (tier <= 2) return !inNether;  // вода: только обычный мир
        if (tier <= 5) return inNether;   // ад: только Незер
        return true;                      // универсальная (tier=6): везде
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

        // Серверная проверка
        if (!world.isRemote && player instanceof EntityPlayerMP) {
            EntityPlayerMP mp = (EntityPlayerMP) player;

            // GUI‑lock: если открыт мини‑игровой/сундуковый экран
            if (mp.getEntityData().getBoolean("FishingLock")) {
                return stack;
            }

            // Пост‑лок: короткий таймаут после завершения предыдущей сессии
            long until = mp.getEntityData().getLong("FishingPostLockUntil");
            if (until > 0 && player.worldObj.getTotalWorldTime() < until) {
                return stack;
            } else if (until > 0 && player.worldObj.getTotalWorldTime() >= until) {
                // очищаем ключ, если таймаут истёк
                mp.getEntityData().setLong("FishingPostLockUntil", 0L);
            }

            // Активная сессия — не запускаем
            if (com.mconlykitchen.mconlykitchen.fishing.FishingSessionManager.hasSession(mp)) {
                return stack;
            }

            // Запускаем новую мини‑игру
            EntityCustomBobber bobber = new EntityCustomBobber(world, player, this.tier);
            world.spawnEntityInWorld(bobber);
        }

        // Звук и анимация
        world.playSoundAtEntity(player, "random.bow", 0.5F,
                0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        player.swingItem();

        // Запоминаем время использования (для подсказки в addInformation)
        setLastUsedTime(stack, player.worldObj.getTotalWorldTime());

        return stack;
    }





    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);

        int biteTime = ModConfig.getBiteTimeByTier(this.tier); // секунды по tier 0..6
        long cooldownTicks = biteTime * 20L;

        // Используем мини‑игровые шансы
        list.add(EnumChatFormatting.GOLD + "Шанс сундука (мини-игра): "
                + ModConfig.getMiniChestChanceByTier(this.tier) + "%");
        list.add(EnumChatFormatting.YELLOW + "Шанс золотого (мини-игра): "
                + ModConfig.getMiniGoldenChestChanceByTier(this.tier) + "%");
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

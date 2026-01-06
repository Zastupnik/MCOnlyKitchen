package com.mconlykitchen.mconlykitchen.fishing;

import com.mconlykitchen.mconlykitchen.config.ModConfig;
import com.mconlykitchen.mconlykitchen.util.FishingLootHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.*;

public class FishingSessionManager {

    private static final Map<UUID, FishingSession> sessions = new HashMap<UUID, FishingSession>();
    private static final long SESSION_TIMEOUT = 60000L; // 60 сек
    private static final Random RAND = new Random();

    public static void removeExpiredSessions() {
        Iterator<Map.Entry<UUID, FishingSession>> it = sessions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, FishingSession> e = it.next();
            if (e.getValue().isExpired()) {
                it.remove();
            }
        }
    }

    public static void startSession(EntityPlayerMP player, int rodTier, boolean isLava, int fishTier) {
        UUID id = player.getUniqueID();

        FishingSession existing = sessions.get(id);
        if (existing != null && !existing.isExpired()) {
            // уже идёт мини‑игра — не создаём новую
            return;
        }

        sessions.put(id, new FishingSession(player, rodTier, isLava, fishTier));
    }



    public static boolean finishSession(EntityPlayerMP player, boolean success, boolean gotChest, boolean isGoldenChest) {
        UUID id = player.getUniqueID();
        FishingSession session = sessions.get(id);
        if (session == null || session.isExpired() || !session.getPlayer().equals(player)) {
            sessions.remove(id);
            return false;
        }

        sessions.remove(id);

        // КОРОТКИЙ ПОСТ-ЛОК: закрываем гонку между сервером и GUI
        long now = player.worldObj.getTotalWorldTime();
        long postLockTicks = 40L; // ~2 секунды
        player.getEntityData().setLong("FishingPostLockUntil", now + postLockTicks);

        if (!success) {
            return true;
        }

        // Анти‑рывок
        player.motionX = 0.0D;
        player.motionY = 0.0D;
        player.motionZ = 0.0D;
        player.velocityChanged = true;

        // Рыба
        ItemStack fish = FishingLootHelper.getFishLoot(session.isLava(), session.getFishTier());
        if (fish != null && fish.getItem() != null) {
            giveRewardToPlayer(player, fish);
        }

        // Сундук
        if (gotChest) {
            ItemStack chestLoot = isGoldenChest ? ModConfig.getGoldenChestLoot() : ModConfig.getNormalChestLoot();
            if (chestLoot != null && chestLoot.getItem() != null) {
                giveRewardToPlayer(player, chestLoot);
                player.getEntityData().setTag("LastChestLoot", chestLoot.writeToNBT(new NBTTagCompound()));
            }
        }

        return true;
    }




    public static boolean hasSession(EntityPlayerMP player) {
        return sessions.containsKey(player.getUniqueID());
    }

    public static void clearSession(EntityPlayerMP player) {
        UUID id = player.getUniqueID();
        sessions.remove(id);
    }

    private static void giveRewardToPlayer(EntityPlayerMP player, ItemStack loot) {
        ItemStack stack = loot.copy();
        if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropPlayerItemWithRandomChoice(stack, false);
            player.addChatMessage(new ChatComponentText(
                    EnumChatFormatting.YELLOW + "Инвентарь полон! Предмет выпал на землю."
            ));
        }
        player.inventoryContainer.detectAndSendChanges();

        ItemStack heldItem = player.getHeldItem();
        if (heldItem != null && heldItem.getItem() != null) {
            heldItem.damageItem(1, player);
        }
    }

    private static class FishingSession {
        private final EntityPlayerMP player;
        private final int rodTier;
        private final boolean isLava;
        private final int fishTier;
        private final long startTime;

        public FishingSession(EntityPlayerMP player, int rodTier, boolean isLava, int fishTier) {
            this.player = player;
            this.rodTier = rodTier;
            this.isLava = isLava;
            this.fishTier = fishTier;
            this.startTime = System.currentTimeMillis();
        }

        public EntityPlayerMP getPlayer() { return player; }
        public int getRodTier() { return rodTier; }
        public boolean isLava() { return isLava; }
        public int getFishTier() { return fishTier; }
        public boolean isExpired() { return System.currentTimeMillis() - startTime > SESSION_TIMEOUT; }
    }
}

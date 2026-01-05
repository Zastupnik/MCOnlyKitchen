package com.mconlykitchen.mconlykitchen.network;

import com.mconlykitchen.mconlykitchen.util.FishingLootHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class PacketFishingResult implements IMessage {

    private boolean success;
    private boolean gotChest;
    private boolean isGoldenChest;
    private int rodTier;
    private boolean isLava;
    private int fishTier;

    public PacketFishingResult() {}

    public PacketFishingResult(boolean success, boolean gotChest, boolean isGoldenChest,
                               int rodTier, boolean isLava, int fishTier) {
        this.success = success;
        this.gotChest = gotChest;
        this.isGoldenChest = isGoldenChest;
        this.rodTier = rodTier;
        this.isLava = isLava;
        this.fishTier = fishTier;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        success = buf.readBoolean();
        gotChest = buf.readBoolean();
        isGoldenChest = buf.readBoolean();
        rodTier = buf.readInt();
        isLava = buf.readBoolean();
        fishTier = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(success);
        buf.writeBoolean(gotChest);
        buf.writeBoolean(isGoldenChest);
        buf.writeInt(rodTier);
        buf.writeBoolean(isLava);
        buf.writeInt(fishTier);
    }

    public static class Handler implements IMessageHandler<PacketFishingResult, IMessage> {
        @Override
        public IMessage onMessage(final PacketFishingResult message, MessageContext ctx) {
            // На клиенте безопасно работать напрямую
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;

            if (!message.success) return null;

            ItemStack loot;

            if (message.gotChest) {
                loot = FishingLootHelper.getChestLoot(message.isGoldenChest);
            } else {
                loot = FishingLootHelper.getFishLoot(message.isLava, message.fishTier);
            }

            if (loot != null) {
                boolean added = player.inventory.addItemStackToInventory(loot);
                if (!added) {
                    player.dropPlayerItemWithRandomChoice(loot, false);
                    player.addChatMessage(new ChatComponentText(
                            EnumChatFormatting.YELLOW + "Инвентарь полон! Предмет выпал на землю."
                    ));
                }
            }

            // Наносим урон удочке
            ItemStack held = player.getHeldItem();
            if (held != null) held.damageItem(1, player);

            return null;
        }
    }
}

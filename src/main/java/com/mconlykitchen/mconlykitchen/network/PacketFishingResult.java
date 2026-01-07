package com.mconlykitchen.mconlykitchen.network;

import com.mconlykitchen.mconlykitchen.entity.EntityCustomBobber;
import com.mconlykitchen.mconlykitchen.fishing.FishingSessionManager;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class PacketFishingResult implements IMessage {

    private boolean success, gotChest, isGoldenChest;

    public PacketFishingResult() {}

    public PacketFishingResult(boolean success, boolean gotChest, boolean isGoldenChest) {
        this.success = success;
        this.gotChest = gotChest;
        this.isGoldenChest = isGoldenChest;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        success = buf.readBoolean();
        gotChest = buf.readBoolean();
        isGoldenChest = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(success);
        buf.writeBoolean(gotChest);
        buf.writeBoolean(isGoldenChest);
    }

    public static class Handler implements IMessageHandler<PacketFishingResult, IMessage> {

        @Override
        public IMessage onMessage(PacketFishingResult message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null || player.isDead) return null;

            EntityCustomBobber bobber = FishingSessionManager.getActiveBobber(player);
            if (bobber == null) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Ошибка рыбалки. Попробуйте снова."));
                return null;
            }

            // Ловим сундук
            if (message.gotChest) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD +
                        (message.isGoldenChest ? "Вы поймали ЗОЛОТОЙ сундук!" : "Вы поймали сундук!")));

                ItemStack[] drops = FishingSessionManager.generateChestContents(message.isGoldenChest);
                for (ItemStack drop : drops) {
                    if (!player.inventory.addItemStackToInventory(drop)) {
                        player.dropPlayerItemWithRandomChoice(drop, false);
                    }
                }
            }

            // Ловим обычную рыбу
            if (message.success) {
                ItemStack fish = FishingSessionManager.generateFish(bobber.getFishTier());
                if (!player.inventory.addItemStackToInventory(fish)) {
                    player.dropPlayerItemWithRandomChoice(fish, false);
                }
            }

            // Завершаем сессию после выдачи всех предметов
            FishingSessionManager.endSession(player);

            return null;
        }
    }
}

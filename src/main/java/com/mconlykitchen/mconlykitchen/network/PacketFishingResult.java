package com.mconlykitchen.mconlykitchen.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

/**
 * Пакет результата рыбалки: клиент → сервер
 * Используется вместе с FishingSessionManager для защиты от читеров
 */
public class PacketFishingResult implements IMessage {

    private boolean success;
    private boolean gotChest;
    private boolean isGoldenChest;

    public PacketFishingResult() {}

    public PacketFishingResult(boolean success, boolean gotChest, boolean isGoldenChest) {
        this.success = success;
        this.gotChest = gotChest;
        this.isGoldenChest = isGoldenChest;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.success = buf.readBoolean();
        this.gotChest = buf.readBoolean();
        this.isGoldenChest = buf.readBoolean();
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

            if (player == null || player.isDead) {
                return null;
            }

            boolean success = com.mconlykitchen.mconlykitchen.fishing.FishingSessionManager
                    .finishSession(player, message.success, message.gotChest, message.isGoldenChest);

            if (!success) {
                player.addChatMessage(new ChatComponentText(
                        EnumChatFormatting.RED + "Ошибка рыбалки. Попробуйте снова."
                ));
            } else if (message.gotChest) {
                // Доп. сообщение при сундуке
                player.addChatMessage(new ChatComponentText(
                        EnumChatFormatting.GOLD + (message.isGoldenChest ? "Вы поймали ЗОЛОТОЙ сундук!" : "Вы поймали сундук!")
                ));
                // при желании: отправить клиенту пакет для GUI анимации сундука
            }

            return null;
        }
    }

}
package com.mconlykitchen.mconlykitchen.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Пакет отправляется когда игрок нажимает пробел на анимации поклёвки
 * Сервер помечает, что GUI открыто и больше не будет слать анимацию
 */
public class PacketSpacePressed implements IMessage {

    private int bobberEntityId;

    public PacketSpacePressed() {}

    public PacketSpacePressed(int bobberEntityId) {
        this.bobberEntityId = bobberEntityId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.bobberEntityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(bobberEntityId);
    }

    public static class Handler implements IMessageHandler<PacketSpacePressed, IMessage> {
        @Override
        public IMessage onMessage(PacketSpacePressed message, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            if (player == null || player.isDead) {
                return null;
            }

            // Находим поплавок по entityId
            Entity entity = player.worldObj.getEntityByID(message.bobberEntityId);
            if (!(entity instanceof com.mconlykitchen.mconlykitchen.entity.EntityCustomBobber)) {
                return null;
            }

            com.mconlykitchen.mconlykitchen.entity.EntityCustomBobber bobber =
                    (com.mconlykitchen.mconlykitchen.entity.EntityCustomBobber) entity;

            int rodTier = bobber.getRodTier();
            boolean isLava = player.worldObj.provider.isHellWorld;
            int fishTier = bobber.getFishTier();

            // Стартуем сессию на сервере
            com.mconlykitchen.mconlykitchen.fishing.FishingSessionManager.startSession(
                    player, rodTier, isLava, fishTier
            );

            // Отправляем пакет на открытие GUI
            com.mconlykitchen.mconlykitchen.network.NetworkHandler.INSTANCE.sendTo(
                    new com.mconlykitchen.mconlykitchen.network.PacketOpenFishingGUI(
                            rodTier, isLava, fishTier, bobber.getEntityId()
                    ),
                    player
            );

            return null;
        }
    }

}

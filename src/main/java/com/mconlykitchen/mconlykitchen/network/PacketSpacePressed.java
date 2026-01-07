package com.mconlykitchen.mconlykitchen.network;

import com.mconlykitchen.mconlykitchen.entity.EntityCustomBobber;
import com.mconlykitchen.mconlykitchen.fishing.FishingSessionManager;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketSpacePressed implements IMessage {

    private int bobberEntityId;

    public PacketSpacePressed() {}
    public PacketSpacePressed(int bobberEntityId) { this.bobberEntityId = bobberEntityId; }

    @Override
    public void fromBytes(ByteBuf buf) { this.bobberEntityId = buf.readInt(); }

    @Override
    public void toBytes(ByteBuf buf) { buf.writeInt(bobberEntityId); }

    public static class Handler implements IMessageHandler<PacketSpacePressed, IMessage> {
        @Override
        public IMessage onMessage(PacketSpacePressed message, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null || player.isDead) return null;

            Entity entity = player.worldObj.getEntityByID(message.bobberEntityId);
            if (!(entity instanceof EntityCustomBobber)) return null;

            EntityCustomBobber bobber = (EntityCustomBobber) entity;

            // Запускаем мини-игру
            if (bobber.getState() == EntityCustomBobber.State.BITE_ANIMATION) {
                bobber.startMiniGame();

                // Помечаем сессию активной
                FishingSessionManager.startSession(player, bobber);

                // Отправляем пакет на клиент для открытия GUI мини-игры
                NetworkHandler.INSTANCE.sendTo(
                        new com.mconlykitchen.mconlykitchen.network.PacketOpenFishingGUI(
                                bobber.getRodTier(),
                                player.worldObj.provider.isHellWorld,
                                bobber.getFishTier(),
                                bobber.getEntityId()
                        ),
                        player
                );
            }

            return null;
        }
    }
}

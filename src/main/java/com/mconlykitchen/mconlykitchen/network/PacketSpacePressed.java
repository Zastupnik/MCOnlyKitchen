package com.mconlykitchen.mconlykitchen.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
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
            // Серверная сторона
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            // В 1.7.10 серверный код уже выполняется в основном потоке,
            // поэтому просто выполняем действие сразу
            // Например, помечаем, что пробел нажат
            // (В реальном коде здесь можно вызвать логику окончания анимации или GUI)
            // player.getDataWatcher().updateObject(...);

            return null;
        }
    }
}

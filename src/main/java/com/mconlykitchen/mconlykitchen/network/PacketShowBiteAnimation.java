package com.mconlykitchen.mconlykitchen.network;

import com.mconlykitchen.mconlykitchen.client.BiteAnimationHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketShowBiteAnimation implements IMessage {

    private int rodTier;
    private boolean isLava;
    private int fishTier;
    private int bobberEntityId;

    public PacketShowBiteAnimation() {}

    public PacketShowBiteAnimation(int rodTier, boolean isLava, int fishTier, int bobberEntityId) {
        this.rodTier = rodTier;
        this.isLava = isLava;
        this.fishTier = fishTier;
        this.bobberEntityId = bobberEntityId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        rodTier = buf.readInt();
        isLava = buf.readBoolean();
        fishTier = buf.readInt();
        bobberEntityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(rodTier);
        buf.writeBoolean(isLava);
        buf.writeInt(fishTier);
        buf.writeInt(bobberEntityId);
    }

    public static class Handler implements IMessageHandler<PacketShowBiteAnimation, IMessage> {
        @Override
        public IMessage onMessage(final PacketShowBiteAnimation message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                // В 1.7.10 можно просто вызвать напрямую
                BiteAnimationHandler.showAnimation(
                        message.rodTier,
                        message.isLava,
                        message.fishTier,
                        message.bobberEntityId
                );
            }
            return null;
        }
    }
}

package com.mconlykitchen.fishingmod.network;

import com.mconlykitchen.fishingmod.gui.GuiFishingMiniGame;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class PacketStartFishing implements IMessage {

    private boolean lava;

    public PacketStartFishing() {}

    public PacketStartFishing(boolean lava) {
        this.lava = lava;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        lava = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(lava);
    }

    public static class Handler implements IMessageHandler<PacketStartFishing, IMessage> {
        @Override
        public IMessage onMessage(PacketStartFishing message, MessageContext ctx) {
            // открываем GUI на клиенте
            Minecraft.getMinecraft().displayGuiScreen(new GuiFishingMiniGame(message.lava));
            return null;
        }
    }
}

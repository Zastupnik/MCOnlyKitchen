package com.mconlykitchen.mconlykitchen.network;

import com.mconlykitchen.mconlykitchen.gui.GuiFishingMiniGame;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class PacketOpenFishingGUI implements IMessage {
    public int rodTier;
    public boolean isLava;
    public int fishTier;
    public int bobberEntityId;

    public PacketOpenFishingGUI() {}

    public PacketOpenFishingGUI(int rodTier, boolean isLava, int fishTier, int bobberEntityId) {
        this.rodTier = rodTier;
        this.isLava = isLava;
        this.fishTier = fishTier;
        this.bobberEntityId = bobberEntityId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.rodTier = buf.readInt();
        this.isLava = buf.readBoolean();
        this.fishTier = buf.readInt();
        this.bobberEntityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.rodTier);
        buf.writeBoolean(this.isLava);
        buf.writeInt(this.fishTier);
        buf.writeInt(this.bobberEntityId);
    }

    public static class Handler implements IMessageHandler<PacketOpenFishingGUI, IMessage> {
        @Override
        public IMessage onMessage(final PacketOpenFishingGUI msg, final MessageContext ctx) {
            if (ctx.side.isClient()) {
                Minecraft mc = Minecraft.getMinecraft();
                mc.displayGuiScreen(new GuiFishingMiniGame(
                        msg.rodTier, msg.isLava, msg.fishTier, msg.bobberEntityId
                ));
            }
            return null;
        }
    }
}

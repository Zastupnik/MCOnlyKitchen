package com.mconlykitchen.mconlykitchen.network;

import com.mconlykitchen.mconlykitchen.gui.GuiFishingMiniGame;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.client.FMLClientHandler;

public class PacketOpenFishingGUI implements IMessage {

    private int rodTier;
    private boolean isLava;
    private boolean gotChest;
    private boolean goldenChest;
    private int fishTier; // <- добавлено

    public PacketOpenFishingGUI() {}

    public PacketOpenFishingGUI(int rodTier, boolean isLava, boolean gotChest, boolean goldenChest, int fishTier) {
        this.rodTier = rodTier;
        this.isLava = isLava;
        this.gotChest = gotChest;
        this.goldenChest = goldenChest;
        this.fishTier = fishTier;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        rodTier = buf.readInt();
        isLava = buf.readBoolean();
        gotChest = buf.readBoolean();
        goldenChest = buf.readBoolean();
        fishTier = buf.readInt(); // <- добавлено
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(rodTier);
        buf.writeBoolean(isLava);
        buf.writeBoolean(gotChest);
        buf.writeBoolean(goldenChest);
        buf.writeInt(fishTier); // <- добавлено
    }

    public static class Handler implements IMessageHandler<PacketOpenFishingGUI, IMessage> {
        @Override
        public IMessage onMessage(final PacketOpenFishingGUI message, MessageContext ctx) {
            // GUI открываем **на клиенте**
            FMLClientHandler.instance().showGuiScreen(
                    new GuiFishingMiniGame(
                            message.rodTier,
                            message.isLava,
                            message.gotChest,
                            message.goldenChest,
                            message.fishTier
                    )
            );
            return null;
        }
    }
}

package com.mconlykitchen.mconlykitchen.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import com.mconlykitchen.mconlykitchen.fishing.FishingSessionManager;

public class PacketFishingInput implements IMessage {

    public enum Action { SUCCESS, FAIL }

    private Action action;
    private boolean gotChest;
    private boolean isGoldenChest;
    private int rodTier;
    private boolean isLava;

    public PacketFishingInput() {}

    public PacketFishingInput(Action action, boolean gotChest, boolean isGoldenChest, int rodTier, boolean isLava) {
        this.action = action;
        this.gotChest = gotChest;
        this.isGoldenChest = isGoldenChest;
        this.rodTier = rodTier;
        this.isLava = isLava;
    }

    public boolean hasChest() { return gotChest; }
    public boolean isGoldenChest() { return isGoldenChest; }

    @Override
    public void fromBytes(ByteBuf buf) {
        action = Action.values()[buf.readInt()];
        gotChest = buf.readBoolean();
        isGoldenChest = buf.readBoolean();
        rodTier = buf.readInt();
        isLava = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(action.ordinal());
        buf.writeBoolean(gotChest);
        buf.writeBoolean(isGoldenChest);
        buf.writeInt(rodTier);
        buf.writeBoolean(isLava);
    }

    public static class Handler implements IMessageHandler<PacketFishingInput, IMessage> {
        @Override
        public IMessage onMessage(PacketFishingInput message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            FishingSessionManager.handleFishingInput(player, message);
            return null;
        }
    }
}

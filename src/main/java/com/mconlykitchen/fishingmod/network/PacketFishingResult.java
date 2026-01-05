package com.mconlykitchen.fishingmod.network;

import com.mconlykitchen.fishingmod.minigame.LootHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketFishingResult implements IMessage {

    private boolean success;
    private boolean lava;
    private boolean chest;

    public PacketFishingResult() {}

    public PacketFishingResult(boolean success, boolean lava, boolean chest) {
        this.success = success;
        this.lava = lava;
        this.chest = chest;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        success = buf.readBoolean();
        lava = buf.readBoolean();
        chest = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(success);
        buf.writeBoolean(lava);
        buf.writeBoolean(chest);
    }

    public static class Handler implements IMessageHandler<PacketFishingResult, IMessage> {
        @Override
        public IMessage onMessage(PacketFishingResult message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            if (message.success) {
                if (message.chest) {
                    LootHandler.giveChestLoot(player, message.lava);
                } else {
                    LootHandler.giveFish(player, message.lava);
                }
            } else {
                player.addChatMessage(new net.minecraft.util.ChatComponentText("Рыба сорвалась..."));
            }

            return null;
        }
    }
}

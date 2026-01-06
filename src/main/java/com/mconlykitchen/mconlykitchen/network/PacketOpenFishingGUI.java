package com.mconlykitchen.mconlykitchen.network;

import com.mconlykitchen.mconlykitchen.gui.GuiFishingMiniGame;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

/**
 * Пакет для открытия GUI мини-игры рыбалки: сервер → клиент
 * НЕ содержит информацию о награде (защита от читеров)
 */
public class PacketOpenFishingGUI implements IMessage {

    private int rodTier;
    private boolean isLava;
    private int fishTier;
    private int bobberEntityId; // теперь нужен

    public PacketOpenFishingGUI() {}

    /** Конструктор пакета (только визуальная информация) */
    public PacketOpenFishingGUI(int rodTier, boolean isLava, int fishTier, int bobberEntityId) {
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
        bobberEntityId = buf.readInt(); // читаем четвертый
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(rodTier);
        buf.writeBoolean(isLava);
        buf.writeInt(fishTier);
        buf.writeInt(bobberEntityId); // пишем четвертый
    }

    public static class Handler implements IMessageHandler<PacketOpenFishingGUI, IMessage> {
        @Override
        public IMessage onMessage(final PacketOpenFishingGUI message, MessageContext ctx) {
            // В 1.7.10 можно напрямую вызвать на клиенте
            Minecraft.getMinecraft().displayGuiScreen(
                    new GuiFishingMiniGame(
                            message.rodTier,
                            message.isLava,
                            message.fishTier,
                            message.bobberEntityId
                    )
            );
            return null;
        }
    }
}

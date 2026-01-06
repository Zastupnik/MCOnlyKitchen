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

    public PacketOpenFishingGUI() {}

    /**
     * Конструктор пакета (только визуальная информация)
     * @param rodTier - тир удочки (для визуала)
     * @param isLava - адская рыбалка или нет (для визуала)
     * @param fishTier - тир рыбы (для визуала сложности)
     */
    public PacketOpenFishingGUI(int rodTier, boolean isLava, int fishTier) {
        this.rodTier = rodTier;
        this.isLava = isLava;
        this.fishTier = fishTier;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.rodTier = buf.readInt();
        this.isLava = buf.readBoolean();
        this.fishTier = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(rodTier);
        buf.writeBoolean(isLava);
        buf.writeInt(fishTier);
    }

    public static class Handler implements IMessageHandler<PacketOpenFishingGUI, IMessage> {
        @Override
        public IMessage onMessage(final PacketOpenFishingGUI message, MessageContext ctx) {
            // В 1.7.10 просто вызываем напрямую - клиентские пакеты обрабатываются в нужном потоке
            Minecraft.getMinecraft().displayGuiScreen(
                    new GuiFishingMiniGame(
                            message.rodTier,
                            message.isLava,
                            message.fishTier
                    )
            );
            return null;
        }
    }
}
package com.mconlykitchen.mconlykitchen.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiChestReward extends GuiScreen {

    private static final ResourceLocation CHEST_TEXTURE =
            new ResourceLocation("mconlykitchen", "textures/gui/chest.png");
    private static final ResourceLocation GOLDEN_TEXTURE =
            new ResourceLocation("mconlykitchen", "textures/gui/chest_golden.png");

    private static final int FRAME_SIZE = 128;
    private static final int FRAME_COUNT = 10;
    private static final int TEXTURE_WIDTH = 128;
    private static final int TEXTURE_HEIGHT = 1280;

    private final boolean isGolden;
    private final ItemStack rewardItem;
    private final RenderItem itemRender = new RenderItem();

    private int ticksExisted = 0;

    public GuiChestReward(boolean isGolden, ItemStack rewardItem) {
        this.isGolden = isGolden;
        this.rewardItem = rewardItem;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        ticksExisted++;

        // 10 кадров * 4 тика = 40 тиков анимации + 20 тиков показ = 60 тиков (3 сек)
        if (ticksExisted > 60) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Затемнённый фон
        this.drawDefaultBackground();

        // Биндим нужную текстуру
        mc.getTextureManager().bindTexture(isGolden ? GOLDEN_TEXTURE : CHEST_TEXTURE);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // ЦЕНТРИРУЕМ на экране
        int x = (this.width - FRAME_SIZE) / 2;
        int y = (this.height - FRAME_SIZE) / 2;

        // Вычисляем текущий кадр (4 тика на кадр = плавно)
        int currentFrame = Math.min(ticksExisted / 4, FRAME_COUNT - 1);

        // Рисуем ОДИН кадр с правильными UV-координатами
        drawChestFrame(x, y, currentFrame);

        // Предмет появляется после 20 тиков (середина анимации)
        if (rewardItem != null && ticksExisted >= 20) {
            drawRewardItem(x, y);
        }

        GL11.glDisable(GL11.GL_BLEND);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Рисует один кадр анимации сундука
     */
    private void drawChestFrame(int x, int y, int frame) {
        // UV координаты в текстуре
        float u = 0.0F; // всегда 0, т.к. ширина текстуры = ширине кадра
        float v = (float)(frame * FRAME_SIZE) / (float)TEXTURE_HEIGHT; // от 0.0 до 0.9
        float u2 = (float)FRAME_SIZE / (float)TEXTURE_WIDTH; // = 1.0
        float v2 = (float)((frame + 1) * FRAME_SIZE) / (float)TEXTURE_HEIGHT;

        // Рисуем кадр используя Tessellator для точного контроля UV
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + FRAME_SIZE, 0, u, v2);
        tessellator.addVertexWithUV(x + FRAME_SIZE, y + FRAME_SIZE, 0, u2, v2);
        tessellator.addVertexWithUV(x + FRAME_SIZE, y, 0, u2, v);
        tessellator.addVertexWithUV(x, y, 0, u, v);
        tessellator.draw();
    }

    /**
     * Рисует предмет-награду
     */
    private void drawRewardItem(int chestX, int chestY) {
        RenderHelper.enableGUIStandardItemLighting();

        // Предмет в центре сундука, чуть выше
        int itemX = chestX + FRAME_SIZE / 2 - 8;
        int itemY = chestY + FRAME_SIZE / 2 - 24;

        // Плавное появление (5 тиков fade-in)
        float fadeProgress = Math.min((ticksExisted - 20) / 5.0F, 1.0F);

        // Эффект всплытия
        float floatOffset = (1.0F - fadeProgress) * 8.0F;

        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, fadeProgress);

        itemRender.renderItemAndEffectIntoGUI(
                this.fontRendererObj,
                this.mc.getTextureManager(),
                rewardItem,
                itemX,
                (int)(itemY - floatOffset)
        );

        // Количество
        if (rewardItem.stackSize > 1) {
            itemRender.renderItemOverlayIntoGUI(
                    this.fontRendererObj,
                    this.mc.getTextureManager(),
                    rewardItem,
                    itemX,
                    (int)(itemY - floatOffset),
                    String.valueOf(rewardItem.stackSize)
            );
        }

        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        RenderHelper.disableStandardItemLighting();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
package com.mconlykitchen.mconlykitchen.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
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

        // 10 кадров * 4 тика = 40 тиков анимации + 20 тиков показа = 60 тиков
        if (ticksExisted > 60) {
            mc.displayGuiScreen(null);
            if (mc.thePlayer != null) {
                mc.thePlayer.getEntityData().setBoolean("FishingLock", false);
            }
        }

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        mc.getTextureManager().bindTexture(isGolden ? GOLDEN_TEXTURE : CHEST_TEXTURE);

        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int x = (width - FRAME_SIZE) / 2;
        int y = (height - FRAME_SIZE) / 2;

        int currentFrame = Math.min(ticksExisted / 4, FRAME_COUNT - 1);
        drawChestFrame(x, y, currentFrame);

        // Предмет появляется строго на последнем кадре
        if (rewardItem != null && currentFrame == FRAME_COUNT - 1) {
            drawRewardItem(x, y);
        }

        GL11.glDisable(GL11.GL_BLEND);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawChestFrame(int x, int y, int frame) {
        float u = 0f;
        float v = (float)(frame * FRAME_SIZE) / TEXTURE_HEIGHT;
        float u2 = (float)FRAME_SIZE / TEXTURE_WIDTH;
        float v2 = (float)((frame + 1) * FRAME_SIZE) / TEXTURE_HEIGHT;

        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertexWithUV(x, y + FRAME_SIZE, 0, u, v2);
        tess.addVertexWithUV(x + FRAME_SIZE, y + FRAME_SIZE, 0, u2, v2);
        tess.addVertexWithUV(x + FRAME_SIZE, y, 0, u2, v);
        tess.addVertexWithUV(x, y, 0, u, v);
        tess.draw();
    }

    private void drawRewardItem(int chestX, int chestY) {
        RenderHelper.enableGUIStandardItemLighting();

        int itemX = chestX + FRAME_SIZE / 2 - 8;
        int itemY = chestY + FRAME_SIZE / 2 - 8;

        GL11.glPushMatrix();
        GL11.glColor4f(1f, 1f, 1f, 1f);

        itemRender.renderItemAndEffectIntoGUI(
                fontRendererObj,
                mc.getTextureManager(),
                rewardItem,
                itemX,
                itemY
        );

        if (rewardItem.stackSize > 1) {
            itemRender.renderItemOverlayIntoGUI(
                    fontRendererObj,
                    mc.getTextureManager(),
                    rewardItem,
                    itemX,
                    itemY,
                    String.valueOf(rewardItem.stackSize)
            );
        }

        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

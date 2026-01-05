package com.mconlykitchen.fishingmod.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderHelper {

    /** Отрисовка прямоугольника с цветом */
    public static void drawRect(int x1, int y1, int x2, int y2, int color) {
        Gui.drawRect(x1, y1, x2, y2, color);
    }

    /** Отрисовка текстуры (нужен экземпляр Gui) */
    public static void drawTextured(Gui gui, ResourceLocation texture, int x, int y, int u, int v, int w, int h) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        gui.drawTexturedModalRect(x, y, u, v, w, h);
    }

    /** Отрисовка текстуры с масштабом */
    public static void drawScaled(Gui gui, ResourceLocation texture, int x, int y, int w, int h, float scale) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(scale, scale, 1.0F);
        gui.drawTexturedModalRect(0, 0, 0, 0, w, h);
        GL11.glPopMatrix();
    }
}

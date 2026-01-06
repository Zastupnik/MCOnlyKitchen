package com.mconlykitchen.mconlykitchen.client.render;

import com.mconlykitchen.mconlykitchen.entity.EntityCustomBobber;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Рендер кастомного поплавка с леской
 */
public class RenderCustomBobber extends Render {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("textures/particle/particles.png");

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        EntityCustomBobber bobber = (EntityCustomBobber) entity;

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        // Рендер поплавка (маленький белый куб)
        this.bindEntityTexture(bobber);
        Tessellator tessellator = Tessellator.instance;

        float size = 0.0625F; // 1/16 блока

        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.addVertexWithUV(-size, -size, 0.0D, 0.0D, 0.0D);
        tessellator.addVertexWithUV(-size, size, 0.0D, 0.0F, 1.0F);
        tessellator.addVertexWithUV(size, size, 0.0D, 1.0F, 1.0F);
        tessellator.addVertexWithUV(size, -size, 0.0D, 1.0F, 0.0F);
        tessellator.draw();

        GL11.glPopMatrix();

        // Рендер лески
        EntityPlayer player = bobber.getOwner();
        if (player != null) {
            renderFishingLine(bobber, player, x, y, z, partialTicks);
        }
    }

    /**
     * Рендер лески от руки игрока до поплавка
     */
    private void renderFishingLine(EntityCustomBobber bobber, EntityPlayer player,
                                   double x, double y, double z, float partialTicks) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);

        // Позиция глаза игрока + смещение для руки
        double playerX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        double playerY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks + player.getEyeHeight();
        double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;

        double handOffsetX = -MathHelper.sin(player.renderYawOffset / 180.0F * (float)Math.PI) * 0.35;
        double handOffsetZ = MathHelper.cos(player.renderYawOffset / 180.0F * (float)Math.PI) * 0.35;

        playerX += handOffsetX;
        playerZ += handOffsetZ;
        playerY -= 0.1; // чуть ниже глаза

        // Позиция поплавка с интерполяцией
        double bobberX = bobber.prevPosX + (bobber.posX - bobber.prevPosX) * partialTicks;
        double bobberY = bobber.prevPosY + (bobber.posY - bobber.prevPosY) * partialTicks + 0.25;
        double bobberZ = bobber.prevPosZ + (bobber.posZ - bobber.prevPosZ) * partialTicks;

        // Относительные координаты лески
        double dx = playerX - bobberX;
        double dy = playerY - bobberY;
        double dz = playerZ - bobberZ;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(3); // GL_LINE_STRIP
        tessellator.setColorOpaque_I(0x000000); // чёрная леска

        int segments = 16;
        for (int i = 0; i <= segments; i++) {
            float f = (float)i / (float)segments;
            double lerpX = dx * f;
            double lerpY = dy * f - f * (1.0F - f) * 0.5; // провисание
            double lerpZ = dz * f;
            tessellator.addVertex(lerpX, lerpY, lerpZ);
        }

        tessellator.draw();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURE;
    }
}

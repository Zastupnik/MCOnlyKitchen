package com.mconlykitchen.mconlykitchen.client.render;

import com.mconlykitchen.mconlykitchen.entity.EntityCustomBobber;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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

        // Рендер поплавка (маленький белый квадратик)
        this.bindEntityTexture(bobber);
        Tessellator tess = Tessellator.instance;

        float size = 0.0625F; // 1/16 блока

        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        tess.startDrawingQuads();
        tess.setNormal(0.0F, 1.0F, 0.0F);
        tess.addVertexWithUV(-size, -size, 0.0D, 0.0D, 0.0D);
        tess.addVertexWithUV(-size,  size, 0.0D, 0.0F, 1.0F);
        tess.addVertexWithUV( size,  size, 0.0D, 1.0F, 1.0F);
        tess.addVertexWithUV( size, -size, 0.0D, 1.0F, 0.0F);
        tess.draw();

        GL11.glPopMatrix();

        // Рендер лески
        EntityPlayer player = bobber.getOwner();
        if (player != null) {
            renderFishingLine(bobber, player, x, y, z, partialTicks);
        }
    }

    /**
     * Леска от руки игрока до поплавка
     */
    private void renderFishingLine(EntityCustomBobber bobber,
                                   EntityPlayer player,
                                   double x, double y, double z,
                                   float partialTicks) {

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);

        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
        float yawp  = player.prevRotationYaw   + (player.rotationYaw   - player.prevRotationYaw) * partialTicks;

        double cosYaw   = net.minecraft.util.MathHelper.cos(-yawp * (float)Math.PI / 180F - (float)Math.PI);
        double sinYaw   = net.minecraft.util.MathHelper.sin(-yawp * (float)Math.PI / 180F - (float)Math.PI);
        double cosPitch = -net.minecraft.util.MathHelper.cos(-pitch * (float)Math.PI / 180F);
        double sinPitch = net.minecraft.util.MathHelper.sin(-pitch * (float)Math.PI / 180F);

        double handX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks
                - cosYaw * 0.35D - sinYaw * 0.8D;
        double handY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks
                + player.getEyeHeight() - cosPitch * 0.45D;
        double handZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks
                - sinYaw * 0.35D + cosYaw * 0.8D;

        double bx = bobber.prevPosX + (bobber.posX - bobber.prevPosX) * partialTicks;
        double by = bobber.prevPosY + (bobber.posY - bobber.prevPosY) * partialTicks + 0.1D;
        double bz = bobber.prevPosZ + (bobber.posZ - bobber.prevPosZ) * partialTicks;

        Tessellator tess = Tessellator.instance;
        tess.startDrawing(GL11.GL_LINE_STRIP);
        tess.setColorOpaque_I(0x202020);

        for (int i = 0; i <= 16; i++) {
            float t = i / 16.0F;
            double sx = handX + (bx - handX) * t - this.renderManager.viewerPosX;
            double sy = handY + (by - handY) * t + Math.sin(t * Math.PI) * 0.05D - this.renderManager.viewerPosY;
            double sz = handZ + (bz - handZ) * t - this.renderManager.viewerPosZ;
            tess.addVertex(sx, sy, sz);
        }
        tess.draw();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURE;
    }
}

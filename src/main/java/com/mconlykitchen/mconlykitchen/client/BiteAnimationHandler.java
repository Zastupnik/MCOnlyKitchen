package com.mconlykitchen.mconlykitchen.client;

import com.mconlykitchen.mconlykitchen.MCOnlyKitchen;
import com.mconlykitchen.mconlykitchen.network.NetworkHandler;
import com.mconlykitchen.mconlykitchen.network.PacketSpacePressed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class BiteAnimationHandler {

    private static final ResourceLocation SPACE_ANIMATION =
            new ResourceLocation(MCOnlyKitchen.MODID, "textures/gui/space_button.png");

    private static boolean isAnimating = false;
    private static int animationTick = 0;

    private static int rodTier;
    private static boolean isLava;
    private static int fishTier;
    private static int bobberEntityId;

    private static final int ANIM_WIDTH = 128;
    private static final int ANIM_HEIGHT = 32;
    private static final int FRAME_TIME = 5;

    private static boolean spacePressed = false;

    public static void showAnimation(int tier, boolean lava, int fish, int entityId) {
        isAnimating = true;
        animationTick = 0;
        rodTier = tier;
        isLava = lava;
        fishTier = fish;
        bobberEntityId = entityId;
        spacePressed = false;
    }

    public static void tick() {
        if (!isAnimating) return;

        animationTick++;

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !spacePressed) {
            spacePressed = true;

            // ❗ только планируем открытие GUI
            ClientEventHandler.scheduleOpenFishingGui(
                    rodTier,
                    isLava,
                    fishTier,
                    bobberEntityId
            );

            // пакет на сервер
            NetworkHandler.INSTANCE.sendToServer(
                    new PacketSpacePressed(bobberEntityId)
            );

            isAnimating = false;
        }

        // авто-закрытие
        if (animationTick > 60) {
            isAnimating = false;
        }
    }

    public static void render(ScaledResolution resolution) {
        if (!isAnimating) return;

        Minecraft mc = Minecraft.getMinecraft();

        int frame = (animationTick / FRAME_TIME) % 4;
        int x = (resolution.getScaledWidth() - ANIM_WIDTH) / 2;
        int y = (resolution.getScaledHeight() - ANIM_HEIGHT) / 2;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        mc.getTextureManager().bindTexture(SPACE_ANIMATION);

        Gui.func_146110_a(
                x, y,
                frame * ANIM_WIDTH, 0,
                ANIM_WIDTH, ANIM_HEIGHT,
                512, 32
        );

        GL11.glDisable(GL11.GL_BLEND);
    }

    public static boolean isAnimating() {
        return isAnimating;
    }

    // ✅ ВОТ ЭТОГО НЕ ХВАТАЛО
    public static void reset() {
        isAnimating = false;
        animationTick = 0;
        spacePressed = false;
    }
}

package com.mconlykitchen.mconlykitchen.client;

import com.mconlykitchen.mconlykitchen.config.ModConfig;
import com.mconlykitchen.mconlykitchen.gui.GuiFishingMiniGame;
import com.mconlykitchen.mconlykitchen.network.NetworkHandler;
import com.mconlykitchen.mconlykitchen.network.PacketSpacePressed;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.settings.KeyBinding;

@SideOnly(Side.CLIENT)
public class BiteAnimationHandler {

    private static final ResourceLocation SPACE_ANIMATION =
            new ResourceLocation("mconlykitchen", "textures/gui/space_button.png");

    private static boolean isAnimating = false;
    private static boolean waitingForPress = false;
    private static boolean spacePressed = false;

    private static int animationTick = 0;
    private static int waitTicks = 0;

    private static int rodTier;
    private static boolean isLava;
    private static int fishTier;
    private static int bobberEntityId;

    private static int biteTimeTicks = 60;
    private static final int WAIT_DURATION = 100;

    private static final int ANIM_WIDTH = 128;
    private static final int ANIM_HEIGHT = 32;
    private static final int FRAME_TIME = 5;
    private static final int MAX_FRAMES = 4;

    /** Запуск анимации */
    public static void showAnimation(int tier, boolean lava, int fish, int entityId) {
        rodTier = Math.max(0, Math.min(6, tier));
        isLava = lava;
        fishTier = fish;
        bobberEntityId = entityId;

        biteTimeTicks = Math.max(1, ModConfig.getBiteTimeByTier(rodTier)) * 20;

        isAnimating = true;
        waitingForPress = false;
        spacePressed = false;
        animationTick = 0;
        waitTicks = 0;
    }

    /** Тик клиента */
    public static void tick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        // Если GUI уже открыт — полностью выходим
        if (mc.currentScreen instanceof GuiFishingMiniGame) {
            reset();
            return;
        }

        if (isAnimating || waitingForPress) {
            KeyBinding.setKeyBindState(
                    mc.gameSettings.keyBindJump.getKeyCode(),
                    false
            );
        }


        // Фаза анимации поклёвки
        if (isAnimating) {
            animationTick++;
            if (animationTick >= biteTimeTicks) {
                isAnimating = false;
                waitingForPress = true;
                waitTicks = 0;
            }
            return;
        }

        // Фаза ожидания пробела
        if (waitingForPress) {
            waitTicks++;

// SPACE может сработать И во время анимации, И во время ожидания
            if ((isAnimating || waitingForPress) && !spacePressed && Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {

                spacePressed = true;

                // Гасим прыжок ЖЁСТКО
                KeyBinding.setKeyBindState(
                        mc.gameSettings.keyBindJump.getKeyCode(),
                        false
                );

                NetworkHandler.INSTANCE.sendToServer(
                        new PacketSpacePressed(bobberEntityId)
                );

                ClientEventHandler.scheduleOpenFishingGui(
                        rodTier, isLava, fishTier, bobberEntityId
                );

                reset();
                return;
            }


            // Таймаут — отмена
            if (waitTicks > WAIT_DURATION) {
                reset();
            }
        }
    }

    /** Рендер (ТОЛЬКО ОТРИСОВКА!) */
    public static void render(ScaledResolution resolution) {
        if (!isAnimating && !waitingForPress) return;

        Minecraft mc = Minecraft.getMinecraft();
        int frame = (animationTick / FRAME_TIME) % MAX_FRAMES;

        int x = (resolution.getScaledWidth() - ANIM_WIDTH) / 2;
        int y = (resolution.getScaledHeight() - ANIM_HEIGHT) / 2;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1f, 1f, 1f, 1f);

        mc.getTextureManager().bindTexture(SPACE_ANIMATION);
        net.minecraft.client.gui.Gui.func_146110_a(
                x, y, frame * ANIM_WIDTH, 0,
                ANIM_WIDTH, ANIM_HEIGHT,
                512, 32
        );

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void reset() {
        isAnimating = false;
        waitingForPress = false;
        spacePressed = false;
        animationTick = 0;
        waitTicks = 0;
    }

    public static boolean isAnimating() {
        return isAnimating || waitingForPress;
    }
}


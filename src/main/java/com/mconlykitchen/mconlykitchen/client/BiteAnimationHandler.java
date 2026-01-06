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

@SideOnly(Side.CLIENT)
public class BiteAnimationHandler {

    private static final ResourceLocation SPACE_ANIMATION =
            new ResourceLocation("mconlykitchen", "textures/gui/space_button.png");

    private static boolean isAnimating = false;
    private static int animationTick = 0;

    private static int rodTier;
    private static boolean isLava;
    private static int fishTier;
    private static int bobberEntityId;

    private static int biteTimeTicks = 60; // тики поклёвки

    private static boolean waitingForPress = false; // ждём пробел для старта мини-игры
    private static boolean spacePressed = false; // зафиксирован пробел
    private static int waitTicks = 0;
    private static final int WAIT_DURATION = 100; // 5 секунд на пробел

    private static final int ANIM_WIDTH = 128;
    private static final int ANIM_HEIGHT = 32;
    private static final int FRAME_TIME = 5; // 5 тиков на кадр
    private static final int MAX_FRAMES = 4;

    /** Запуск анимации поклёвки */
    public static void showAnimation(int tier, boolean lava, int fish, int entityId) {
        rodTier = tier;
        isLava = lava;
        fishTier = fish;
        bobberEntityId = entityId;

        // Универсальная удочка = фиксированные 5 секунд
        if (tier == 4) {
            biteTimeTicks = 5 * 20;
        } else {
            biteTimeTicks = ModConfig.getBiteTime(rodTier, lava) * 20;
        }

        isAnimating = true;
        animationTick = 0;
        waitingForPress = false;
        spacePressed = false;
        waitTicks = 0;
    }

    /** Тик анимации, вызывается из ClientTickHandler */
    public static void tick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        // Блокировка движения игрока
        if (isAnimating || waitingForPress) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
            mc.thePlayer.motionY = 0;
            mc.thePlayer.jumpMovementFactor = 0;
        }

        // Анимация поклёвки
        if (isAnimating) {
            animationTick++;

            // Время поклёвки истекло — ждём пробел
            if (animationTick >= biteTimeTicks) {
                isAnimating = false;
                waitingForPress = true;
                waitTicks = 0;
            }
        }

        // Таймер ожидания пробела
        if (waitingForPress) {
            waitTicks++;

            // Если прошло время ожидания без нажатия — сбрасываем
            if (waitTicks > WAIT_DURATION) {
                waitingForPress = false;
                spacePressed = false;
            }
        }
    }

    /** Рендер иконки SPACE */
    public static void render(ScaledResolution resolution) {
        if (!isAnimating && !waitingForPress) return;

        Minecraft mc = Minecraft.getMinecraft();
        int frame = (animationTick / FRAME_TIME) % MAX_FRAMES;

        int x = (resolution.getScaledWidth() - ANIM_WIDTH) / 2;
        int y = (resolution.getScaledHeight() - ANIM_HEIGHT) / 2;

        GL11.glPushMatrix();
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        mc.getTextureManager().bindTexture(SPACE_ANIMATION);
        // Текстура 4 кадра по 128x32 = 512x32
        net.minecraft.client.gui.Gui.func_146110_a(x, y, frame * ANIM_WIDTH, 0, ANIM_WIDTH, ANIM_HEIGHT, 512, 32);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();

        // Проверяем пробел здесь, чтобы GUI открывался только при рендере
        if (waitingForPress && !spacePressed && Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            spacePressed = true;

            // Отправляем серверу
            NetworkHandler.INSTANCE.sendToServer(new PacketSpacePressed(bobberEntityId));

            // Открываем GUI мини-игры
            mc.displayGuiScreen(new GuiFishingMiniGame(rodTier, isLava, fishTier, bobberEntityId));

            waitingForPress = false;
        }

        // ESC закрывает анимацию
        if (waitingForPress && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            waitingForPress = false;
        }
    }

    /** Проверка, активна ли анимация */
    public static boolean isAnimating() {
        return isAnimating || waitingForPress;
    }
}

package com.mconlykitchen.fishingmod.gui;

import com.mconlykitchen.fishingmod.FishingMod;
import com.mconlykitchen.fishingmod.logic.FishingMiniGameLogic;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiFishingMiniGame extends GuiScreen {

    public static final int GUI_ID = 1;

    private static final ResourceLocation NORMAL_TEXTURE =
            new ResourceLocation(FishingMod.MODID, "textures/gui/minigame.png");
    private static final ResourceLocation NETHER_TEXTURE =
            new ResourceLocation(FishingMod.MODID, "textures/gui/minigame_nether.png");

    /* ---------- MAIN PANEL ---------- */
    private static final int PANEL_W = 38;
    private static final int PANEL_H = 152;
    private static final int PANEL_U = 0;
    private static final int PANEL_V = 0;

    /* ---------- BOBBER (GREEN BAR) ---------- */
    private static final int BOBBER_U = 38;
    private static final int BOBBER_V_TOP = 0;      // верхняя часть (2px)
    private static final int BOBBER_V_MID = 2;      // средняя часть (1px, повторяющаяся)
    private static final int BOBBER_V_BOT = 3;      // нижняя часть (2px)
    private static final int BOBBER_W = 9;

    /* ---------- FISH ICON ---------- */
    private static final int FISH_U = 55;
    private static final int FISH_V = 0;
    private static final int FISH_W = 16;
    private static final int FISH_H = 15;

    /* ---------- REEL HANDLE ---------- */
    private static final int HANDLE_U = 47;
    private static final int HANDLE_V = 0;
    private static final int HANDLE_W = 8;
    private static final int HANDLE_H = 3;

    /* ---------- HIT TEXT ---------- */
    private static final int HIT_U = 71;
    private static final int HIT_V = 0;
    private static final int HIT_W = 73;
    private static final int HIT_H = 29;

    /* ---------- PERFECT TEXT ---------- */
    private static final int PERFECT_U = 144;
    private static final int PERFECT_V = 0;
    private static final int PERFECT_W = 41;
    private static final int PERFECT_H = 12;

    /* ---------- CHANNEL (игровая зона внутри панели) ---------- */
    private static final int CHANNEL_X = 18;     // смещение от левого края панели
    private static final int CHANNEL_Y = 4;      // смещение от верхнего края панели
    private static final int CHANNEL_H = 142;    // высота игровой зоны

    /* ---------- STATE ---------- */
    private final boolean lava;
    private FishingMiniGameLogic logic;

    private boolean finished;
    private int hitTimer;
    private boolean showPerfect;
    private boolean mouseDown;

    public GuiFishingMiniGame(boolean lava) {
        this.lava = lava;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        logic = new FishingMiniGameLogic(0, CHANNEL_H);
        finished = false;
        hitTimer = 0;
        showPerfect = false;
        mouseDown = false;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (finished) return;

        if (keyCode == Keyboard.KEY_SPACE || keyCode == Keyboard.KEY_W) {
            if (!mouseDown) {
                logic.press();
                mouseDown = true;
            }
        } else if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (finished) return;

        if (button == 0 || button == 1) { // левая или правая кнопка мыши
            if (!mouseDown) {
                logic.press();
                mouseDown = true;
            }
        }
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        if (button == 0 || button == 1) {
            mouseDown = false;
        }
    }

    @Override
    public void updateScreen() {
        if (finished) return;

        logic.tick(lava);

        // HIT появляется только при первом совпадении
        if (logic.isOverlap() && hitTimer == 0) {
            hitTimer = 15;
        }

        if (hitTimer > 0) hitTimer--;

        if (logic.isSuccess()) {
            showPerfect = true;
            onSuccess();
        }

        if (logic.isFailed()) {
            onFail();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        /* ---------- ANCHOR POSITION ---------- */
        int guiX = width / 2 - PANEL_W / 2;
        int guiY = height / 2 - PANEL_H / 2;

        mc.getTextureManager().bindTexture(lava ? NETHER_TEXTURE : NORMAL_TEXTURE);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        /* ---------- MAIN PANEL ---------- */
        drawTexturedModalRect(guiX, guiY, PANEL_U, PANEL_V, PANEL_W, PANEL_H);

        /* ---------- GREEN BOBBER BAR ---------- */
        int barSize = logic.getBarSize();
        int bobberY = guiY + CHANNEL_Y + logic.getBarY();

        // Верхняя часть бобера (2px)
        drawTexturedModalRect(guiX + CHANNEL_X, bobberY, BOBBER_U, BOBBER_V_TOP, BOBBER_W, 2);

        // Средняя часть бобера (повторяющаяся, 1px высота)
        for (int i = 0; i < barSize - 4; i++) {
            drawTexturedModalRect(guiX + CHANNEL_X, bobberY + 2 + i, BOBBER_U, BOBBER_V_MID, BOBBER_W, 1);
        }

        // Нижняя часть бобера (2px)
        drawTexturedModalRect(guiX + CHANNEL_X, bobberY + barSize - 2, BOBBER_U, BOBBER_V_BOT, BOBBER_W, 2);

        /* ---------- FISH ICON ---------- */
        int fishY = guiY + CHANNEL_Y + logic.getFishY();
        drawTexturedModalRect(guiX + CHANNEL_X - 2, fishY, FISH_U, FISH_V, FISH_W, FISH_H);

        /* ---------- PROGRESS BAR (справа от панели) ---------- */
        int progress = logic.getProgress();
        int progHeight = (int)((progress / 100.0f) * 145.0f);
        int progColor = getProgressColor(progress);

        // Прогресс бар рисуется справа от панели, внизу вверх
        int progX = guiX + PANEL_W - 5;
        int progY = guiY + 148;

        drawRect(progX, progY - progHeight, progX + 4, progY, progColor);

        /* ---------- REEL HANDLE (катушка) ---------- */
        // Рисуется слева внизу панели
        drawTexturedModalRect(guiX + 5, guiY + 129, HANDLE_U, HANDLE_V, HANDLE_W, HANDLE_H);

        /* ---------- HIT TEXT ---------- */
        if (hitTimer > 0) {
            float scale = 1.5f;
            if (hitTimer > 10) {
                scale = 0.5f + (hitTimer - 10) * 0.2f; // появление
            } else if (hitTimer < 5) {
                scale = hitTimer * 0.3f; // исчезновение
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(guiX - 16, guiY - 40, 0);
            GL11.glScalef(scale, scale, 1.0f);
            drawTexturedModalRect(0, 0, HIT_U, HIT_V, HIT_W, HIT_H);
            GL11.glPopMatrix();
        }

        /* ---------- PERFECT TEXT ---------- */
        if (showPerfect) {
            drawTexturedModalRect(guiX - 2, guiY - 20, PERFECT_U, PERFECT_V, PERFECT_W, PERFECT_H);
        }

        GL11.glDisable(GL11.GL_BLEND);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private int getProgressColor(int progress) {
        // Зелёный цвет: от тёмного к яркому
        float ratio = Math.min(progress / 100.0f, 1.0f);
        int green = (int)(ratio * 255);
        return 0xFF000000 | (green << 8);
    }

    private void onSuccess() {
        finished = true;
        mc.thePlayer.addChatMessage(
                new ChatComponentText(I18n.format("gui.fishing.success"))
        );
        mc.displayGuiScreen(null);
    }

    private void onFail() {
        finished = true;
        mc.thePlayer.addChatMessage(
                new ChatComponentText(I18n.format("gui.fishing.escape"))
        );
        mc.displayGuiScreen(null);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
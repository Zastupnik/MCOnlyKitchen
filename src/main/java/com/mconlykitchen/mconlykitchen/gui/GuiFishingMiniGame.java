package com.mconlykitchen.mconlykitchen.gui;

import com.mconlykitchen.mconlykitchen.MCOnlyKitchen;
import com.mconlykitchen.mconlykitchen.config.ModConfig;
import com.mconlykitchen.mconlykitchen.logic.FishingMiniGameLogic;
import com.mconlykitchen.mconlykitchen.network.NetworkHandler;
import com.mconlykitchen.mconlykitchen.network.PacketFishingResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiFishingMiniGame extends GuiScreen {

    private static final ResourceLocation NORMAL_TEXTURE =
            new ResourceLocation(MCOnlyKitchen.MODID, "textures/gui/minigame.png");
    private static final ResourceLocation NETHER_TEXTURE =
            new ResourceLocation(MCOnlyKitchen.MODID, "textures/gui/minigame_nether.png");

    private static final int PANEL_W = 38, PANEL_H = 152;
    private static final int BOBBER_U = 38, BOBBER_W = 9;
    private static final int FISH_U = 55, FISH_W = 16, FISH_H = 15;
    private static final int CHEST_U = 211, CHEST_V_NORMAL = 0, CHEST_V_GOLDEN = 13, CHEST_W = 13, CHEST_H = 13;
    private static final int HIT_U = 71, HIT_W = 73, HIT_H = 29;
    private static final int PERFECT_U = 144, PERFECT_W = 41, PERFECT_H = 12;
    private static final int CHANNEL_X = 18, CHANNEL_Y = 4, CHANNEL_H = 142;

    private final int rodTier;
    private final boolean isLava;
    private final int fishTier;

    private final Minecraft mc = Minecraft.getMinecraft();

    private FishingMiniGameLogic logic;

    private boolean finished;
    private boolean resultSent;

    private int hitTimer;
    private boolean showPerfect;

    private boolean mouseDown;
    private boolean keyPressed;

    private final int bobberEntityId;

    public GuiFishingMiniGame(int rodTier, boolean isLava, int fishTier, int entityId) {
        this.rodTier = rodTier;
        this.isLava = isLava;
        this.fishTier = fishTier;
        this.bobberEntityId = entityId;
    }

    @Override
    public void initGui() {
        super.initGui();
        if (mc.thePlayer != null) {
            mc.thePlayer.getEntityData().setBoolean("FishingLock", false);
        }
        Keyboard.enableRepeatEvents(true);
        logic = new FishingMiniGameLogic(0, CHANNEL_H, rodTier, isLava, fishTier);

        finished = false;
        resultSent = false;
        hitTimer = 0;
        showPerfect = false;
        mouseDown = false;
        keyPressed = false;
    }
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
        if (mc.thePlayer != null) {
            mc.thePlayer.getEntityData().setBoolean("FishingLock", false);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (finished) return;

        if (keyCode == Keyboard.KEY_ESCAPE) {
            onFail();
            return;
        }

        if (keyCode == Keyboard.KEY_SPACE || keyCode == Keyboard.KEY_W || keyCode == Keyboard.KEY_UP) {
            if (!keyPressed) {
                logic.press();
                keyPressed = true;
            }
        }
    }

    private void refreshKeyState() {
        boolean down = Keyboard.isKeyDown(Keyboard.KEY_SPACE)
                || Keyboard.isKeyDown(Keyboard.KEY_W)
                || Keyboard.isKeyDown(Keyboard.KEY_UP);
        if (!down) keyPressed = false;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (finished) return;
        if ((button == 0 || button == 1) && !mouseDown) {
            logic.press();
            mouseDown = true;
        }
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        if (button == 0 || button == 1) mouseDown = false;
    }

    @Override
    public void updateScreen() {
        refreshKeyState();
        if (finished) return;

        logic.tick();

        if (logic.isFailed()) {
            onFail();
            return;
        }

        if (!finished && logic.isFinishedByChest()) {
            finished = true;
            fireResult(true, true, logic.isGoldenChest());
            mc.displayGuiScreen(null);
            return;
        }

        if (!finished && logic.isSuccess()) {
            finished = true;
            fireResult(true, false, false);
            mc.displayGuiScreen(null);
        }
    }


    private void drawChest(int guiX, int guiY) {
        int chestY = guiY + CHANNEL_Y + logic.getChestY();
        int chestV = logic.isGoldenChest() ? CHEST_V_GOLDEN : CHEST_V_NORMAL;

        float pulseBase = 0.7f;
        float pulseAmp = 0.3f;
        float worldTime = mc.theWorld != null ? mc.theWorld.getTotalWorldTime() : 0;
        float pulse = pulseBase + (float) Math.sin(worldTime * 0.25f) * pulseAmp;

        GL11.glColor4f(1f, 1f, 1f, logic.isBobberOnChest() ? pulse : 1f);
        drawTexturedModalRect(guiX + CHANNEL_X + 1, chestY, CHEST_U, chestV, CHEST_W, CHEST_H);

        float progress = logic.getChestProgress();
        if (progress > 0f) {
            int barW = (int) (progress * (CHEST_W - 2));
            drawRect(guiX + CHANNEL_X + 2, chestY + CHEST_H + 1,
                    guiX + CHANNEL_X + 2 + barW, chestY + CHEST_H + 3, 0xFFFFD700);
        }

        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    private void drawHitText(int guiX, int guiY, float partialTicks) {
        if (hitTimer <= 0) return;
        float s = hitTimer / 15f;
        float scale = s < 0.5f ? (0.5f + s * 2.0f) : (1.5f - (s - 0.5f));
        GL11.glPushMatrix();
        GL11.glTranslatef(guiX - 16, guiY - 40, 0);
        GL11.glScalef(scale, scale, 1f);
        drawTexturedModalRect(0, 0, HIT_U, 0, HIT_W, HIT_H);
        GL11.glPopMatrix();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        // Space button "bite" prompt animation
        if (com.mconlykitchen.mconlykitchen.client.BiteAnimationHandler.isAnimating()) {
            int widthCenter = width / 2;
            int heightCenter = height / 2 - 50;
            int frame = (int) ((mc.theWorld.getTotalWorldTime() / 5) % 4);

            mc.getTextureManager().bindTexture(
                    new net.minecraft.util.ResourceLocation("mconlykitchen", "textures/gui/space_button.png")
            );

            drawTexturedModalRect(widthCenter - 64, heightCenter, frame * 128, 0, 128, 32);
        }

        final int guiX = width / 2 - PANEL_W / 2;
        final int guiY = height / 2 - PANEL_H / 2;

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_TRANSFORM_BIT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        mc.getTextureManager().bindTexture(isLava ? NETHER_TEXTURE : NORMAL_TEXTURE);

        drawTexturedModalRect(guiX, guiY, 0, 0, PANEL_W, PANEL_H);
        drawBobber(guiX, guiY);
        drawFish(guiX, guiY);
        if (logic.isChestVisible()) drawChest(guiX, guiY);
        drawProgressBar(guiX, guiY);
        drawReelHandle(guiX, guiY);
        drawHitText(guiX, guiY, partialTicks);

        if (showPerfect) {
            drawTexturedModalRect(guiX - 2, guiY - 20, PERFECT_U, 0, PERFECT_W, PERFECT_H);
        }

        GL11.glPopAttrib();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawBobber(int guiX, int guiY) {
        int barSize = logic.getBarSize();
        int bobberY = guiY + CHANNEL_Y + logic.getBarY();
        drawTexturedModalRect(guiX + CHANNEL_X, bobberY, BOBBER_U, 0, BOBBER_W, 2);
        for (int i = 0; i < barSize - 4; i++) {
            drawTexturedModalRect(guiX + CHANNEL_X, bobberY + 2 + i, BOBBER_U, 2, BOBBER_W, 1);
        }
        drawTexturedModalRect(guiX + CHANNEL_X, bobberY + barSize - 2, BOBBER_U, 3, BOBBER_W, 2);
    }

    private void drawFish(int guiX, int guiY) {
        int fishY = guiY + CHANNEL_Y + logic.getFishY();
        int v = getFishTextureV(fishTier);
        if (v < 0) v = 0;
        drawTexturedModalRect(guiX + CHANNEL_X - 2, fishY, FISH_U, v, FISH_W, FISH_H);
    }

    private void drawProgressBar(int guiX, int guiY) {
        int progress = logic.getProgress();
        int progHeight = (int) (progress / 100.0f * 145);
        int progX = guiX + PANEL_W - 5;
        int progBottomY = guiY + 148;
        int progTopY = progBottomY - progHeight;
        drawRect(progX, progTopY, progX + 4, progBottomY, getProgressColor(progress));
    }

    private void drawReelHandle(int guiX, int guiY) {
        // простая ручка катушки
        drawTexturedModalRect(guiX + 5, guiY + 129, 47, 0, 8, 3);
    }

    private int getProgressColor(int progress) {
        float ratio = progress / 100.0f;
        if (ratio < 0.5f) {
            return 0xFF000000 | (255 << 16) | ((int)(ratio * 2 * 255) << 8);
        }
        int red = (int) ((1 - (ratio - 0.5f) * 2) * 255);
        return 0xFF000000 | (red << 16) | (255 << 8);
    }

    private int getFishTextureV(int tier) {
        switch (tier) {
            case 1: return 0;
            case 2: return 15;
            case 3: return 30;
            default: return 0;
        }
    }

    private void fireResult(boolean success, boolean gotChest, boolean isGolden) {
        if (resultSent) return;
        resultSent = true;
        NetworkHandler.INSTANCE.sendToServer(new PacketFishingResult(success, gotChest, isGolden));
    }

    private void onSuccess(boolean gotChest, boolean isGolden) {
        finished = true;
        fireResult(true, gotChest, isGolden);
        String msg = gotChest
                ? (isGolden ? "gui.fishing.golden_chest" : "gui.fishing.chest")
                : "gui.fishing.success";
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(I18n.format(msg)));
        }
        mc.displayGuiScreen(null);
    }

    private void onFail() {
        finished = true;
        // В месте, где ты закрываешь GUI после завершения без сундука:
        com.mconlykitchen.mconlykitchen.client.ClientEventHandler.applyJumpCooldown(6); // ~0.3 сек на 1.7.10
        fireResult(false, false, false);
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(I18n.format("gui.fishing.escape")));
        }
        mc.displayGuiScreen(null);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

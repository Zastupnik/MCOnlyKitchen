package com.mconlykitchen.mconlykitchen.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
@cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
public class ClientEventHandler {

    private static boolean openFishingGuiScheduled = false;
    private static int scheduledRodTier = 0;
    private static boolean scheduledIsLava = false;
    private static int scheduledFishTier = 0;
    private static int scheduledBobberEntityId = -1;

    private static int jumpCooldownTicks = 0;

    public static void scheduleOpenFishingGui(int rodTier, boolean isLava, int fishTier, int bobberEntityId) {
        scheduledRodTier = rodTier;
        scheduledIsLava = isLava;
        scheduledFishTier = fishTier;
        scheduledBobberEntityId = bobberEntityId;
        openFishingGuiScheduled = true;
    }

    public static void applyJumpCooldown(int ticks) {
        if (ticks > jumpCooldownTicks) jumpCooldownTicks = ticks;
    }

    @cpw.mods.fml.common.eventhandler.SubscribeEvent
    public void onClientTick(cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent event) {
        if (event.phase != cpw.mods.fml.common.gameevent.TickEvent.Phase.END) return;

        final net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return;

        com.mconlykitchen.mconlykitchen.client.BiteAnimationHandler.tick();

        if (jumpCooldownTicks > 0) {
            net.minecraft.client.settings.KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
            jumpCooldownTicks--;
        }

        if (openFishingGuiScheduled) {
            openFishingGuiScheduled = false;
            if (!(mc.currentScreen instanceof com.mconlykitchen.mconlykitchen.gui.GuiFishingMiniGame)) {
                mc.displayGuiScreen(new com.mconlykitchen.mconlykitchen.gui.GuiFishingMiniGame(
                        scheduledRodTier, scheduledIsLava, scheduledFishTier, scheduledBobberEntityId
                ));
            }
            com.mconlykitchen.mconlykitchen.client.BiteAnimationHandler.reset();
        }
    }




    @SubscribeEvent
    public void onRenderGameOverlay(net.minecraftforge.client.event.RenderGameOverlayEvent.Post event) {
        if (event.type != net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.ALL) return;

        final Minecraft mc = Minecraft.getMinecraft();
        if (!BiteAnimationHandler.isAnimating()) return;

        ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        BiteAnimationHandler.render(resolution);
    }
}

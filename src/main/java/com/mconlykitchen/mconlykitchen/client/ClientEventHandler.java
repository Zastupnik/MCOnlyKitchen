package com.mconlykitchen.mconlykitchen.client;

import com.mconlykitchen.mconlykitchen.gui.GuiFishingMiniGame;
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

    @SubscribeEvent
    public void onClientTick(cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent event) {
        if (event.phase != cpw.mods.fml.common.gameevent.TickEvent.Phase.END) return;

        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return;

        com.mconlykitchen.mconlykitchen.client.BiteAnimationHandler.tick();

        // --- НАЧАЛО ФИКСА: проверка прыжка для мини-игры ---
        if (mc.gameSettings.keyBindJump.isPressed()) {
            if (com.mconlykitchen.mconlykitchen.fishing.FishingSessionManager.hasActiveSession(mc.thePlayer)) {
                com.mconlykitchen.mconlykitchen.entity.EntityCustomBobber bobber =
                        com.mconlykitchen.mconlykitchen.fishing.FishingSessionManager.getActiveBobber(mc.thePlayer);
                if (bobber != null && bobber.getState() == com.mconlykitchen.mconlykitchen.entity.EntityCustomBobber.State.BITE_ANIMATION) {
                    com.mconlykitchen.mconlykitchen.network.NetworkHandler.INSTANCE.sendToServer(
                            new com.mconlykitchen.mconlykitchen.network.PacketSpacePressed(bobber.getEntityId())
                    );
                    applyJumpCooldown(5); // ❗ НЕ даём прыжку сработать
                    return; // выходим, чтобы обычный прыжок не сработал
                }
            }
        }
        // --- КОНЕЦ ФИКСА ---

        if (jumpCooldownTicks > 0) {
            net.minecraft.client.settings.KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
            jumpCooldownTicks--;
        }

        if (openFishingGuiScheduled) {
            openFishingGuiScheduled = false;

            if (!(mc.currentScreen instanceof GuiFishingMiniGame)) {
                mc.displayGuiScreen(new GuiFishingMiniGame(
                        scheduledRodTier,
                        scheduledIsLava,
                        scheduledFishTier,
                        scheduledBobberEntityId
                ));
            }

            BiteAnimationHandler.reset();
            applyJumpCooldown(5);
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

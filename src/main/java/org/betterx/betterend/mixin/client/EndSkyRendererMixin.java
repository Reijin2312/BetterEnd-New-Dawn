package org.betterx.betterend.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.world.level.Level;
import org.betterx.betterend.client.render.BetterEndSkyRenderer;
import org.betterx.betterend.config.Configs;
import org.betterx.bclib.util.BackgroundInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SkyRenderer.class, priority = 1100)
public class EndSkyRendererMixin {
    @Unique private static final float BETTEREND_HORIZON_RED = 0.38F;
    @Unique private static final float BETTEREND_HORIZON_GREEN = 0.13F;
    @Unique private static final float BETTEREND_HORIZON_BLUE = 0.40F;
    @Unique private final BetterEndSkyRenderer betterend$skyRenderer = new BetterEndSkyRenderer();

    @Inject(method = "renderEndSky", at = @At("HEAD"), cancellable = true, remap = false)
    private void betterend$renderEndSky(CallbackInfo info) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!Configs.CLIENT_CONFIG.customSky.get()
                || minecraft.level == null
                || minecraft.level.dimension() != Level.END) {
            BackgroundInfo.hasSkyHorizonColor = false;
            return;
        }

        BackgroundInfo.skyHorizonRed = BETTEREND_HORIZON_RED;
        BackgroundInfo.skyHorizonGreen = BETTEREND_HORIZON_GREEN;
        BackgroundInfo.skyHorizonBlue = BETTEREND_HORIZON_BLUE;
        BackgroundInfo.hasSkyHorizonColor = true;

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(RenderSystem.getModelViewStack());
        float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        float time = (float) (((minecraft.level.getGameTime() + (double) partialTick) % 360000L) * 0.000017453292F);
        betterend$skyRenderer.renderSkyboxWithStars(poseStack, time, () -> {});
        info.cancel();
    }
}

package org.betterx.betterend.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import org.betterx.betterend.client.render.BetterEndSkyRenderer;
import org.betterx.betterend.config.Configs;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, priority = 1100)
public class EndSkyRendererMixin {
    @Unique private final BetterEndSkyRenderer betterend$skyRenderer = new BetterEndSkyRenderer();

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void betterend$renderSky(
            Matrix4f modelViewMatrix,
            Matrix4f projectionMatrix,
            float partialTick,
            Camera camera,
            boolean isFoggy,
            Runnable setupFog,
            CallbackInfo info
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!Configs.CLIENT_CONFIG.customSky.get()
                || minecraft.level == null
                || minecraft.level.dimension() != Level.END
                || camera.getFluidInCamera() != FogType.NONE) {
            return;
        }

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(modelViewMatrix);
        float time = (float) (((minecraft.level.getGameTime() + (double) partialTick) % 360000L) * 0.000017453292F);
        betterend$skyRenderer.renderSkyboxWithStars(poseStack, projectionMatrix, time);
        info.cancel();
    }
}

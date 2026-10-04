package org.betterx.betterend.mixin.client;

import org.betterx.betterend.client.effects.EternalHint;
import org.betterx.betterend.client.effects.InfusionHint;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "bobHurt", at = @At("RETURN"), remap = false)
    private void betterend_wobbleForBuildHint(
            CameraRenderState cameraRenderState,
            PoseStack poseStack,
            CallbackInfo info
    ) {
        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        float[] wobble = InfusionHint.wobble(partialTick);
        if (wobble == null) wobble = EternalHint.wobble(partialTick);
        if (wobble == null) return;

        poseStack.mulPose(new org.joml.Matrix4f().rotation(Axis.ZP.rotationDegrees(wobble[0])));
        poseStack.mulPose(new org.joml.Matrix4f().rotation(Axis.XP.rotationDegrees(wobble[1])));
    }
}

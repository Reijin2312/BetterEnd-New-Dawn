package org.betterx.betterend.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SkyRenderer;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.world.level.Level;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import org.betterx.betterend.client.render.BetterEndSkyRenderer;
import org.betterx.betterend.config.Configs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SkyRenderer.class, priority = 1100)
public class EndSkyRendererMixin {
    @Unique private final BetterEndSkyRenderer betterend$skyRenderer = new BetterEndSkyRenderer();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void betterend$initialiseTextures(
            TextureManager textureManager,
            AtlasManager atlasManager,
            RenderTarget renderTarget,
            CallbackInfo info
    ) {
        betterend$skyRenderer.initialiseResources(textureManager);
    }

    @Inject(method = "renderEndSky", at = @At("HEAD"), cancellable = true, remap = false)
    private void betterend$renderEndSky(RenderPass renderPass, CallbackInfo info) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!Configs.CLIENT_CONFIG.customSky.get()
                || minecraft.level == null
                || minecraft.level.dimension() != Level.END) {
            return;
        }

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(RenderSystem.getModelViewStack());
        float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        float time = (float) (((minecraft.level.getGameTime() + (double) partialTick) % 360000L) * 0.000017453292F);
        betterend$skyRenderer.renderSkyboxWithStars(renderPass, poseStack, time, () -> {});
        info.cancel();
    }
}

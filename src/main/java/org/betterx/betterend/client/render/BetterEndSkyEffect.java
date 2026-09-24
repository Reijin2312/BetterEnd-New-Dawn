package org.betterx.betterend.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.neoforged.neoforge.client.CustomSkyboxRenderer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import org.joml.Matrix4fc;

public class BetterEndSkyEffect implements CustomSkyboxRenderer {
    private final BetterEndSkyRenderer renderer = new BetterEndSkyRenderer();

    @Override
    public boolean renderSky(
            LevelRenderState levelRenderState,
            SkyRenderState skyRenderState,
            Matrix4fc modelViewMatrix,
            GpuBufferSlice fogBuffer
    ) {
        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(modelViewMatrix);
        float time = (float) ((levelRenderState.gameTime % 360000L) * 0.000017453292F);
        renderer.renderSkyboxWithStars(poseStack, time, () -> {});
        return true;
    }
}

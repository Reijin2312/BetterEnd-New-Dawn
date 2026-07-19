package org.betterx.betterend.client.render;

import org.betterx.betterend.BetterEnd;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;

public final class BetterEndRenderPipelines {
    private static final BlendFunction LEGACY_TRANSLUCENT_BLEND = new BlendFunction(
            SourceFactor.SRC_ALPHA,
            DestFactor.ONE_MINUS_SRC_ALPHA
    );

    public static final RenderPipeline SKY_TEXTURED = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
            .withLocation(BetterEnd.C.mk("pipeline/sky_textured"))
            .withVertexShader("core/position_tex")
            .withFragmentShader("core/position_tex")
            .withSampler("Sampler0")
            .withColorTargetState(new ColorTargetState(LEGACY_TRANSLUCENT_BLEND))
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
            .build();

    public static final RenderPipeline SKY_STARS = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
            .withLocation(BetterEnd.C.mk("pipeline/sky_stars"))
            .withVertexShader("core/stars")
            .withFragmentShader("core/stars")
            .withColorTargetState(new ColorTargetState(LEGACY_TRANSLUCENT_BLEND))
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
            .withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS)
            .build();

    private BetterEndRenderPipelines() {
    }
}

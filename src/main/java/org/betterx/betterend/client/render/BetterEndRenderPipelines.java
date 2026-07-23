package org.betterx.betterend.client.render;

import org.betterx.betterend.BetterEnd;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;

public final class BetterEndRenderPipelines {
    public static final RenderPipeline SKY_TEXTURED = RenderPipeline.builder(RenderPipelines.GLOBALS_SNIPPET)
            .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
            .withLocation(BetterEnd.C.mk("pipeline/sky_textured"))
            .withVertexShader("core/position_tex")
            .withFragmentShader("core/position_tex")
            .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
            .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .build();

    public static final RenderPipeline SKY_STARS = RenderPipeline.builder(RenderPipelines.GLOBALS_SNIPPET)
            .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
            .withLocation(BetterEnd.C.mk("pipeline/sky_stars"))
            .withVertexShader("core/stars")
            .withFragmentShader("core/stars")
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
            .withVertexBinding(0, DefaultVertexFormat.POSITION)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .build();

    private BetterEndRenderPipelines() {
    }
}

package org.betterx.betterend.client.render;

import org.betterx.betterend.BetterEnd;

import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

public class BetterEndRenderPipelines {
    public static final RenderPipeline SKY_TEXTURED = RenderPipeline.builder(RenderPipelines.GLOBALS_SNIPPET)
                                                                    .withBindGroupLayout(BindGroupLayouts.PROJECTION)
                                                                    .withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
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
                                                                 .withBindGroupLayout(BindGroupLayouts.PROJECTION)
                                                                 .withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
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

    public static void register(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(SKY_TEXTURED);
        event.registerPipeline(SKY_STARS);
    }
}

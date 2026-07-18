package org.betterx.betterend.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class BeamRenderer {
    private static final Identifier BEAM_TEXTURE = Identifier.withDefaultNamespace("textures/entity/end_gateway_beam.png");

    public static void renderLightBeam(
            PoseStack matrices,
            SubmitNodeCollector submitNodeCollector,
            int age,
            float tick,
            int minY,
            int maxY,
            float[] colors,
            float alpha,
            float beamIn,
            float beamOut
    ) {
        float red = colors[0];
        float green = colors[1];
        float blue = colors[2];

        int maxBY = minY + maxY;
        float delta = maxY < 0 ? tick : -tick;
        float fractDelta = Mth.frac(delta * 0.2F - (float) Mth.floor(delta * 0.1F));
        float xIn = -beamIn;
        float minV = Mth.clamp(fractDelta - 1.0F, 0.0F, 1.0F);
        float maxV = (float) maxY * (0.5F / beamIn) + minV;
        float rotation = (age + tick) / 25.0F + 6.0F;
        int color = ARGB.color(
                Mth.clamp(Mth.floor(alpha * 255.0F), 0, 255),
                Mth.clamp(Mth.floor(red * 255.0F), 0, 255),
                Mth.clamp(Mth.floor(green * 255.0F), 0, 255),
                Mth.clamp(Mth.floor(blue * 255.0F), 0, 255)
        );
        int glowColor = ARGB.color(
                Mth.clamp(Mth.floor(alpha * 0.125F * 255.0F), 0, 255),
                Mth.clamp(Mth.floor(red * 255.0F), 0, 255),
                Mth.clamp(Mth.floor(green * 255.0F), 0, 255),
                Mth.clamp(Mth.floor(blue * 255.0F), 0, 255)
        );

        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotation(-rotation));

        submitBeam(
                matrices,
                submitNodeCollector,
                color,
                minY,
                maxBY,
                beamIn,
                0.0F,
                0.0F,
                beamIn,
                0.0F,
                xIn,
                xIn,
                0.0F,
                0.0F,
                1.0F,
                minV,
                maxV
        );

        float xOut = -beamOut;
        maxV = (float) maxY + minV;
        submitBeam(
                matrices,
                submitNodeCollector,
                glowColor,
                minY,
                maxBY,
                xOut,
                xOut,
                beamOut,
                xOut,
                xOut,
                beamOut,
                beamOut,
                beamOut,
                0.0F,
                1.0F,
                minV,
                maxV
        );

        matrices.popPose();
    }

    private static void submitBeam(
            PoseStack matrices,
            SubmitNodeCollector submitNodeCollector,
            int color,
            int minY,
            int maxY,
            float x1,
            float d1,
            float x2,
            float d2,
            float x3,
            float d3,
            float x4,
            float d4,
            float minU,
            float maxU,
            float minV,
            float maxV
    ) {
        submitQuad(matrices, submitNodeCollector, color, minY, maxY, x1, d1, x2, d2, minU, maxU, minV, maxV);
        submitQuad(matrices, submitNodeCollector, color, minY, maxY, x4, d4, x3, d3, minU, maxU, minV, maxV);
        submitQuad(matrices, submitNodeCollector, color, minY, maxY, x2, d2, x4, d4, minU, maxU, minV, maxV);
        submitQuad(matrices, submitNodeCollector, color, minY, maxY, x3, d3, x1, d1, minU, maxU, minV, maxV);
    }

    private static void submitQuad(
            PoseStack matrices,
            SubmitNodeCollector submitNodeCollector,
            int color,
            int minY,
            int maxY,
            float minX,
            float minD,
            float maxX,
            float maxD,
            float minU,
            float maxU,
            float minV,
            float maxV
    ) {
        submitNodeCollector.submitCustomGeometry(
                matrices,
                RenderTypes.beaconBeam(BEAM_TEXTURE, color >>> 24 < 255),
                (entry, vertexConsumer) -> {
                    addVertex(entry, vertexConsumer, color, maxX, minY, maxD, maxU, minV);
                    addVertex(entry, vertexConsumer, color, maxX, maxY, maxD, maxU, maxV);
                    addVertex(entry, vertexConsumer, color, minX, maxY, minD, minU, maxV);
                    addVertex(entry, vertexConsumer, color, minX, minY, minD, minU, minV);
                }
        );
    }

    private static void addVertex(
            PoseStack.Pose entry,
            VertexConsumer vertexConsumer,
            int color,
            float x,
            float y,
            float d,
            float u,
            float v
    ) {
        vertexConsumer.addVertex(entry, x, y, d)
                      .setColor(color)
                      .setUv(u, v)
                      .setOverlay(OverlayTexture.NO_OVERLAY)
                      .setLight(15728880)
                      .setNormal(entry, 0.0F, 1.0F, 0.0F);
    }
}

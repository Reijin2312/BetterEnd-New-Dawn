package org.betterx.betterend.client.render;

import org.betterx.bclib.util.BackgroundInfo;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.BetterEnd;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.OptionalDouble;
import java.util.OptionalInt;

public class BetterEndSkyRenderer {
    private static final int VERTEX_BUFFER_USAGE = 32;
    // 1.21 effective end-fog tint in sky pass was ~0.6 of final fog RGB (0.15 * legacy *4 path).
    private static final float LEGACY_END_FOG_COLOR_SCALE = 0.6F;

    private static final class MeshBuffer implements AutoCloseable {
        final GpuBuffer buffer;
        final int vertexCount;
        final int indexCount;
        final VertexFormat.Mode mode;

        private MeshBuffer(GpuBuffer buffer, int vertexCount, int indexCount, VertexFormat.Mode mode) {
            this.buffer = buffer;
            this.vertexCount = vertexCount;
            this.indexCount = indexCount;
            this.mode = mode;
        }

        @Override
        public void close() {
            this.buffer.close();
        }
    }

    @FunctionalInterface
    interface BufferFunction {
        BufferBuilder make(Tesselator tesselator, float minSize, float maxSize, int count, long seed);
    }

    private static final Identifier NEBULA_1 = BetterEnd.C.mk("textures/sky/nebula_2.png");
    private static final Identifier NEBULA_2 = BetterEnd.C.mk("textures/sky/nebula_3.png");
    private static final Identifier HORIZON = BetterEnd.C.mk("textures/sky/nebula_1.png");
    private static final Identifier STARS = BetterEnd.C.mk("textures/sky/stars.png");
    private static final Identifier FOG = BetterEnd.C.mk("textures/sky/fog.png");

    private MeshBuffer nebula1;
    private MeshBuffer nebula2;
    private MeshBuffer horizon;
    private MeshBuffer stars1;
    private MeshBuffer stars2;
    private MeshBuffer stars3;
    private MeshBuffer stars4;
    private MeshBuffer fog;
    private Vector3f axis1;
    private Vector3f axis2;
    private Vector3f axis3;
    private Vector3f axis4;

    private boolean initialised;

    private void initialise() {
        if (!initialised) {
            initStars();
            RandomSource random = new LegacyRandomSource(131);
            axis1 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
            axis2 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
            axis3 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
            axis4 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
            axis1.normalize();
            axis2.normalize();
            axis3.normalize();
            axis4.normalize();
            initialised = true;
        }
    }

    public void renderFallback(PoseStack matrices, Matrix4f projectionMatrix, float time) {
        renderFallback(matrices, time, () -> {
        });
    }

    public void renderFallback(PoseStack matrices, float time, Runnable setupFog) {
        initialise();

        float time2 = time * 2;
        float time3 = time * 3;

        float blindA = 1F - BackgroundInfo.blindness;
        float blind02 = blindA * 0.2f;
        float blind06 = blindA * 0.6f;

        if (blindA > 0) {
            matrices.pushPose();
            matrices.mulPose(new Quaternionf().rotationXYZ(0, time, 0));
            renderBuffer(
                    matrices,
                    horizon,
                    HORIZON,
                    true,
                    0.77f,
                    0.31f,
                    0.73f,
                    0.7f * blindA,
                    setupFog
            );
            matrices.popPose();

            // TEMP: disable nebula pass 1 to isolate overexposure source.
            // matrices.pushPose();
            // matrices.mulPose(new Quaternionf().rotationXYZ(0, -time, 0));
            // renderBuffer(
            //         matrices,
            //         nebula1,
            //         NEBULA_1,
            //         true,
            //         0.77f,
            //         0.31f,
            //         0.73f,
            //         blind02,
            //         setupFog
            // );
            // matrices.popPose();

            // TEMP: disable nebula pass 2 to isolate overexposure source.
            // matrices.pushPose();
            // matrices.mulPose(new Quaternionf().rotationXYZ(0, time2, 0));
            // renderBuffer(
            //         matrices,
            //         nebula2,
            //         NEBULA_2,
            //         true,
            //         0.77f,
            //         0.31f,
            //         0.73f,
            //         blind02,
            //         setupFog
            // );
            // matrices.popPose();

            // TEMP: disable textured star layers to isolate sky tint source.
            // matrices.pushPose();
            // matrices.mulPose(new Quaternionf().setAngleAxis(time, axis3.x, axis3.y, axis3.z));
            // renderBuffer(
            //         matrices,
            //         stars3,
            //         STARS,
            //         true,
            //         0.77f,
            //         0.31f,
            //         0.73f,
            //         blind06,
            //         setupFog
            // );
            // matrices.popPose();

            // matrices.pushPose();
            // matrices.mulPose(new Quaternionf().setAngleAxis(time2, axis4.x, axis4.y, axis4.z));
            // renderBuffer(matrices, stars4, STARS, true, 1F, 1F, 1F, blind06, setupFog);
            // matrices.popPose();
        }

        // TEMP: sky fog texture pass is disabled for debugging.
        // float a = BackgroundInfo.fogDensity - 1F;
        // if (a > 0) {
        //     if (a > 1F) {
        //         a = 1F;
        //     }
        //     float fogRed = Mth.clamp(BackgroundInfo.fogColorRed * LEGACY_END_FOG_COLOR_SCALE, 0.0F, 1.0F);
        //     float fogGreen = Mth.clamp(BackgroundInfo.fogColorGreen * LEGACY_END_FOG_COLOR_SCALE, 0.0F, 1.0F);
        //     float fogBlue = Mth.clamp(BackgroundInfo.fogColorBlue * LEGACY_END_FOG_COLOR_SCALE, 0.0F, 1.0F);
        //     renderBuffer(
        //             matrices,
        //             fog,
        //             FOG,
        //             true,
        //             fogRed,
        //             fogGreen,
        //             fogBlue,
        //             a,
        //             setupFog
        //     );
        // }

        if (blindA > 0) {
            matrices.pushPose();
            matrices.mulPose(new Quaternionf().setAngleAxis(time3, axis1.x, axis1.y, axis1.z));
            renderBuffer(matrices, stars1, null, false, 1, 1, 1, blind06, setupFog);
            matrices.popPose();

            matrices.pushPose();
            matrices.mulPose(new Quaternionf().setAngleAxis(time2, axis2.x, axis2.y, axis2.z));
            renderBuffer(
                    matrices,
                    stars2,
                    null,
                    false,
                    0.95f,
                    0.64f,
                    0.93f,
                    blind06,
                    setupFog
            );
            matrices.popPose();
        }
    }

    public void renderSkyboxOnly(PoseStack matrices, Matrix4f projectionMatrix) {
        renderSkyboxOnly(matrices);
    }

    public void renderSkyboxOnly(PoseStack matrices) {
        initialise();
        renderBuffer(
                matrices,
                horizon,
                HORIZON,
                true,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                () -> {
                }
        );
    }

    public void renderSkyboxWithStars(PoseStack matrices, Matrix4f projectionMatrix, float time) {
        renderFallback(matrices, time, () -> {
        });
    }

    public void renderSkyboxWithStars(PoseStack matrices, float time, Runnable setupFog) {
        renderFallback(matrices, time, setupFog);
    }

    private void renderBuffer(
            PoseStack matrices,
            MeshBuffer buffer,
            Identifier texture,
            boolean textured,
            float r,
            float g,
            float b,
            float a,
            Runnable setupFog
    ) {
        if (buffer == null || a <= 0.0f) {
            return;
        }

        // TEMP: disable sky fog uniform setup to isolate fog-color bleeding into sky layers.
        // setupFog.run();

        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.identity();
        matrix4fStack.mul(matrices.last().pose());
        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms()
                                                .writeTransform(
                                                        matrix4fStack,
                                                        new Vector4f(r, g, b, a),
                                                        new Vector3f(),
                                                        new Matrix4f()
                                                );
        matrix4fStack.popMatrix();

        Minecraft minecraft = Minecraft.getInstance();
        GpuTextureView colorTexture = minecraft.getMainRenderTarget().getColorTextureView();
        GpuTextureView depthTexture = minecraft.getMainRenderTarget().getDepthTextureView();
        RenderPipeline pipeline = textured ? BetterEndRenderPipelines.SKY_TEXTURED : BetterEndRenderPipelines.SKY_STARS;
        AbstractTexture abstractTexture = null;

        // Texture upload cannot happen while a render pass is open.
        if (textured && texture != null) {
            abstractTexture = minecraft.getTextureManager().getTexture(texture);
        }

        try (RenderPass renderPass = RenderSystem.getDevice()
                                                 .createCommandEncoder()
                                                 .createRenderPass(
                                                         () -> "BetterEnd sky",
                                                         colorTexture,
                                                         OptionalInt.empty(),
                                                         depthTexture,
                                                         OptionalDouble.empty()
                                                 )) {
            renderPass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", transforms);
            renderPass.setVertexBuffer(0, buffer.buffer);

            if (abstractTexture != null) {
                renderPass.bindTexture("Sampler0", abstractTexture.getTextureView(), abstractTexture.getSampler());
            }

            if (buffer.mode == VertexFormat.Mode.QUADS) {
                RenderSystem.AutoStorageIndexBuffer quadIndices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
                GpuBuffer indexBuffer = quadIndices.getBuffer(buffer.indexCount);
                renderPass.setIndexBuffer(indexBuffer, quadIndices.type());
                renderPass.drawIndexed(0, 0, buffer.indexCount, 1);
            } else {
                renderPass.draw(0, buffer.vertexCount);
            }
        }
    }

    private void initStars() {
        Tesselator tesselator = Tesselator.getInstance();

        stars1 = buildBuffer(tesselator, stars1, 0.1f, 0.30f, 3500, 41315, this::makeStars);
        stars2 = buildBuffer(tesselator, stars2, 0.1f, 0.35f, 2000, 35151, this::makeStars);
        stars3 = buildBuffer(tesselator, stars3, 0.4f, 1.2f, 1000, 61354, this::makeUVStars);
        stars4 = buildBuffer(tesselator, stars4, 0.4f, 1.2f, 1000, 61355, this::makeUVStars);
        nebula1 = buildBuffer(tesselator, nebula1, 40, 60, 30, 11515, this::makeFarFog);
        nebula2 = buildBuffer(tesselator, nebula2, 40, 60, 10, 14151, this::makeFarFog);
        horizon = buildBufferHorizon(tesselator, horizon);
        fog = buildBufferFog(tesselator, fog);
    }

    private MeshBuffer buildBuffer(
            Tesselator tesselator,
            MeshBuffer vertexBuffer,
            float minSize,
            float maxSize,
            int count,
            long seed,
            BufferFunction fkt
    ) {
        if (vertexBuffer != null) {
            vertexBuffer.close();
        }

        BufferBuilder bufferBuilder = fkt.make(tesselator, minSize, maxSize, count, seed);
        MeshData meshData = bufferBuilder.build();
        if (meshData == null) {
            return null;
        }

        try (meshData) {
            GpuBuffer gpuBuffer = RenderSystem.getDevice()
                                              .createBuffer(() -> "BetterEnd sky buffer", VERTEX_BUFFER_USAGE, meshData.vertexBuffer());
            MeshData.DrawState drawState = meshData.drawState();
            return new MeshBuffer(gpuBuffer, drawState.vertexCount(), drawState.indexCount(), drawState.mode());
        }
    }


    private MeshBuffer buildBufferHorizon(Tesselator tesselator, MeshBuffer buffer) {
        return buildBuffer(
                tesselator, buffer, 0, 0, 0, 0,
                (_builder, _minSize, _maxSize, _count, _seed) -> makeCylinder(_builder, 16, 50, 100)
        );

    }

    private MeshBuffer buildBufferFog(Tesselator tesselator, MeshBuffer buffer) {
        return buildBuffer(
                tesselator, buffer, 0, 0, 0, 0,
                (_builder, _minSize, _maxSize, _count, _seed) -> makeCylinder(_builder, 16, 50, 70)
        );
    }

    private BufferBuilder makeStars(Tesselator tesselator, float minSize, float maxSize, int count, long seed) {
        RandomSource random = new LegacyRandomSource(seed);
        final BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        for (int i = 0; i < count; ++i) {
            float posX = random.nextFloat() * 2.0f - 1.0f;
            float posY = random.nextFloat() * 2.0f - 1.0f;
            float posZ = random.nextFloat() * 2.0f - 1.0f;
            float size = MHelper.randRange(minSize, maxSize, random);
            float length = posX * posX + posY * posY + posZ * posZ;

            if (length < 1.0f && length > 0.001f) {
                length = 1.0f / (float) Math.sqrt(length);
                posX *= length;
                posY *= length;
                posZ *= length;

                float px = posX * 100.0f;
                float py = posY * 100.0f;
                float pz = posZ * 100.0f;

                float angle = (float) Math.atan2(posX, posZ);
                float sin1 = (float) Math.sin(angle);
                float cos1 = (float) Math.cos(angle);
                angle = (float) Math.atan2(Math.sqrt(posX * posX + posZ * posZ), posY);
                float sin2 = (float) Math.sin(angle);
                float cos2 = (float) Math.cos(angle);
                angle = random.nextFloat() * (float) Math.PI * 2.0f;
                float sin3 = (float) Math.sin(angle);
                float cos3 = (float) Math.cos(angle);

                for (int index = 0; index < 4; ++index) {
                    float x = (float) ((index & 2) - 1) * size;
                    float y = (float) ((index + 1 & 2) - 1) * size;
                    float aa = x * cos3 - y * sin3;
                    float ab = y * cos3 + x * sin3;
                    float dy = aa * sin2 + 0.0f * cos2;
                    float ae = 0.0f * sin2 - aa * cos2;
                    float dx = ae * sin1 - ab * cos1;
                    float dz = ab * sin1 + ae * cos1;
                    buffer.addVertex(px + dx, py + dy, pz + dz);
                }
            }
        }

        return buffer;
    }

    private BufferBuilder makeUVStars(Tesselator tesselator, float minSize, float maxSize, int count, long seed) {
        RandomSource random = new LegacyRandomSource(seed);
        final BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        for (int i = 0; i < count; ++i) {
            float posX = random.nextFloat() * 2.0f - 1.0f;
            float posY = random.nextFloat() * 2.0f - 1.0f;
            float posZ = random.nextFloat() * 2.0f - 1.0f;
            float size = MHelper.randRange(minSize, maxSize, random);
            float length = posX * posX + posY * posY + posZ * posZ;

            if (length < 1.0f && length > 0.001f) {
                length = 1.0f / (float) Math.sqrt(length);
                posX *= length;
                posY *= length;
                posZ *= length;

                float px = posX * 100.0f;
                float py = posY * 100.0f;
                float pz = posZ * 100.0f;

                float angle = (float) Math.atan2(posX, posZ);
                float sin1 = (float) Math.sin(angle);
                float cos1 = (float) Math.cos(angle);
                angle = (float) Math.atan2(Math.sqrt(posX * posX + posZ * posZ), posY);
                float sin2 = (float) Math.sin(angle);
                float cos2 = (float) Math.cos(angle);
                angle = random.nextFloat() * (float) Math.PI * 2.0f;
                float sin3 = (float) Math.sin(angle);
                float cos3 = (float) Math.cos(angle);

                float minV = random.nextInt(4) / 4F;
                for (int index = 0; index < 4; ++index) {
                    float x = (float) ((index & 2) - 1) * size;
                    float y = (float) ((index + 1 & 2) - 1) * size;
                    float aa = x * cos3 - y * sin3;
                    float ab = y * cos3 + x * sin3;
                    float dy = aa * sin2 + 0.0f * cos2;
                    float ae = 0.0f * sin2 - aa * cos2;
                    float dx = ae * sin1 - ab * cos1;
                    float dz = ab * sin1 + ae * cos1;
                    float texU = (index >> 1) & 1;
                    float texV = (((index + 1) >> 1) & 1) / 4F + minV;
                    buffer.addVertex(px + dx, py + dy, pz + dz).setUv(texU, texV);
                }
            }
        }
        return buffer;
    }

    private BufferBuilder makeFarFog(Tesselator tesselator, float minSize, float maxSize, int count, long seed) {
        RandomSource random = new LegacyRandomSource(seed);
        final BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        for (int i = 0; i < count; ++i) {
            float posX = random.nextFloat() * 2.0f - 1.0f;
            float posY = random.nextFloat() - 0.5f;
            float posZ = random.nextFloat() * 2.0f - 1.0f;
            float size = MHelper.randRange(minSize, maxSize, random);
            float length = posX * posX + posY * posY + posZ * posZ;
            float distance = 2.0f;

            if (length < 1.0f && length > 0.001f) {
                length = distance / (float) Math.sqrt(length);
                size *= distance;
                posX *= length;
                posY *= length;
                posZ *= length;

                float px = posX * 100.0f;
                float py = posY * 100.0f;
                float pz = posZ * 100.0f;

                float angle = (float) Math.atan2(posX, posZ);
                float sin1 = (float) Math.sin(angle);
                float cos1 = (float) Math.cos(angle);
                angle = (float) Math.atan2(Math.sqrt(posX * posX + posZ * posZ), posY);
                float sin2 = (float) Math.sin(angle);
                float cos2 = (float) Math.cos(angle);
                angle = random.nextFloat() * (float) Math.PI * 2.0f;
                float sin3 = (float) Math.sin(angle);
                float cos3 = (float) Math.cos(angle);

                for (int index = 0; index < 4; ++index) {
                    float x = (float) ((index & 2) - 1) * size;
                    float y = (float) ((index + 1 & 2) - 1) * size;
                    float aa = x * cos3 - y * sin3;
                    float ab = y * cos3 + x * sin3;
                    float dy = aa * sin2 + 0.0f * cos2;
                    float ae = 0.0f * sin2 - aa * cos2;
                    float dx = ae * sin1 - ab * cos1;
                    float dz = ab * sin1 + ae * cos1;
                    float texU = (index >> 1) & 1;
                    float texV = ((index + 1) >> 1) & 1;
                    buffer.addVertex(px + dx, py + dy, pz + dz).setUv(texU, texV);
                }
            }
        }
        return buffer;
    }

    private BufferBuilder makeCylinder(Tesselator tesselator, int segments, float height, float radius) {
        final BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        for (int i = 0; i < segments; i++) {
            float a1 = (float) i * (float) Math.PI * 2.0f / (float) segments;
            float a2 = (float) (i + 1) * (float) Math.PI * 2.0f / (float) segments;
            float px1 = (float) Math.sin(a1) * radius;
            float pz1 = (float) Math.cos(a1) * radius;
            float px2 = (float) Math.sin(a2) * radius;
            float pz2 = (float) Math.cos(a2) * radius;

            float u0 = (float) i / (float) segments;
            float u1 = (float) (i + 1) / (float) segments;

            buffer.addVertex(px1, -height, pz1).setUv(u0, 0);
            buffer.addVertex(px1, height, pz1).setUv(u0, 1);
            buffer.addVertex(px2, height, pz2).setUv(u1, 1);
            buffer.addVertex(px2, -height, pz2).setUv(u1, 0);
        }
        return buffer;
    }
}

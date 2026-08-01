package org.betterx.betterend.entity.render;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.entity.EndSlimeEntity;
import org.betterx.betterend.entity.model.EndSlimeEntityModel;
import org.betterx.betterend.registry.EndEntitiesRenders;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class RendererEntityEndSlime extends MobRenderer<EndSlimeEntity, RendererEntityEndSlime.EndSlimeRenderState, EndSlimeEntityModel<RendererEntityEndSlime.EndSlimeRenderState>> {
    private static final Identifier[] TEXTURE = new Identifier[4];
    private static final RenderType[] GLOW = new RenderType[4];

    private final EndSlimeEntityModel<EndSlimeRenderState> flowerModel;
    private final EndSlimeEntityModel<EndSlimeRenderState> cropModel;

    public RendererEntityEndSlime(EntityRendererProvider.Context ctx) {
        super(ctx, new EndSlimeEntityModel<>(ctx.bakeLayer(EndEntitiesRenders.END_SLIME_MODEL), false), 0.25F);

        this.flowerModel = new EndSlimeEntityModel<>(
                ctx.bakeLayer(EndEntitiesRenders.END_SLIME_MODEL),
                false,
                EndSlimeEntityModel.RenderMode.FLOWER_ONLY
        );
        this.cropModel = new EndSlimeEntityModel<>(
                ctx.bakeLayer(EndEntitiesRenders.END_SLIME_MODEL),
                false,
                EndSlimeEntityModel.RenderMode.CROP_ONLY
        );

        this.addLayer(new OverlayFeatureRenderer(this, ctx));
        this.addLayer(new EyesLayer<EndSlimeRenderState, EndSlimeEntityModel<EndSlimeRenderState>>(this) {
            @Override
            public RenderType renderType() {
                return GLOW[0];
            }

            @Override
            public void submit(
                    PoseStack matrices,
                    SubmitNodeCollector submitNodeCollector,
                    int light,
                    EndSlimeRenderState state,
                    float yRot,
                    float xRot
            ) {
                submitNodeCollector.submitCustomGeometry(matrices, GLOW[state.slimeType], (pose, buffer) -> {
                    PoseStack local = new PoseStack();
                    local.mulPose(pose.pose());
                    this.getParentModel()
                        .renderToBuffer(local, buffer, 15728640, OverlayTexture.NO_OVERLAY, 0xffffffff);
                });

                if (state.isLake) {
                    flowerModel.setupAnim(state);
                    submitNodeCollector.submitCustomGeometry(matrices, GLOW[state.slimeType], (pose, buffer) -> {
                        PoseStack local = new PoseStack();
                        local.mulPose(pose.pose());
                        flowerModel.renderToBuffer(local, buffer, 15728640, OverlayTexture.NO_OVERLAY, 0xffffffff);
                    });
                }
            }
        });
    }

    @Override
    public Identifier getTextureLocation(EndSlimeRenderState state) {
        return TEXTURE[state.slimeType];
    }

    @Override
    protected float getShadowRadius(EndSlimeRenderState state) {
        return state.size * 0.25F;
    }

    @Override
    protected void scale(EndSlimeRenderState state, PoseStack matrixStack) {
        matrixStack.scale(0.999F, 0.999F, 0.999F);
        matrixStack.translate(0.0F, 0.001F, 0.0F);
        float size = state.size;
        float squish = state.squish / (size * 0.5F + 1.0F);
        float squash = 1.0F / (squish + 1.0F);
        matrixStack.scale(squash * size, 1.0F / squash * size, squash * size);
    }

    @Override
    public EndSlimeRenderState createRenderState() {
        return new EndSlimeRenderState();
    }

    @Override
    public void extractRenderState(EndSlimeEntity entity, EndSlimeRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.squish = Mth.lerp(partialTick, entity.oSquish, entity.squish);
        state.size = entity.getSize();
        state.slimeType = entity.getSlimeType();
        state.isLake = entity.isLake();
        state.isAmber = entity.isAmber();
        state.isChorus = entity.isChorus();
    }

    private final class OverlayFeatureRenderer extends RenderLayer<EndSlimeRenderState, EndSlimeEntityModel<EndSlimeRenderState>> {
        private final EndSlimeEntityModel<EndSlimeRenderState> shellModel;

        public OverlayFeatureRenderer(
                RenderLayerParent<EndSlimeRenderState, EndSlimeEntityModel<EndSlimeRenderState>> layerParent,
                EntityRendererProvider.Context ctx
        ) {
            super(layerParent);
            this.shellModel = new EndSlimeEntityModel<>(
                    ctx.bakeLayer(EndEntitiesRenders.END_SLIME_SHELL_MODEL),
                    true
            );
        }

        @Override
        public void submit(
                PoseStack matrixStack,
                SubmitNodeCollector submitNodeCollector,
                int light,
                EndSlimeRenderState state,
                float yRot,
                float xRot
        ) {
            if (state.isInvisible) {
                return;
            }

            final Identifier texture = TEXTURE[state.slimeType];
            final int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);

            if (state.isLake) {
                flowerModel.setupAnim(state);
                submitNodeCollector.submitCustomGeometry(
                        matrixStack,
                        RenderTypes.entityCutoutCull(texture),
                        (pose, buffer) -> {
                            PoseStack local = new PoseStack();
                            local.mulPose(pose.pose());
                            flowerModel.renderToBuffer(local, buffer, light, overlay, 0xffffffff);
                        }
                );
            } else if (state.isAmber || state.isChorus) {
                cropModel.setupAnim(state);
                submitNodeCollector.submitCustomGeometry(
                        matrixStack,
                        RenderTypes.entityCutoutCull(texture),
                        (pose, buffer) -> {
                            PoseStack local = new PoseStack();
                            local.mulPose(pose.pose());
                            cropModel.renderToBuffer(local, buffer, light, overlay, 0xffffffff);
                        }
                );
            }

            shellModel.setupAnim(state);
            int shellColor = state.slimeType == 0 ? 0xA0ffffff : 0xffffffff;
            submitNodeCollector.submitCustomGeometry(
                    matrixStack,
                    RenderTypes.entityTranslucent(texture),
                    (pose, buffer) -> {
                        PoseStack local = new PoseStack();
                        local.mulPose(pose.pose());
                        shellModel.renderToBuffer(local, buffer, light, overlay, shellColor);
                    }
            );
        }
    }

    public static class EndSlimeRenderState extends SlimeRenderState {
        public int slimeType;
        public boolean isLake;
        public boolean isAmber;
        public boolean isChorus;
    }

    static {
        TEXTURE[0] = BetterEnd.C.mk("textures/entity/end_slime/end_slime.png");
        TEXTURE[1] = BetterEnd.C.mk("textures/entity/end_slime/end_slime_mossy.png");
        TEXTURE[2] = BetterEnd.C.mk("textures/entity/end_slime/end_slime_lake.png");
        TEXTURE[3] = BetterEnd.C.mk("textures/entity/end_slime/end_slime_amber.png");
        GLOW[0] = RenderTypes.eyes(BetterEnd.C.mk("textures/entity/end_slime/end_slime_glow.png"));
        GLOW[1] = GLOW[0];
        GLOW[2] = RenderTypes.eyes(BetterEnd.C.mk("textures/entity/end_slime/end_slime_lake_glow.png"));
        GLOW[3] = RenderTypes.eyes(BetterEnd.C.mk("textures/entity/end_slime/end_slime_amber_glow.png"));
    }
}

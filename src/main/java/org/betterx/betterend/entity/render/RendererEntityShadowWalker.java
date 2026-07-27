package org.betterx.betterend.entity.render;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.entity.ShadowWalkerEntity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

public class RendererEntityShadowWalker extends HumanoidMobRenderer<ShadowWalkerEntity, HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {
    private static final Identifier TEXTURE = BetterEnd.C.mk("textures/entity/shadow_walker.png");

    public RendererEntityShadowWalker(EntityRendererProvider.Context ctx) {
        super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public Identifier getTextureLocation(HumanoidRenderState state) {
        return TEXTURE;
    }

    @Override
    public HumanoidRenderState createRenderState() {
        return new HumanoidRenderState();
    }
}

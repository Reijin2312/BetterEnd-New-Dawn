package org.betterx.betterend.entity.render;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.entity.ShadowWalkerEntity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;

public class RendererEntityShadowWalker extends HumanoidMobRenderer<ShadowWalkerEntity, AvatarRenderState, PlayerModel> {
    private static final Identifier TEXTURE = BetterEnd.C.mk("textures/entity/shadow_walker.png");

    public RendererEntityShadowWalker(EntityRendererProvider.Context ctx) {
        super(ctx, new PlayerModel(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public Identifier getTextureLocation(AvatarRenderState state) {
        return TEXTURE;
    }

    @Override
    public AvatarRenderState createRenderState() {
        return new AvatarRenderState();
    }
}

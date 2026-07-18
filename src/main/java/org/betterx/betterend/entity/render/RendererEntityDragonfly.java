package org.betterx.betterend.entity.render;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.entity.DragonflyEntity;
import org.betterx.betterend.entity.model.DragonflyEntityModel;
import org.betterx.betterend.registry.EndEntitiesRenders;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class RendererEntityDragonfly extends MobRenderer<DragonflyEntity, LivingEntityRenderState, DragonflyEntityModel> {
    private static final Identifier TEXTURE = BetterEnd.C.mk("textures/entity/dragonfly.png");
    private static final RenderType GLOW = RenderTypes.eyes(BetterEnd.C.mk("textures/entity/dragonfly_glow.png"));

    public RendererEntityDragonfly(EntityRendererProvider.Context ctx) {
        super(ctx, new DragonflyEntityModel(ctx.bakeLayer(EndEntitiesRenders.DRAGONFLY_MODEL)), 0.5F);
        this.addLayer(new EyesLayer<LivingEntityRenderState, DragonflyEntityModel>(this) {
            @Override
            public RenderType renderType() {
                return GLOW;
            }
        });
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }
}

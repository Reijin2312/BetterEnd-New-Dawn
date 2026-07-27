package org.betterx.betterend.registry;

// import org.betterx.betterend.client.render.FlowerPotItemRenderer;
import org.betterx.betterend.client.render.PedestalItemRenderer;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@OnlyIn(Dist.CLIENT)
public class EndBlockEntityRenders {
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(EndBlockEntities.PEDESTAL, PedestalItemRenderer::new);
        event.registerBlockEntityRenderer(EndBlockEntities.ETERNAL_PEDESTAL, PedestalItemRenderer::new);
        event.registerBlockEntityRenderer(EndBlockEntities.INFUSION_PEDESTAL, PedestalItemRenderer::new);
        // Disabled together with the experimental block-entity flower-pot pipeline.
        // event.registerBlockEntityRenderer(EndBlockEntities.FLOWER_POT, FlowerPotItemRenderer::new);
    }
}

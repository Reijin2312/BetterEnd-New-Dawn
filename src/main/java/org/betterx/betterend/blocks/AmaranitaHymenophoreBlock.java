package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourCompostable;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.blocks.BaseBlock;
import org.betterx.bclib.client.render.BCLRenderLayer;
import org.betterx.bclib.interfaces.RenderLayerProvider;

import net.minecraft.world.level.block.SoundType;

public class AmaranitaHymenophoreBlock extends BaseBlock.Wood implements RenderLayerProvider, BehaviourCompostable {
    public AmaranitaHymenophoreBlock() {
        super(BehaviourBuilders.createWood().sound(SoundType.WOOD));
    }

    @Override
    public BCLRenderLayer getRenderLayer() {
        return BCLRenderLayer.CUTOUT;
    }
}

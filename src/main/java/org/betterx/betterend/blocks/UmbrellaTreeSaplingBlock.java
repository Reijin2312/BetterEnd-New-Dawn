package org.betterx.betterend.blocks;

import org.betterx.bclib.client.render.BCLRenderLayer;
import org.betterx.betterend.blocks.basis.PottableFeatureSapling;
import org.betterx.betterend.interfaces.survives.SurvivesOnJungleMoss;
import org.betterx.betterend.registry.features.EndConfiguredVegetation;
import org.betterx.betterend.world.features.trees.UmbrellaTreeFeature;


public class UmbrellaTreeSaplingBlock extends PottableFeatureSapling implements SurvivesOnJungleMoss {
    public UmbrellaTreeSaplingBlock() {
        super((level, pos, state, rnd) -> EndConfiguredVegetation.UMBRELLA_TREE.placeInWorld(level, pos, rnd));
    }

    @Override
    public BCLRenderLayer getRenderLayer() {
        return BCLRenderLayer.TRANSLUCENT;
    }
}

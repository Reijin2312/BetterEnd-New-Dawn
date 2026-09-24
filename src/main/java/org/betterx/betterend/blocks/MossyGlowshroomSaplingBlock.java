package org.betterx.betterend.blocks;

import org.betterx.betterend.blocks.basis.PottableFeatureSapling;
import org.betterx.betterend.interfaces.survives.SurvivesOnMossOrMycelium;
import org.betterx.betterend.registry.features.EndConfiguredVegetation;
import org.betterx.betterend.world.features.trees.MossyGlowshroomFeature;


public class MossyGlowshroomSaplingBlock extends PottableFeatureSapling implements SurvivesOnMossOrMycelium {
    public MossyGlowshroomSaplingBlock() {
        super(7, (level, pos, state, rnd) -> EndConfiguredVegetation.MOSSY_GLOWSHROOM.placeInWorld(level, pos, rnd));
    }
}

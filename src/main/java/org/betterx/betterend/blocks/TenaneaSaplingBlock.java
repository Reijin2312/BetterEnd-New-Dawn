package org.betterx.betterend.blocks;

import org.betterx.betterend.blocks.basis.PottableFeatureSapling;
import org.betterx.betterend.interfaces.survives.SurvivesOnPinkMoss;
import org.betterx.betterend.registry.features.EndConfiguredVegetation;
import org.betterx.betterend.world.features.trees.TenaneaFeature;


public class TenaneaSaplingBlock extends PottableFeatureSapling implements SurvivesOnPinkMoss {
    public TenaneaSaplingBlock() {
        super((level, pos, state, rnd) -> EndConfiguredVegetation.TENANEA.placeInWorld(level, pos, rnd));
    }
}

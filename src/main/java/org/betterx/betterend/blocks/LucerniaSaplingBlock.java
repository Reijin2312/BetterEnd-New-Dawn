package org.betterx.betterend.blocks;

import org.betterx.betterend.blocks.basis.PottableFeatureSapling;
import org.betterx.betterend.interfaces.survives.SurvivesOnRutiscus;
import org.betterx.betterend.registry.features.EndConfiguredVegetation;


public class LucerniaSaplingBlock extends PottableFeatureSapling implements SurvivesOnRutiscus {
    public LucerniaSaplingBlock() {
        super((level, pos, state, rnd) -> EndConfiguredVegetation.LUCERNIA.placeInWorld(level, pos, rnd));
    }
}

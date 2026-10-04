package org.betterx.betterend.blocks;

import org.betterx.betterend.blocks.basis.PottableFeatureSapling;
import org.betterx.betterend.interfaces.survives.SurvivesOnChorusNylium;
import org.betterx.betterend.registry.features.EndConfiguredVegetation;


public class PythadendronSaplingBlock extends PottableFeatureSapling implements SurvivesOnChorusNylium {
    public PythadendronSaplingBlock() {
        super((level, pos, state, rnd) -> EndConfiguredVegetation.PYTHADENDRON_TREE.placeInWorld(level, pos, rnd));
    }
}

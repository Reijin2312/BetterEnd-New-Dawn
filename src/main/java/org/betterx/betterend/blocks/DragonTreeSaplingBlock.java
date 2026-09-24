package org.betterx.betterend.blocks;

import org.betterx.betterend.blocks.basis.PottableFeatureSapling;
import org.betterx.betterend.interfaces.survives.SurvivesOnShadowGrass;
import org.betterx.betterend.registry.features.EndConfiguredVegetation;
import org.betterx.betterend.world.features.trees.DragonTreeFeature;

import net.minecraft.world.level.block.Block;

public class DragonTreeSaplingBlock extends PottableFeatureSapling implements SurvivesOnShadowGrass {
    public DragonTreeSaplingBlock() {
        super((level, pos, state, rnd) -> EndConfiguredVegetation.DRAGON_TREE.placeInWorld(level, pos, rnd));
    }

    @Override
    public boolean canPlantOn(Block block) {
        return isSurvivable(block.defaultBlockState());
    }
}

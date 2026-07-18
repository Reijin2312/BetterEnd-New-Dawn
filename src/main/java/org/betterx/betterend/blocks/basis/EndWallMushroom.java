package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.blocks.WallMushroomBlock;
import org.betterx.betterend.interfaces.survives.SurvivesOnEndStone;
import org.betterx.wover.block.api.model.BlockModelProvider;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;

import net.minecraft.world.level.block.state.BlockState;


public class EndWallMushroom extends WallMushroomBlock implements SurvivesOnEndStone, BlockModelProvider {

    public EndWallMushroom(int light) {
        super(light);
    }

    @Override
    public boolean isTerrain(BlockState state) {
        return SurvivesOnEndStone.super.isTerrain(state);
    }

    @Override
    public void provideBlockModels(Object modelGenerator) {
    WoverBlockModelGenerators generator = (WoverBlockModelGenerators) modelGenerator;
        generator.createCubeModel(this);
        generator.createFlatItem(this);
    }
}

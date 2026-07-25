package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.blocks.BaseUnderwaterWallPlantBlock;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class EndUnderwaterWallPlantBlock extends BaseUnderwaterWallPlantBlock {

    public EndUnderwaterWallPlantBlock(MapColor color) {
        super(BehaviourBuilders.createWaterPlant(color));
    }

    @Override
    public boolean isTerrain(BlockState state) {
        return true;
    }
}

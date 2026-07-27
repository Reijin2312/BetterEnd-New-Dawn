package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourPlant;
import org.betterx.bclib.blocks.BaseWallPlantBlock;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.MapColor;

public class EndWallPlantBlock extends BaseWallPlantBlock implements BehaviourPlant {
    public EndWallPlantBlock(MapColor color) {
        super(BehaviourBuilders.createPlant(color));
    }

    public EndWallPlantBlock(MapColor color, int light) {
        super(BehaviourBuilders.createPlant(color).lightLevel((bs) -> light));
    }

    @Override
    public boolean isTerrain(BlockState state) {
        return state.getBlock() != EndBlocks.TENANEA_OUTER_LEAVES
                && state.getBlock() != EndBlocks.LUCERNIA_OUTER_LEAVES;
    }

    @Override
    public boolean isSupport(LevelReader world, BlockPos pos, BlockState state, Direction direction) {
        if (state.getBlock() == EndBlocks.TENANEA_OUTER_LEAVES
                || state.getBlock() == EndBlocks.LUCERNIA_OUTER_LEAVES) {
            return false;
        }
        return super.isSupport(world, pos, state, direction);
    }
}

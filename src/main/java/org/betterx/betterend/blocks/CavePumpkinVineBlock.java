package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourPlant;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.blocks.basis.EndPlantWithAgeBlock;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.block.api.BlockProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CavePumpkinVineBlock extends EndPlantWithAgeBlock implements BehaviourPlant {
    public CavePumpkinVineBlock() {
        super(BehaviourBuilders.createPlant(MapColor.TERRACOTTA_ORANGE));
    }

    private static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 16, 12);

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos above = pos.above();
        return BlocksHelper.isDecorationSupport(world, above, world.getBlockState(above), Direction.DOWN);
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        int age = state.getValue(AGE);
        BlockState down = world.getBlockState(pos.below());
        if (down.canBeReplaced() || (down.is(EndBlocks.CAVE_PUMPKIN) && down.getValue(BlockProperties.SMALL))) {
            if (age < 3) {
                world.setBlockAndUpdate(pos, state.setValue(AGE, age + 1));
            }
            if (age == 2) {
                world.setBlockAndUpdate(
                        pos.below(),
                        EndBlocks.CAVE_PUMPKIN.defaultBlockState().setValue(BlockProperties.SMALL, true)
                );
            } else if (age == 3) {
                world.setBlockAndUpdate(pos.below(), EndBlocks.CAVE_PUMPKIN.defaultBlockState());
            }
        }
    }

    @Override
    public void growAdult(WorldGenLevel world, RandomSource random, BlockPos pos) {
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction facing,
            BlockState neighborState,
            LevelAccessor world,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        state = super.updateShape(state, facing, neighborState, world, pos, neighborPos);
        if (state.is(this) && state.getValue(BlockProperties.AGE) > 1) {
            BlockState down = world.getBlockState(pos.below());
            if (!down.is(EndBlocks.CAVE_PUMPKIN)) {
                state = state.setValue(BlockProperties.AGE, 1);
            }
        }
        return state;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE;
    }

    @Override
    public boolean isTerrain(BlockState state) {
        return true;
    }
}

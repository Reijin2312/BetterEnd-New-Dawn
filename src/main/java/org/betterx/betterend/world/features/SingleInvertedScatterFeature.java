package org.betterx.betterend.world.features;

import com.mojang.serialization.MapCodec;
import org.betterx.bclib.blocks.BaseAttachedBlock;
import org.betterx.bclib.util.BlocksHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class SingleInvertedScatterFeature extends InvertedScatterFeature<SinglePlantFeatureConfig> {
    public static final MapCodec<SingleInvertedScatterFeature> CODEC = SinglePlantFeatureConfig.CODEC.xmap(SingleInvertedScatterFeature::new, f -> f.config);
    private BlockState block;

    public SingleInvertedScatterFeature(SinglePlantFeatureConfig config) {
        super(config);
    }

    @Override
    public MapCodec<SingleInvertedScatterFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean canGenerate(
            SinglePlantFeatureConfig cfg,
            WorldGenLevel world,
            RandomSource random,
            BlockPos center,
            BlockPos blockPos,
            float radius
    ) {
        if (!world.isEmptyBlock(blockPos)) {
            return false;
        }
        block = cfg.getPlantState(world, random, blockPos);
        BlockState state = block;
        if (block.getBlock() instanceof BaseAttachedBlock) {
            state = state.setValue(BlockStateProperties.FACING, Direction.DOWN);
        }
        return state.canSurvive(world, blockPos);
    }

    @Override
    public void generate(SinglePlantFeatureConfig cfg, WorldGenLevel world, RandomSource random, BlockPos blockPos) {
        BlockState state = block;
        if (block.getBlock() instanceof BaseAttachedBlock) {
            state = state.setValue(BlockStateProperties.FACING, Direction.DOWN);
        }
        BlocksHelper.setWithoutUpdate(world, blockPos, state);
    }
}

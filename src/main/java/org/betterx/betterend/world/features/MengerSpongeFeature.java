package org.betterx.betterend.world.features;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public class MengerSpongeFeature extends UnderwaterPlantScatter<ScatterFeatureConfig> {
    public MengerSpongeFeature() {
        super(ScatterFeatureConfig.CODEC);
    }

    @Override
    public void generate(ScatterFeatureConfig cfg, WorldGenLevel world, RandomSource random, BlockPos blockPos) {
        BlocksHelper.setWithoutUpdate(world, blockPos, EndBlocks.MENGER_SPONGE_WET);
        if (random.nextBoolean()) {
            for (Direction dir : BlocksHelper.DIRECTIONS) {
                BlockPos pos = blockPos.relative(dir);
                if (canReplace(world.getBlockState(pos))) {
                    BlocksHelper.setWithoutUpdate(world, pos, EndBlocks.MENGER_SPONGE_WET);
                }
            }
        }
    }

    private boolean canReplace(BlockState state) {
        if (state.is(EndBlocks.END_LOTUS_STEM)) {
            return false;
        }
        return !state.getFluidState().isEmpty() || state.canBeReplaced();
    }
}

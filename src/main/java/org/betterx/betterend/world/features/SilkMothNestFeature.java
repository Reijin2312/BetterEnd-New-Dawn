package org.betterx.betterend.world.features;


import com.mojang.serialization.MapCodec;

import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.util.GlobalState;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;

public class SilkMothNestFeature extends DefaultFeature {
    public static final MapCodec<SilkMothNestFeature> CODEC = MapCodec.unit(SilkMothNestFeature::new);

    @Override
    public MapCodec<SilkMothNestFeature> codec() {
        return CODEC;
    }
    private boolean canGenerate(WorldGenLevel world, BlockPos pos) {
        BlockState state = world.getBlockState(pos.above());
        if (state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS)) {
            state = world.getBlockState(pos);
            if ((state.isAir() || state.is(EndBlocks.TENANEA_OUTER_LEAVES)) && world.isEmptyBlock(pos.below())) {
                for (Direction dir : BlocksHelper.HORIZONTAL) {
                    return !world.getBlockState(pos.below().relative(dir)).isSolid();
                }
            }
        }
        return false;
    }

    @Override
    public boolean place(WorldGenLevel featureWorld, ChunkGenerator featureGenerator, RandomSource featureRandom, BlockPos featureOrigin) {
        final MutableBlockPos POS = GlobalState.stateForThread().POS;
        final RandomSource random = featureRandom;
        final BlockPos center = featureOrigin;
        final WorldGenLevel world = featureWorld;
        int maxY = world.getHeight(Heightmap.Types.WORLD_SURFACE, center.getX(), center.getZ());
        int minY = BlocksHelper.upRay(world, new BlockPos(center.getX(), 0, center.getZ()), maxY);
        POS.set(center);
        for (int y = maxY; y > minY; y--) {
            POS.setY(y);
            if (canGenerate(world, POS)) {
                Direction dir = BlocksHelper.randomHorizontal(random);
                BlocksHelper.setWithoutUpdate(
                        world,
                        POS,
                        EndBlocks.SILK_MOTH_NEST.defaultBlockState()
                                                .setValue(BlockStateProperties.HORIZONTAL_FACING, dir)
                                                .setValue(BlockProperties.ACTIVE, false)
                );
                POS.setY(y - 1);
                BlocksHelper.setWithoutUpdate(
                        world,
                        POS,
                        EndBlocks.SILK_MOTH_NEST.defaultBlockState()
                                                .setValue(BlockStateProperties.HORIZONTAL_FACING, dir)
                );
                return true;
            }
        }
        return false;
    }
}

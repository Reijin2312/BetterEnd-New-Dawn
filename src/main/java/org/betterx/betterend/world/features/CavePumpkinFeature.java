package org.betterx.betterend.world.features;


import com.mojang.serialization.MapCodec;

import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.blocks.EndBlockProperties;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class CavePumpkinFeature extends DefaultFeature {
    public static final MapCodec<CavePumpkinFeature> CODEC = MapCodec.unit(CavePumpkinFeature::new);

    @Override
    public MapCodec<CavePumpkinFeature> codec() {
        return CODEC;
    }
    @Override
    public boolean place(WorldGenLevel featureWorld, ChunkGenerator featureGenerator, RandomSource featureRandom, BlockPos featureOrigin) {
        final RandomSource random = featureRandom;
        final BlockPos pos = featureOrigin;
        final WorldGenLevel world = featureWorld;
        if (!world.getBlockState(pos.above())
                  .is(CommonBlockTags.END_STONES) || !world.isEmptyBlock(pos) || !world.isEmptyBlock(
                pos.below())) {
            return false;
        }

        int age = random.nextInt(4);
        BlocksHelper.setWithoutUpdate(
                world,
                pos,
                EndBlocks.CAVE_PUMPKIN_SEED.defaultBlockState().setValue(EndBlockProperties.AGE, age)
        );
        if (age > 1) {
            BlocksHelper.setWithoutUpdate(
                    world,
                    pos.below(),
                    EndBlocks.CAVE_PUMPKIN.defaultBlockState().setValue(EndBlockProperties.SMALL, age < 3)
            );
        }

        return true;
    }
}

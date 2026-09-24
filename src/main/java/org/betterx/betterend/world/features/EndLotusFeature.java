package org.betterx.betterend.world.features;

import com.mojang.serialization.MapCodec;
import org.betterx.betterend.blocks.EndLotusSeedBlock;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;

public class EndLotusFeature extends UnderwaterPlantScatter<ScatterFeatureConfig> {
    public static final MapCodec<EndLotusFeature> CODEC = ScatterFeatureConfig.CODEC.xmap(EndLotusFeature::new, f -> f.config);

    public EndLotusFeature(ScatterFeatureConfig config) {
        super(config);
    }

    @Override
    public MapCodec<EndLotusFeature> codec() {
        return CODEC;
    }

    @Override
    public void generate(ScatterFeatureConfig cfg, WorldGenLevel world, RandomSource random, BlockPos blockPos) {
        EndLotusSeedBlock seed = (EndLotusSeedBlock) EndBlocks.END_LOTUS_SEED;
        seed.grow(world, random, blockPos);
    }

    @Override
    protected int getChance() {
        return 15;
    }
}

package org.betterx.betterend.world.features;

import com.mojang.serialization.MapCodec;
import org.betterx.betterend.blocks.EndLilySeedBlock;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;

public class EndLilyFeature extends UnderwaterPlantScatter<ScatterFeatureConfig> {
    public static final MapCodec<EndLilyFeature> CODEC = ScatterFeatureConfig.CODEC.xmap(EndLilyFeature::new, f -> f.config);

    public EndLilyFeature(ScatterFeatureConfig config) {
        super(config);
    }

    @Override
    public MapCodec<EndLilyFeature> codec() {
        return CODEC;
    }

    @Override
    public void generate(ScatterFeatureConfig cfg, WorldGenLevel world, RandomSource random, BlockPos blockPos) {
        EndLilySeedBlock seed = (EndLilySeedBlock) EndBlocks.END_LILY_SEED;
        seed.grow(world, random, blockPos);
    }

    @Override
    protected int getChance() {
        return 15;
    }
}

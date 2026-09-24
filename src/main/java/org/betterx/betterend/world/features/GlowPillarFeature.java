package org.betterx.betterend.world.features;

import com.mojang.serialization.MapCodec;

import org.betterx.betterend.blocks.basis.EndPlantWithAgeBlock;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;

public class GlowPillarFeature extends ScatterFeature<ScatterFeatureConfig> {
    public static final MapCodec<GlowPillarFeature> CODEC = ScatterFeatureConfig.CODEC.xmap(GlowPillarFeature::new, f -> f.config);

    @Override
    public MapCodec<GlowPillarFeature> codec() {
        return CODEC;
    }
    public GlowPillarFeature(ScatterFeatureConfig config) {
        super(config);
    }

    @Override
    public boolean canGenerate(
            ScatterFeatureConfig cfg,
            WorldGenLevel world,
            RandomSource random,
            BlockPos center,
            BlockPos blockPos,
            float radius
    ) {
        return EndBlocks.GLOWING_PILLAR_SEED.defaultBlockState().canSurvive(world, blockPos);
    }

    @Override
    public void generate(
            ScatterFeatureConfig cfg, WorldGenLevel world, RandomSource random, BlockPos blockPos
    ) {
        EndPlantWithAgeBlock seed = ((EndPlantWithAgeBlock) EndBlocks.GLOWING_PILLAR_SEED);
        seed.growAdult(world, random, blockPos);
    }

    @Override
    protected int getChance() {
        return 10;
    }
}

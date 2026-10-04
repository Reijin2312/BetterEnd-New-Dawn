package org.betterx.betterend.world.features;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public class WallPlantOnLogFeature extends WallPlantFeature {
    public static final MapCodec<WallPlantOnLogFeature> CODEC = WallPlantFeatureConfig.CODEC.xmap(WallPlantOnLogFeature::new, f -> f.config);

    public WallPlantOnLogFeature(WallPlantFeatureConfig config) {
        super(config);
    }

    @Override
    public MapCodec<WallPlantOnLogFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean canGenerate(
            WallPlantFeatureConfig cfg,
            WorldGenLevel world,
            RandomSource random,
            BlockPos pos,
            Direction dir
    ) {
        plant = cfg.getPlantState(world, random, pos);
        BlockPos blockPos = pos.relative(dir.getOpposite());
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.is(BlockTags.LOGS);
    }
}

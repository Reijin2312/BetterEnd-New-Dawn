package org.betterx.betterend.world.features;

import com.mojang.serialization.MapCodec;
import org.betterx.bclib.blocks.BaseAttachedBlock;
import org.betterx.bclib.blocks.BaseWallPlantBlock;
import org.betterx.bclib.util.BlocksHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;

public class WallPlantFeature extends WallScatterFeature<WallPlantFeatureConfig> {
    public static final MapCodec<WallPlantFeature> CODEC = WallPlantFeatureConfig.CODEC.xmap(WallPlantFeature::new, f -> f.config);
    protected BlockState plant;

    public WallPlantFeature(WallPlantFeatureConfig config) {
        super(config);
    }

    @Override
    public MapCodec<? extends Feature> codec() {
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
        Block block = plant.getBlock();
        if (block instanceof BaseWallPlantBlock) {
            BlockState state = plant.setValue(BaseWallPlantBlock.FACING, dir);
            return state.canSurvive(world, pos);
        } else if (block instanceof BaseAttachedBlock) {
            BlockState state = plant.setValue(BlockStateProperties.FACING, dir);
            return state.canSurvive(world, pos);
        }
        return plant.canSurvive(world, pos);
    }

    @Override
    public void generate(
            WallPlantFeatureConfig cfg,
            WorldGenLevel world,
            RandomSource random,
            BlockPos pos,
            Direction dir
    ) {
        Block block = plant.getBlock();
        if (block instanceof BaseWallPlantBlock) {
            plant = plant.setValue(BaseWallPlantBlock.FACING, dir);
        } else if (block instanceof BaseAttachedBlock) {
            plant = plant.setValue(BlockStateProperties.FACING, dir);
        }
        BlocksHelper.setWithoutUpdate(world, pos, plant);
    }
}

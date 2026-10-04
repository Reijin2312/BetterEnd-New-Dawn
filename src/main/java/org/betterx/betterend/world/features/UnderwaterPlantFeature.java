package org.betterx.betterend.world.features;

import com.mojang.serialization.MapCodec;
import org.betterx.bclib.blocks.BaseDoublePlantBlock;
import org.betterx.bclib.util.BlocksHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public class UnderwaterPlantFeature extends UnderwaterPlantScatter<SinglePlantFeatureConfig> {
    private BlockState plant;

    public static final MapCodec<UnderwaterPlantFeature> CODEC = SinglePlantFeatureConfig.CODEC.xmap(UnderwaterPlantFeature::new, f -> f.config);

    public UnderwaterPlantFeature(SinglePlantFeatureConfig config) {
        super(config);
    }

    @Override
    public MapCodec<? extends UnderwaterPlantFeature> codec() {
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
        plant = cfg.getPlantState(world, random, blockPos);
        //noinspection deprecation
        return super.canSpawn(cfg, world, blockPos) && plant.canSurvive(world, blockPos);
    }

    @Override
    public void generate(SinglePlantFeatureConfig cfg, WorldGenLevel world, RandomSource random, BlockPos blockPos) {
        if (plant.getBlock() instanceof BaseDoublePlantBlock) {
            int rot = random.nextInt(4);
            BlockState state = plant.setValue(BaseDoublePlantBlock.ROTATION, rot);
            BlocksHelper.setWithoutUpdate(world, blockPos, state);
            BlocksHelper.setWithoutUpdate(world, blockPos.above(), state.setValue(BaseDoublePlantBlock.TOP, true));
        } else {
            BlocksHelper.setWithoutUpdate(world, blockPos, plant);
        }
    }
}

package org.betterx.betterend.world.features;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;

public class WallPlantOnLogFeature extends WallPlantFeature {
    @Override
    public void generate(WallPlantFeatureConfig cfg, WorldGenLevel world, RandomSource random, BlockPos pos, Direction dir) {
        if (!world.getBlockState(pos.relative(dir.getOpposite())).is(BlockTags.LOGS)) {
            return;
        }
        super.generate(cfg, world, random, pos, dir);
    }
}

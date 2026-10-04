package org.betterx.betterend.world.features;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;

public abstract class SkyScatterFeature extends ScatterFeature<ScatterFeatureConfig> {
    protected SkyScatterFeature(ScatterFeatureConfig config) {
        super(config);
    }

    @Override
    protected int getChance() {
        return 10;
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
        if (!world.isEmptyBlock(blockPos)) {
            return false;
        }

        for (Direction dir : BlocksHelper.HORIZONTAL) {
            if (!world.isEmptyBlock(blockPos.relative(dir))) {
                return false;
            }
        }

        int maxD = getYOffset() + 2;
        int maxV = getYOffset() - 2;

        return BlocksHelper.upRay(world, blockPos, maxD) > maxV && BlocksHelper.downRay(world, blockPos, maxD) > maxV;
    }

    @Override
    protected boolean canSpawn(ScatterFeatureConfig cfg, WorldGenLevel world, BlockPos pos) {
        return true;
    }

    @Override
    protected BlockPos getCenterGround(ScatterFeatureConfig cfg, WorldGenLevel world, BlockPos pos) {
        return new BlockPos(pos.getX(), MHelper.randRange(32, 192, world.getRandom()), pos.getZ());
    }

    protected boolean getGroundPlant(ScatterFeatureConfig cfg, WorldGenLevel world, MutableBlockPos pos) {
        pos.setY(pos.getY() + MHelper.randRange(-getYOffset(), getYOffset(), world.getRandom()));
        return true;
    }
}

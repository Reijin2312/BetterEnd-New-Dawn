package org.betterx.betterend.world.features;

import org.betterx.bclib.blocks.BaseAttachedBlock;
import org.betterx.bclib.blocks.BaseWallPlantBlock;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.BetterEnd;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WallPlantFeature extends WallScatterFeature<WallPlantFeatureConfig> {
    private static final Set<String> WARNED_MISSING_PROPERTIES = ConcurrentHashMap.newKeySet();

    public WallPlantFeature() {
        super(WallPlantFeatureConfig.CODEC);
    }

    @Override
    public boolean canGenerate(
            WallPlantFeatureConfig cfg,
            WorldGenLevel world,
            RandomSource random,
            BlockPos pos,
            Direction dir
    ) {
        return true;
    }

    @Override
    public void generate(WallPlantFeatureConfig cfg, WorldGenLevel world, RandomSource random, BlockPos pos, Direction dir) {
        BlockState plant = cfg.getPlantState(random, pos);
        Block block = plant.getBlock();
        if (block instanceof BaseWallPlantBlock) {
            if (!plant.hasProperty(BaseWallPlantBlock.FACING)) {
                logMissingFacingProperty(block, pos, dir, plant, "BaseWallPlantBlock");
                return;
            }
            plant = plant.setValue(BaseWallPlantBlock.FACING, dir);
        } else if (block instanceof BaseAttachedBlock) {
            if (!plant.hasProperty(BlockStateProperties.FACING)) {
                logMissingFacingProperty(block, pos, dir, plant, "BaseAttachedBlock");
                return;
            }
            plant = plant.setValue(BlockStateProperties.FACING, dir);
        }

        if (!plant.canSurvive(world, pos)) {
            return;
        }
        BlocksHelper.setWithoutUpdate(world, pos, plant);
    }

    private static void logMissingFacingProperty(Block block, BlockPos pos, Direction dir, BlockState plant, String blockType) {
        String blockId = BuiltInRegistries.BLOCK.getKey(block).toString();
        if (WARNED_MISSING_PROPERTIES.add(blockType + "|" + blockId)) {
            BetterEnd.LOGGER.warn("WallPlantFeature: block {} ({}) is {} but has no FACING property; samplePos={}, dir={}, state={}", blockId, block.getClass().getName(), blockType, pos, dir, plant);
        }
    }
}

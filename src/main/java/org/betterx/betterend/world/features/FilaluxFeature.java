package org.betterx.betterend.world.features;

import com.mojang.serialization.MapCodec;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.block.api.BlockProperties.TripleShape;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class FilaluxFeature extends SkyScatterFeature {
    public static final MapCodec<FilaluxFeature> CODEC = ScatterFeatureConfig.CODEC.xmap(FilaluxFeature::new, f -> f.config);

    public FilaluxFeature(ScatterFeatureConfig config) {
        super(config);
    }

    @Override
    public MapCodec<FilaluxFeature> codec() {
        return CODEC;
    }

    @Override
    public void generate(ScatterFeatureConfig cfg, WorldGenLevel world, RandomSource random, BlockPos blockPos) {
        BlockState vine = EndBlocks.FILALUX.defaultBlockState();
        BlockState wings = EndBlocks.FILALUX_WINGS.defaultBlockState();
        BlocksHelper.setWithoutUpdate(world, blockPos, EndBlocks.FILALUX_LANTERN);
        BlocksHelper.setWithoutUpdate(
                world,
                blockPos.above(),
                wings.setValue(BlockStateProperties.FACING, Direction.UP)
        );
        for (Direction dir : BlocksHelper.HORIZONTAL) {
            BlocksHelper.setWithoutUpdate(
                    world,
                    blockPos.relative(dir),
                    wings.setValue(BlockStateProperties.FACING, dir)
            );
        }
        int length = MHelper.randRange(1, 3, random);
        for (int i = 1; i <= length; i++) {
            TripleShape shape = length > 1 ? TripleShape.TOP : TripleShape.BOTTOM;
            if (i > 1) {
                shape = i == length ? TripleShape.BOTTOM : TripleShape.MIDDLE;
            }
            BlocksHelper.setWithoutUpdate(world, blockPos.below(i), vine.setValue(BlockProperties.TRIPLE_SHAPE, shape));
        }
    }
}

package org.betterx.betterend.world.features.bushes;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;

import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.block.api.BlockProperties.TripleShape;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.block.state.BlockState;

public class LargeAmaranitaFeature extends DefaultFeature {
    public static final MapCodec<LargeAmaranitaFeature> CODEC = MapCodec.unit(LargeAmaranitaFeature::new);

    @Override
    public MapCodec<LargeAmaranitaFeature> codec() {
        return CODEC;
    }
    @Override
    public boolean place(WorldGenLevel featureWorld, ChunkGenerator featureGenerator, RandomSource featureRandom, BlockPos featureOrigin) {
        final RandomSource random = featureRandom;
        final BlockPos pos = featureOrigin;
        final WorldGenLevel world = featureWorld;
        if (!world.getBlockState(pos.below()).is(CommonBlockTags.END_STONES)) return false;

        MutableBlockPos mut = new MutableBlockPos().set(pos);
        int height = MHelper.randRange(2, 3, random);
        for (int i = 1; i < height; i++) {
            mut.setY(mut.getY() + 1);
            if (!world.isEmptyBlock(mut)) {
                return false;
            }
        }
        mut.set(pos);

        BlockState state = EndBlocks.LARGE_AMARANITA_MUSHROOM.defaultBlockState();
        BlocksHelper.setWithUpdate(world, mut, state.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.BOTTOM));
        if (height > 2) {
            BlocksHelper.setWithUpdate(
                    world,
                    mut.move(Direction.UP),
                    state.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.MIDDLE)
            );
        }
        BlocksHelper.setWithUpdate(
                world,
                mut.move(Direction.UP),
                state.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.TOP)
        );

        return true;
    }
}

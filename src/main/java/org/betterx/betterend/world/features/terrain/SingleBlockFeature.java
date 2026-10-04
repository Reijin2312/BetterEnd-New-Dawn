package org.betterx.betterend.world.features.terrain;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class SingleBlockFeature implements Feature {
    public static final MapCodec<SingleBlockFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(BlockStateProvider.DIRECT_CODEC.fieldOf("to_place").forGetter(o -> o.toPlace))
            .apply(instance, SingleBlockFeature::new));
    public final BlockStateProvider toPlace;

    public SingleBlockFeature(BlockStateProvider toPlace) {
        this.toPlace = toPlace;
    }

    @Override
    public MapCodec<SingleBlockFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel world, ChunkGenerator generator, RandomSource random, BlockPos pos) {
        if (!world.getBlockState(pos.below()).is(CommonBlockTags.END_STONES)) {
            return false;
        }

        BlockState state = toPlace.getState(world, random, pos);
        if (state.getBlock().getStateDefinition().getProperty("waterlogged") != null) {
            boolean waterlogged = !world.getFluidState(pos).isEmpty();
            state = state.setValue(BlockStateProperties.WATERLOGGED, waterlogged);
        }
        BlocksHelper.setWithoutUpdate(world, pos, state);

        return true;
    }
}

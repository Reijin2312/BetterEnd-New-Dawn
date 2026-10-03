package org.betterx.betterend.world.features;


import com.mojang.serialization.MapCodec;

import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.betterend.blocks.NeonCactusPlantBlock;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.block.state.BlockState;

public class NeonCactusFeature extends DefaultFeature {
    public static final MapCodec<NeonCactusFeature> CODEC = MapCodec.unit(NeonCactusFeature::new);

    @Override
    public MapCodec<NeonCactusFeature> codec() {
        return CODEC;
    }
    @Override
    public boolean place(WorldGenLevel featureWorld, ChunkGenerator featureGenerator, RandomSource featureRandom, BlockPos featureOrigin) {
        final RandomSource random = featureRandom;
        final BlockPos pos = featureOrigin;
        final WorldGenLevel world = featureWorld;
        BlockState ground = world.getBlockState(pos.below());
        if (!ground.is(EndBlocks.ENDSTONE_DUST) && !ground.is(EndBlocks.END_MOSS)) {
            return false;
        }

        NeonCactusPlantBlock cactus = ((NeonCactusPlantBlock) EndBlocks.NEON_CACTUS);
        cactus.growPlant(world, pos, random);

        return true;
    }
}

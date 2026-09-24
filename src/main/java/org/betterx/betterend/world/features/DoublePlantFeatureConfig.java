package org.betterx.betterend.world.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class DoublePlantFeatureConfig extends ScatterFeatureConfig {
    public static final MapCodec<DoublePlantFeatureConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    BlockStateProvider.DIRECT_CODEC.fieldOf("small_state").forGetter(o -> o.smallPlant),
                    BlockStateProvider.DIRECT_CODEC.fieldOf("large_state").forGetter(o -> o.largePlant),
                    Codec.INT.fieldOf("radius").forGetter(o -> o.radius)
            )
            .apply(
                    instance,
                    DoublePlantFeatureConfig::new
            ));

    public final BlockStateProvider smallPlant;
    public final BlockStateProvider largePlant;

    public DoublePlantFeatureConfig(Block smallPlant, Block largePlant, int radius) {
        this(BlockStateProvider.of(smallPlant), BlockStateProvider.of(largePlant), radius);
    }

    public DoublePlantFeatureConfig(BlockStateProvider smallPlant, BlockStateProvider largePlant, int radius) {
        super(radius);
        this.smallPlant = smallPlant;
        this.largePlant = largePlant;
    }

    BlockState getLargePlantState(WorldGenLevel world, RandomSource rnd, BlockPos pos) {
        return largePlant.getState(world, rnd, pos);
    }

    BlockState getSmallPlantState(WorldGenLevel world, RandomSource rnd, BlockPos pos) {
        return smallPlant.getState(world, rnd, pos);
    }
}

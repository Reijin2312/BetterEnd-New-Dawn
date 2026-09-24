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

public class WallPlantFeatureConfig extends ScatterFeatureConfig {
    public static final MapCodec<WallPlantFeatureConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    BlockStateProvider.DIRECT_CODEC.fieldOf("state").forGetter(o -> o.plant),
                    Codec.INT.fieldOf("radius").forGetter(o -> o.radius)
            )
            .apply(
                    instance,
                    WallPlantFeatureConfig::new
            ));


    public final BlockStateProvider plant;

    public WallPlantFeatureConfig(Block plant, int radius) {
        this(BlockStateProvider.of(plant), radius);
    }

    public WallPlantFeatureConfig(BlockStateProvider plant, int radius) {
        super(radius);
        this.plant = plant;
    }

    public BlockState getPlantState(WorldGenLevel world, RandomSource rnd, BlockPos pos) {
        return plant.getState(world, rnd, pos);
    }

}

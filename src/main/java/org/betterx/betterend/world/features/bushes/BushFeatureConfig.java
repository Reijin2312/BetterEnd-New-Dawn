package org.betterx.betterend.world.features.bushes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class BushFeatureConfig {
    public static final MapCodec<BushFeatureConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    BlockStateProvider.DIRECT_CODEC.fieldOf("leaves").forGetter(o -> o.leaves),
                    BlockStateProvider.DIRECT_CODEC.fieldOf("stem").forGetter(o -> o.stem)
            )
            .apply(instance, BushFeatureConfig::new));


    public final BlockStateProvider leaves;
    public final BlockStateProvider stem;

    public BushFeatureConfig(Block leaves, Block stem) {
        this(
                BlockStateProvider.of(leaves),
                BlockStateProvider.of(stem)
        );
    }

    public BushFeatureConfig(
            BlockStateProvider leaves,
            BlockStateProvider stem
    ) {
        this.leaves = leaves;
        this.stem = stem;
    }
}

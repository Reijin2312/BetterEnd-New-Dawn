package org.betterx.betterend.world.features.bushes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class BushWithOuterFeatureConfig extends BushFeatureConfig {
    public static final MapCodec<BushWithOuterFeatureConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    BlockStateProvider.DIRECT_CODEC.fieldOf("leaves").forGetter(o -> o.leaves),
                    BlockStateProvider.DIRECT_CODEC.fieldOf("outer_leaves").forGetter(o -> o.outer_leaves),
                    BlockStateProvider.DIRECT_CODEC.fieldOf("stem").forGetter(o -> o.stem)
            )
            .apply(instance, BushWithOuterFeatureConfig::new));


    public final BlockStateProvider outer_leaves;

    public BushWithOuterFeatureConfig(Block leaves, Block outer_leaves, Block stem) {
        this(
                BlockStateProvider.of(leaves),
                BlockStateProvider.of(outer_leaves),
                BlockStateProvider.of(stem)
        );
    }

    public BushWithOuterFeatureConfig(
            BlockStateProvider leaves,
            BlockStateProvider outer_leaves,
            BlockStateProvider stem
    ) {
        super(leaves, stem);
        this.outer_leaves = outer_leaves;
    }
}

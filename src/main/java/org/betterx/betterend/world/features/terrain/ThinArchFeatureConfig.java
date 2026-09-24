package org.betterx.betterend.world.features.terrain;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class ThinArchFeatureConfig {
    public static final MapCodec<ThinArchFeatureConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    BlockStateProvider.DIRECT_CODEC.fieldOf("states").forGetter(o -> o.block)
            )
            .apply(instance, ThinArchFeatureConfig::new));


    public final BlockStateProvider block;

    public ThinArchFeatureConfig(Block block) {
        this(BlockStateProvider.of(block));
    }

    public ThinArchFeatureConfig(BlockStateProvider block) {
        this.block = block;
    }
}

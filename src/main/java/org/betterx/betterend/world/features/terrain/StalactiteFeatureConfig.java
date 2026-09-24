package org.betterx.betterend.world.features.terrain;

import com.mojang.serialization.Codec;


import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

public class StalactiteFeatureConfig {
    public static final MapCodec<StalactiteFeatureConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    Codec.BOOL.fieldOf("ceiling").forGetter(o -> o.ceiling),
                    BlockStateProvider.DIRECT_CODEC.fieldOf("states").forGetter(o -> o.block),
                    BlockPredicate.CODEC.fieldOf("allowed_ground").forGetter(o -> o.allowedGround)
            )
            .apply(instance, StalactiteFeatureConfig::new));


    public final boolean ceiling;
    public final BlockStateProvider block;
    public final BlockPredicate allowedGround;

    public StalactiteFeatureConfig(boolean ceiling, Block block, Block... ground) {
        this(
                ceiling,
                BlockStateProvider.of(block),
                net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.matchesBlocks(ground)
        );
    }

    public StalactiteFeatureConfig(
            boolean ceiling,
            Block block,
            int weight,
            Block block2,
            int weight2,
            Block... ground
    ) {
        this(
                ceiling,
                new WeightedStateProvider(
                        new WeightedList.Builder<BlockState>()
                                .add(block.defaultBlockState(), weight)
                                .add(block2.defaultBlockState(), weight2)
                                .build()
                ),
                net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.matchesBlocks(ground)
        );
    }

    public StalactiteFeatureConfig(boolean ceiling, BlockStateProvider block, BlockPredicate allowedGround) {
        this.ceiling = ceiling;
        this.block = block;
        this.allowedGround = allowedGround;
    }
}

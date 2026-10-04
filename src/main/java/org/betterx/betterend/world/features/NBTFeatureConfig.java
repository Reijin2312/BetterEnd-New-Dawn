package org.betterx.betterend.world.features;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;

public class NBTFeatureConfig {
    public static final MapCodec<NBTFeatureConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    BlockState.CODEC.fieldOf("default").forGetter(o -> o.defaultBlock)
            )
            .apply(instance, NBTFeatureConfig::new)
    );
    public final BlockState defaultBlock;

    public NBTFeatureConfig(BlockState defaultBlock) {
        this.defaultBlock = defaultBlock;
    }
}

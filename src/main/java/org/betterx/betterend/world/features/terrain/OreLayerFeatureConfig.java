package org.betterx.betterend.world.features.terrain;

import com.mojang.serialization.Codec;

import org.betterx.betterend.noise.OpenSimplexNoise;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;

public class OreLayerFeatureConfig {
    public static final MapCodec<OreLayerFeatureConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    BlockState.CODEC.fieldOf("state").forGetter(o -> o.state),
                    Codec.FLOAT.fieldOf("radius").forGetter(o -> o.radius),
                    Codec.INT.fieldOf("min_y").forGetter(o -> o.minY),
                    Codec.INT.fieldOf("max_y").forGetter(o -> o.maxY)
            )
            .apply(instance, OreLayerFeatureConfig::new));

    public final BlockState state;
    public final float radius;
    public final int minY;
    public final int maxY;
    private OpenSimplexNoise noise;

    public OreLayerFeatureConfig(BlockState state, float radius, int minY, int maxY) {
        this.state = state;
        this.radius = radius;
        this.minY = minY;
        this.maxY = maxY;
    }

    public OpenSimplexNoise getNoise(long seed) {
        if (noise == null) {
            noise = new OpenSimplexNoise(seed);
        }
        return noise;
    }
}

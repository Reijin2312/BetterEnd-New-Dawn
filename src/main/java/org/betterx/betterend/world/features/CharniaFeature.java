package org.betterx.betterend.world.features;

import com.mojang.serialization.MapCodec;

public class CharniaFeature extends UnderwaterPlantFeature {
    public static final MapCodec<CharniaFeature> CODEC =
            SinglePlantFeatureConfig.CODEC.xmap(CharniaFeature::new, feature -> feature.config);

    public CharniaFeature(SinglePlantFeatureConfig config) {
        super(config);
    }

    @Override
    public MapCodec<CharniaFeature> codec() {
        return CODEC;
    }

    @Override
    protected int getChance() {
        return 3;
    }
}

package org.betterx.betterend.world.features;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BuildingListFeatureConfig extends NBTFeatureConfig {
    public static final MapCodec<BuildingListFeatureConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    ExtraCodecs.nonEmptyList(BuildingListFeature.StructureInfo.CODEC.listOf())
                               .fieldOf("structures")
                               .forGetter(a -> a.list),
                    BlockState.CODEC.fieldOf("default").forGetter(o -> o.defaultBlock)
            )
            .apply(instance, BuildingListFeatureConfig::new)
    );
    protected final List<BuildingListFeature.StructureInfo> list;

    public BuildingListFeatureConfig(List<BuildingListFeature.StructureInfo> list, BlockState defaultBlock) {
        super(defaultBlock);
        this.list = list;
    }

    public BuildingListFeature.StructureInfo getRandom(RandomSource random) {
        return this.list.get(random.nextInt(this.list.size()));
    }
}

package org.betterx.betterend.world.features.terrain.caves;

import org.betterx.betterend.world.biome.cave.EndCaveBiome;
import org.betterx.wover.biome.api.BiomeManager;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import org.jetbrains.annotations.Nullable;

public record CaveChunkPopulatorFeatureConfig(Identifier biomeID) {
    public static final MapCodec<CaveChunkPopulatorFeatureConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Identifier.CODEC.fieldOf("biome").forGetter(o -> o.biomeID))
            .apply(instance, CaveChunkPopulatorFeatureConfig::new));

    public @Nullable EndCaveBiome getCaveBiome() {
        return (EndCaveBiome) BiomeManager.biomeData(biomeID);
    }
}

package org.betterx.betterend.world.features.terrain.caves;

import org.betterx.betterend.world.biome.cave.EndCaveBiome;
import org.betterx.wover.biome.api.BiomeManager;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import org.jetbrains.annotations.Nullable;

public record CaveChunkPopulatorFeatureConfig(Identifier biomeID) implements FeatureConfiguration {
    public static final Codec<CaveChunkPopulatorFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Identifier.CODEC.fieldOf("biome").forGetter(o -> o.biomeID))
            .apply(instance, CaveChunkPopulatorFeatureConfig::new));

    public @Nullable EndCaveBiome getCaveBiome() {
        return (EndCaveBiome) BiomeManager.biomeData(biomeID);
    }
}

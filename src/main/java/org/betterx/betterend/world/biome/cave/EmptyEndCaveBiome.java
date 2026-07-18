package org.betterx.betterend.world.biome.cave;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.bclib.util.WeightedList;
import org.betterx.betterend.registry.features.EndConfiguredCaveFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import org.betterx.betterend.world.biome.EndBiomeKey;
import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeGenerationDataContainer;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmptyEndCaveBiome extends EndCaveBiome.Config<EmptyEndCaveBiome> {
    public static final MapCodec<Biome> CODEC = EndCaveBiome.simpleCaveBiomeCodec(EmptyEndCaveBiome.Biome::new);
    public static final KeyDispatchDataCodec<EmptyEndCaveBiome.Biome> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);
    public static final MapCodec<Biome> NETWORK_CODEC = EndCaveBiome.simpleCaveBiomeNetworkCodec(EmptyEndCaveBiome.Biome::new);
    public static final KeyDispatchDataCodec<EmptyEndCaveBiome.Biome> NETWORK_KEY_CODEC = KeyDispatchDataCodec.of(NETWORK_CODEC);

    public static class Biome extends EndCaveBiome {
        @Override
        public void datagenSetup(BootstrapContext<BiomeData> dataContext) {
            this.addFloorFeature(EndConfiguredCaveFeatures.END_STONE_STALAGMITE.getHolder(dataContext), 1);
            this.addCeilFeature(EndConfiguredCaveFeatures.END_STONE_STALACTITE.getHolder(dataContext), 1);
        }

        @Override
        public KeyDispatchDataCodec<? extends EndCaveBiome> codec() {
            return EmptyEndCaveBiome.KEY_CODEC;
        }

        @Override
        public KeyDispatchDataCodec<? extends EndCaveBiome> networkCodec() {
            return EmptyEndCaveBiome.NETWORK_KEY_CODEC;
        }

        protected Biome(
                float fogDensity,
                @NotNull ResourceKey<net.minecraft.world.level.biome.Biome> biome,
                @NotNull BiomeGenerationDataContainer generatorData,
                float terrainHeight,
                float genChance,
                int edgeSize,
                boolean vertical,
                @Nullable ResourceKey<net.minecraft.world.level.biome.Biome> edge,
                @Nullable ResourceKey<net.minecraft.world.level.biome.Biome> parent,
                boolean hasCaves,
                SurfaceMaterialProvider surface,
                WeightedList<Holder<ConfiguredFeature<?, ?>>> floorFeatures,
                WeightedList<Holder<ConfiguredFeature<?, ?>>> ceilFeatures
        ) {
            super(
                    fogDensity, biome, generatorData, terrainHeight,
                    genChance, edgeSize, vertical,
                    edge, parent,
                    hasCaves, surface, floorFeatures, ceilFeatures
            );
        }

        @Override
        public float getFloorDensity() {
            return 0.1F;
        }

        @Override
        public float getCeilDensity() {
            return 0.1F;
        }
    }

    public EmptyEndCaveBiome(EndBiomeKey<EmptyEndCaveBiome, ?> key) {
        super(key);
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        super.addCustomBuildData(builder);
        builder.fogDensity(2.0F);
    }

    @Override
    public @NotNull EndBiome instantiateBiome(
            float fogDensity,
            BiomeKey<?> key,
            @NotNull BiomeGenerationDataContainer generatorData,
            float terrainHeight,
            float genChance,
            int edgeSize,
            boolean vertical,
            @Nullable ResourceKey<net.minecraft.world.level.biome.Biome> edge,
            @Nullable ResourceKey<net.minecraft.world.level.biome.Biome> parent,
            boolean hasCave,
            SurfaceMaterialProvider surface
    ) {
        return new EmptyEndCaveBiome.Biome(fogDensity, key.key, generatorData, terrainHeight, genChance, edgeSize, vertical, edge, parent, hasCave, surface, new WeightedList<>(), new WeightedList<>());
    }
}

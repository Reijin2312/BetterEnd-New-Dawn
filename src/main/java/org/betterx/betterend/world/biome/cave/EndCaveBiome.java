package org.betterx.betterend.world.biome.cave;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.bclib.util.WeightedList;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndSounds;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import org.betterx.betterend.world.biome.EndBiomeKey;
import org.betterx.betterend.world.features.terrain.caves.CaveChunkPopulatorFeatureConfig;
import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.data.BiomeGenerationDataContainer;
import org.betterx.wover.feature.api.placed.PlacedFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureManager;
import org.betterx.wover.generator.api.biomesource.WoverBiomeData;

import com.mojang.datafixers.util.Function13;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.betterx.wover.state.api.WorldState;

public class EndCaveBiome extends EndBiome {
    private static final Codec<WeightedList<ResourceKey<Feature>>> FEATURE_KEY_LIST_CODEC =
            WeightedList.listCodec(
                    ResourceKey.codec(Registries.FEATURE),
                    "configured_features",
                    "configured_feature"
            );
    public static final MapCodec<EndCaveBiome> CODEC = simpleCaveBiomeCodec(EndCaveBiome::new);


    public static <T extends EndCaveBiome> MapCodec<T> simpleCaveBiomeCodec(final Function13<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, Boolean, SurfaceMaterialProvider, WeightedList<Holder<Feature>>, WeightedList<Holder<Feature>>, T> factory) {
        return codec(
                Codec.BOOL.fieldOf("has_caves").orElse(true).forGetter(EndBiome::hasCaves),
                SurfaceMaterialProvider.CODEC.fieldOf("surface")
                                             .orElse(new DefaultSurfaceMaterialProvider())
                                             .forGetter(o -> o.surfMatProv),
                WeightedList.listCodec(Feature.CODEC, "configured_features", "configured_feature")
                            .fieldOf("floor_features")
                            .forGetter(o -> (WeightedList) ((EndCaveBiome) o).floorFeatures),
                WeightedList.listCodec(Feature.CODEC, "configured_features", "configured_feature")
                            .fieldOf("ceil_features")
                            .forGetter(o -> (WeightedList) ((EndCaveBiome) o).ceilFeatures),
                factory
        );
    }

    public static <T extends EndCaveBiome> MapCodec<T> simpleCaveBiomeNetworkCodec(final Function13<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, Boolean, SurfaceMaterialProvider, WeightedList<Holder<Feature>>, WeightedList<Holder<Feature>>, T> factory) {
        return codec(
                Codec.BOOL.fieldOf("has_caves").orElse(true).forGetter(EndBiome::hasCaves),
                SurfaceMaterialProvider.CODEC.fieldOf("surface")
                                             .orElse(new DefaultSurfaceMaterialProvider())
                                             .forGetter(o -> o.surfMatProv),
                FEATURE_KEY_LIST_CODEC.optionalFieldOf("floor_features", new WeightedList<>())
                                      .forGetter(o -> ((EndCaveBiome) o).getFloorFeatureKeysForCodec()),
                FEATURE_KEY_LIST_CODEC.optionalFieldOf("ceil_features", new WeightedList<>())
                                      .forGetter(o -> ((EndCaveBiome) o).getCeilFeatureKeysForCodec()),
                (fogDensity,
                 biome,
                 generatorData,
                 terrainHeight,
                 genChance,
                 edgeSize,
                 vertical,
                 edge,
                 parent,
                 hasCaves,
                 surface,
                 floorKeys,
                 ceilKeys) -> {
                    T biomeData = factory.apply(
                            fogDensity,
                            biome,
                            generatorData,
                            terrainHeight,
                            genChance,
                            edgeSize,
                            vertical,
                            edge,
                            parent,
                            hasCaves,
                            surface,
                            new WeightedList<>(),
                            new WeightedList<>()
                    );
                    biomeData.setFeatureKeys(floorKeys, ceilKeys);
                    return biomeData;
                }
        );
    }


    @Override
    public MapCodec<? extends WoverBiomeData> codec() {
        return CODEC;
    }


    protected EndCaveBiome(
            float fogDensity,
            @NotNull ResourceKey<Biome> biome,
            @NotNull BiomeGenerationDataContainer generatorData,
            float terrainHeight,
            float genChance,
            int edgeSize,
            boolean vertical,
            @Nullable ResourceKey<Biome> edge,
            @Nullable ResourceKey<Biome> parent,
            boolean hasCaves,
            SurfaceMaterialProvider surface,
            WeightedList<Holder<Feature>> floorFeatures,
            WeightedList<Holder<Feature>> ceilFeatures
    ) {
        super(
                fogDensity, biome, generatorData, terrainHeight,
                genChance, edgeSize, vertical,
                edge, parent,
                hasCaves,
                surface
        );
        this.floorFeatures.addAll((WeightedList) floorFeatures);
        this.ceilFeatures.addAll((WeightedList) ceilFeatures);
        this.floorFeatureKeys.addAll(toKeyList(floorFeatures));
        this.ceilFeatureKeys.addAll(toKeyList(ceilFeatures));
    }

    public static abstract class Config<C extends Config<?>> extends EndBiome.Config {
        public final PlacedFeatureKey populatorFeature;
        public final CaveChunkPopulatorFeatureConfig populatorConfig;

        protected Config(EndBiomeKey<C, ?> key) {
            super();
            this.populatorFeature = PlacedFeatureManager
                    .createKey(BetterEnd.C.mk(key.key
                            .identifier()
                            .getPath() + "_cave_populator"))
                    .setDecoration(GenerationStep.Decoration.UNDERGROUND_DECORATION);
            this.populatorConfig = new CaveChunkPopulatorFeatureConfig(key.dataKey.identifier());
        }


        @Override
        public void addCustomBuildData(EndBiomeBuilder builder) {
            builder.feature(populatorFeature)
                   .music(EndSounds.MUSIC_CAVES)
                   .loop(EndSounds.AMBIENT_CAVES);
        }

        @Override
        public boolean hasCaves() {
            return false;
        }

        @Override
        public boolean hasReturnGateway() {
            return false;
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
                @Nullable ResourceKey<Biome> edge,
                @Nullable ResourceKey<Biome> parent,
                boolean hasCave,
                SurfaceMaterialProvider surface
        ) {
            return new EndCaveBiome(
                    fogDensity, key.key, generatorData,
                    terrainHeight, genChance, edgeSize, vertical, edge, parent,
                    hasCave, surface, new WeightedList<>(), new WeightedList<>()
            );
        }

    }

    private final WeightedList<Holder<? extends Feature>> floorFeatures = new WeightedList<>();
    private final WeightedList<Holder<? extends Feature>> ceilFeatures = new WeightedList<>();
    private final WeightedList<ResourceKey<Feature>> floorFeatureKeys = new WeightedList<>();
    private final WeightedList<ResourceKey<Feature>> ceilFeatureKeys = new WeightedList<>();
    private boolean featuresResolved = false;

    public void addFloorFeature(Holder<? extends Feature> feature, float weight) {
        floorFeatures.add(feature, weight);
        feature.unwrapKey().ifPresent(key -> floorFeatureKeys.add(castFeatureKey(key), weight));
    }

    public void addCeilFeature(Holder<? extends Feature> feature, float weight) {
        ceilFeatures.add(feature, weight);
        feature.unwrapKey().ifPresent(key -> ceilFeatureKeys.add(castFeatureKey(key), weight));
    }

    public Holder<? extends Feature> getFloorFeature(RandomSource random) {
        resolveFeatureKeys();
        return floorFeatures.isEmpty() ? null : floorFeatures.get(random);
    }

    public Holder<? extends Feature> getCeilFeature(RandomSource random) {
        resolveFeatureKeys();
        return ceilFeatures.isEmpty() ? null : ceilFeatures.get(random);
    }

    protected void setFeatureKeys(
            WeightedList<ResourceKey<Feature>> floorKeys,
            WeightedList<ResourceKey<Feature>> ceilKeys
    ) {
        if (floorKeys != null && !floorKeys.isEmpty()) {
            this.floorFeatureKeys.addAll(floorKeys);
        }
        if (ceilKeys != null && !ceilKeys.isEmpty()) {
            this.ceilFeatureKeys.addAll(ceilKeys);
        }
    }

    private WeightedList<ResourceKey<Feature>> getFloorFeatureKeysForCodec() {
        if (floorFeatureKeys.isEmpty() && !floorFeatures.isEmpty()) {
            floorFeatureKeys.addAll(toKeyList(floorFeatures));
        }
        return floorFeatureKeys;
    }

    private WeightedList<ResourceKey<Feature>> getCeilFeatureKeysForCodec() {
        if (ceilFeatureKeys.isEmpty() && !ceilFeatures.isEmpty()) {
            ceilFeatureKeys.addAll(toKeyList(ceilFeatures));
        }
        return ceilFeatureKeys;
    }

    private void resolveFeatureKeys() {
        if (featuresResolved) {
            return;
        }
        if (!floorFeatures.isEmpty() || !ceilFeatures.isEmpty()) {
            featuresResolved = true;
            return;
        }
        if (floorFeatureKeys.isEmpty() && ceilFeatureKeys.isEmpty()) {
            featuresResolved = true;
            return;
        }

        RegistryAccess access = WorldState.registryAccess();
        if (access == null) {
            access = WorldState.allStageRegistryAccess();
        }
        if (access == null) {
            return;
        }
        Registry<Feature> registry = access.lookup(Registries.FEATURE).orElse(null);
        if (registry == null) {
            return;
        }

        addFeaturesFromKeys(registry, floorFeatureKeys, floorFeatures);
        addFeaturesFromKeys(registry, ceilFeatureKeys, ceilFeatures);
        featuresResolved = true;
    }

    @SuppressWarnings("unchecked")
    private static void addFeaturesFromKeys(
            Registry<Feature> registry,
            WeightedList<ResourceKey<Feature>> keys,
            WeightedList<Holder<? extends Feature>> target
    ) {
        for (int i = 0; i < keys.size(); i++) {
            ResourceKey<Feature> key = keys.get(i);
            final float weight = keys.getWeight(i);
            registry.get(key).ifPresent(holder -> target.add((Holder) holder, weight));
        }
    }

    @SuppressWarnings("unchecked")
    private static WeightedList<ResourceKey<Feature>> toKeyList(
            WeightedList<? extends Holder<? extends Feature>> holders
    ) {
        WeightedList<ResourceKey<Feature>> keys = new WeightedList<>();
        if (holders == null || holders.isEmpty()) {
            return keys;
        }
        for (int i = 0; i < holders.size(); i++) {
            Holder<? extends Feature> holder = holders.get(i);
            ResourceKey<? extends Feature> key = holder.unwrapKey().orElse(null);
            if (key != null) {
                keys.add(castFeatureKey(key), holders.getWeight(i));
            }
        }
        return keys;
    }

    @SuppressWarnings("unchecked")
    private static ResourceKey<Feature> castFeatureKey(
            ResourceKey<? extends Feature> key
    ) {
        return (ResourceKey<Feature>) key;
    }

    public float getFloorDensity() {
        return 0;
    }

    public float getCeilDensity() {
        return 0;
    }

    public BlockState getCeil(BlockPos pos) {
        return null;
    }

    public BlockState getWall(BlockPos pos) {
        return null;
    }
}

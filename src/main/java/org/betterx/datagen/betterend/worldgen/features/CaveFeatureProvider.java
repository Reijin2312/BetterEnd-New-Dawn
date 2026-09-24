package org.betterx.datagen.betterend.worldgen.features;

import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndFeatures;
import org.betterx.betterend.registry.features.EndConfiguredCaveFeatures;
import org.betterx.betterend.world.biome.cave.EndCaveBiome;
import org.betterx.betterend.world.features.VineFeatureConfig;
import org.betterx.betterend.world.features.bushes.BushFeatureConfig;
import org.betterx.betterend.world.features.terrain.StalactiteFeatureConfig;
import org.betterx.datagen.betterend.worldgen.EndBiomesProvider;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.multi.WoverFeatureProvider;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;

public class CaveFeatureProvider extends WoverFeatureProvider {
    public CaveFeatureProvider(@NotNull ModCore modCore) {
        super(modCore, modCore.id("caves"));
    }

    @Override
    protected void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        //EndConfiguredCaveFeatures.CAVE_BUSH.bootstrap(context).register();

        EndConfiguredCaveFeatures.SMARAGDANT_CRYSTAL.bootstrap(context).register();

        EndConfiguredCaveFeatures.SMARAGDANT_CRYSTAL_SHARD
                .bootstrap(context)
                .configuration(new SimpleBlockConfiguration(BlockStateProvider.simple(EndBlocks.SMARAGDANT_CRYSTAL_SHARD)))
                .register();

        EndConfiguredCaveFeatures.BIG_AURORA_CRYSTAL.bootstrap(context).register();

        EndConfiguredCaveFeatures.CAVE_BUSH
                .bootstrap(context)
                .configuration(new BushFeatureConfig(EndBlocks.CAVE_BUSH, EndBlocks.CAVE_BUSH))
                .register();

        EndConfiguredCaveFeatures.CAVE_GRASS
                .bootstrap(context)
                .configuration(new SimpleBlockConfiguration(BlockStateProvider.simple(EndBlocks.CAVE_GRASS)))
                .register();

        EndConfiguredCaveFeatures.RUBINEA
                .bootstrap(context)
                .configuration(new VineFeatureConfig(EndBlocks.RUBINEA, 8))
                .register();

        EndConfiguredCaveFeatures.MAGNULA
                .bootstrap(context)
                .configuration(new VineFeatureConfig(EndBlocks.MAGNULA, 8))
                .register();


        EndConfiguredCaveFeatures.END_STONE_STALACTITE
                .bootstrap(context)
                .configuration(new StalactiteFeatureConfig(true, EndBlocks.END_STONE_STALACTITE, Blocks.END_STONE))
                .register();
        EndConfiguredCaveFeatures.END_STONE_STALAGMITE
                .bootstrap(context)
                .configuration(new StalactiteFeatureConfig(false, EndBlocks.END_STONE_STALACTITE, Blocks.END_STONE))
                .register();
        EndConfiguredCaveFeatures.END_STONE_STALACTITE_CAVEMOSS
                .bootstrap(context)
                .configuration(new StalactiteFeatureConfig(true, EndBlocks.END_STONE_STALACTITE_CAVEMOSS, Blocks.END_STONE, EndBlocks.CAVE_MOSS))
                .register();
        EndConfiguredCaveFeatures.END_STONE_STALAGMITE_CAVEMOSS
                .bootstrap(context)
                .configuration(new StalactiteFeatureConfig(false, EndBlocks.END_STONE_STALACTITE_CAVEMOSS, EndBlocks.CAVE_MOSS))
                .register();
        EndConfiguredCaveFeatures.END_STONE_WITH_CAVEMOSS_STALACTITE
                .bootstrap(context)
                .configuration(new StalactiteFeatureConfig(true, EndBlocks.END_STONE_STALACTITE, 5, EndBlocks.END_STONE_STALACTITE_CAVEMOSS, 2, Blocks.END_STONE))
                .register();
        EndConfiguredCaveFeatures.END_STONE_WITH_CAVEMOSS_STALAGMITE
                .bootstrap(context)
                .configuration(new StalactiteFeatureConfig(false, EndBlocks.END_STONE_STALACTITE, 5, EndBlocks.END_STONE_STALACTITE_CAVEMOSS, 1, Blocks.END_STONE))
                .register();

        EndConfiguredCaveFeatures.CAVE_PUMPKIN.bootstrap(context).register();


    }

    @Override
    protected void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        EndBiomesProvider
                .BIOMES
                .values()
                .stream()
                .filter(info -> info.config() instanceof EndCaveBiome.Config<?>)
                .map(info -> (EndCaveBiome.Config<?>) info.config())
                .forEach(config -> config
                        .populatorFeature
                        .inlineConfiguration(context)
                        .withFeature(EndFeatures.CAVE_CHUNK_POPULATOR)
                        .configuration(config.populatorConfig)
                        .inlinePlace()
                        .count(1)
                        .onlyInBiome()
                        .register());
    }
}

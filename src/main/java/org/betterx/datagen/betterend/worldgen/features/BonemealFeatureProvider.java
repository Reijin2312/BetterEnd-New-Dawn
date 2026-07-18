package org.betterx.datagen.betterend.worldgen.features;


import org.betterx.betterend.registry.EndBiomes;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.features.EndBonemealFeature;
import org.betterx.betterend.registry.features.EndConfiguredBonemealFeature;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.multi.WoverFeatureProvider;
import org.betterx.wover.feature.api.features.config.ConditionFeatureConfig;
import org.betterx.wover.feature.api.placed.modifiers.InBiome;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;

public class BonemealFeatureProvider extends WoverFeatureProvider {
    public BonemealFeatureProvider(@NotNull ModCore modCore) {
        super(modCore, modCore.id("bonemeal"));
    }

    @Override
    protected void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        EndConfiguredBonemealFeature.BONEMEAL_END_MOSS
                .bootstrap(context)
                .configuration(new ConditionFeatureConfig(
                        InBiome.matchingID(EndBiomes.GLOWING_GRASSLANDS.key.identifier()),
                        EndBonemealFeature.BONEMEAL_END_MOSS_GLOWING_GRASSLANDS.getHolder(context),
                        EndBonemealFeature.BONEMEAL_END_MOSS_NOT_GLOWING_GRASSLANDS.getHolder(context)
                ))
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_RUTISCUS
                .bootstrap(context)
                .configuration(new ConditionFeatureConfig(
                        InBiome.matchingID(EndBiomes.LANTERN_WOODS.key.identifier()),
                        EndBonemealFeature.BONEMEAL_RUTISCUS_LANTERN_WOODS.getHolder(context),
                        EndBonemealFeature.BONEMEAL_RUTISCUS_NOT_LANTERN_WOODS.getHolder(context)
                ))
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_END_MYCELIUM
                .bootstrap(context).add(EndBlocks.CREEPING_MOSS, 100)
                .add(EndBlocks.UMBRELLA_MOSS, 100)
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_JUNGLE_MOSS
                .bootstrap(context).add(EndBlocks.JUNGLE_GRASS, 100)
                .add(EndBlocks.TWISTED_UMBRELLA_MOSS, 100)
                .add(EndBlocks.SMALL_JELLYSHROOM, 10)
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_SANGNUM
                .bootstrap(context).add(EndBlocks.CLAWFERN, 100)
                .add(EndBlocks.GLOBULAGUS, 100)
                .add(EndBlocks.SMALL_AMARANITA_MUSHROOM, 10)
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_MOSSY_DRAGON_BONE
                .bootstrap(context).add(EndBlocks.CLAWFERN, 100)
                .add(EndBlocks.GLOBULAGUS, 100)
                .add(EndBlocks.SMALL_AMARANITA_MUSHROOM, 10)
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_MOSSY_OBSIDIAN
                .bootstrap(context).add(EndBlocks.CLAWFERN, 100)
                .add(EndBlocks.GLOBULAGUS, 100)
                .add(EndBlocks.SMALL_AMARANITA_MUSHROOM, 10)
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_CAVE_MOSS
                .bootstrap(context).add(EndBlocks.CAVE_GRASS, 100)
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_CHORUS_NYLIUM
                .bootstrap(context).add(EndBlocks.CHORUS_GRASS, 100)
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_CRYSTAL_MOSS
                .bootstrap(context).add(EndBlocks.CRYSTAL_GRASS, 100)
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_SHADOW_GRASS
                .bootstrap(context).add(EndBlocks.SHADOW_PLANT, 100)
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_PINK_MOSS
                .bootstrap(context).add(EndBlocks.BUSHY_GRASS, 100)
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_AMBER_MOSS
                .bootstrap(context).add(EndBlocks.AMBER_GRASS, 100)
                .register();
    }

    @Override
    protected void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        EndBonemealFeature.BONEMEAL_END_MOSS_NOT_GLOWING_GRASSLANDS
                .inlineConfiguration(context).bonemealPatch()
                .add(EndBlocks.CREEPING_MOSS, 10)
                .add(EndBlocks.UMBRELLA_MOSS, 10)
                .inlinePlace()
                .register();

        EndBonemealFeature.BONEMEAL_END_MOSS_GLOWING_GRASSLANDS
                .inlineConfiguration(context).bonemealPatch()
                .add(EndBlocks.CREEPING_MOSS, 10)
                .add(EndBlocks.UMBRELLA_MOSS, 10)
                .add(EndBlocks.BLOOMING_COOKSONIA, 100)
                .add(EndBlocks.VAIOLUSH_FERN, 100)
                .add(EndBlocks.FRACTURN, 100)
                .add(EndBlocks.SALTEAGO, 100)
                .add(EndBlocks.TWISTED_UMBRELLA_MOSS, 10)
                .inlinePlace()
                .register();

        EndBonemealFeature.BONEMEAL_RUTISCUS_NOT_LANTERN_WOODS
                .inlineConfiguration(context).bonemealPatch()
                .add(EndBlocks.ORANGO, 100)
                .add(EndBlocks.AERIDIUM, 20)
                .add(EndBlocks.LUTEBUS, 20)
                .add(EndBlocks.LAMELLARIUM, 100)
                .inlinePlace()
                .register();

        EndBonemealFeature.BONEMEAL_RUTISCUS_LANTERN_WOODS
                .inlineConfiguration(context).bonemealPatch()
                .add(EndBlocks.AERIDIUM, 20)
                .add(EndBlocks.BOLUX_MUSHROOM, 5)
                .add(EndBlocks.LAMELLARIUM, 100)
                .inlinePlace()
                .register();
    }
}

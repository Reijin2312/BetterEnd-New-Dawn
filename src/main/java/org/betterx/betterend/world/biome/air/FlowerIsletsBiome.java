package org.betterx.betterend.world.biome.air;


import org.betterx.betterend.registry.EndBlocks;
import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.EndParticles;
import org.betterx.betterend.registry.EndSounds;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.features.EndVegetationFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A LUSH void-ring small-island biome: tiny end-stone islets carpeted in colourful End
 * flowers and moss. Explicitly contains NO trees or large mushrooms. Routed into the
 * WoverEndBiomeSource small-island/void picker via the {@code IS_SMALL_END_ISLAND} biome
 * tag (see {@link org.betterx.datagen.betterend.worldgen.EndBiomesProvider}); opts into the
 * {@link EndStructures#END_BRIDGE} structure so bridges can anchor across the void ring.
 */
public class FlowerIsletsBiome extends EndBiome.Config {
    public FlowerIsletsBiome() {
        super();
    }

    @Override
    public boolean hasCaves() {
        return false;
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .fogColor(215, 165, 240)
                .fogDensity(1.2F)
                .particles(EndParticles.TENANEA_PETAL, 0.0007F)
                .music(EndSounds.MUSIC_OPENSPACE)
                .waterAndFogColor(150, 210, 230)
                .plantsColor(140, 200, 120)
                .genChance(0.4F)
                .structure(EndStructures.END_BRIDGE)
                .structure(EndStructures.SMALL_ISLAND)
                // Colourful floor flowers / small plants (NO trees, NO large mushrooms)
                .feature(EndVegetationFeatures.BLOOMING_COOKSONIA)
                .feature(EndVegetationFeatures.SALTEAGO)
                .feature(EndVegetationFeatures.VAIOLUSH_FERN)
                .feature(EndVegetationFeatures.AERIDIUM)
                .feature(EndVegetationFeatures.LUTEBUS)
                .feature(EndVegetationFeatures.LAMELLARIUM)
                // Glowing plants for night ambience
                .feature(EndVegetationFeatures.FRACTURN)
                .feature(EndVegetationFeatures.GLOW_PILLAR)
                .feature(EndVegetationFeatures.AMARANITA_PATCH)
                // Dense moss / grass ground cover
                .feature(EndVegetationFeatures.UMBRELLA_MOSS)
                .feature(EndVegetationFeatures.CREEPING_MOSS_RARE)
                .feature(EndVegetationFeatures.TWISTED_UMBRELLA_MOSS_RARE)
                .spawn(EntityType.ENDERMAN, 3, 1, 2);
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return EndBlocks.END_MOSS.defaultBlockState();
            }
        };
    }
}

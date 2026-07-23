package org.betterx.betterend.world.biome.land;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndSounds;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.features.EndOreFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import org.betterx.wover.surface.api.SurfaceRuleBuilder;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

public class DustWastelandsBiome extends EndBiome.Config {
    public DustWastelandsBiome() {
        super();
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .fogColor(226, 239, 168)
                .fogDensity(2)
                .waterAndFogColor(192, 180, 131)
                .terrainHeight(1.5F)
                .particles(ParticleTypes.WHITE_ASH, 0.01F)
                .loop(EndSounds.AMBIENT_DUST_WASTELANDS)
                .music(EndSounds.MUSIC_OPENSPACE)
                .structure(EndStructures.END_VILLAGE)
                .structure(EndStructures.ETERNAL_PORTAL)
                .feature(EndOreFeatures.FLAVOLITE_LAYER)
                .spawn(EntityTypes.ENDERMAN, 50, 1, 2);
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return EndBlocks.ENDSTONE_DUST.defaultBlockState();
            }

            @Override
            public SurfaceRuleBuilder surface() {
                return super
                        .surface()
                        .ceil(Blocks.END_STONE.defaultBlockState())
                        .rule(SurfaceRules.ifTrue(
                                SurfaceRules.stoneDepthCheck(5, false, CaveSurface.FLOOR),
                                SurfaceRules.state(EndBlocks.ENDSTONE_DUST.defaultBlockState())
                        ), 4);
            }
        };
    }
}

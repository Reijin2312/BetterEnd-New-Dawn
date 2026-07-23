package org.betterx.betterend.world.biome.land;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndSounds;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.features.EndLakeFeatures;
import org.betterx.betterend.registry.features.EndOreFeatures;
import org.betterx.betterend.registry.features.EndVegetationFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import org.betterx.betterend.world.surface.SplitNoiseCondition;
import org.betterx.wover.surface.api.SurfaceRuleBuilder;
import org.betterx.wover.surface.impl.rules.SwitchRuleSource;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

import java.util.List;

public class NeonOasisBiome extends EndBiome.Config {
    public NeonOasisBiome() {
        super();
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .genChance(0.5F)
                .fogColor(226, 239, 168)
                .fogDensity(2)
                .waterAndFogColor(106, 238, 215)
                .particles(ParticleTypes.WHITE_ASH, 0.01F)
                .loop(EndSounds.AMBIENT_DUST_WASTELANDS)
                .music(EndSounds.MUSIC_OPENSPACE)
                .feature(EndLakeFeatures.DESERT_LAKE)
                .feature(EndVegetationFeatures.NEON_CACTUS)
                .feature(EndVegetationFeatures.UMBRELLA_MOSS)
                .feature(EndVegetationFeatures.CREEPING_MOSS)
                .feature(EndVegetationFeatures.CHARNIA_CYAN)
                .feature(EndVegetationFeatures.CHARNIA_GREEN)
                .feature(EndVegetationFeatures.CHARNIA_RED)
                .feature(EndOreFeatures.FLAVOLITE_LAYER)
                .structure(BiomeTags.HAS_END_CITY)
                .structure(EndStructures.ETERNAL_PORTAL)
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
            public BlockState getAltTopMaterial() {
                return EndBlocks.END_MOSS.defaultBlockState();
            }

            @Override
            public boolean generateFloorRule() {
                return false;
            }

            @Override
            public SurfaceRuleBuilder surface() {
                RuleSource surfaceBlockRule = new SwitchRuleSource(
                        new SplitNoiseCondition(),
                        List.of(
                                SurfaceRules.state(EndBlocks.ENDSTONE_DUST.defaultBlockState()),
                                SurfaceRules.state(EndBlocks.END_MOSS.defaultBlockState())
                        )
                );
                return super
                        .surface()
                        .ceil(Blocks.END_STONE.defaultBlockState())
                        .rule(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, surfaceBlockRule), SurfaceRuleBuilder.FLOOR_PRIORITY)
                        .rule(SurfaceRules.ifTrue(
                                SurfaceRules.stoneDepthCheck(5, false, CaveSurface.FLOOR),
                                SurfaceRules.state(EndBlocks.ENDSTONE_DUST.defaultBlockState())
                        ), SurfaceRuleBuilder.BELOW_FLOOR_PRIORITY);
            }
        };
    }
}

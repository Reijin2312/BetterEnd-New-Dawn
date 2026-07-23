package org.betterx.betterend.world.biome.land;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndSounds;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.features.EndVegetationFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import org.betterx.betterend.world.surface.SplitNoiseCondition;
import org.betterx.wover.surface.api.SurfaceRuleBuilder;
import org.betterx.wover.surface.impl.rules.SwitchRuleSource;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.List;

public class CrystalMountainsBiome extends EndBiome.Config {
    public CrystalMountainsBiome() {
        super();
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .structure(EndStructures.MOUNTAIN)
                .plantsColor(255, 133, 211)
                .music(EndSounds.MUSIC_OPENSPACE)
                .feature(EndVegetationFeatures.CRYSTAL_GRASS)
                .feature(EndVegetationFeatures.CRYSTAL_MOSS_COVER)
                .spawn(EntityTypes.ENDERMAN, 50, 1, 2);
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return EndBlocks.CRYSTAL_MOSS.defaultBlockState();
            }

            @Override
            public SurfaceRuleBuilder surface() {
                SurfaceRules.RuleSource surfaceBlockRule = new SwitchRuleSource(
                        new SplitNoiseCondition(),
                        List.of(
                                SurfaceRules.state(EndBlocks.END_MOSS.defaultBlockState()),
                                SurfaceRules.state(Blocks.END_STONE.defaultBlockState())
                        )
                );
                return super
                        .surface()
                        .rule(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, surfaceBlockRule), 1);
            }
        };
    }
}

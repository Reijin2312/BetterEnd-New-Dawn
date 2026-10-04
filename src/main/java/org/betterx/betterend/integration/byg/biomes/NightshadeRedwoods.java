package org.betterx.betterend.integration.byg.biomes;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.integration.Integrations;
import org.betterx.betterend.integration.byg.features.BYGFeatures;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import org.betterx.wover.surface.api.SurfaceRuleBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.material.MaterialRules;

public class NightshadeRedwoods extends EndBiome.Config {
    public NightshadeRedwoods() {
        super();
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder.fogColor(140, 108, 47)
               .fogDensity(1.5F)
               .waterAndFogColor(55, 70, 186)
               .foliageColorOverride(122, 17, 155)
               .particles(
                       ParticleTypes.REVERSE_PORTAL,
                       0.002F
               )
               .grassColorOverride(48, 13, 89)
               .plantsColor(200, 125, 9)
               .structure(EndStructures.END_LAKE_RARE)
               .feature(BYGFeatures.NIGHTSHADE_REDWOOD_TREE)
               .feature(BYGFeatures.NIGHTSHADE_MOSS_WOOD)
               .feature(BYGFeatures.NIGHTSHADE_MOSS);

        Holder<Biome> biome = Integrations.BYG.getBiome("nightshade_forest");
        if (biome == null) return;

        biome.value().getGenerationSettings()
             .features()
             .forEach((list) -> {
                 list.forEach((feature) -> {
                     builder.feature(Decoration.VEGETAL_DECORATION, feature);
                 });
             });

        for (MobCategory group : MobCategory.values()) {
            var spawns = org.betterx.wover.biome.impl.modification.MobSettingsWorker.mobSettingsOf(biome.value()).getMobsInCategory(group);
            if (spawns == null) continue;
            spawns.unwrap().forEach(entry -> {
                var data = entry.value();
                builder.spawn(data.type(), entry.weight(), data.count().minInclusive(), data.count().maxInclusive());
            });
        }
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return Integrations.BYG.getBlock("nightshade_phylium").defaultBlockState();
            }

            @Override
            public SurfaceRuleBuilder surface() {
                return SurfaceRuleBuilder
                        .start()
                        .rule(MaterialRules.sequence(MaterialRules.ifTrue(
                                                BYGBiomes.BYG_WATER_CHECK,
                                                MaterialRules.ifTrue(
                                                        MaterialRules.stoneDepthCheck(0, false, net.minecraft.world.level.levelgen.placement.CaveSurface.FLOOR),
                                                        MaterialRules.state(getTopMaterial())
                                                )
                                        )
                                ), 4
                        );
            }
        };
    }
}

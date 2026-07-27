package org.betterx.datagen.betterend.tags;

import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.EndTags;
import org.betterx.betterend.registry.EndBiomes;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonBiomeTags;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class BiomeTagProvider extends WoverTagProvider.ForBiomes {
    public BiomeTagProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    public void prepareTags(TagBootstrapContext<Biome> context) {
        context.add(CommonBiomeTags.IS_END_BARRENS, EndBiomes.ICE_STARFIELD.key);

        context.add(
                EndStructures.ETERNAL_PORTAL.biomeTag(),
                Biomes.END_BARRENS,
                Biomes.END_MIDLANDS,
                Biomes.END_HIGHLANDS
        );

        context.add(
                EndTags.IS_END_HIGH_OR_MIDLAND,
                CommonBiomeTags.IS_END_HIGHLAND,
                CommonBiomeTags.IS_END_MIDLAND
        );
    }
}

package org.betterx.datagen.betterend.tags;

import org.betterx.betterend.complexmaterials.MaterialManager;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndTags;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.datagen.betterend.worldgen.EndBiomesProvider;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Set;

public class BlockTagProvider extends WoverTagProvider.ForBlocks {
    public BlockTagProvider(ModCore modCore) {
        super(modCore, Set.of(EndTags.INCORRECT_FOR_AETERNIUM_TOOL));
    }


    private static void addEndGround(TagBootstrapContext<Block> context, Block bl) {
        context.add(CommonBlockTags.END_STONES, bl);
    }

    @Override
    public void prepareTags(TagBootstrapContext<Block> context) {
        context.add(CommonBlockTags.END_STONES, Blocks.END_STONE);
        context.add(EndTags.SURVIVES_ON_SHADOW_GRASS, EndBlocks.SHADOW_GRASS);
        context.add(EndTags.SURVIVES_ON_AMBER_MOSS, EndBlocks.AMBER_MOSS);
        context.add(
                EndTags.SURVIVES_ON_END_MOSS,
                EndBlocks.END_MOSS,
                EndBlocks.PALLIDIUM_FULL,
                EndBlocks.PALLIDIUM_HEAVY,
                EndBlocks.PALLIDIUM_THIN,
                EndBlocks.PALLIDIUM_TINY,
                EndBlocks.SANGNUM
        );
        context.add(
                EndTags.SURVIVES_ON_RUTISCUS,
                EndBlocks.PALLIDIUM_FULL,
                EndBlocks.PALLIDIUM_HEAVY,
                EndBlocks.PALLIDIUM_THIN,
                EndBlocks.PALLIDIUM_TINY,
                EndBlocks.RUTISCUS,
                EndBlocks.SANGNUM
        );
        // Vanilla sulfur and cinnabar make up parts of the sulphur spring floor and sulphuric cave,
        // so plants that root on brimstone must accept those materials as well.
        context.add(
                EndTags.SURVIVES_ON_BRIMSTONE,
                EndBlocks.BRIMSTONE,
                Blocks.SULFUR,
                Blocks.CINNABAR
        );
        addEndGround(context, EndBlocks.THALLASIUM.ore);
        addEndGround(context, EndBlocks.ENDSTONE_DUST);
        addEndGround(context, EndBlocks.AMBER_ORE);
        addEndGround(context, EndBlocks.CAVE_MOSS);

        context.add(
                CommonBlockTags.END_STONES,
                EndBlocks.ENDER_ORE,
                EndBlocks.BRIMSTONE
        );
        context.add(CommonBlockTags.END_STONES, EndBlocks.BRIMSTONE);
        context.add(BlockTags.ANVIL, EndBlocks.AETERNIUM_ANVIL);
        context.add(BlockTags.BEACON_BASE_BLOCKS, EndBlocks.AETERNIUM_BLOCK);
        context.add(
                BlockTags.DRAGON_IMMUNE,
                EndBlocks.ENDER_ORE,
                EndBlocks.ETERNAL_PEDESTAL,
                EndBlocks.FLAVOLITE_RUNED_ETERNAL,
                EndBlocks.FLAVOLITE_RUNED
        );

        context.add(EndTags.BONEMEAL_TARGET_WATER_GRASS, CommonBlockTags.END_STONES);

        EndBiomesProvider
                .BIOMES
                .values()
                .stream()
                .filter(info -> info.config() instanceof EndBiome.Config)
                .map(info -> info.config())
                .forEach(config -> {
                    config.surfaceMaterial().addBiomeSurfaceToEndGroup(context, CommonBlockTags.END_STONES);
                });

        MaterialManager.stream().forEach(m -> m.registerBlockTags(context));

        Block[] charnias = new Block[]{
                EndBlocks.CHARNIA_CYAN,
                EndBlocks.CHARNIA_GREEN,
                EndBlocks.CHARNIA_ORANGE,
                EndBlocks.CHARNIA_LIGHT_BLUE,
                EndBlocks.CHARNIA_PURPLE,
                EndBlocks.CHARNIA_RED
        };

        for (Block charnia : charnias) {
            context.add(EndTags.BONEMEAL_SOURCE_WATER_GRASS, charnia);
        }
    }
}

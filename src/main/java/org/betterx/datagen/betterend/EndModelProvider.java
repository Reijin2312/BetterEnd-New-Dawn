package org.betterx.datagen.betterend;

import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.FlowerPotBlock;
import org.betterx.betterend.blocks.basis.PedestalBlock;
import org.betterx.betterend.blocks.basis.PottableLeavesBlock;
import org.betterx.betterend.client.models.EndModels;
import org.betterx.betterend.interfaces.PottablePlant;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.core.api.IntegrationCore;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverModelProvider;

import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.core.registries.BuiltInRegistries;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class EndModelProvider extends WoverModelProvider {
    private static final ResourceLocation POTTED_CROSS_PARENT = BetterEnd.C.mk("block/potted_cross");
    private static final ResourceLocation POTTED_TINTED_CROSS_PARENT = BetterEnd.C.mk("block/potted_tinted_cross");
    private static final double POTTED_PLANT_MIN_Y = 7.5D;
    private final Set<ResourceLocation> generatedModels = new HashSet<>();
    private final Map<ResourceLocation, String> resourceCache = new HashMap<>();

    @Override
    protected void bootstrapItemModels(ItemModelGenerators itemModelGenerator) {
        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            if (id != null
                    && id.getNamespace().equals(BetterEnd.MOD_ID)
                    && (id.getPath().endsWith("_boat") || id.getPath().endsWith("_raft"))) {
                itemModelGenerator.generateFlatItem(item, "", ModelTemplates.FLAT_ITEM);
            }
        }
    }

    @Override
    protected void bootstrapBlockStateModels(WoverBlockModelGenerators generator) {
        this.addFromRegistry(
                generator,
                BlockRegistry.forMod(BetterEnd.C),
                true,
                ModelOverides.create()
                             .override(EndBlocks.TWISTED_VINE, createTwistedVineModel(generator))
                             .override(EndBlocks.AMBER_MOSS, createAmberMossModel(generator))
                             .override(EndBlocks.AMBER_MOSS_PATH, createAmberMossPathModel(generator, EndBlocks.AMBER_MOSS))
                             .override(EndBlocks.QUARTZ_PEDESTAL, block -> PedestalBlock.provideBlockModel(generator, new TextureMapping()
                                     .put(TextureSlot.TOP, IntegrationCore.MINECRAFT.mk("block/quartz_pillar_top"))
                                     .put(TextureSlot.BOTTOM, IntegrationCore.MINECRAFT.mk("block/quartz_block_bottom"))
                                     .put(EndModels.BASE, IntegrationCore.MINECRAFT.mk("block/quartz_block_side"))
                                     .put(EndModels.PILLAR, IntegrationCore.MINECRAFT.mk("block/quartz_pillar")), block))
                             .override(EndBlocks.PURPUR_PEDESTAL, block -> PedestalBlock.provideBlockModel(generator, new TextureMapping()
                                     .put(TextureSlot.TOP, IntegrationCore.MINECRAFT.mk("block/purpur_pillar_top"))
                                     .put(TextureSlot.BOTTOM, IntegrationCore.MINECRAFT.mk("block/purpur_block"))
                                     .put(EndModels.BASE, IntegrationCore.MINECRAFT.mk("block/purpur_block"))
                                     .put(EndModels.PILLAR, IntegrationCore.MINECRAFT.mk("block/purpur_pillar")), block))
                             .override(EndBlocks.NEON_CACTUS_BLOCK_STAIRS, block -> {
                                 final var id = TextureMapping.getBlockTexture(block);
                                 final var texture = BetterEnd.C.mk("block/neon_cactus_block");
                                 final var mapping = new TextureMapping()
                                         .put(TextureSlot.TOP, texture.withSuffix("_top"))
                                         .put(TextureSlot.BOTTOM, texture.withSuffix("_top"))
                                         .put(TextureSlot.SIDE, texture.withSuffix("_side"));
                                 final var stairs = EndModels.LIT_STAIRS.create(id, mapping, generator.modelOutput());
                                 final var stairs_outer = EndModels.LIT_STAIRS_OUTER.create(id.withSuffix("_outer"), mapping, generator.modelOutput());
                                 final var stairs_inner = EndModels.LIT_STAIRS_INNER.create(id.withSuffix("_inner"), mapping, generator.modelOutput());
                                 generator.createStairsWithModels(block, stairs, stairs_outer, stairs_inner);
                             }).override(EndBlocks.NEON_CACTUS_BLOCK_SLAB, block -> {
                                 final var id = TextureMapping.getBlockTexture(block);
                                 final var texture = TextureMapping.getBlockTexture(EndBlocks.NEON_CACTUS_BLOCK);
                                 final var mapping = new TextureMapping()
                                         .put(TextureSlot.TOP, texture.withSuffix("_top"))
                                         .put(TextureSlot.BOTTOM, texture.withSuffix("_top"))
                                         .put(TextureSlot.SIDE, texture.withSuffix("_side"));
                                 generator.createSlab(block, EndBlocks.NEON_CACTUS_BLOCK, mapping);
                             })
                             .ignore(EndBlocks.CRYSTAL_GRASS)
                             .ignore(EndBlocks.CYAN_MOSS)
                             .override(EndBlocks.AERIDIUM, generator::createFlatItem)
                             .ignore(EndBlocks.BUSHY_GRASS)
                             .ignore(EndBlocks.JUNGLE_FERN)
                             .ignore(EndBlocks.BULB_MOSS)
                             .ignore(EndBlocks.FLAMAEA)
                             .ignore(EndBlocks.CAVE_BUSH)
                             .ignore(EndBlocks.BULB_VINE)
                             .ignore(EndBlocks.BULB_VINE_SEED)
                             .ignore(EndBlocks.SILK_MOTH_NEST)
                             .override(EndBlocks.SILK_MOTH_HIVE, generator::delegateItemModel)
                             .ignore(EndBlocks.SMARAGDANT_SUBBLOCKS.stairs)
                             .ignore(EndBlocks.SMARAGDANT_SUBBLOCKS.wall)
                             .ignore(EndBlocks.SMARAGDANT_SUBBLOCKS.brick_stairs)
                             .ignore(EndBlocks.SMARAGDANT_SUBBLOCKS.brick_wall)
                             .override(EndBlocks.SMARAGDANT_CRYSTAL, block -> {
                                 final var texture = BetterEnd.C.mk("block/smaragdant_crystal");
                                 final var mapping = new TextureMapping()
                                         .put(TextureSlot.END, texture.withSuffix("_top"))
                                         .put(TextureSlot.SIDE, texture.withSuffix("_side"));
                                 generator.createRotatedPillar(block, mapping);
                             })
                             .override(EndBlocks.BUDDING_SMARAGDANT_CRYSTAL, block -> {
                                 final var texture = BetterEnd.C.mk("block/budding_smaragdant_crystal");
                                 final var mapping = new TextureMapping()
                                         .put(TextureSlot.END, texture.withSuffix("_top"))
                                         .put(TextureSlot.SIDE, texture.withSuffix("_side"));
                                 generator.createRotatedPillar(block, mapping);
                             })
                             .override(EndBlocks.SMARAGDANT_SUBBLOCKS.slab, block -> {
                                 final var texture = BetterEnd.C.mk("block/smaragdant_crystal");
                                 final var mapping = new TextureMapping()
                                         .put(TextureSlot.TOP, texture.withSuffix("_top"))
                                         .put(TextureSlot.BOTTOM, texture.withSuffix("_top"))
                                         .put(TextureSlot.SIDE, texture.withSuffix("_side"));
                                 generator.createSlab(block, EndBlocks.SMARAGDANT_CRYSTAL, mapping);
                             })
                             .override(EndBlocks.SMARAGDANT_SUBBLOCKS.pillar, block -> {
                                 final var texture = BetterEnd.C.mk("block/smaragdant_crystal_pillar");
                                 final var mapping = new TextureMapping()
                                         .put(TextureSlot.END, texture.withSuffix("_top"))
                                         .put(TextureSlot.SIDE, texture.withSuffix("_side"));
                                 generator.createRotatedPillar(block, mapping);
                             })
//                             .ignore(EndBlocks.GOLD_CHANDELIER)
//                             .ignore(EndBlocks.IRON_CHANDELIER)
                             .ignore(EndBlocks.LUCERNIA_OUTER_LEAVES)
                             .ignore(EndBlocks.SULPHUR_CRYSTAL)
                             .override(EndBlocks.MOSSY_OBSIDIAN, generator::delegateItemModel)
                             .override(EndBlocks.HYDROTHERMAL_VENT, generator::delegateItemModel)
                             .ignore(EndBlocks.FLAVOLITE_RUNED_ETERNAL)
                             .ignore(EndBlocks.FLAVOLITE_RUNED)
                             .ignore(EndBlocks.MOSSY_GLOWSHROOM_HYMENOPHORE)
                             .ignore(EndBlocks.END_LOTUS_SEED)
                             .ignore(EndBlocks.END_LOTUS_LEAF)
                             .override(EndBlocks.CAVE_PUMPKIN, generator::delegateItemModel)
                             .ignore(EndBlocks.FILALUX)
                             .ignore(EndBlocks.CREEPING_MOSS)
                             .ignore(EndBlocks.RESPAWN_OBELISK)
                             .override(EndBlocks.TWISTED_UMBRELLA_MOSS, b -> generator.createFlatItem(b, BetterEnd.C.mk("item/twisted_umbrella_moss_small")))
                             .override(EndBlocks.TWISTED_UMBRELLA_MOSS_TALL, b -> generator.createFlatItem(b, BetterEnd.C.mk("item/twisted_umbrella_moss_large")))
                             .override(EndBlocks.UMBRELLA_MOSS, b -> generator.createFlatItem(b, BetterEnd.C.mk("item/umbrella_moss_small")))
                             .override(EndBlocks.UMBRELLA_MOSS_TALL, b -> generator.createFlatItem(b, BetterEnd.C.mk("item/umbrella_moss_large")))
                             .ignore(EndBlocks.BLOSSOM_BERRY)
                             .ignore(EndBlocks.CAVE_PUMPKIN_SEED)
                             .ignore(EndBlocks.CHORUS_MUSHROOM)
                             .ignore(EndBlocks.SHADOW_BERRY)
                             .ignore(EndBlocks.LUMECORN_SEED)
                             .ignore(EndBlocks.HYDRALUX_SAPLING)
                             .ignore(EndBlocks.CHORUS_GRASS)
                             .ignore(EndBlocks.SMALL_JELLYSHROOM)
                             .ignore(EndBlocks.CAVE_GRASS)
                             .ignore(EndBlocks.GLOWING_PILLAR_SEED)
                             .ignore(EndBlocks.END_LILY_SEED)
                             .ignore(EndBlocks.BLUE_VINE_SEED)
                             .ignore(EndBlocks.TUBE_WORM)
                             .ignore(EndBlocks.AMBER_ROOT)
                             .ignore(EndBlocks.PURPLE_POLYPORE)
                             .ignore(EndBlocks.AURANT_POLYPORE)
                             .ignore(EndBlocks.NEEDLEGRASS)
                             .ignore(EndBlocks.VIOLECITE.flowerPot)
                             .ignore(EndBlocks.UMBRALITH.flowerPot)
                             .ignore(EndBlocks.SULPHURIC_ROCK.flowerPot)
                             .ignore(EndBlocks.VIRID_JADESTONE.flowerPot)
                             .ignore(EndBlocks.AZURE_JADESTONE.flowerPot)
                             .ignore(EndBlocks.SANDY_JADESTONE.flowerPot)
                             .ignore(EndBlocks.FLAVOLITE.flowerPot)
                             .ignore(EndBlocks.ENDSTONE_FLOWER_POT)
                             .ignore(EndBlocks.SMARAGDANT_CRYSTAL_SHARD)
                             .override(EndBlocks.END_LOTUS_STEM, generator::delegateItemModel)
//                             .ignore(EndBlocks.THALLASIUM.chandelier)
//                             .ignore(EndBlocks.TERMINITE.chandelier)
                             .ignore(EndBlocks.RUTISCUS)
                             .ignore(EndBlocks.MURKWEED)
                             .ignore(EndBlocks.LANCELEAF_SEED)
                             .ignore(EndBlocks.CHARNIA_RED)
                             .ignore(EndBlocks.CHARNIA_CYAN)
                             .ignore(EndBlocks.CHARNIA_GREEN)
                             .ignore(EndBlocks.CHARNIA_ORANGE)
                             .ignore(EndBlocks.CHARNIA_PURPLE)
                             .ignore(EndBlocks.CHARNIA_LIGHT_BLUE)
                             .ignore(EndBlocks.BOLUX_MUSHROOM)
                             .ignore(EndBlocks.SMALL_AMARANITA_MUSHROOM)
                             .ignore(EndBlocks.BUBBLE_CORAL)
                             .ignore(EndBlocks.FILALUX_WINGS)
                             .override(EndBlocks.SALTEAGO, generator::createFlatItem)
                             .ignore(EndBlocks.RUBINEA)
                             .ignore(EndBlocks.RUSCUS)
                             .ignore(EndBlocks.TENANEA_FLOWERS)
                             .ignore(EndBlocks.MAGNULA)
                             .override(EndBlocks.CLAWFERN, generator::createFlatItem)
                             .override(EndBlocks.LARGE_AMARANITA_MUSHROOM, generator::createFlatItem)
                             .override(EndBlocks.LANCELEAF, generator::createFlatItem)
                             .override(EndBlocks.END_LILY, generator::createFlatItem)
                             .override(EndBlocks.LUMECORN, generator::createFlatItem)
                             .override(EndBlocks.END_LOTUS_FLOWER, generator::createFlatItem)
                             .override(EndBlocks.HYDRALUX, generator::createFlatItem)
                             .override(EndBlocks.LAMELLARIUM, generator::createFlatItem)
                             .override(EndBlocks.LUTEBUS, generator::createFlatItem)
                             .override(EndBlocks.MOSSY_GLOWSHROOM_CAP, generator::delegateItemModel)
                             .override(EndBlocks.MOSSY_GLOWSHROOM_FUR, generator::createFlatItem)
                             .override(EndBlocks.AMARANITA_FUR, generator::createFlatItem)
                             .override(EndBlocks.ORANGO, generator::createFlatItem)
                             .override(EndBlocks.FRACTURN, generator::createFlatItem)
                             .override(EndBlocks.GLOBULAGUS, generator::createFlatItem)
                             .override(EndBlocks.FLAMMALIX, generator::createFlatItem)
                             .override(EndBlocks.AMBER_GRASS, generator::createFlatItem)
                             .override(EndBlocks.BLOOMING_COOKSONIA, generator::createFlatItem)
                             .override(EndBlocks.BLUE_VINE, generator::createFlatItem)
                             .override(EndBlocks.BLUE_VINE_FUR, generator::createFlatItem)
                             .ignore(EndBlocks.DENSE_VINE)
                             .override(EndBlocks.GLOWING_PILLAR_LEAVES, generator::createFlatItem)
                             .override(EndBlocks.GLOWING_PILLAR_ROOTS, generator::createFlatItem)
                             //.ignore(BYGBlocks.IVIS_MOSS).ignore(BYGBlocks.IVIS_VINE).ignore(BYGBlocks.NIGHTSHADE_MOSS)
                             .override(EndBlocks.JUNGLE_GRASS, generator::createFlatItem)
                             .ignore(EndBlocks.JUNGLE_VINE)
                             .ignore(EndBlocks.POND_ANEMONE)
                             .override(EndBlocks.SHADOW_PLANT, generator::createFlatItem)
                             .override(EndBlocks.TAIL_MOSS, generator::createFlatItem)
                             .override(EndBlocks.SULPHURIC_ROCK.stone, generator::delegateItemModel)
                             .override(EndBlocks.TENANEA_OUTER_LEAVES, generator::delegateItemModel)
                             .override(EndBlocks.UMBRALITH.stone, b -> generator.delegateItemModel(b, BetterEnd.C.mk("block/umbralith_5")))
                             .override(EndBlocks.TWISTED_MOSS, generator::createFlatItem)
                             .override(EndBlocks.VAIOLUSH_FERN, generator::createFlatItem)
                             .ignore(EndBlocks.BRIMSTONE)
                             .override(EndBlocks.HELIX_TREE_LEAVES, generator::delegateItemModel)
                             .override(EndBlocks.MENGER_SPONGE, generator::delegateItemModel)
                             .override(EndBlocks.MENGER_SPONGE_WET, generator::delegateItemModel)
                             .override(EndBlocks.VIOLECITE.brickWall, b -> generator.delegateItemModel(b, BetterEnd.C.mk("block/violecite_bricks_wall_post")))
                             .override(EndBlocks.MOSSY_GLOWSHROOM.getLog(), createRandomPillarModel(generator))
                             .override(EndBlocks.MOSSY_GLOWSHROOM.getBark(), createRandomPillarModel(generator))
                             .override(EndBlocks.MOSSY_GLOWSHROOM.getBlock(WoodSlots.STRIPPED_LOG), createRandomPillarModel(generator))
                             .override(EndBlocks.MOSSY_GLOWSHROOM.getBlock(WoodSlots.STRIPPED_BARK), createRandomPillarModel(generator))
                             .override(EndBlocks.PYTHADENDRON.getLog(), createRandomPillarModel(generator))
                             .override(EndBlocks.PYTHADENDRON.getBark(), createRandomPillarModel(generator))
                             .override(EndBlocks.PYTHADENDRON.getBlock(WoodSlots.STRIPPED_LOG), createRandomPillarModel(generator))
                             .override(EndBlocks.PYTHADENDRON.getBlock(WoodSlots.STRIPPED_BARK), createRandomPillarModel(generator))
                             .override(EndBlocks.LACUGROVE.getLog(), createRandomPillarModel(generator))
                             .override(EndBlocks.LACUGROVE.getBark(), createRandomPillarModel(generator))
                             .override(EndBlocks.LACUGROVE.getBlock(WoodSlots.STRIPPED_LOG), createRandomPillarModel(generator))
                             .override(EndBlocks.LACUGROVE.getBlock(WoodSlots.STRIPPED_BARK), createRandomPillarModel(generator))
                             .override(EndBlocks.DRAGON_TREE.getLog(), createRandomPillarModel(generator))
                             .override(EndBlocks.DRAGON_TREE.getBark(), createRandomPillarModel(generator))
                             .override(EndBlocks.DRAGON_TREE.getBlock(WoodSlots.STRIPPED_LOG), createRandomPillarModel(generator))
                             .override(EndBlocks.DRAGON_TREE.getBlock(WoodSlots.STRIPPED_BARK), createRandomPillarModel(generator))
                             .override(EndBlocks.LUCERNIA.getLog(), createRandomPillarModel(generator))
                             .override(EndBlocks.LUCERNIA.getBark(), createRandomPillarModel(generator))
                             .override(EndBlocks.LUCERNIA.getBlock(WoodSlots.STRIPPED_LOG), createRandomPillarModel(generator))
                             .override(EndBlocks.LUCERNIA.getBlock(WoodSlots.STRIPPED_BARK), createRandomPillarModel(generator))
                             .override(EndBlocks.END_LOTUS.getLog(), createRandomPillarModel(generator))
                             .override(EndBlocks.END_LOTUS.getBark(), createRandomPillarModel(generator))
                             .override(EndBlocks.END_LOTUS.getBlock(WoodSlots.STRIPPED_LOG), createRandomPillarModel(generator))
                             .override(EndBlocks.END_LOTUS.getBlock(WoodSlots.STRIPPED_BARK), createRandomPillarModel(generator))
                             .override(EndBlocks.TENANEA.getLog(), createRandomPillarModel(generator))
                             .override(EndBlocks.TENANEA.getBark(), createRandomPillarModel(generator))
                             .override(EndBlocks.TENANEA.getBlock(WoodSlots.STRIPPED_LOG), createRandomPillarModel(generator))
                             .override(EndBlocks.TENANEA.getBlock(WoodSlots.STRIPPED_BARK), createRandomPillarModel(generator))
                             .override(EndBlocks.HELIX_TREE.getLog(), createRandomPillarModel(generator))
                             .override(EndBlocks.HELIX_TREE.getBark(), createRandomPillarModel(generator))
                             .override(EndBlocks.HELIX_TREE.getBlock(WoodSlots.STRIPPED_LOG), createRandomPillarModel(generator))
                             .override(EndBlocks.HELIX_TREE.getBlock(WoodSlots.STRIPPED_BARK), createRandomPillarModel(generator))
                             .override(EndBlocks.UMBRELLA_TREE.getLog(), createRandomPillarModel(generator))
                             .override(EndBlocks.UMBRELLA_TREE.getBark(), createRandomPillarModel(generator))
                             .override(EndBlocks.UMBRELLA_TREE.getBlock(WoodSlots.STRIPPED_LOG), createRandomPillarModel(generator))
                             .override(EndBlocks.UMBRELLA_TREE.getBlock(WoodSlots.STRIPPED_BARK), createRandomPillarModel(generator))
                             .override(EndBlocks.JELLYSHROOM.getLog(), createRandomPillarModel(generator))
                             .override(EndBlocks.JELLYSHROOM.getBark(), createRandomPillarModel(generator))
                             .override(EndBlocks.JELLYSHROOM.getBlock(WoodSlots.STRIPPED_LOG), createRandomPillarModel(generator))
                             .override(EndBlocks.JELLYSHROOM.getBlock(WoodSlots.STRIPPED_BARK), createRandomPillarModel(generator))
                             .override(EndBlocks.NEON_CACTUS, b -> generator.delegateItemModel(b, BetterEnd.C.mk("block/neon_cactus_small")))
                             .ignore(EndBlocks.AMARANITA_STEM)
                             .ignore(EndBlocks.MOSSY_DRAGON_BONE)
        );

        generateFlowerPotModels(generator);
    }

    private static ModelOverides.@NotNull BlockModelProvider createAmberMossPathModel(
            WoverBlockModelGenerators generator,
            Block baseBlock
    ) {
        return block -> {
            final var endStone = TextureMapping.getBlockTexture(Blocks.END_STONE);
            final var baseTexture = TextureMapping.getBlockTexture(block, "_top");
            final var models = List.of("_1", "_2", "_3").stream().map(suffix -> {
                final var side = TextureMapping.getBlockTexture(baseBlock, "_side" + suffix);
                final var mapping = new TextureMapping()
                        .put(TextureSlot.BOTTOM, endStone)
                        .put(TextureSlot.TOP, baseTexture)
                        .put(TextureSlot.SIDE, side);
                return EndModels.PATH.createWithSuffix(block, suffix, mapping, generator.modelOutput());
            }).toList();

            buildRotated(generator, block, models);
        };
    }

    private static ModelOverides.@NotNull BlockModelProvider createAmberMossModel(WoverBlockModelGenerators generator) {
        return block -> {
            final var endStone = TextureMapping.getBlockTexture(Blocks.END_STONE);
            final var baseTexture = TextureMapping.getBlockTexture(block, "_top");
            final var models = List.of("_1", "_2", "_3").stream().map(suffix -> {
                final var side = TextureMapping.getBlockTexture(block, "_side" + suffix);
                final var mapping = new TextureMapping()
                        .put(TextureSlot.DOWN, endStone)
                        .put(TextureSlot.UP, baseTexture)
                        .put(TextureSlot.PARTICLE, side)
                        .put(TextureSlot.EAST, side)
                        .put(TextureSlot.NORTH, side)
                        .put(TextureSlot.SOUTH, side)
                        .put(TextureSlot.WEST, side);
                return ModelTemplates.CUBE.createWithSuffix(block, suffix, mapping, generator.modelOutput());
            }).toList();

            buildRotated(generator, block, models);
        };
    }

    private static void buildRotated(WoverBlockModelGenerators generator, Block block, List<ResourceLocation> models) {
        final ArrayList<Variant> variants = new ArrayList<>(models.size() * 4);
        models.forEach(model -> {
            variants.add(Variant.variant().with(VariantProperties.MODEL, model));
            variants.add(Variant.variant()
                                .with(VariantProperties.MODEL, model)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90));
            variants.add(Variant.variant()
                                .with(VariantProperties.MODEL, model)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180));
            variants.add(Variant.variant()
                                .with(VariantProperties.MODEL, model)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
        });

        generator.acceptBlockState(MultiVariantGenerator.multiVariant(block, variants.toArray(new Variant[0])));
        generator.delegateItemModel(block, models.get(0));
    }

    private ModelOverides.@NotNull BlockModelProvider createRandomPillarModel(WoverBlockModelGenerators generator) {
        return block -> {
            ResourceLocation model = ModelLocationUtils.getModelLocation(block);
            if (!modelExists(model)) {
                ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
                String path = blockId.getPath();
                boolean isBark = path.endsWith("_bark");
                String textureBase = isBark ? path.substring(0, path.length() - "_bark".length()) + "_log" : path;
                ResourceLocation side = BetterEnd.C.mk("block/" + textureBase + "_side");
                ResourceLocation end = isBark ? side : BetterEnd.C.mk("block/" + textureBase + "_top");

                ModelTemplates.CUBE_COLUMN.create(
                        block,
                        new TextureMapping().put(TextureSlot.END, end).put(TextureSlot.SIDE, side),
                        generator.modelOutput()
                );
                generatedModels.add(model);
            }

            List<ResourceLocation> models = new ArrayList<>();
            models.add(model);
            for (int i = 2; i <= 5; i++) {
                ResourceLocation variant = model.withSuffix("_" + i);
                if (modelExists(variant)) {
                    models.add(variant);
                }
            }

            generator.acceptBlockState(MultiVariantGenerator
                    .multiVariant(block)
                    .with(PropertyDispatch
                                  .property(RotatedPillarBlock.AXIS)
                                  .select(Direction.Axis.X, createPillarVariants(models, VariantProperties.Rotation.R90, VariantProperties.Rotation.R90))
                                  .select(Direction.Axis.Y, createPillarVariants(models, null, null))
                                  .select(Direction.Axis.Z, createPillarVariants(models, VariantProperties.Rotation.R90, null))
                    )
            );
            generator.delegateItemModel(block, model);
        };
    }

    private static List<Variant> createPillarVariants(
            List<ResourceLocation> models,
            VariantProperties.Rotation xRotation,
            VariantProperties.Rotation yRotation
    ) {
        return models.stream().map(model -> {
            Variant variant = Variant.variant().with(VariantProperties.MODEL, model);
            if (xRotation != null) {
                variant = variant.with(VariantProperties.X_ROT, xRotation);
            }
            if (yRotation != null) {
                variant = variant.with(VariantProperties.Y_ROT, yRotation);
            }
            return variant;
        }).toList();
    }

    private void generateFlowerPotModels(WoverBlockModelGenerators generator) {
        EndBlocks.ensureRegistered();
        for (Block block : EndBlocks.getModBlocks()) {
            if (!(block instanceof FlowerPotBlock)) continue;

            Variant variant = Variant.variant()
                                     .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(block));
            generator.acceptBlockState(
                    MultiVariantGenerator.multiVariant(block)
                                         .with(PropertyDispatch.property(FlowerPotBlock.POT_LIGHT)
                                                               .select(0, variant)
                                                               .select(1, variant)
                                                               .select(2, variant)
                                                               .select(3, variant))
            );
        }
    }

    private ResourceLocation resolvePottedPlantModel(WoverBlockModelGenerators generator, Block plant) {
        ResourceLocation plantId = BuiltInRegistries.BLOCK.getKey(plant);
        if (plantId == null) return null;

        ResourceLocation pottedModelId = ResourceLocation.fromNamespaceAndPath(
                plantId.getNamespace(),
                "block/" + plantId.getPath() + "_potted"
        );
        if (modelExists(pottedModelId)) return pottedModelId;

        if (plant instanceof SaplingBlock) {
            return createPottedCross(generator, pottedModelId, TextureMapping.getBlockTexture(plant), false);
        }

        if (plant instanceof PottableLeavesBlock) {
            ResourceLocation leavesModel = createPottedLeavesModel(generator, pottedModelId, plantId);
            if (leavesModel != null) return leavesModel;
        }

        ResourceLocation stateModel = resolveStateModel(plant);
        if (stateModel != null) {
            ResourceLocation crossModel = createPottedCrossFromModel(generator, pottedModelId, stateModel);
            if (crossModel != null) return crossModel;

            ResourceLocation shiftedModel = createPottedModelFromModel(generator, pottedModelId, stateModel);
            if (shiftedModel != null) return shiftedModel;

            return stateModel;
        }

        return createPottedCross(generator, pottedModelId, TextureMapping.getBlockTexture(plant), false);
    }

    private ResourceLocation resolveStateModel(Block plant) {
        ResourceLocation plantId = BuiltInRegistries.BLOCK.getKey(plant);
        if (plantId == null) return null;

        JsonObject blockState = readBlockStateJson(plantId);
        if (blockState == null || !blockState.has("variants")) return null;

        JsonElement variants = blockState.get("variants");
        JsonElement variant = null;
        if (variants.isJsonArray()) {
            if (!variants.getAsJsonArray().isEmpty()) variant = variants.getAsJsonArray().get(0);
        } else if (variants.isJsonObject()) {
            String state = plant instanceof PottablePlant pottablePlant ? pottablePlant.getPottedState() : "";
            variant = variants.getAsJsonObject().get(state == null ? "" : state);
        }
        if (variant == null) return null;

        String model = null;
        if (variant.isJsonArray()) {
            if (!variant.getAsJsonArray().isEmpty()) {
                model = variant.getAsJsonArray().get(0).getAsJsonObject().get("model").getAsString();
            }
        } else if (variant.isJsonObject() && variant.getAsJsonObject().has("model")) {
            model = variant.getAsJsonObject().get("model").getAsString();
        }
        if (model == null || model.isBlank()) return null;

        ResourceLocation modelId = ResourceLocation.tryParse(model);
        return modelId != null ? modelId : ResourceLocation.withDefaultNamespace(model);
    }

    private ResourceLocation createPottedLeavesModel(
            WoverBlockModelGenerators generator,
            ResourceLocation modelId,
            ResourceLocation plantId
    ) {
        if (modelExists(modelId)) return modelId;

        String leaves = plantId.getPath().contains("lucernia") ? plantId.getPath() + "_1" : plantId.getPath();
        String stem = plantId.getPath().replace("_leaves", "_log_side");
        JsonObject model = createPatternModel(
                BetterEnd.C.mk("patterns/block/potted_leaves.json"),
                Map.of("%leaves%", leaves, "%stem%", stem)
        );
        if (model == null) {
            BetterEnd.LOGGER.warn("Missing potted leaves pattern for {}", plantId);
            return null;
        }
        generator.acceptModelOutput(modelId, () -> model);
        generatedModels.add(modelId);
        return modelId;
    }

    private ResourceLocation createPottedCrossFromModel(
            WoverBlockModelGenerators generator,
            ResourceLocation pottedModelId,
            ResourceLocation sourceModelId
    ) {
        JsonObject model = readModelJson(sourceModelId);
        if (model == null || !model.has("parent")) return null;

        String parent = model.get("parent").getAsString();
        boolean tinted = parent.endsWith("tinted_cross");
        if (!tinted && !parent.endsWith("cross")) return null;

        ResourceLocation texture = getModelTexture(model, "cross");
        if (texture == null) texture = getModelTexture(model, "particle");
        return texture == null ? null : createPottedCross(generator, pottedModelId, texture, tinted);
    }

    private ResourceLocation createPottedModelFromModel(
            WoverBlockModelGenerators generator,
            ResourceLocation pottedModelId,
            ResourceLocation sourceModelId
    ) {
        if (modelExists(pottedModelId)) return pottedModelId;

        JsonObject model = readModelJson(sourceModelId);
        if (model == null || !model.has("elements") || !model.get("elements").isJsonArray()) return null;
        JsonArray elements = model.getAsJsonArray("elements");
        if (elements.isEmpty()) return null;

        double minY = findMinElementY(elements);
        if (!Double.isFinite(minY)) return null;

        JsonObject pottedModel = model.deepCopy();
        shiftElementsY(pottedModel.getAsJsonArray("elements"), POTTED_PLANT_MIN_Y - minY);
        generator.acceptModelOutput(pottedModelId, () -> pottedModel);
        generatedModels.add(pottedModelId);
        return pottedModelId;
    }

    private ResourceLocation createPottedCross(
            WoverBlockModelGenerators generator,
            ResourceLocation modelId,
            ResourceLocation texture,
            boolean tinted
    ) {
        if (modelExists(modelId)) return modelId;

        JsonObject model = new JsonObject();
        model.addProperty("parent", (tinted ? POTTED_TINTED_CROSS_PARENT : POTTED_CROSS_PARENT).toString());
        JsonObject textures = new JsonObject();
        textures.addProperty("plant", texture.toString());
        model.add("textures", textures);
        generator.acceptModelOutput(modelId, () -> model);
        generatedModels.add(modelId);
        return modelId;
    }

    private double findMinElementY(JsonArray elements) {
        double minY = Double.POSITIVE_INFINITY;
        for (JsonElement element : elements) {
            if (!element.isJsonObject()) continue;
            JsonObject modelElement = element.getAsJsonObject();
            minY = minCoordinateY(minY, modelElement.get("from"));
            minY = minCoordinateY(minY, modelElement.get("to"));
        }
        return minY;
    }

    private double minCoordinateY(double minY, JsonElement coordinates) {
        if (coordinates == null || !coordinates.isJsonArray()) return minY;
        JsonArray array = coordinates.getAsJsonArray();
        return array.size() < 2 ? minY : Math.min(minY, array.get(1).getAsDouble());
    }

    private void shiftElementsY(JsonArray elements, double offset) {
        for (JsonElement element : elements) {
            if (!element.isJsonObject()) continue;
            JsonObject modelElement = element.getAsJsonObject();
            shiftCoordinateY(modelElement, "from", offset);
            shiftCoordinateY(modelElement, "to", offset);
            if (modelElement.has("rotation") && modelElement.get("rotation").isJsonObject()) {
                shiftCoordinateY(modelElement.getAsJsonObject("rotation"), "origin", offset);
            }
        }
    }

    private void shiftCoordinateY(JsonObject modelElement, String key, double offset) {
        if (!modelElement.has(key) || !modelElement.get(key).isJsonArray()) return;
        JsonArray coordinates = modelElement.getAsJsonArray(key);
        if (coordinates.size() >= 2) {
            coordinates.set(1, new JsonPrimitive(coordinates.get(1).getAsDouble() + offset));
        }
    }

    private ResourceLocation getModelTexture(JsonObject model, String key) {
        if (!model.has("textures")) return null;
        JsonObject textures = model.getAsJsonObject("textures");
        if (textures == null || !textures.has(key)) return null;

        String value = textures.get(key).getAsString();
        if (value.isBlank() || value.startsWith("#")) return null;
        ResourceLocation texture = ResourceLocation.tryParse(value);
        return texture != null ? texture : ResourceLocation.withDefaultNamespace(value);
    }

    private JsonObject createPatternModel(ResourceLocation patternId, Map<String, String> replacements) {
        String template = readRawResource(patternId);
        if (template == null) return null;

        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            template = template.replace(entry.getKey(), entry.getValue());
        }
        try {
            return JsonParser.parseString(template).getAsJsonObject();
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private boolean modelExists(ResourceLocation modelId) {
        return generatedModels.contains(modelId) || readModelJson(modelId) != null;
    }

    private JsonObject readBlockStateJson(ResourceLocation blockId) {
        return readJson(ResourceLocation.fromNamespaceAndPath(
                blockId.getNamespace(),
                "blockstates/" + blockId.getPath() + ".json"
        ));
    }

    private JsonObject readModelJson(ResourceLocation modelId) {
        return readJson(ResourceLocation.fromNamespaceAndPath(
                modelId.getNamespace(),
                "models/" + modelId.getPath() + ".json"
        ));
    }

    private JsonObject readJson(ResourceLocation resourceId) {
        String json = readRawResource(resourceId);
        if (json == null) return null;
        try {
            return JsonParser.parseString(json).getAsJsonObject();
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private String readRawResource(ResourceLocation id) {
        String cached = resourceCache.get(id);
        if (cached != null) return cached;

        String path = "assets/" + id.getNamespace() + "/" + id.getPath();
        try (InputStream input = EndModelProvider.class.getClassLoader().getResourceAsStream(path)) {
            if (input == null) return null;
            String content = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            resourceCache.put(id, content);
            return content;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static ModelOverides.@NotNull BlockModelProvider createTwistedVineModel(WoverBlockModelGenerators generator) {
        return block -> {
            final var itemTextureLocation = TextureMapping.getBlockTexture(block, "_bottom");
            var bottomMapping = new TextureMapping()
                    .put(TextureSlot.TEXTURE, itemTextureLocation);
            var middleMapping = new TextureMapping()
                    .put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(block));
            var topMapping = new TextureMapping()
                    .put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(block))
                    .put(EndModels.ROOTS, TextureMapping.getBlockTexture(block, "_roots"));

            var bottom_1 = EndModels.CROSS_NO_DISTORTION.createWithSuffix(block, "_bottom_1", bottomMapping, generator.modelOutput());
            var bottom_2 = EndModels.CROSS_NO_DISTORTION_INVERTED.createWithSuffix(block, "_bottom_2", bottomMapping, generator.modelOutput());
            var middle_1 = EndModels.CROSS_NO_DISTORTION.createWithSuffix(block, "_middle_1", middleMapping, generator.modelOutput());
            var middle_2 = EndModels.CROSS_NO_DISTORTION_INVERTED.createWithSuffix(block, "_middle_2", middleMapping, generator.modelOutput());
            var top = EndModels.TWISTED_VINE.createWithSuffix(block, "_top", topMapping, generator.modelOutput());

            generator.acceptBlockState(MultiVariantGenerator
                    .multiVariant(block)
                    .with(PropertyDispatch.property(BaseVineBlock.SHAPE)
                                          .select(
                                                  BlockProperties.TripleShape.TOP,
                                                  Variant.variant().with(VariantProperties.MODEL, top)
                                          )
                                          .select(
                                                  BlockProperties.TripleShape.MIDDLE,
                                                  List.of(
                                                          Variant.variant().with(VariantProperties.MODEL, middle_1),
                                                          Variant.variant().with(VariantProperties.MODEL, middle_2)
                                                  )

                                          )
                                          .select(
                                                  BlockProperties.TripleShape.BOTTOM,
                                                  List.of(
                                                          Variant.variant().with(VariantProperties.MODEL, bottom_1),
                                                          Variant.variant().with(VariantProperties.MODEL, bottom_2)
                                                  )
                                          )
                    )
            );

            generator.createFlatItem(block, itemTextureLocation);
        };
    }

    public EndModelProvider(ModCore modCore) {
        super(modCore);
    }
}

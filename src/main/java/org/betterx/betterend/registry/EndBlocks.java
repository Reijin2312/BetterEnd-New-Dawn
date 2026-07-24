package org.betterx.betterend.registry;
import java.util.List;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.material.MapColor;
import org.betterx.bclib.api.v3.tag.BCLBlockTags;
import org.betterx.bclib.blocks.*;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.*;
import org.betterx.betterend.blocks.basis.*;
import org.betterx.betterend.complexmaterials.*;
import org.betterx.betterend.item.material.EndArmorTier;
import org.betterx.betterend.item.material.EndToolMaterial;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;
import org.betterx.wover.tag.api.predefined.CommonPoiTags;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;


public class EndBlocks {
    // Terrain //
    public static Block ENDSTONE_DUST;
    public static Block END_MYCELIUM;
    public static Block END_MOSS;
    public static Block CHORUS_NYLIUM;
    public static Block CAVE_MOSS;
    public static Block CRYSTAL_MOSS;
    public static Block SHADOW_GRASS;
    public static Block PINK_MOSS;
    public static Block AMBER_MOSS;
    public static Block JUNGLE_MOSS;
    public static Block SANGNUM;
    public static Block RUTISCUS;
    public static Block PALLIDIUM_FULL;
    public static Block PALLIDIUM_HEAVY;
    public static Block PALLIDIUM_THIN;
    public static Block PALLIDIUM_TINY;

    // Roads //
    public static Block END_MYCELIUM_PATH;
    public static Block END_MOSS_PATH;
    public static Block CHORUS_NYLIUM_PATH;
    public static Block CAVE_MOSS_PATH;
    public static Block CRYSTAL_MOSS_PATH;
    public static Block SHADOW_GRASS_PATH;
    public static Block PINK_MOSS_PATH;
    public static Block AMBER_MOSS_PATH;
    public static Block JUNGLE_MOSS_PATH;
    public static Block SANGNUM_PATH;
    public static Block RUTISCUS_PATH;

    public static Block MOSSY_OBSIDIAN;
    public static Block DRAGON_BONE_BLOCK;
    public static Block DRAGON_BONE_STAIRS;
    public static Block DRAGON_BONE_SLAB;
    public static Block MOSSY_DRAGON_BONE;

    // Rocks //
    public static StoneMaterial FLAVOLITE;
    public static StoneMaterial VIOLECITE;
    public static StoneMaterial SULPHURIC_ROCK;
    public static StoneMaterial VIRID_JADESTONE;
    public static StoneMaterial AZURE_JADESTONE;
    public static StoneMaterial SANDY_JADESTONE;
    public static StoneMaterial UMBRALITH;
    public static Block BRIMSTONE;
    public static Block SULPHUR_CRYSTAL;
    public static Block MISSING_TILE;
    public static Block ENDSTONE_FLOWER_POT;

    public static Block FLAVOLITE_RUNED;
    public static Block FLAVOLITE_RUNED_ETERNAL;

    public static Block ANDESITE_PEDESTAL;
    public static Block DIORITE_PEDESTAL;
    public static Block GRANITE_PEDESTAL;
    public static Block QUARTZ_PEDESTAL;
    public static Block PURPUR_PEDESTAL;

    public static Block HYDROTHERMAL_VENT;
    public static Block VENT_BUBBLE_COLUMN;

    public static Block DENSE_SNOW;
    public static Block EMERALD_ICE;
    public static Block DENSE_EMERALD_ICE;
    public static Block ANCIENT_EMERALD_ICE;

    public static Block END_STONE_STALACTITE;
    public static Block END_STONE_STALACTITE_CAVEMOSS;

    // Wooden Materials And Trees //
    public static Block MOSSY_GLOWSHROOM_SAPLING;
    public static Block MOSSY_GLOWSHROOM_CAP;
    public static Block MOSSY_GLOWSHROOM_HYMENOPHORE;
    public static Block MOSSY_GLOWSHROOM_FUR;
    public static EndWoodenComplexMaterial MOSSY_GLOWSHROOM;

    public static Block PYTHADENDRON_SAPLING;
    public static Block PYTHADENDRON_LEAVES;
    public static EndWoodenComplexMaterial PYTHADENDRON;

    public static Block END_LOTUS_SEED;
    public static Block END_LOTUS_STEM;
    public static Block END_LOTUS_LEAF;
    public static Block END_LOTUS_FLOWER;
    public static EndWoodenComplexMaterial END_LOTUS;

    public static Block LACUGROVE_SAPLING;
    public static Block LACUGROVE_LEAVES;
    public static EndWoodenComplexMaterial LACUGROVE;

    public static Block DRAGON_TREE_SAPLING;
    public static Block DRAGON_TREE_LEAVES;
    public static EndWoodenComplexMaterial DRAGON_TREE;

    public static Block TENANEA_SAPLING;
    public static Block TENANEA_LEAVES;
    public static Block TENANEA_FLOWERS;
    public static Block TENANEA_OUTER_LEAVES;
    public static EndWoodenComplexMaterial TENANEA;

    public static Block HELIX_TREE_SAPLING;
    public static Block HELIX_TREE_LEAVES;
    public static EndWoodenComplexMaterial HELIX_TREE;

    public static Block UMBRELLA_TREE_SAPLING;
    public static Block UMBRELLA_TREE_MEMBRANE;
    public static Block UMBRELLA_TREE_CLUSTER;
    public static Block UMBRELLA_TREE_CLUSTER_EMPTY;
    public static EndWoodenComplexMaterial UMBRELLA_TREE;

    public static Block JELLYSHROOM_CAP_PURPLE;
    public static EndWoodenComplexMaterial JELLYSHROOM;

    public static Block LUCERNIA_SAPLING;
    public static Block LUCERNIA_LEAVES;
    public static Block LUCERNIA_OUTER_LEAVES;
    public static EndWoodenComplexMaterial LUCERNIA;

    public static EndWoodenComplexMaterial LUCERNIA_JELLY;

    // Small Plants //
    public static Block UMBRELLA_MOSS;
    public static Block UMBRELLA_MOSS_TALL;
    public static Block CREEPING_MOSS;
    public static Block CHORUS_GRASS;
    public static Block CAVE_GRASS;
    public static Block CRYSTAL_GRASS;
    public static Block SHADOW_PLANT;
    public static Block BUSHY_GRASS;
    public static Block AMBER_GRASS;
    public static Block TWISTED_UMBRELLA_MOSS;
    public static Block TWISTED_UMBRELLA_MOSS_TALL;
    public static Block JUNGLE_GRASS;
    public static Block BLOOMING_COOKSONIA;
    public static Block SALTEAGO;
    public static Block VAIOLUSH_FERN;
    public static Block FRACTURN;
    public static Block CLAWFERN;
    public static Block GLOBULAGUS;
    public static Block ORANGO;
    public static Block AERIDIUM;
    public static Block LUTEBUS;
    public static Block LAMELLARIUM;
    public static Block INFLEXIA;
    public static Block FLAMMALIX;


    public static MultifaceBlock CRYSTAL_MOSS_COVER;

    public static Block BLUE_VINE_SEED;
    public static Block BLUE_VINE;
    public static Block BLUE_VINE_LANTERN;
    public static Block BLUE_VINE_FUR;

    public static Block LANCELEAF_SEED;
    public static Block LANCELEAF;

    public static Block GLOWING_PILLAR_SEED;
    public static Block GLOWING_PILLAR_ROOTS;
    public static Block GLOWING_PILLAR_LUMINOPHOR;
    public static Block GLOWING_PILLAR_LEAVES;

    public static Block SMALL_JELLYSHROOM;
    public static Block BOLUX_MUSHROOM;

    public static Block LUMECORN_SEED;
    public static Block LUMECORN;

    public static Block SMALL_AMARANITA_MUSHROOM;
    public static Block LARGE_AMARANITA_MUSHROOM;
    public static Block AMARANITA_STEM;
    public static Block AMARANITA_HYPHAE;
    public static Block AMARANITA_HYMENOPHORE;
    public static Block AMARANITA_LANTERN;
    public static Block AMARANITA_FUR;
    public static Block AMARANITA_CAP;

    public static Block NEON_CACTUS;
    public static Block NEON_CACTUS_BLOCK;
    public static Block NEON_CACTUS_BLOCK_STAIRS;
    public static Block NEON_CACTUS_BLOCK_SLAB;

    // Crops
    public static Block SHADOW_BERRY;
    public static Block BLOSSOM_BERRY;
    public static Block AMBER_ROOT;
    public static Block CHORUS_MUSHROOM;
    //public static final Block PEARLBERRY = registerBlock(EndBlocks.prepareBlockPath("pearlberry_seed"), new PottableCropBlock(EndItems.BLOSSOM_BERRY, END_MOSS, END_MYCELIUM));
    public static Block CAVE_PUMPKIN_SEED;
    public static Block CAVE_PUMPKIN;

    // Water plants
    public static Block BUBBLE_CORAL;
    public static Block MENGER_SPONGE;
    public static Block MENGER_SPONGE_WET;
    public static Block CHARNIA_RED;
    public static Block CHARNIA_PURPLE;
    public static Block CHARNIA_ORANGE;
    public static Block CHARNIA_LIGHT_BLUE;
    public static Block CHARNIA_CYAN;
    public static Block CHARNIA_GREEN;

    public static Block END_LILY;
    public static Block END_LILY_SEED;

    public static Block HYDRALUX_SAPLING;
    public static Block HYDRALUX;
    public static Block HYDRALUX_PETAL_BLOCK;
    public static ColoredMaterial HYDRALUX_PETAL_BLOCK_COLORED;

    public static Block POND_ANEMONE;

    public static Block FLAMAEA;

    public static Block CAVE_BUSH;

    public static Block MURKWEED;
    public static Block NEEDLEGRASS;

    // Wall Plants //
    public static Block PURPLE_POLYPORE;
    public static Block AURANT_POLYPORE;
    public static Block TAIL_MOSS;
    public static Block CYAN_MOSS;
    public static Block TWISTED_MOSS;
    public static Block TUBE_WORM;
    public static Block BULB_MOSS;
    public static Block JUNGLE_FERN;
    public static Block RUSCUS;

    // Vines //
    public static Block DENSE_VINE;
    public static Block TWISTED_VINE;
    public static Block BULB_VINE_SEED;
    public static Block BULB_VINE;
    public static Block JUNGLE_VINE;
    public static Block RUBINEA;
    public static Block MAGNULA;
    public static Block FILALUX;
    public static Block FILALUX_WINGS;
    public static Block FILALUX_LANTERN;

    // Mob-Related
    public static Block SILK_MOTH_NEST;
    public static Block SILK_MOTH_HIVE;

    // Ores //
    public static Block ENDER_ORE;
    public static Block AMBER_ORE;

    // Materials //
    public static MetalMaterial THALLASIUM;

    public static MetalMaterial TERMINITE;
    public static Block AETERNIUM_BLOCK;
    public static Block CHARCOAL_BLOCK;

    public static Block ENDER_BLOCK;
    public static Block AURORA_CRYSTAL;
    public static Block AMBER_BLOCK;
    public static Block SMARAGDANT_CRYSTAL_SHARD;
    public static Block SMARAGDANT_CRYSTAL;
    public static CrystalSubblocksMaterial SMARAGDANT_SUBBLOCKS;
    public static Block BUDDING_SMARAGDANT_CRYSTAL;

    public static Block RESPAWN_OBELISK;

    // Lanterns
    public static Block ANDESITE_LANTERN;
    public static Block DIORITE_LANTERN;
    public static Block GRANITE_LANTERN;
    public static Block QUARTZ_LANTERN;
    public static Block PURPUR_LANTERN;
    public static Block END_STONE_LANTERN;
    public static Block BLACKSTONE_LANTERN;

    public static Block IRON_BULB_LANTERN;
    public static ColoredMaterial IRON_BULB_LANTERN_COLORED;

    public static Block IRON_CHANDELIER;
    public static Block GOLD_CHANDELIER;

    // Blocks With Entity //
    public static Block END_STONE_FURNACE;
    public static Block END_STONE_SMELTER;
    public static Block ETERNAL_PEDESTAL;
    public static Block INFUSION_PEDESTAL;
    public static Block AETERNIUM_ANVIL;

    // Technical
    public static Block END_PORTAL_BLOCK;

    // Variations
    public static VanillaVariantStoneMaterial END_STONE_BRICK_VARIATIONS;

    public static Block END_STONE_SLAB;
    public static Block END_STONE_STAIR;
    public static Block END_STONE_WALLS;


    private static BlockRegistry BLOCKS_REGISTRY;
    private static boolean blocksRegistered;

    static {
        getBlockRegistry();
    }

    /**
     * Гарантированно инициализирует все блоки (ленивая регистрация через BlockRegistry).
     * Вызываем из других реестров, чтобы поля не оставались null во время RegisterEvent.
     */
    public static void ensureRegistered() {
        getBlockRegistry();
    }

    private static void registerBlocks() {
        if (blocksRegistered) return;
        blocksRegistered = true;
        ENDSTONE_DUST = registerBlock(EndBlocks.prepareBlockPath("endstone_dust"), new EndstoneDustBlock());
        END_MYCELIUM = registerBlock(EndBlocks.prepareBlockPath("end_mycelium"), new EndTerrainBlock(MapColor.COLOR_LIGHT_BLUE) );
        END_MOSS = registerBlock(EndBlocks.prepareBlockPath("end_moss"), new EndTerrainBlock(MapColor.COLOR_CYAN), BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM );
        CHORUS_NYLIUM = registerBlock(EndBlocks.prepareBlockPath("chorus_nylium"), new EndTerrainBlock(MapColor.COLOR_MAGENTA), BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM );
        CAVE_MOSS = registerBlock(EndBlocks.prepareBlockPath("cave_moss"), new EndTripleTerrain(MapColor.COLOR_PURPLE), BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM );
        CRYSTAL_MOSS = registerBlock(EndBlocks.prepareBlockPath("crystal_moss"), new EndTerrainBlock(MapColor.COLOR_CYAN), BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM );
        SHADOW_GRASS = registerBlock(EndBlocks.prepareBlockPath("shadow_grass"), new ShadowGrassBlock(), BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM );
        PINK_MOSS = registerBlock(EndBlocks.prepareBlockPath("pink_moss"), new EndTerrainBlock(MapColor.COLOR_PINK), BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM );
        AMBER_MOSS = registerBlock(EndBlocks.prepareBlockPath("amber_moss"), new EndTerrainBlock(MapColor.COLOR_ORANGE), BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM );
        JUNGLE_MOSS = registerBlock(EndBlocks.prepareBlockPath("jungle_moss"), new EndTerrainBlock(MapColor.COLOR_GREEN), BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM );
        SANGNUM = registerBlock(EndBlocks.prepareBlockPath("sangnum"), new EndTerrainBlock(MapColor.COLOR_RED), BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM );
        RUTISCUS = registerBlock(EndBlocks.prepareBlockPath("rutiscus"), new EndTerrainBlock(MapColor.COLOR_ORANGE), BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM );
        PALLIDIUM_FULL = registerBlock(EndBlocks.prepareBlockPath("pallidium_full"), new PallidiumBlock("full", null), BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM );
        PALLIDIUM_HEAVY = registerBlock(EndBlocks.prepareBlockPath("pallidium_heavy"), new PallidiumBlock("heavy", PALLIDIUM_FULL), BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM );
        PALLIDIUM_THIN = registerBlock(EndBlocks.prepareBlockPath("pallidium_thin"), new PallidiumBlock("thin", PALLIDIUM_HEAVY), BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM );
        PALLIDIUM_TINY = registerBlock(EndBlocks.prepareBlockPath("pallidium_tiny"), new PallidiumBlock("tiny", PALLIDIUM_THIN), BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM );
        END_MYCELIUM_PATH = registerBlock(EndBlocks.prepareBlockPath("end_mycelium_path"), new BasePathBlock.Stone(END_MYCELIUM) );
        END_MOSS_PATH = registerBlock(EndBlocks.prepareBlockPath("end_moss_path"), new BasePathBlock.Stone(END_MOSS));
        CHORUS_NYLIUM_PATH = registerBlock(EndBlocks.prepareBlockPath("chorus_nylium_path"), new BasePathBlock.Stone(CHORUS_NYLIUM) );
        CAVE_MOSS_PATH = registerBlock(EndBlocks.prepareBlockPath("cave_moss_path"), new BasePathBlock.Stone(CAVE_MOSS));
        CRYSTAL_MOSS_PATH = registerBlock(EndBlocks.prepareBlockPath("crystal_moss_path"), new BasePathBlock.Stone(CRYSTAL_MOSS) );
        SHADOW_GRASS_PATH = registerBlock(EndBlocks.prepareBlockPath("shadow_grass_path"), new BasePathBlock.Stone(SHADOW_GRASS) );
        PINK_MOSS_PATH = registerBlock(EndBlocks.prepareBlockPath("pink_moss_path"), new BasePathBlock.Stone(PINK_MOSS));
        AMBER_MOSS_PATH = registerBlock(EndBlocks.prepareBlockPath("amber_moss_path"), new BasePathBlock.Stone(AMBER_MOSS));
        JUNGLE_MOSS_PATH = registerBlock(EndBlocks.prepareBlockPath("jungle_moss_path"), new BasePathBlock.Stone(JUNGLE_MOSS) );
        SANGNUM_PATH = registerBlock(EndBlocks.prepareBlockPath("sangnum_path"), new BasePathBlock.Stone(SANGNUM));
        RUTISCUS_PATH = registerBlock(EndBlocks.prepareBlockPath("rutiscus_path"), new BasePathBlock.Stone(RUTISCUS));
        MOSSY_OBSIDIAN = registerBlock(EndBlocks.prepareBlockPath("mossy_obsidian"), new MossyObsidian(), BCLBlockTags.BONEMEAL_SOURCE_OBSIDIAN );
        DRAGON_BONE_BLOCK = registerBlock(EndBlocks.prepareBlockPath("dragon_bone_block"), new BaseRotatedPillarBlock.Wood(Blocks.BONE_BLOCK, false), EndTags.BONEMEAL_TARGET_DRAGON_BONE );
        DRAGON_BONE_STAIRS = registerBlock(EndBlocks.prepareBlockPath("dragon_bone_stairs"), new BaseStairsBlock.Wood(DRAGON_BONE_BLOCK) );
        DRAGON_BONE_SLAB = registerBlock(EndBlocks.prepareBlockPath("dragon_bone_slab"), new BaseSlabBlock.Wood(DRAGON_BONE_BLOCK) );
        MOSSY_DRAGON_BONE = registerBlock(EndBlocks.prepareBlockPath("mossy_dragon_bone"), new MossyDragonBoneBlock(), EndTags.BONEMEAL_SOURCE_DRAGON_BONE );
        FLAVOLITE = new StoneMaterial("flavolite", MapColor.SAND);
        VIOLECITE = new StoneMaterial("violecite", MapColor.COLOR_PURPLE);
        SULPHURIC_ROCK = new StoneMaterial("sulphuric_rock", MapColor.COLOR_BROWN);
        VIRID_JADESTONE = new StoneMaterial("virid_jadestone", MapColor.COLOR_GREEN);
        AZURE_JADESTONE = new StoneMaterial( "azure_jadestone", MapColor.COLOR_LIGHT_BLUE );
        SANDY_JADESTONE = new StoneMaterial( "sandy_jadestone", MapColor.COLOR_YELLOW );
        UMBRALITH = new StoneMaterial("umbralith", MapColor.DEEPSLATE);
        BRIMSTONE = registerBlock(EndBlocks.prepareBlockPath("brimstone"), new BrimstoneBlock());
        SULPHUR_CRYSTAL = registerBlock(EndBlocks.prepareBlockPath("sulphur_crystal"), new SulphurCrystalBlock());
        MISSING_TILE = registerBlock(EndBlocks.prepareBlockPath("missing_tile"), new MissingTileBlock());
        ENDSTONE_FLOWER_POT = registerBlock(EndBlocks.prepareBlockPath("endstone_flower_pot"), new FlowerPotBlock.Stone(Blocks.END_STONE) );
        FLAVOLITE_RUNED = registerBlock(EndBlocks.prepareBlockPath("flavolite_runed"), new RunedFlavolite(false));
        FLAVOLITE_RUNED_ETERNAL = registerBlock(EndBlocks.prepareBlockPath("flavolite_runed_eternal"), new RunedFlavolite(true) );
        ANDESITE_PEDESTAL = registerBlock(EndBlocks.prepareBlockPath("andesite_pedestal"), new PedestalVanilla(Blocks.ANDESITE) );
        DIORITE_PEDESTAL = registerBlock(EndBlocks.prepareBlockPath("diorite_pedestal"), new PedestalVanilla(Blocks.DIORITE));
        GRANITE_PEDESTAL = registerBlock(EndBlocks.prepareBlockPath("granite_pedestal"), new PedestalVanilla(Blocks.GRANITE));
        QUARTZ_PEDESTAL = registerBlock(EndBlocks.prepareBlockPath("quartz_pedestal"), new PedestalVanilla(Blocks.QUARTZ_BLOCK) );
        PURPUR_PEDESTAL = registerBlock(EndBlocks.prepareBlockPath("purpur_pedestal"), new PedestalVanilla(Blocks.PURPUR_BLOCK) );
        HYDROTHERMAL_VENT = registerBlock(EndBlocks.prepareBlockPath("hydrothermal_vent"), new HydrothermalVentBlock());
        VENT_BUBBLE_COLUMN = registerEndBlockOnly(EndBlocks.prepareBlockPath("vent_bubble_column"), new VentBubbleColumnBlock() );
        DENSE_SNOW = registerBlock(EndBlocks.prepareBlockPath("dense_snow"), new DenseSnowBlock());
        EMERALD_ICE = registerBlock(EndBlocks.prepareBlockPath("emerald_ice"), new EmeraldIceBlock());
        DENSE_EMERALD_ICE = registerBlock(EndBlocks.prepareBlockPath("dense_emerald_ice"), new DenseEmeraldIceBlock());
        ANCIENT_EMERALD_ICE = registerBlock(EndBlocks.prepareBlockPath("ancient_emerald_ice"), new AncientEmeraldIceBlock());
        END_STONE_STALACTITE = registerBlock(EndBlocks.prepareBlockPath("end_stone_stalactite"), new StalactiteBlock.Stone(Blocks.END_STONE) );
        END_STONE_STALACTITE_CAVEMOSS = registerBlock(EndBlocks.prepareBlockPath("end_stone_stalactite_cavemoss"), new StalactiteBlock.Stone(CAVE_MOSS) );
        MOSSY_GLOWSHROOM_SAPLING = registerBlock(EndBlocks.prepareBlockPath("mossy_glowshroom_sapling"), new MossyGlowshroomSaplingBlock() );
        MOSSY_GLOWSHROOM_CAP = registerBlock(EndBlocks.prepareBlockPath("mossy_glowshroom_cap"), new MossyGlowshroomCapBlock() );
        MOSSY_GLOWSHROOM_HYMENOPHORE = registerBlock(EndBlocks.prepareBlockPath("mossy_glowshroom_hymenophore"), new GlowingHymenophoreBlock() );
        MOSSY_GLOWSHROOM_FUR = registerBlock(EndBlocks.prepareBlockPath("mossy_glowshroom_fur"), new FurBlock( MapColor.COLOR_LIGHT_BLUE, MOSSY_GLOWSHROOM_SAPLING, 15, 16, true ) );
        MOSSY_GLOWSHROOM = new EndWoodenComplexMaterial( "mossy_glowshroom", MapColor.COLOR_GRAY, MapColor.WOOD, Blocks.GRAY_WOOL ).init();
        PYTHADENDRON_SAPLING = registerBlock(EndBlocks.prepareBlockPath("pythadendron_sapling"), new PythadendronSaplingBlock() );
        PYTHADENDRON_LEAVES = registerBlock(EndBlocks.prepareBlockPath("pythadendron_leaves"), new PottableLeavesBlock( PYTHADENDRON_SAPLING, MapColor.COLOR_MAGENTA ) );
        PYTHADENDRON = new EndWoodenComplexMaterial( "pythadendron", MapColor.COLOR_MAGENTA, MapColor.COLOR_PURPLE, Blocks.BLACK_WOOL ).init();
        END_LOTUS_SEED = registerBlock(EndBlocks.prepareBlockPath("end_lotus_seed"), new EndLotusSeedBlock());
        END_LOTUS_STEM = registerBlock(EndBlocks.prepareBlockPath("end_lotus_stem"), new EndLotusStemBlock());
        END_LOTUS_LEAF = registerEndBlockOnly(EndBlocks.prepareBlockPath("end_lotus_leaf"), new EndLotusLeafBlock());
        END_LOTUS_FLOWER = registerEndBlockOnly(EndBlocks.prepareBlockPath("end_lotus_flower"), new EndLotusFlowerBlock());
        END_LOTUS = new EndWoodenComplexMaterial( "end_lotus", MapColor.COLOR_LIGHT_BLUE, MapColor.COLOR_CYAN, Blocks.LIGHT_BLUE_WOOL ).init();
        LACUGROVE_SAPLING = registerBlock(EndBlocks.prepareBlockPath("lacugrove_sapling"), new LacugroveSaplingBlock());
        LACUGROVE_LEAVES = registerBlock(EndBlocks.prepareBlockPath("lacugrove_leaves"), new PottableLeavesBlock( LACUGROVE_SAPLING, MapColor.COLOR_CYAN ) );
        LACUGROVE = new EndWoodenComplexMaterial( "lacugrove", MapColor.COLOR_BROWN, MapColor.COLOR_YELLOW, Blocks.CYAN_WOOL ).init();
        DRAGON_TREE_SAPLING = registerBlock(EndBlocks.prepareBlockPath("dragon_tree_sapling"), new DragonTreeSaplingBlock());
        DRAGON_TREE_LEAVES = registerBlock(EndBlocks.prepareBlockPath("dragon_tree_leaves"), new PottableLeavesBlock( DRAGON_TREE_SAPLING, MapColor.COLOR_MAGENTA ) );
        DRAGON_TREE = new EndWoodenComplexMaterial( "dragon_tree", MapColor.COLOR_BLACK, MapColor.COLOR_MAGENTA , Blocks.BLACK_WOOL ).init();
        TENANEA_SAPLING = registerBlock(EndBlocks.prepareBlockPath("tenanea_sapling"), new TenaneaSaplingBlock());
        TENANEA_LEAVES = registerBlock(EndBlocks.prepareBlockPath("tenanea_leaves"), new PottableLeavesBlock( TENANEA_SAPLING, MapColor.COLOR_PINK ) );
        TENANEA_FLOWERS = registerBlock(EndBlocks.prepareBlockPath("tenanea_flowers"), new TenaneaFlowersBlock());
        TENANEA_OUTER_LEAVES = registerBlock(EndBlocks.prepareBlockPath("tenanea_outer_leaves"), new FurBlock(MapColor.COLOR_PINK, TENANEA_SAPLING, 32) );
        TENANEA = new EndWoodenComplexMaterial( "tenanea", MapColor.COLOR_BROWN, MapColor.COLOR_PINK, Blocks.PINK_WOOL ).init();
        HELIX_TREE_SAPLING = registerBlock(EndBlocks.prepareBlockPath("helix_tree_sapling"), new HelixTreeSaplingBlock());
        HELIX_TREE_LEAVES = registerBlock(EndBlocks.prepareBlockPath("helix_tree_leaves"), new HelixTreeLeavesBlock());
        HELIX_TREE = new EndWoodenComplexMaterial( "helix_tree", MapColor.COLOR_GRAY, MapColor.COLOR_ORANGE, Blocks.GRAY_WOOL ).init();
        UMBRELLA_TREE_SAPLING = registerBlock(EndBlocks.prepareBlockPath("umbrella_tree_sapling"), new UmbrellaTreeSaplingBlock() );
        UMBRELLA_TREE_MEMBRANE = registerBlock(EndBlocks.prepareBlockPath("umbrella_tree_membrane"), new UmbrellaTreeMembraneBlock() );
        UMBRELLA_TREE_CLUSTER = registerBlock(EndBlocks.prepareBlockPath("umbrella_tree_cluster"), new UmbrellaTreeClusterBlock() );
        UMBRELLA_TREE_CLUSTER_EMPTY = registerBlock(EndBlocks.prepareBlockPath("umbrella_tree_cluster_empty"), new UmbrellaTreeClusterEmptyBlock() );
        UMBRELLA_TREE = new EndWoodenComplexMaterial( "umbrella_tree", MapColor.COLOR_BLUE, MapColor.COLOR_GREEN, Blocks.MAGENTA_WOOL ).init();
        JELLYSHROOM_CAP_PURPLE = registerBlock(EndBlocks.prepareBlockPath("jellyshroom_cap_purple"), new JellyshroomCapBlock( 217, 142, 255, 164, 0, 255 ) );
        JELLYSHROOM = new EndWoodenComplexMaterial( "jellyshroom", MapColor.COLOR_PURPLE, MapColor.COLOR_LIGHT_BLUE, Blocks.PURPLE_WOOL ).init();
        LUCERNIA_SAPLING = registerBlock(EndBlocks.prepareBlockPath("lucernia_sapling"), new LucerniaSaplingBlock());
        LUCERNIA_LEAVES = registerBlock(EndBlocks.prepareBlockPath("lucernia_leaves"), new PottableLeavesBlock( LUCERNIA_SAPLING, MapColor.COLOR_ORANGE ) );
        LUCERNIA_OUTER_LEAVES = registerBlock(EndBlocks.prepareBlockPath("lucernia_outer_leaves"), new FurBlock(MapColor.COLOR_RED, LUCERNIA_SAPLING, 32) );
        LUCERNIA = new EndWoodenComplexMaterial( "lucernia", MapColor.COLOR_ORANGE, MapColor.COLOR_ORANGE, Blocks.WHITE_WOOL ).init();
        LUCERNIA_JELLY = new JellyLucerniaWoodMaterial().init();
        UMBRELLA_MOSS = registerBlock(EndBlocks.prepareBlockPath("umbrella_moss"), new UmbrellaMossBlock());
        UMBRELLA_MOSS_TALL = registerBlock(EndBlocks.prepareBlockPath("umbrella_moss_tall"), new UmbrellaMossTallBlock());
        CREEPING_MOSS = registerBlock(EndBlocks.prepareBlockPath("creeping_moss"), new GlowingMossBlock(11));
        CHORUS_GRASS = registerBlock(EndBlocks.prepareBlockPath("chorus_grass"), new ChorusGrassBlock());
        CAVE_GRASS = registerBlock(EndBlocks.prepareBlockPath("cave_grass"), new TerrainPlantBlock(CAVE_MOSS) );
        CRYSTAL_GRASS = registerBlock(EndBlocks.prepareBlockPath("crystal_grass"), new TerrainPlantBlock(CRYSTAL_MOSS) );
        SHADOW_PLANT = registerBlock(EndBlocks.prepareBlockPath("shadow_plant"), new TerrainPlantBlock(SHADOW_GRASS) );
        BUSHY_GRASS = registerBlock(EndBlocks.prepareBlockPath("bushy_grass"), new TerrainPlantBlock(PINK_MOSS) );
        AMBER_GRASS = registerBlock(EndBlocks.prepareBlockPath("amber_grass"), new TerrainPlantBlock(AMBER_MOSS) );
        TWISTED_UMBRELLA_MOSS = registerBlock(EndBlocks.prepareBlockPath("twisted_umbrella_moss"), new TwistedUmbrellaMossBlock() );
        TWISTED_UMBRELLA_MOSS_TALL = registerBlock(EndBlocks.prepareBlockPath("twisted_umbrella_moss_tall"), new TwistedUmbrellaMossTallBlock() );
        JUNGLE_GRASS = registerBlock(EndBlocks.prepareBlockPath("jungle_grass"), new TerrainPlantBlock(JUNGLE_MOSS) );
        BLOOMING_COOKSONIA = registerBlock(EndBlocks.prepareBlockPath("blooming_cooksonia"), new TerrainPlantBlock(END_MOSS) );
        SALTEAGO = registerBlock(EndBlocks.prepareBlockPath("salteago"), new TerrainPlantBlock(END_MOSS));
        VAIOLUSH_FERN = registerBlock(EndBlocks.prepareBlockPath("vaiolush_fern"), new TerrainPlantBlock(END_MOSS));
        FRACTURN = registerBlock(EndBlocks.prepareBlockPath("fracturn"), new TerrainPlantBlock(END_MOSS));
        CLAWFERN = registerBlock(EndBlocks.prepareBlockPath("clawfern"), new TerrainPlantBlock( SANGNUM, MOSSY_OBSIDIAN, MOSSY_DRAGON_BONE ) );
        GLOBULAGUS = registerBlock(EndBlocks.prepareBlockPath("globulagus"), new TerrainPlantBlock( SANGNUM, MOSSY_OBSIDIAN, MOSSY_DRAGON_BONE ) );
        ORANGO = registerBlock(EndBlocks.prepareBlockPath("orango"), new TerrainPlantBlock(RUTISCUS));
        AERIDIUM = registerBlock(EndBlocks.prepareBlockPath("aeridium"), new TerrainPlantBlock(RUTISCUS));
        LUTEBUS = registerBlock(EndBlocks.prepareBlockPath("lutebus"), new TerrainPlantBlock(RUTISCUS));
        LAMELLARIUM = registerBlock(EndBlocks.prepareBlockPath("lamellarium"), new TerrainPlantBlock(RUTISCUS));
        INFLEXIA = registerBlock(EndBlocks.prepareBlockPath("inflexia"), new TerrainPlantBlock( PALLIDIUM_FULL, PALLIDIUM_HEAVY, PALLIDIUM_THIN, PALLIDIUM_TINY ) );
        FLAMMALIX = registerBlock(EndBlocks.prepareBlockPath("flammalix"), new FlammalixBlock());
        CRYSTAL_MOSS_COVER = registerBlock(EndBlocks.prepareBlockPath("crystal_moss_cover"), new CrystalMossCoverBlock(MapColor.COLOR_PINK) );
        BLUE_VINE_SEED = registerBlock(EndBlocks.prepareBlockPath("blue_vine_seed"), new BlueVineSeedBlock());
        BLUE_VINE = registerEndBlockOnly(EndBlocks.prepareBlockPath("blue_vine"), new BlueVineBlock());
        BLUE_VINE_LANTERN = registerBlock(EndBlocks.prepareBlockPath("blue_vine_lantern"), new BlueVineLanternBlock());
        BLUE_VINE_FUR = registerBlock(EndBlocks.prepareBlockPath("blue_vine_fur"), new FurBlock(MapColor.COLOR_BLUE, BLUE_VINE_SEED, 15, 3, false) );
        LANCELEAF_SEED = registerBlock(EndBlocks.prepareBlockPath("lanceleaf_seed"), new LanceleafSeedBlock());
        LANCELEAF = registerEndBlockOnly(EndBlocks.prepareBlockPath("lanceleaf"), new LanceleafBlock());
        GLOWING_PILLAR_SEED = registerBlock(EndBlocks.prepareBlockPath("glowing_pillar_seed"), new GlowingPillarSeedBlock());
        GLOWING_PILLAR_ROOTS = registerEndBlockOnly(EndBlocks.prepareBlockPath("glowing_pillar_roots"), new GlowingPillarRootsBlock() );
        GLOWING_PILLAR_LUMINOPHOR = registerBlock(EndBlocks.prepareBlockPath("glowing_pillar_luminophor"), new GlowingPillarLuminophorBlock() );
        GLOWING_PILLAR_LEAVES = registerBlock(EndBlocks.prepareBlockPath("glowing_pillar_leaves"), new FurBlock(MapColor.COLOR_ORANGE, GLOWING_PILLAR_SEED, 15, 3, false) );
        SMALL_JELLYSHROOM = registerBlock(EndBlocks.prepareBlockPath("small_jellyshroom"), new SmallJellyshroomBlock());
        BOLUX_MUSHROOM = registerBlock(EndBlocks.prepareBlockPath("bolux_mushroom"), new BoluxMushroomBlock());
        LUMECORN_SEED = registerBlock(EndBlocks.prepareBlockPath("lumecorn_seed"), new LumecornSeedBlock());
        LUMECORN = registerEndBlockOnly(EndBlocks.prepareBlockPath("lumecorn"), new LumecornBlock());
        SMALL_AMARANITA_MUSHROOM = registerBlock(EndBlocks.prepareBlockPath("small_amaranita_mushroom"), new SmallAmaranitaBlock() );
        LARGE_AMARANITA_MUSHROOM = registerEndBlockOnly(EndBlocks.prepareBlockPath("large_amaranita_mushroom"), new LargeAmaranitaBlock() );
        AMARANITA_STEM = registerBlock(EndBlocks.prepareBlockPath("amaranita_stem"), new AmaranitaStemBlock());
        AMARANITA_HYPHAE = registerBlock(EndBlocks.prepareBlockPath("amaranita_hyphae"), new AmaranitaStemBlock());
        AMARANITA_HYMENOPHORE = registerBlock(EndBlocks.prepareBlockPath("amaranita_hymenophore"), new AmaranitaHymenophoreBlock() );
        AMARANITA_LANTERN = registerBlock(
                EndBlocks.prepareBlockPath("amaranita_lantern"),
                new GlowingHymenophoreBlock(MapColor.WOOD)
        );
        AMARANITA_FUR = registerBlock(EndBlocks.prepareBlockPath("amaranita_fur"), new FurBlock(MapColor.COLOR_CYAN, SMALL_AMARANITA_MUSHROOM, 15, 4, true) );
        AMARANITA_CAP = registerBlock(EndBlocks.prepareBlockPath("amaranita_cap"), new AmaranitaCapBlock());
        NEON_CACTUS = registerBlock(EndBlocks.prepareBlockPath("neon_cactus"), new NeonCactusPlantBlock());
        NEON_CACTUS_BLOCK = registerBlock(EndBlocks.prepareBlockPath("neon_cactus_block"), new NeonCactusBlock());
        NEON_CACTUS_BLOCK_STAIRS = registerBlock(EndBlocks.prepareBlockPath("neon_cactus_stairs"), new BaseStairsBlock.Wood(NEON_CACTUS_BLOCK) );
        NEON_CACTUS_BLOCK_SLAB = registerBlock(EndBlocks.prepareBlockPath("neon_cactus_slab"), new BaseSlabBlock.Wood(NEON_CACTUS_BLOCK) );
        SHADOW_BERRY = registerBlock(EndBlocks.prepareBlockPath("shadow_berry"), new ShadowBerryBlock());
        BLOSSOM_BERRY = registerBlock(EndBlocks.prepareBlockPath("blossom_berry_seed"), new PottableCropBlock(EndItems.BLOSSOM_BERRY, PINK_MOSS) );
        AMBER_ROOT = registerBlock(EndBlocks.prepareBlockPath("amber_root_seed"), new PottableCropBlock(EndItems.AMBER_ROOT_RAW, AMBER_MOSS) );
        CHORUS_MUSHROOM = registerBlock(EndBlocks.prepareBlockPath("chorus_mushroom_seed"), new PottableCropBlock( EndItems.CHORUS_MUSHROOM_RAW, CHORUS_NYLIUM ) );
        CAVE_PUMPKIN_SEED = registerBlock(EndBlocks.prepareBlockPath("cave_pumpkin_seed"), new CavePumpkinVineBlock());
        CAVE_PUMPKIN = registerBlock(EndBlocks.prepareBlockPath("cave_pumpkin"), new CavePumpkinBlock());
        BUBBLE_CORAL = registerBlock(EndBlocks.prepareBlockPath("bubble_coral"), new BubbleCoralBlock());
        MENGER_SPONGE = registerBlock(EndBlocks.prepareBlockPath("menger_sponge"), new MengerSpongeBlock());
        MENGER_SPONGE_WET = registerBlock(EndBlocks.prepareBlockPath("menger_sponge_wet"), new MengerSpongeWetBlock());
        CHARNIA_RED = registerBlock(EndBlocks.prepareBlockPath("charnia_red"), new CharniaBlock());
        CHARNIA_PURPLE = registerBlock(EndBlocks.prepareBlockPath("charnia_purple"), new CharniaBlock());
        CHARNIA_ORANGE = registerBlock(EndBlocks.prepareBlockPath("charnia_orange"), new CharniaBlock());
        CHARNIA_LIGHT_BLUE = registerBlock(EndBlocks.prepareBlockPath("charnia_light_blue"), new CharniaBlock());
        CHARNIA_CYAN = registerBlock(EndBlocks.prepareBlockPath("charnia_cyan"), new CharniaBlock());
        CHARNIA_GREEN = registerBlock(EndBlocks.prepareBlockPath("charnia_green"), new CharniaBlock());
        END_LILY = registerEndBlockOnly(EndBlocks.prepareBlockPath("end_lily"), new EndLilyBlock());
        END_LILY_SEED = registerBlock(EndBlocks.prepareBlockPath("end_lily_seed"), new EndLilySeedBlock());
        HYDRALUX_SAPLING = registerBlock(EndBlocks.prepareBlockPath("hydralux_sapling"), new HydraluxSaplingBlock());
        HYDRALUX = registerEndBlockOnly(EndBlocks.prepareBlockPath("hydralux"), new HydraluxBlock());
        HYDRALUX_PETAL_BLOCK = registerBlock(EndBlocks.prepareBlockPath("hydralux_petal_block"), new HydraluxPetalBlock());
        HYDRALUX_PETAL_BLOCK_COLORED = new ColoredMaterial(
                "hydralux_petal_block",
                HydraluxPetalColoredBlock::new,
                HYDRALUX_PETAL_BLOCK,
                true
        );
        if (BetterEnd.FLAMBOYANT.isLoaded()) {
            org.betterx.betterend.integration.FlamboyantRefabricatedIntegration.registerBlocks();
        }
        POND_ANEMONE = registerBlock(EndBlocks.prepareBlockPath("pond_anemone"), new PondAnemoneBlock());
        FLAMAEA = registerBlock(EndBlocks.prepareBlockPath("flamaea"), new FlamaeaBlock());
        CAVE_BUSH = registerBlock(EndBlocks.prepareBlockPath("cave_bush"), new SimpleLeavesBlock(MapColor.COLOR_MAGENTA) );
        MURKWEED = registerBlock(EndBlocks.prepareBlockPath("murkweed"), new MurkweedBlock());
        NEEDLEGRASS = registerBlock(EndBlocks.prepareBlockPath("needlegrass"), new NeedlegrassBlock());
        PURPLE_POLYPORE = registerBlock(EndBlocks.prepareBlockPath("purple_polypore"), new EndWallMushroom(13));
        AURANT_POLYPORE = registerBlock(EndBlocks.prepareBlockPath("aurant_polypore"), new EndWallMushroom(13));
        TAIL_MOSS = registerBlock(EndBlocks.prepareBlockPath("tail_moss"), new EndWallPlantBlock(MapColor.COLOR_BLACK));
        CYAN_MOSS = registerBlock(EndBlocks.prepareBlockPath("cyan_moss"), new EndWallPlantBlock(MapColor.COLOR_CYAN));
        TWISTED_MOSS = registerBlock(EndBlocks.prepareBlockPath("twisted_moss"), new EndWallPlantBlock(MapColor.COLOR_LIGHT_BLUE) );
        TUBE_WORM = registerBlock(EndBlocks.prepareBlockPath("tube_worm"), new EndUnderwaterWallPlantBlock(MapColor.TERRACOTTA_BROWN) );
        BULB_MOSS = registerBlock(EndBlocks.prepareBlockPath("bulb_moss"), new EndWallPlantBlock(MapColor.TERRACOTTA_ORANGE, 12) );
        JUNGLE_FERN = registerBlock(EndBlocks.prepareBlockPath("jungle_fern"), new EndWallPlantBlock(MapColor.COLOR_GREEN));
        RUSCUS = registerBlock(EndBlocks.prepareBlockPath("ruscus"), new EndWallPlantBlock(MapColor.COLOR_RED));
        DENSE_VINE = registerBlock(EndBlocks.prepareBlockPath("dense_vine"), new BaseVineBlock(15, true));
        TWISTED_VINE = registerBlock(EndBlocks.prepareBlockPath("twisted_vine"), new BaseVineBlock());
        BULB_VINE_SEED = registerBlock(EndBlocks.prepareBlockPath("bulb_vine_seed"), new BulbVineSeedBlock());
        BULB_VINE = registerBlock(EndBlocks.prepareBlockPath("bulb_vine"), new BulbVineBlock());
        JUNGLE_VINE = registerBlock(EndBlocks.prepareBlockPath("jungle_vine"), new BaseVineBlock());
        RUBINEA = registerBlock(EndBlocks.prepareBlockPath("rubinea"), new BaseVineBlock());
        MAGNULA = registerBlock(EndBlocks.prepareBlockPath("magnula"), new BaseVineBlock());
        FILALUX = registerBlock(EndBlocks.prepareBlockPath("filalux"), new FilaluxBlock());
        FILALUX_WINGS = registerBlock(EndBlocks.prepareBlockPath("filalux_wings"), new FilaluxWingsBlock());
        FILALUX_LANTERN = registerBlock(EndBlocks.prepareBlockPath("filalux_lantern"), new FilaluxLanternBlock());
        SILK_MOTH_NEST = registerBlock(EndBlocks.prepareBlockPath("silk_moth_nest"), new SilkMothNestBlock());
        SILK_MOTH_HIVE = registerBlock(EndBlocks.prepareBlockPath("silk_moth_hive"), new SilkMothHiveBlock());
        ENDER_ORE = registerBlock(EndBlocks.prepareBlockPath("ender_ore"), new BaseOreBlock(() -> EndItems.ENDER_SHARD, 1, 3, 5) );
        AMBER_ORE = registerBlock(EndBlocks.prepareBlockPath("amber_ore"), new BaseOreBlock(() -> EndItems.RAW_AMBER, 1, 2, 4) );
        THALLASIUM = MetalMaterial.makeNormal( "thallasium", MapColor.COLOR_BLUE, EndToolMaterial.THALLASIUM.toolMaterial(), EndArmorTier.THALLASIUM, EndToolMaterial.THALLASIUM.getLevel(), EndTags.ANVIL_IRON_TOOL, EndTemplates.THALLASIUM_UPGRADE );
        TERMINITE = MetalMaterial.makeOreless( "terminite", MapColor.WARPED_WART_BLOCK, 7F, 9F, EndToolMaterial.TERMINITE.toolMaterial(), EndArmorTier.TERMINITE, EndToolMaterial.TERMINITE.getLevel(), EndTags.ANVIL_DIAMOND_TOOL, EndTemplates.TERMINITE_UPGRADE );
        AETERNIUM_BLOCK = registerBlock(EndBlocks.prepareBlockPath("aeternium_block"), new AeterniumBlock());
        CHARCOAL_BLOCK = registerBlock(EndBlocks.prepareBlockPath("charcoal_block"), new CharcoalBlock());
        ENDER_BLOCK = registerBlock(EndBlocks.prepareBlockPath("ender_block"), new EnderBlock());
        AURORA_CRYSTAL = registerBlock(EndBlocks.prepareBlockPath("aurora_crystal"), new AuroraCrystalBlock());
        AMBER_BLOCK = registerBlock(EndBlocks.prepareBlockPath("amber_block"), new AmberBlock());
        SMARAGDANT_CRYSTAL_SHARD = registerBlock(EndBlocks.prepareBlockPath("smaragdant_crystal_shard"), new SmaragdantCrystalShardBlock() );
        SMARAGDANT_CRYSTAL = registerBlock(EndBlocks.prepareBlockPath("smaragdant_crystal"), new SmaragdantCrystalBlock());
        SMARAGDANT_SUBBLOCKS = new CrystalSubblocksMaterial( "smaragdant_crystal", SMARAGDANT_CRYSTAL );
        BUDDING_SMARAGDANT_CRYSTAL = registerBlock(EndBlocks.prepareBlockPath("budding_smaragdant_crystal"), new BuddingSmaragdantCrystalBlock(), CommonBlockTags.BUDDING_BLOCKS );
        RESPAWN_OBELISK = registerBlock(EndBlocks.prepareBlockPath("respawn_obelisk"), new RespawnObeliskBlock());
        ANDESITE_LANTERN = registerBlock(EndBlocks.prepareBlockPath("andesite_lantern"), new StoneLanternBlock(Blocks.ANDESITE) );
        DIORITE_LANTERN = registerBlock(EndBlocks.prepareBlockPath("diorite_lantern"), new StoneLanternBlock(Blocks.DIORITE));
        GRANITE_LANTERN = registerBlock(EndBlocks.prepareBlockPath("granite_lantern"), new StoneLanternBlock(Blocks.GRANITE));
        QUARTZ_LANTERN = registerBlock(EndBlocks.prepareBlockPath("quartz_lantern"), new StoneLanternBlock(Blocks.QUARTZ_BLOCK) );
        PURPUR_LANTERN = registerBlock(EndBlocks.prepareBlockPath("purpur_lantern"), new StoneLanternBlock(Blocks.PURPUR_BLOCK) );
        END_STONE_LANTERN = registerBlock(EndBlocks.prepareBlockPath("end_stone_lantern"), new StoneLanternBlock(Blocks.END_STONE) );
        BLACKSTONE_LANTERN = registerBlock(EndBlocks.prepareBlockPath("blackstone_lantern"), new StoneLanternBlock(Blocks.BLACKSTONE) );
        IRON_BULB_LANTERN = registerBlock(EndBlocks.prepareBlockPath("iron_bulb_lantern"), new BulbVineLanternBlock());
        IRON_BULB_LANTERN_COLORED = new ColoredMaterial(
                "iron_bulb_lantern",
                BulbVineLanternColoredBlock::new,
                IRON_BULB_LANTERN,
                false
        );
        IRON_CHANDELIER = EndBlocks.registerBlock(EndBlocks.prepareBlockPath("iron_chandelier"), new ChandelierBlock(Blocks.GOLD_BLOCK) );
        GOLD_CHANDELIER = EndBlocks.registerBlock(EndBlocks.prepareBlockPath("gold_chandelier"), new ChandelierBlock(Blocks.GOLD_BLOCK) );
        END_STONE_FURNACE = registerBlock(EndBlocks.prepareBlockPath("end_stone_furnace"), new BaseFurnaceBlock.Stone(Blocks.END_STONE), CommonPoiTags.ARMORER_WORKSTATION );
        END_STONE_SMELTER = registerBlock(EndBlocks.prepareBlockPath("end_stone_smelter"), new EndStoneSmelter());
        ETERNAL_PEDESTAL = registerBlock(EndBlocks.prepareBlockPath("eternal_pedestal"), new EternalPedestal());
        INFUSION_PEDESTAL = registerBlock(EndBlocks.prepareBlockPath("infusion_pedestal"), new InfusionPedestal());
        AETERNIUM_ANVIL = registerBlock(EndBlocks.prepareBlockPath("aeternium_anvil"), new AeterniumAnvil());
        END_PORTAL_BLOCK = registerEndBlockOnly(EndBlocks.prepareBlockPath("end_portal_block"), new EndPortalBlock());
        END_STONE_BRICK_VARIATIONS = new VanillaVariantStoneMaterial( "end_stone_brick", Blocks.END_STONE_BRICKS, MapColor.SAND ).init();
        END_STONE_SLAB = registerBlock(EndBlocks.prepareBlockPath("end_stone_slab"), new BaseSlabBlock.Stone(Blocks.END_STONE));
        END_STONE_STAIR = registerBlock(EndBlocks.prepareBlockPath("end_stone_stairs"), new BaseStairsBlock.Stone(Blocks.END_STONE));
        END_STONE_WALLS = registerBlock(EndBlocks.prepareBlockPath("end_stone_wall"), new BaseWallBlock.Stone(Blocks.END_STONE));
    }

    public static List<Block> getModBlocks() {
        return getBlockRegistry().allBlocks().toList();
    }

    public static List<BlockItem> getModBlockItems() {
        return getBlockRegistry().allBlockItems().toList();
    }

    @SafeVarargs
    public static <T extends Block> T registerBlock(String name, T block, TagKey<Block>... tags) {
        try {
            return getBlockRegistry().register(name, block, tags);
        } finally {
            getBlockRegistry().finishConstructionPath();
        }
    }

    public static Block registerEndBlockOnly(String name, Block block) {
        try {
            return getBlockRegistry().registerBlockOnly(name, block);
        } finally {
            getBlockRegistry().finishConstructionPath();
        }
    }

    public static String prepareBlockPath(String name) {
        return getBlockRegistry().prepareConstructionPath(name);
    }

    @NotNull
    public static BlockRegistry getBlockRegistry() {
        if (BLOCKS_REGISTRY == null) {
            BLOCKS_REGISTRY = BlockRegistry.forMod(BetterEnd.C);
        }
        return BLOCKS_REGISTRY;
    }

    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        getBlockRegistry();
        registerBlocks();
    }
}

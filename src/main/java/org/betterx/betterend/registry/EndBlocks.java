package org.betterx.betterend.registry;

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

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.material.MapColor;

import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class EndBlocks {
    private static BlockRegistry BLOCKS_REGISTRY;

    // Terrain //
    public static final Block ENDSTONE_DUST = registerBlock("endstone_dust", new EndstoneDustBlock());
    public static final Block END_MYCELIUM = registerBlock(
            "end_mycelium",
            new EndTerrainBlock(MapColor.COLOR_LIGHT_BLUE)
    );
    public static final Block END_MOSS = registerBlock(
            "end_moss",
            new EndTerrainBlock(MapColor.COLOR_CYAN),
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE,
            BlockTags.NYLIUM
    );
    public static final Block CHORUS_NYLIUM = registerBlock(
            "chorus_nylium",
            new EndTerrainBlock(MapColor.COLOR_MAGENTA),
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE,
            BlockTags.NYLIUM
    );
    public static final Block CAVE_MOSS = registerBlock(
            "cave_moss",
            new EndTripleTerrain(MapColor.COLOR_PURPLE),
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE,
            BlockTags.NYLIUM
    );
    public static final Block CRYSTAL_MOSS = registerBlock(
            "crystal_moss",
            new EndTerrainBlock(MapColor.COLOR_CYAN),
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE,
            BlockTags.NYLIUM
    );
    public static final Block SHADOW_GRASS = registerBlock(
            "shadow_grass",
            new ShadowGrassBlock(),
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE,
            BlockTags.NYLIUM
    );
    public static final Block PINK_MOSS = registerBlock(
            "pink_moss",
            new EndTerrainBlock(MapColor.COLOR_PINK),
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE,
            BlockTags.NYLIUM
    );
    public static final Block AMBER_MOSS = registerBlock(
            "amber_moss",
            new EndTerrainBlock(MapColor.COLOR_ORANGE),
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE,
            BlockTags.NYLIUM
    );
    public static final Block JUNGLE_MOSS = registerBlock(
            "jungle_moss",
            new EndTerrainBlock(MapColor.COLOR_GREEN),
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE,
            BlockTags.NYLIUM
    );
    public static final Block SANGNUM = registerBlock(
            "sangnum",
            new EndTerrainBlock(MapColor.COLOR_RED),
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE,
            BlockTags.NYLIUM
    );
    public static final Block RUTISCUS = registerBlock(
            "rutiscus",
            new EndTerrainBlock(MapColor.COLOR_ORANGE),
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE,
            BlockTags.NYLIUM
    );
    public static final Block PALLIDIUM_FULL = registerBlock(
            "pallidium_full",
            new PallidiumBlock("full", null),
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE,
            BlockTags.NYLIUM
    );
    public static final Block PALLIDIUM_HEAVY = registerBlock(
            "pallidium_heavy",
            new PallidiumBlock("heavy", PALLIDIUM_FULL),
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE,
            BlockTags.NYLIUM
    );
    public static final Block PALLIDIUM_THIN = registerBlock(
            "pallidium_thin",
            new PallidiumBlock("thin", PALLIDIUM_HEAVY),
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE,
            BlockTags.NYLIUM
    );
    public static final Block PALLIDIUM_TINY = registerBlock(
            "pallidium_tiny",
            new PallidiumBlock("tiny", PALLIDIUM_THIN),
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE,
            BlockTags.NYLIUM
    );

    // Roads //
    public static final Block END_MYCELIUM_PATH = registerBlock(
            "end_mycelium_path",
            new BasePathBlock.Stone(END_MYCELIUM)
    );
    public static final Block END_MOSS_PATH = registerBlock("end_moss_path", new BasePathBlock.Stone(END_MOSS));
    public static final Block CHORUS_NYLIUM_PATH = registerBlock(
            "chorus_nylium_path",
            new BasePathBlock.Stone(CHORUS_NYLIUM)
    );
    public static final Block CAVE_MOSS_PATH = registerBlock("cave_moss_path", new BasePathBlock.Stone(CAVE_MOSS));
    public static final Block CRYSTAL_MOSS_PATH = registerBlock(
            "crystal_moss_path",
            new BasePathBlock.Stone(CRYSTAL_MOSS)
    );
    public static final Block SHADOW_GRASS_PATH = registerBlock(
            "shadow_grass_path",
            new BasePathBlock.Stone(SHADOW_GRASS)
    );
    public static final Block PINK_MOSS_PATH = registerBlock("pink_moss_path", new BasePathBlock.Stone(PINK_MOSS));
    public static final Block AMBER_MOSS_PATH = registerBlock("amber_moss_path", new BasePathBlock.Stone(AMBER_MOSS));
    public static final Block JUNGLE_MOSS_PATH = registerBlock(
            "jungle_moss_path",
            new BasePathBlock.Stone(JUNGLE_MOSS)
    );
    public static final Block SANGNUM_PATH = registerBlock("sangnum_path", new BasePathBlock.Stone(SANGNUM));
    public static final Block RUTISCUS_PATH = registerBlock("rutiscus_path", new BasePathBlock.Stone(RUTISCUS));

    public static final Block MOSSY_OBSIDIAN = registerBlock(
            "mossy_obsidian",
            new MossyObsidian(),
            BCLBlockTags.BONEMEAL_SOURCE_OBSIDIAN
    );
    public static final Block DRAGON_BONE_BLOCK = registerBlock(
            "dragon_bone_block",
            new BaseRotatedPillarBlock.Wood(Blocks.BONE_BLOCK, false),
            EndTags.BONEMEAL_TARGET_DRAGON_BONE
    );
    public static final Block DRAGON_BONE_STAIRS = registerBlock(
            "dragon_bone_stairs",
            new BaseStairsBlock.Wood(DRAGON_BONE_BLOCK)
    );
    public static final Block DRAGON_BONE_SLAB = registerBlock(
            "dragon_bone_slab",
            new BaseSlabBlock.Wood(DRAGON_BONE_BLOCK)
    );
    public static final Block MOSSY_DRAGON_BONE = registerBlock(
            "mossy_dragon_bone",
            new MossyDragonBoneBlock(),
            EndTags.BONEMEAL_SOURCE_DRAGON_BONE
    );

    // Rocks //
    public static final StoneMaterial FLAVOLITE = new StoneMaterial("flavolite", MapColor.SAND);
    public static final StoneMaterial VIOLECITE = new StoneMaterial("violecite", MapColor.COLOR_PURPLE);
    public static final StoneMaterial SULPHURIC_ROCK = new StoneMaterial("sulphuric_rock", MapColor.COLOR_BROWN);
    public static final StoneMaterial VIRID_JADESTONE = new StoneMaterial("virid_jadestone", MapColor.COLOR_GREEN);
    public static final StoneMaterial AZURE_JADESTONE = new StoneMaterial(
            "azure_jadestone",
            MapColor.COLOR_LIGHT_BLUE
    );
    public static final StoneMaterial SANDY_JADESTONE = new StoneMaterial(
            "sandy_jadestone",
            MapColor.COLOR_YELLOW
    );
    public static final StoneMaterial UMBRALITH = new StoneMaterial("umbralith", MapColor.DEEPSLATE);
    public static final Block BRIMSTONE = registerBlock("brimstone", new BrimstoneBlock());
    public static final Block SULPHUR_CRYSTAL = registerBlock("sulphur_crystal", new SulphurCrystalBlock());
    public static final Block MISSING_TILE = registerBlock("missing_tile", new MissingTileBlock());
    public static final Block ENDSTONE_FLOWER_POT = registerBlock(
            "endstone_flower_pot",
            new FlowerPotBlock.Stone(Blocks.END_STONE)
    );

    public static final Block FLAVOLITE_RUNED = registerBlock("flavolite_runed", new RunedFlavolite(false));
    public static final Block FLAVOLITE_RUNED_ETERNAL = registerBlock(
            "flavolite_runed_eternal",
            new RunedFlavolite(true)
    );

    public static final Block ANDESITE_PEDESTAL = registerBlock(
            "andesite_pedestal",
            new PedestalVanilla(Blocks.ANDESITE)
    );
    public static final Block DIORITE_PEDESTAL = registerBlock("diorite_pedestal", new PedestalVanilla(Blocks.DIORITE));
    public static final Block GRANITE_PEDESTAL = registerBlock("granite_pedestal", new PedestalVanilla(Blocks.GRANITE));
    public static final Block QUARTZ_PEDESTAL = registerBlock(
            "quartz_pedestal",
            new PedestalVanilla(Blocks.QUARTZ_BLOCK)
    );
    public static final Block PURPUR_PEDESTAL = registerBlock(
            "purpur_pedestal",
            new PedestalVanilla(Blocks.PURPUR_BLOCK)
    );

    public static final Block HYDROTHERMAL_VENT = registerBlock("hydrothermal_vent", new HydrothermalVentBlock());
    public static final Block VENT_BUBBLE_COLUMN = registerEndBlockOnly(
            "vent_bubble_column",
            new VentBubbleColumnBlock()
    );

    public static final Block DENSE_SNOW = registerBlock("dense_snow", new DenseSnowBlock());
    public static final Block EMERALD_ICE = registerBlock("emerald_ice", new EmeraldIceBlock());
    public static final Block DENSE_EMERALD_ICE = registerBlock("dense_emerald_ice", new DenseEmeraldIceBlock());
    public static final Block ANCIENT_EMERALD_ICE = registerBlock("ancient_emerald_ice", new AncientEmeraldIceBlock());

    public static final Block END_STONE_STALACTITE = registerBlock(
            "end_stone_stalactite",
            new StalactiteBlock.Stone(Blocks.END_STONE)
    );
    public static final Block END_STONE_STALACTITE_CAVEMOSS = registerBlock(
            "end_stone_stalactite_cavemoss",
            new StalactiteBlock.Stone(CAVE_MOSS)
    );

    // Wooden Materials And Trees //
    public static final Block MOSSY_GLOWSHROOM_SAPLING = registerBlock(
            "mossy_glowshroom_sapling",
            new MossyGlowshroomSaplingBlock()
    );
    public static final Block MOSSY_GLOWSHROOM_CAP = registerBlock(
            "mossy_glowshroom_cap",
            new MossyGlowshroomCapBlock()
    );
    public static final Block MOSSY_GLOWSHROOM_HYMENOPHORE = registerBlock(
            "mossy_glowshroom_hymenophore",
            new GlowingHymenophoreBlock()
    );
    public static final Block MOSSY_GLOWSHROOM_FUR = registerBlock(
            "mossy_glowshroom_fur",
            new FurBlock(
                    MapColor.COLOR_LIGHT_BLUE,
                    MOSSY_GLOWSHROOM_SAPLING,
                    15,
                    16,
                    true
            )
    );
    public static final EndWoodenComplexMaterial MOSSY_GLOWSHROOM = new EndWoodenComplexMaterial(
            "mossy_glowshroom",
            MapColor.COLOR_GRAY,
            MapColor.WOOD,
            Blocks.GRAY_WOOL
    ).init();

    public static final Block PYTHADENDRON_SAPLING = registerBlock(
            "pythadendron_sapling",
            new PythadendronSaplingBlock()
    );
    public static final Block PYTHADENDRON_LEAVES = registerBlock(
            "pythadendron_leaves",
            new PottableLeavesBlock(
                    PYTHADENDRON_SAPLING,
                    MapColor.COLOR_MAGENTA
            )
    );
    public static final EndWoodenComplexMaterial PYTHADENDRON = new EndWoodenComplexMaterial(
            "pythadendron",
            MapColor.COLOR_MAGENTA,
            MapColor.COLOR_PURPLE,
            Blocks.BLACK_WOOL
    ).init();

    public static final Block END_LOTUS_SEED = registerBlock("end_lotus_seed", new EndLotusSeedBlock());
    public static final Block END_LOTUS_STEM = registerBlock("end_lotus_stem", new EndLotusStemBlock());
    public static final Block END_LOTUS_LEAF = registerEndBlockOnly("end_lotus_leaf", new EndLotusLeafBlock());
    public static final Block END_LOTUS_FLOWER = registerEndBlockOnly("end_lotus_flower", new EndLotusFlowerBlock());
    public static final EndWoodenComplexMaterial END_LOTUS = new EndWoodenComplexMaterial(
            "end_lotus",
            MapColor.COLOR_LIGHT_BLUE,
            MapColor.COLOR_CYAN,
            Blocks.LIGHT_BLUE_WOOL
    ).init();

    public static final Block LACUGROVE_SAPLING = registerBlock("lacugrove_sapling", new LacugroveSaplingBlock());
    public static final Block LACUGROVE_LEAVES = registerBlock(
            "lacugrove_leaves",
            new PottableLeavesBlock(
                    LACUGROVE_SAPLING,
                    MapColor.COLOR_CYAN
            )
    );
    public static final EndWoodenComplexMaterial LACUGROVE = new EndWoodenComplexMaterial(
            "lacugrove",
            MapColor.COLOR_BROWN,
            MapColor.COLOR_YELLOW,
            Blocks.CYAN_WOOL
    ).init();

    public static final Block DRAGON_TREE_SAPLING = registerBlock("dragon_tree_sapling", new DragonTreeSaplingBlock());
    public static final Block DRAGON_TREE_LEAVES = registerBlock(
            "dragon_tree_leaves",
            new PottableLeavesBlock(
                    DRAGON_TREE_SAPLING,
                    MapColor.COLOR_MAGENTA
            )
    );
    public static final EndWoodenComplexMaterial DRAGON_TREE = new EndWoodenComplexMaterial(
            "dragon_tree",
            MapColor.COLOR_BLACK,
            MapColor.COLOR_MAGENTA
            , Blocks.BLACK_WOOL
    ).init();

    public static final Block TENANEA_SAPLING = registerBlock("tenanea_sapling", new TenaneaSaplingBlock());
    public static final Block TENANEA_LEAVES = registerBlock(
            "tenanea_leaves",
            new PottableLeavesBlock(
                    TENANEA_SAPLING,
                    MapColor.COLOR_PINK
            )
    );
    public static final Block TENANEA_FLOWERS = registerBlock("tenanea_flowers", new TenaneaFlowersBlock());
    public static final Block TENANEA_OUTER_LEAVES = registerBlock(
            "tenanea_outer_leaves",
            new FurBlock(MapColor.COLOR_PINK, TENANEA_SAPLING, 32)
    );
    public static final EndWoodenComplexMaterial TENANEA = new EndWoodenComplexMaterial(
            "tenanea",
            MapColor.COLOR_BROWN,
            MapColor.COLOR_PINK,
            Blocks.PINK_WOOL
    ).init();

    public static final Block HELIX_TREE_SAPLING = registerBlock("helix_tree_sapling", new HelixTreeSaplingBlock());
    public static final Block HELIX_TREE_LEAVES = registerBlock("helix_tree_leaves", new HelixTreeLeavesBlock());
    public static final EndWoodenComplexMaterial HELIX_TREE = new EndWoodenComplexMaterial(
            "helix_tree",
            MapColor.COLOR_GRAY,
            MapColor.COLOR_ORANGE,
            Blocks.GRAY_WOOL
    ).init();

    public static final Block UMBRELLA_TREE_SAPLING = registerBlock(
            "umbrella_tree_sapling",
            new UmbrellaTreeSaplingBlock()
    );
    public static final Block UMBRELLA_TREE_MEMBRANE = registerBlock(
            "umbrella_tree_membrane",
            new UmbrellaTreeMembraneBlock()
    );
    public static final Block UMBRELLA_TREE_CLUSTER = registerBlock(
            "umbrella_tree_cluster",
            new UmbrellaTreeClusterBlock()
    );
    public static final Block UMBRELLA_TREE_CLUSTER_EMPTY = registerBlock(
            "umbrella_tree_cluster_empty",
            new UmbrellaTreeClusterEmptyBlock()
    );
    public static final EndWoodenComplexMaterial UMBRELLA_TREE = new EndWoodenComplexMaterial(
            "umbrella_tree",
            MapColor.COLOR_BLUE,
            MapColor.COLOR_GREEN,
            Blocks.MAGENTA_WOOL
    ).init();

    public static final Block JELLYSHROOM_CAP_PURPLE = registerBlock(
            "jellyshroom_cap_purple",
            new JellyshroomCapBlock(
                    217,
                    142,
                    255,
                    164,
                    0,
                    255
            )
    );
    public static final EndWoodenComplexMaterial JELLYSHROOM = new EndWoodenComplexMaterial(
            "jellyshroom",
            MapColor.COLOR_PURPLE,
            MapColor.COLOR_LIGHT_BLUE,
            Blocks.PURPLE_WOOL
    ).init();

    public static final Block LUCERNIA_SAPLING = registerBlock("lucernia_sapling", new LucerniaSaplingBlock());
    public static final Block LUCERNIA_LEAVES = registerBlock(
            "lucernia_leaves",
            new PottableLeavesBlock(
                    LUCERNIA_SAPLING,
                    MapColor.COLOR_ORANGE
            )
    );
    public static final Block LUCERNIA_OUTER_LEAVES = registerBlock(
            "lucernia_outer_leaves",
            new FurBlock(MapColor.COLOR_RED, LUCERNIA_SAPLING, 32)
    );
    public static final EndWoodenComplexMaterial LUCERNIA = new EndWoodenComplexMaterial(
            "lucernia",
            MapColor.COLOR_ORANGE,
            MapColor.COLOR_ORANGE,
            Blocks.WHITE_WOOL
    ).init();

    public static final EndWoodenComplexMaterial LUCERNIA_JELLY = new JellyLucerniaWoodMaterial().init();

    // Small Plants //
    public static final Block UMBRELLA_MOSS = registerBlock("umbrella_moss", new UmbrellaMossBlock());
    public static final Block UMBRELLA_MOSS_TALL = registerBlock("umbrella_moss_tall", new UmbrellaMossTallBlock());
    public static final Block CREEPING_MOSS = registerBlock("creeping_moss", new GlowingMossBlock(11));
    public static final Block CHORUS_GRASS = registerBlock("chorus_grass", new ChorusGrassBlock());
    public static final Block CAVE_GRASS = registerBlock(
            "cave_grass",
            new TerrainPlantBlock(CAVE_MOSS)
    );
    public static final Block CRYSTAL_GRASS = registerBlock(
            "crystal_grass",
            new TerrainPlantBlock(CRYSTAL_MOSS)
    );
    public static final Block SHADOW_PLANT = registerBlock(
            "shadow_plant",
            new TerrainPlantBlock(SHADOW_GRASS)
    );
    public static final Block BUSHY_GRASS = registerBlock(
            "bushy_grass",
            new TerrainPlantBlock(PINK_MOSS)
    );
    public static final Block AMBER_GRASS = registerBlock(
            "amber_grass",
            new TerrainPlantBlock(AMBER_MOSS)
    );
    public static final Block TWISTED_UMBRELLA_MOSS = registerBlock(
            "twisted_umbrella_moss",
            new TwistedUmbrellaMossBlock()
    );
    public static final Block TWISTED_UMBRELLA_MOSS_TALL = registerBlock(
            "twisted_umbrella_moss_tall",
            new TwistedUmbrellaMossTallBlock()
    );
    public static final Block JUNGLE_GRASS = registerBlock(
            "jungle_grass",
            new TerrainPlantBlock(CommonBlockTags.SOIL)
    );
    public static final Block BLOOMING_COOKSONIA = registerBlock(
            "blooming_cooksonia",
            new TerrainPlantBlock(END_MOSS)
    );
    public static final Block SALTEAGO = registerBlock("salteago", new TerrainPlantBlock(END_MOSS));
    public static final Block VAIOLUSH_FERN = registerBlock("vaiolush_fern", new TerrainPlantBlock(END_MOSS));
    public static final Block FRACTURN = registerBlock("fracturn", new TerrainPlantBlock(END_MOSS));
    public static final Block CLAWFERN = registerBlock(
            "clawfern",
            new TerrainPlantBlock(
                    SANGNUM,
                    MOSSY_OBSIDIAN,
                    MOSSY_DRAGON_BONE
            )
    );
    public static final Block GLOBULAGUS = registerBlock(
            "globulagus",
            new TerrainPlantBlock(
                    SANGNUM,
                    MOSSY_OBSIDIAN,
                    MOSSY_DRAGON_BONE
            )
    );
    public static final Block ORANGO = registerBlock("orango", new TerrainPlantBlock(RUTISCUS));
    public static final Block AERIDIUM = registerBlock("aeridium", new TerrainPlantBlock(RUTISCUS));
    public static final Block LUTEBUS = registerBlock("lutebus", new TerrainPlantBlock(RUTISCUS));
    public static final Block LAMELLARIUM = registerBlock("lamellarium", new TerrainPlantBlock(RUTISCUS));
    public static final Block INFLEXIA = registerBlock(
            "inflexia",
            new TerrainPlantBlock(
                    PALLIDIUM_FULL,
                    PALLIDIUM_HEAVY,
                    PALLIDIUM_THIN,
                    PALLIDIUM_TINY
            )
    );
    public static final Block FLAMMALIX = registerBlock("flammalix", new FlammalixBlock());


    public static final MultifaceBlock CRYSTAL_MOSS_COVER = registerBlock(
            "crystal_moss_cover",
            new CrystalMossCoverBlock(MapColor.COLOR_PINK)
    );

    public static final Block BLUE_VINE_SEED = registerBlock("blue_vine_seed", new BlueVineSeedBlock());
    public static final Block BLUE_VINE = registerEndBlockOnly("blue_vine", new BlueVineBlock());
    public static final Block BLUE_VINE_LANTERN = registerBlock("blue_vine_lantern", new BlueVineLanternBlock());
    public static final Block BLUE_VINE_FUR = registerBlock(
            "blue_vine_fur",
            new FurBlock(MapColor.COLOR_BLUE, BLUE_VINE_SEED, 15, 3, false)
    );

    public static final Block LANCELEAF_SEED = registerBlock("lanceleaf_seed", new LanceleafSeedBlock());
    public static final Block LANCELEAF = registerEndBlockOnly("lanceleaf", new LanceleafBlock());

    public static final Block GLOWING_PILLAR_SEED = registerBlock("glowing_pillar_seed", new GlowingPillarSeedBlock());
    public static final Block GLOWING_PILLAR_ROOTS = registerEndBlockOnly(
            "glowing_pillar_roots",
            new GlowingPillarRootsBlock()
    );
    public static final Block GLOWING_PILLAR_LUMINOPHOR = registerBlock(
            "glowing_pillar_luminophor",
            new GlowingPillarLuminophorBlock()
    );
    public static final Block GLOWING_PILLAR_LEAVES = registerBlock(
            "glowing_pillar_leaves",
            new FurBlock(MapColor.COLOR_ORANGE, GLOWING_PILLAR_SEED, 15, 3, false)
    );

    public static final Block SMALL_JELLYSHROOM = registerBlock("small_jellyshroom", new SmallJellyshroomBlock());
    public static final Block BOLUX_MUSHROOM = registerBlock("bolux_mushroom", new BoluxMushroomBlock());

    public static final Block LUMECORN_SEED = registerBlock("lumecorn_seed", new LumecornSeedBlock());
    public static final Block LUMECORN = registerEndBlockOnly("lumecorn", new LumecornBlock());

    public static final Block SMALL_AMARANITA_MUSHROOM = registerBlock(
            "small_amaranita_mushroom",
            new SmallAmaranitaBlock()
    );
    public static final Block LARGE_AMARANITA_MUSHROOM = registerEndBlockOnly(
            "large_amaranita_mushroom",
            new LargeAmaranitaBlock()
    );
    public static final Block AMARANITA_STEM = registerBlock("amaranita_stem", new AmaranitaStemBlock());
    public static final Block AMARANITA_HYPHAE = registerBlock("amaranita_hyphae", new AmaranitaStemBlock());
    public static final Block AMARANITA_HYMENOPHORE = registerBlock(
            "amaranita_hymenophore",
            new AmaranitaHymenophoreBlock()
    );
    public static final Block AMARANITA_LANTERN = registerBlock(
            "amaranita_lantern",
            new GlowingHymenophoreBlock(MapColor.WOOD)
    );
    public static final Block AMARANITA_FUR = registerBlock(
            "amaranita_fur",
            new FurBlock(MapColor.COLOR_CYAN, SMALL_AMARANITA_MUSHROOM, 15, 4, true)
    );
    public static final Block AMARANITA_CAP = registerBlock("amaranita_cap", new AmaranitaCapBlock());

    public static final Block NEON_CACTUS = registerBlock("neon_cactus", new NeonCactusPlantBlock());
    public static final Block NEON_CACTUS_BLOCK = registerBlock("neon_cactus_block", new NeonCactusBlock());
    public static final Block NEON_CACTUS_BLOCK_STAIRS = registerBlock(
            "neon_cactus_stairs",
            new BaseStairsBlock.Wood(NEON_CACTUS_BLOCK)
    );
    public static final Block NEON_CACTUS_BLOCK_SLAB = registerBlock(
            "neon_cactus_slab",
            new BaseSlabBlock.Wood(NEON_CACTUS_BLOCK)
    );

    // Crops
    public static final Block SHADOW_BERRY = registerBlock("shadow_berry", new ShadowBerryBlock());
    public static final Block BLOSSOM_BERRY = registerBlock(
            "blossom_berry_seed",
            new PottableCropBlock(EndItems.BLOSSOM_BERRY, PINK_MOSS)
    );
    public static final Block AMBER_ROOT = registerBlock(
            "amber_root_seed",
            new PottableCropBlock(EndItems.AMBER_ROOT_RAW, AMBER_MOSS)
    );
    public static final Block CHORUS_MUSHROOM = registerBlock(
            "chorus_mushroom_seed",
            new PottableCropBlock(
                    EndItems.CHORUS_MUSHROOM_RAW,
                    CHORUS_NYLIUM
            )
    );
    //public static final Block PEARLBERRY = registerBlock("pearlberry_seed", new PottableCropBlock(EndItems.BLOSSOM_BERRY, END_MOSS, END_MYCELIUM));
    public static final Block CAVE_PUMPKIN_SEED = registerBlock("cave_pumpkin_seed", new CavePumpkinVineBlock());
    public static final Block CAVE_PUMPKIN = registerBlock("cave_pumpkin", new CavePumpkinBlock());

    // Water plants
    public static final Block BUBBLE_CORAL = registerBlock("bubble_coral", new BubbleCoralBlock());
    public static final Block MENGER_SPONGE = registerBlock("menger_sponge", new MengerSpongeBlock());
    public static final Block MENGER_SPONGE_WET = registerBlock("menger_sponge_wet", new MengerSpongeWetBlock());
    public static final Block CHARNIA_RED = registerBlock("charnia_red", new CharniaBlock());
    public static final Block CHARNIA_PURPLE = registerBlock("charnia_purple", new CharniaBlock());
    public static final Block CHARNIA_ORANGE = registerBlock("charnia_orange", new CharniaBlock());
    public static final Block CHARNIA_LIGHT_BLUE = registerBlock("charnia_light_blue", new CharniaBlock());
    public static final Block CHARNIA_CYAN = registerBlock("charnia_cyan", new CharniaBlock());
    public static final Block CHARNIA_GREEN = registerBlock("charnia_green", new CharniaBlock());

    public static final Block END_LILY = registerEndBlockOnly("end_lily", new EndLilyBlock());
    public static final Block END_LILY_SEED = registerBlock("end_lily_seed", new EndLilySeedBlock());

    public static final Block HYDRALUX_SAPLING = registerBlock("hydralux_sapling", new HydraluxSaplingBlock());
    public static final Block HYDRALUX = registerEndBlockOnly("hydralux", new HydraluxBlock());
    public static final Block HYDRALUX_PETAL_BLOCK = registerBlock("hydralux_petal_block", new HydraluxPetalBlock());
    public static final ColoredMaterial HYDRALUX_PETAL_BLOCK_COLORED = new ColoredMaterial(
            HydraluxPetalColoredBlock::new,
            HYDRALUX_PETAL_BLOCK,
            true
    );

    public static final Block POND_ANEMONE = registerBlock("pond_anemone", new PondAnemoneBlock());

    public static final Block FLAMAEA = registerBlock("flamaea", new FlamaeaBlock());

    public static final Block CAVE_BUSH = registerBlock(
            "cave_bush",
            new SimpleLeavesBlock(MapColor.COLOR_MAGENTA)
    );

    public static final Block MURKWEED = registerBlock("murkweed", new MurkweedBlock());
    public static final Block NEEDLEGRASS = registerBlock("needlegrass", new NeedlegrassBlock());

    // Wall Plants //
    public static final Block PURPLE_POLYPORE = registerBlock("purple_polypore", new EndWallMushroom(13));
    public static final Block AURANT_POLYPORE = registerBlock("aurant_polypore", new EndWallMushroom(13));
    public static final Block TAIL_MOSS = registerBlock("tail_moss", new EndWallPlantBlock(MapColor.COLOR_BLACK));
    public static final Block CYAN_MOSS = registerBlock("cyan_moss", new EndWallPlantBlock(MapColor.COLOR_CYAN));
    public static final Block TWISTED_MOSS = registerBlock(
            "twisted_moss",
            new EndWallPlantBlock(MapColor.COLOR_LIGHT_BLUE)
    );
    public static final Block TUBE_WORM = registerBlock(
            "tube_worm",
            new EndUnderwaterWallPlantBlock(MapColor.TERRACOTTA_BROWN)
    );
    public static final Block BULB_MOSS = registerBlock(
            "bulb_moss",
            new EndWallPlantBlock(MapColor.TERRACOTTA_ORANGE, 12)
    );
    public static final Block JUNGLE_FERN = registerBlock("jungle_fern", new EndWallPlantBlock(MapColor.COLOR_GREEN));
    public static final Block RUSCUS = registerBlock("ruscus", new EndWallPlantBlock(MapColor.COLOR_RED));

    // Vines //
    public static final Block DENSE_VINE = registerBlock("dense_vine", new BaseVineBlock(15, true));
    public static final Block TWISTED_VINE = registerBlock("twisted_vine", new BaseVineBlock());
    public static final Block BULB_VINE_SEED = registerBlock("bulb_vine_seed", new BulbVineSeedBlock());
    public static final Block BULB_VINE = registerBlock("bulb_vine", new BulbVineBlock());
    public static final Block JUNGLE_VINE = registerBlock("jungle_vine", new BaseVineBlock());
    public static final Block RUBINEA = registerBlock("rubinea", new BaseVineBlock());
    public static final Block MAGNULA = registerBlock("magnula", new BaseVineBlock());
    public static final Block FILALUX = registerBlock("filalux", new FilaluxBlock());
    public static final Block FILALUX_WINGS = registerBlock("filalux_wings", new FilaluxWingsBlock());
    public static final Block FILALUX_LANTERN = registerBlock("filalux_lantern", new FilaluxLanternBlock());

    // Mob-Related
    public static final Block SILK_MOTH_NEST = registerBlock("silk_moth_nest", new SilkMothNestBlock());
    public static final Block SILK_MOTH_HIVE = registerBlock("silk_moth_hive", new SilkMothHiveBlock());

    // Ores //
    public static final Block ENDER_ORE = registerBlock(
            "ender_ore",
            new BaseOreBlock(() -> EndItems.ENDER_SHARD, 1, 3, 5)
    );
    public static final Block AMBER_ORE = registerBlock(
            "amber_ore",
            new BaseOreBlock(() -> EndItems.RAW_AMBER, 1, 2, 4)
    );

    // Materials //
    public static final MetalMaterial THALLASIUM = MetalMaterial.makeNormal(
            "thallasium",
            MapColor.COLOR_BLUE,
            EndToolMaterial.THALLASIUM,
            EndArmorTier.THALLASIUM,
            EndToolMaterial.THALLASIUM.getLevel(),
            EndTags.ANVIL_IRON_TOOL,
            EndTemplates.THALLASIUM_UPGRADE
    );

    public static final MetalMaterial TERMINITE = MetalMaterial.makeOreless(
            "terminite",
            MapColor.WARPED_WART_BLOCK,
            7F,
            9F,
            EndToolMaterial.TERMINITE,
            EndArmorTier.TERMINITE,
            EndToolMaterial.TERMINITE.getLevel(),
            EndTags.ANVIL_DIAMOND_TOOL,
            EndTemplates.TERMINITE_UPGRADE
    );
    public static final Block AETERNIUM_BLOCK = registerBlock("aeternium_block", new AeterniumBlock());
    public static final Block CHARCOAL_BLOCK = registerBlock("charcoal_block", new CharcoalBlock());

    public static final Block ENDER_BLOCK = registerBlock("ender_block", new EnderBlock());
    public static final Block AURORA_CRYSTAL = registerBlock("aurora_crystal", new AuroraCrystalBlock());
    public static final Block AMBER_BLOCK = registerBlock("amber_block", new AmberBlock());
    public static final Block SMARAGDANT_CRYSTAL_SHARD = registerBlock(
            "smaragdant_crystal_shard",
            new SmaragdantCrystalShardBlock()
    );
    public static final Block SMARAGDANT_CRYSTAL = registerBlock("smaragdant_crystal", new SmaragdantCrystalBlock());
    public static final CrystalSubblocksMaterial SMARAGDANT_SUBBLOCKS = new CrystalSubblocksMaterial(
            "smaragdant_crystal",
            SMARAGDANT_CRYSTAL
    );
    public static final Block BUDDING_SMARAGDANT_CRYSTAL = registerBlock(
            "budding_smaragdant_crystal",
            new BuddingSmaragdantCrystalBlock(),
            CommonBlockTags.BUDDING_BLOCKS
    );

    public static final Block RESPAWN_OBELISK = registerBlock("respawn_obelisk", new RespawnObeliskBlock());

    // Lanterns
    public static final Block ANDESITE_LANTERN = registerBlock(
            "andesite_lantern",
            new StoneLanternBlock(Blocks.ANDESITE)
    );
    public static final Block DIORITE_LANTERN = registerBlock("diorite_lantern", new StoneLanternBlock(Blocks.DIORITE));
    public static final Block GRANITE_LANTERN = registerBlock("granite_lantern", new StoneLanternBlock(Blocks.GRANITE));
    public static final Block QUARTZ_LANTERN = registerBlock(
            "quartz_lantern",
            new StoneLanternBlock(Blocks.QUARTZ_BLOCK)
    );
    public static final Block PURPUR_LANTERN = registerBlock(
            "purpur_lantern",
            new StoneLanternBlock(Blocks.PURPUR_BLOCK)
    );
    public static final Block END_STONE_LANTERN = registerBlock(
            "end_stone_lantern",
            new StoneLanternBlock(Blocks.END_STONE)
    );
    public static final Block BLACKSTONE_LANTERN = registerBlock(
            "blackstone_lantern",
            new StoneLanternBlock(Blocks.BLACKSTONE)
    );

    public static final Block IRON_BULB_LANTERN = registerBlock("iron_bulb_lantern", new BulbVineLanternBlock());
    public static final ColoredMaterial IRON_BULB_LANTERN_COLORED = new ColoredMaterial(
            BulbVineLanternColoredBlock::new,
            IRON_BULB_LANTERN,
            false
    );

    public static final Block IRON_CHANDELIER = EndBlocks.registerBlock(
            "iron_chandelier",
            new ChandelierBlock(Blocks.GOLD_BLOCK)
    );
    public static final Block GOLD_CHANDELIER = EndBlocks.registerBlock(
            "gold_chandelier",
            new ChandelierBlock(Blocks.GOLD_BLOCK)
    );

    // Blocks With Entity //
    public static final Block END_STONE_FURNACE = registerBlock(
            "end_stone_furnace",
            new BaseFurnaceBlock.Stone(Blocks.END_STONE),
            CommonPoiTags.ARMORER_WORKSTATION
    );
    public static final Block END_STONE_SMELTER = registerBlock("end_stone_smelter", new EndStoneSmelter());
    public static final Block ETERNAL_PEDESTAL = registerBlock("eternal_pedestal", new EternalPedestal());
    public static final Block INFUSION_PEDESTAL = registerBlock("infusion_pedestal", new InfusionPedestal());
    public static final Block AETERNIUM_ANVIL = registerBlock("aeternium_anvil", new AeterniumAnvil());

    // Technical
    public static final Block END_PORTAL_BLOCK = registerEndBlockOnly("end_portal_block", new EndPortalBlock());

    // Variations
    public static final VanillaVariantStoneMaterial END_STONE_BRICK_VARIATIONS = new VanillaVariantStoneMaterial(
            "end_stone_brick",
            Blocks.END_STONE_BRICKS,
            MapColor.SAND
    ).init();

    public static final Block END_STONE_SLAB = registerBlock("end_stone_slab", new BaseSlabBlock.Stone(Blocks.END_STONE));
    public static final Block END_STONE_STAIR = registerBlock("end_stone_stairs", new BaseStairsBlock.Stone(Blocks.END_STONE));
    public static final Block END_STONE_WALLS = registerBlock("end_stone_wall", new BaseWallBlock.Stone(Blocks.END_STONE));


    public static List<Block> getModBlocks() {
        return getBlockRegistry().allBlocks().toList();
    }

    /**
     * Ensures the lazy block registry has populated all static block fields.
     * This is also needed by datagen providers that inspect the mod block list.
     */
    public static void ensureRegistered() {
        getBlockRegistry();
    }

    @SafeVarargs
    public static <T extends Block> T registerBlock(String name, T block, TagKey<Block>... tags) {
        return getBlockRegistry().register(name, block, tags);
    }

    public static Block registerEndBlockOnly(String name, Block block) {
        return getBlockRegistry().registerBlockOnly(name, block);
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

    }
}

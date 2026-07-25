package org.betterx.betterend.registry;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.features.EndOreFeatures;
import org.betterx.betterend.registry.features.EndTerrainFeatures;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import org.betterx.betterend.world.features.*;
import org.betterx.betterend.world.features.bushes.*;
import org.betterx.betterend.world.features.terrain.*;
import org.betterx.betterend.world.features.terrain.caves.CaveChunkPopulatorFeature;
import org.betterx.betterend.world.features.terrain.caves.RoundCaveFeature;
import org.betterx.betterend.world.features.terrain.caves.TunelCaveFeature;
import org.betterx.betterend.world.features.trees.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;

import net.neoforged.neoforge.registries.RegisterEvent;

public class EndFeatures {
    public static final StalactiteFeature STALACTITE_FEATURE = new StalactiteFeature();
    public static final BuildingListFeature BUILDING_LIST_FEATURE = new BuildingListFeature();
    public static final VineFeature VINE_FEATURE = new VineFeature();
    public static final WallPlantFeature WALL_PLANT_FEATURE = new WallPlantFeature();
    public static final WallPlantOnLogFeature WALL_PLANT_ON_LOG_FEATURE = new WallPlantOnLogFeature();
    public static final GlowPillarFeature GLOW_PILLAR_FEATURE = new GlowPillarFeature();
    public static final HydraluxFeature HYDRALUX_FEATURE = new HydraluxFeature();
    public static final LanceleafFeature LANCELEAF_FEATURE = new LanceleafFeature();
    public static final MengerSpongeFeature MENGER_SPONGE_FEATURE = new MengerSpongeFeature();
    public static final CaveChunkPopulatorFeature CAVE_CHUNK_POPULATOR = new CaveChunkPopulatorFeature();
    public static final SinglePlantFeature SINGLE_PLANT_FEATURE = new SinglePlantFeature();
    public static final SingleInvertedScatterFeature SINGLE_INVERTED_SCATTER_FEATURE = new SingleInvertedScatterFeature();
    public static final DoublePlantFeature DOUBLE_PLANT_FEATURE = new DoublePlantFeature();
    public static final UnderwaterPlantFeature UNDERWATER_PLANT_FEATURE = new UnderwaterPlantFeature();
    public static final ArchFeature ARCH_FEATURE = new ArchFeature();
    public static final ThinArchFeature THIN_ARCH_FEATURE = new ThinArchFeature();
    public static final CharniaFeature CHARNIA_FEATURE = new CharniaFeature();
    public static final BlueVineFeature BLUE_VINE_FEATURE = new BlueVineFeature();
    public static final FilaluxFeature FILALUX_FEATURE = new FilaluxFeature();
    public static final EndLilyFeature END_LILY_FEATURE = new EndLilyFeature();
    public static final EndLotusFeature END_LOTUS_FEATURE = new EndLotusFeature();
    public static final EndLotusLeafFeature END_LOTUS_LEAF_FEATURE = new EndLotusLeafFeature();
    public static final BushFeature BUSH_FEATURE = new BushFeature();
    public static final SingleBlockFeature SINGLE_BLOCK_FEATURE = new SingleBlockFeature();
    public static final BushWithOuterFeature BUSH_WITH_OUTER_FEATURE = new BushWithOuterFeature();
    public static final MossyGlowshroomFeature MOSSY_GLOWSHROOM_FEATURE = new MossyGlowshroomFeature();
    public static final PythadendronTreeFeature PYTHADENDRON_TREE_FEATURE = new PythadendronTreeFeature();
    public static final LacugroveFeature LACUGROVE_FEATURE = new LacugroveFeature();
    public static final DragonTreeFeature DRAGON_TREE_FEATURE = new DragonTreeFeature();
    public static final TenaneaFeature TENANEA_FEATURE = new TenaneaFeature();
    public static final HelixTreeFeature HELIX_TREE_FEATURE = new HelixTreeFeature();
    public static final UmbrellaTreeFeature UMBRELLA_TREE_FEATURE = new UmbrellaTreeFeature();
    public static final JellyshroomFeature JELLYSHROOM_FEATURE = new JellyshroomFeature();
    public static final GiganticAmaranitaFeature GIGANTIC_AMARANITA_FEATURE = new GiganticAmaranitaFeature();
    public static final LucerniaFeature LUCERNIA_FEATURE = new LucerniaFeature();
    public static final TenaneaBushFeature TENANEA_BUSH_FEATURE = new TenaneaBushFeature();
    public static final Lumecorn LUMECORN_FEATURE = new Lumecorn();
    public static final LargeAmaranitaFeature LARGE_AMARANITA_FEATURE = new LargeAmaranitaFeature();
    public static final NeonCactusFeature NEON_CACTUS_FEATURE = new NeonCactusFeature();

    //Ores
    public static final OreLayerFeature LAYERED_ORE_FEATURE = new OreLayerFeature();

    //Lakes
    public static final EndLakeFeature END_LAKE_FEATURE = new EndLakeFeature();
    public static final DesertLakeFeature DESERT_LAKE_FEATURE = new DesertLakeFeature();
    public static final SulphuricLakeFeature SULPHURIC_LAKE_FEATURE = new SulphuricLakeFeature();

    //Terrain
    public static final SurfaceVentFeature SURFACE_VENT_FEATURE = new SurfaceVentFeature();
    public static final SulphurHillFeature SULPHUR_HILL_FEATURE = new SulphurHillFeature();
    public static final ObsidianPillarBasementFeature OBSIDIAN_PILLAR_FEATURE = new ObsidianPillarBasementFeature();
    public static final ObsidianBoulderFeature OBSIDIAN_BOULDER_FEATURE = new ObsidianBoulderFeature();
    public static final FallenPillarFeature FALLEN_PILLAR_FEATURE = new FallenPillarFeature();
    public static final CrashedShipFeature CRASHED_SHIP_FEATURE = new CrashedShipFeature();
    public static final SilkMothNestFeature SILK_MOTH_NEST_FEATURE = new SilkMothNestFeature();
    public static final IceStarFeature ICE_STAR_FEATURE = new IceStarFeature();
    public static final RoundCaveFeature ROUND_CAVE_FEATURE = new RoundCaveFeature();
    public static final SpireFeature SPIRE_FEATURE = new SpireFeature();
    public static final FloatingSpireFeature FLOATING_SPIRE_FEATURE = new FloatingSpireFeature();
    public static final GeyserFeature GEYSER_FEATURE = new GeyserFeature();
    public static final PondWithWaterfallFeature POND_WITH_WATERFALL_FEATURE = new PondWithWaterfallFeature();
    public static final BiomeIslandFeature OVERWORLD_ISLAND = new BiomeIslandFeature();

    // Caves
    public static final SulphuricCaveFeature SULPHURIC_CAVE_FEATURE = new SulphuricCaveFeature();
    public static final TunelCaveFeature TUNEL_CAVE_FEATURE = new TunelCaveFeature();
    public static final SmaragdantCrystalFeature SMARAGDANT_CRYSTAL_FEATURE = new SmaragdantCrystalFeature();
    public static final BigAuroraCrystalFeature BIG_AURORA_CRYSTAL_FEATURE = new BigAuroraCrystalFeature();
    public static final CavePumpkinFeature CAVE_PUMPKIN_FEATURE = new CavePumpkinFeature();
    private static boolean registered = false;

//    public static void addBiomeFeatures(Identifier id, Holder<Biome> biome) {
//        if (!BetterEnd.MOD_ID.equals(id.getNamespace())) {
//            BiomeAPI.addBiomeFeature(biome, EndOreFeatures.FLAVOLITE_LAYER);
//            BiomeAPI.addBiomeFeature(biome, EndOreFeatures.THALLASIUM_ORE);
//            BiomeAPI.addBiomeFeature(biome, EndOreFeatures.ENDER_ORE);
//            BiomeAPI.addBiomeFeature(biome, EndTerrainFeatures.CRASHED_SHIP);
//            BCLBiome bclbiome = BiomeAPI.getBiome(id);
//            if (!BCLBiomeRegistry.isEmptyBiome(bclbiome)) {
//                BCLFeature<BuildingListFeature, BuildingListFeatureConfig> feature = getBiomeStructures(bclbiome.getID());
//                if (feature != null) {
//                    BiomeAPI.addBiomeFeature(biome, feature);
//                }
//            }
//
//            boolean hasCaves = !(bclbiome instanceof EndCaveBiome);
//            if (!(bclbiome instanceof EndCaveBiome) && bclbiome instanceof EndBiome endBiome) {
//                hasCaves = endBiome.hasCaves();
//            }
//            if (hasCaves && !BiomeAPI.wasRegisteredAsEndVoidBiome(id) /*!BiomeAPI.END_VOID_BIOME_PICKER.containsImmutable(id)*/) {
//                if (Configs.BIOME_CONFIG.getBoolean(id, "hasCaves", true)) {
//                    BiomeAPI.addBiomeFeature(biome, EndTerrainFeatures.ROUND_CAVE);
//                    BiomeAPI.addBiomeFeature(biome, EndTerrainFeatures.TUNEL_CAVE);
//                }
//            }
//        }
//    }


    public static void addDefaultFeatures(
            EndBiomeBuilder builder, boolean hasCaves
    ) {
        builder.feature(EndOreFeatures.THALLASIUM_ORE);
        builder.feature(EndOreFeatures.ENDER_ORE);
        builder.feature(EndTerrainFeatures.CRASHED_SHIP);

        if (hasCaves) {
            builder.feature(EndTerrainFeatures.ROUND_CAVE);
            builder.feature(EndTerrainFeatures.TUNEL_CAVE);
        }
    }

    public static void register(RegisterEvent.RegisterHelper<Feature<?>> helper) {
        if (registered) return;
        registered = true;

        helper.register(BetterEnd.C.mk("stalactite_feature"), STALACTITE_FEATURE);
        helper.register(BetterEnd.C.mk("building_list_feature"), BUILDING_LIST_FEATURE);
        helper.register(BetterEnd.C.mk("vine_feature"), VINE_FEATURE);
        helper.register(BetterEnd.C.mk("wall_plant_feature"), WALL_PLANT_FEATURE);
        helper.register(BetterEnd.C.mk("wall_plant_on_log_feature"), WALL_PLANT_ON_LOG_FEATURE);
        helper.register(BetterEnd.C.mk("glow_pillar_feature"), GLOW_PILLAR_FEATURE);
        helper.register(BetterEnd.C.mk("hydralux_feature"), HYDRALUX_FEATURE);
        helper.register(BetterEnd.C.mk("lanceleaf_feature"), LANCELEAF_FEATURE);
        helper.register(BetterEnd.C.mk("menger_sponge_feature"), MENGER_SPONGE_FEATURE);
        helper.register(BetterEnd.C.mk("cave_chunk_populator"), CAVE_CHUNK_POPULATOR);
        helper.register(BetterEnd.C.mk("single_plant_feature"), SINGLE_PLANT_FEATURE);
        helper.register(BetterEnd.C.mk("single_inverted_scatter_feature"), SINGLE_INVERTED_SCATTER_FEATURE);
        helper.register(BetterEnd.C.mk("double_plant_feature"), DOUBLE_PLANT_FEATURE);
        helper.register(BetterEnd.C.mk("underwater_plant_feature"), UNDERWATER_PLANT_FEATURE);
        helper.register(BetterEnd.C.mk("arch_feature"), ARCH_FEATURE);
        helper.register(BetterEnd.C.mk("thin_arch_feature"), THIN_ARCH_FEATURE);
        helper.register(BetterEnd.C.mk("charnia_feature"), CHARNIA_FEATURE);
        helper.register(BetterEnd.C.mk("blue_vine_feature"), BLUE_VINE_FEATURE);
        helper.register(BetterEnd.C.mk("filalux_feature"), FILALUX_FEATURE);
        helper.register(BetterEnd.C.mk("end_lily_feature"), END_LILY_FEATURE);
        helper.register(BetterEnd.C.mk("end_lotus_feature"), END_LOTUS_FEATURE);
        helper.register(BetterEnd.C.mk("end_lotus_leaf_feature"), END_LOTUS_LEAF_FEATURE);
        helper.register(BetterEnd.C.mk("bush_feature"), BUSH_FEATURE);
        helper.register(BetterEnd.C.mk("single_block_feature"), SINGLE_BLOCK_FEATURE);
        helper.register(BetterEnd.C.mk("bush_with_outer_feature"), BUSH_WITH_OUTER_FEATURE);
        helper.register(BetterEnd.C.mk("mossy_glowshroom"), MOSSY_GLOWSHROOM_FEATURE);
        helper.register(BetterEnd.C.mk("pythadendron_tree"), PYTHADENDRON_TREE_FEATURE);
        helper.register(BetterEnd.C.mk("lacugrove"), LACUGROVE_FEATURE);
        helper.register(BetterEnd.C.mk("dragon_tree"), DRAGON_TREE_FEATURE);
        helper.register(BetterEnd.C.mk("tenanea"), TENANEA_FEATURE);
        helper.register(BetterEnd.C.mk("helix_tree"), HELIX_TREE_FEATURE);
        helper.register(BetterEnd.C.mk("umbrella_tree"), UMBRELLA_TREE_FEATURE);
        helper.register(BetterEnd.C.mk("jellyshroom"), JELLYSHROOM_FEATURE);
        helper.register(BetterEnd.C.mk("gigantic_amaranita"), GIGANTIC_AMARANITA_FEATURE);
        helper.register(BetterEnd.C.mk("lucernia"), LUCERNIA_FEATURE);
        helper.register(BetterEnd.C.mk("tenanea_bush"), TENANEA_BUSH_FEATURE);
        helper.register(BetterEnd.C.mk("lumecorn"), LUMECORN_FEATURE);
        helper.register(BetterEnd.C.mk("large_amaranita"), LARGE_AMARANITA_FEATURE);
        helper.register(BetterEnd.C.mk("neon_cactus"), NEON_CACTUS_FEATURE);
        helper.register(BetterEnd.C.mk("ore_layer"), LAYERED_ORE_FEATURE);
        helper.register(BetterEnd.C.mk("end_lake"), END_LAKE_FEATURE);
        helper.register(BetterEnd.C.mk("desert_lake"), DESERT_LAKE_FEATURE);
        helper.register(BetterEnd.C.mk("sulphuric_lake"), SULPHURIC_LAKE_FEATURE);
        helper.register(BetterEnd.C.mk("surface_vent"), SURFACE_VENT_FEATURE);
        helper.register(BetterEnd.C.mk("sulphur_hill"), SULPHUR_HILL_FEATURE);
        helper.register(BetterEnd.C.mk("obsidian_pillar_basement"), OBSIDIAN_PILLAR_FEATURE);
        helper.register(BetterEnd.C.mk("obsidian_boulder"), OBSIDIAN_BOULDER_FEATURE);
        helper.register(BetterEnd.C.mk("fallen_pillar"), FALLEN_PILLAR_FEATURE);
        helper.register(BetterEnd.C.mk("crashed_ship"), CRASHED_SHIP_FEATURE);
        helper.register(BetterEnd.C.mk("silk_moth_nest"), SILK_MOTH_NEST_FEATURE);
        helper.register(BetterEnd.C.mk("ice_star"), ICE_STAR_FEATURE);
        helper.register(BetterEnd.C.mk("round_cave"), ROUND_CAVE_FEATURE);
        helper.register(BetterEnd.C.mk("spire"), SPIRE_FEATURE);
        helper.register(BetterEnd.C.mk("floating_spire"), FLOATING_SPIRE_FEATURE);
        helper.register(BetterEnd.C.mk("geyser"), GEYSER_FEATURE);
        helper.register(BetterEnd.C.mk("pond_with_waterfall"), POND_WITH_WATERFALL_FEATURE);
        helper.register(BetterEnd.C.mk("overworld_island"), OVERWORLD_ISLAND);
        helper.register(BetterEnd.C.mk("sulphuric_cave"), SULPHURIC_CAVE_FEATURE);
        helper.register(BetterEnd.C.mk("tunel_cave"), TUNEL_CAVE_FEATURE);
        helper.register(BetterEnd.C.mk("smaragdant_crystal"), SMARAGDANT_CRYSTAL_FEATURE);
        helper.register(BetterEnd.C.mk("big_aurora_crystal"), BIG_AURORA_CRYSTAL_FEATURE);
        helper.register(BetterEnd.C.mk("cave_pumpkin"), CAVE_PUMPKIN_FEATURE);
    }

    public static void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.FEATURE)) return;
        event.register(Registries.FEATURE, EndFeatures::register);
    }
}

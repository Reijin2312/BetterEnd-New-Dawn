package org.betterx.betterend.registry;

import com.mojang.serialization.MapCodec;
import org.betterx.wover.feature.api.FeatureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.features.EndOreFeatures;
import org.betterx.betterend.registry.features.EndTerrainFeatures;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import org.betterx.betterend.world.features.BiomeIslandFeature;
import org.betterx.betterend.world.features.BlueVineFeature;
import org.betterx.betterend.world.features.BuildingListFeature;
import org.betterx.betterend.world.features.CavePumpkinFeature;
import org.betterx.betterend.world.features.CharniaFeature;
import org.betterx.betterend.world.features.CrashedShipFeature;
import org.betterx.betterend.world.features.DoublePlantFeature;
import org.betterx.betterend.world.features.EndLilyFeature;
import org.betterx.betterend.world.features.EndLotusFeature;
import org.betterx.betterend.world.features.EndLotusLeafFeature;
import org.betterx.betterend.world.features.FilaluxFeature;
import org.betterx.betterend.world.features.GlowPillarFeature;
import org.betterx.betterend.world.features.HydraluxFeature;
import org.betterx.betterend.world.features.LanceleafFeature;
import org.betterx.betterend.world.features.MengerSpongeFeature;
import org.betterx.betterend.world.features.NeonCactusFeature;
import org.betterx.betterend.world.features.SilkMothNestFeature;
import org.betterx.betterend.world.features.SingleInvertedScatterFeature;
import org.betterx.betterend.world.features.SinglePlantFeature;
import org.betterx.betterend.world.features.UnderwaterPlantFeature;
import org.betterx.betterend.world.features.VineFeature;
import org.betterx.betterend.world.features.WallPlantFeature;
import org.betterx.betterend.world.features.WallPlantOnLogFeature;
import org.betterx.betterend.world.features.bushes.AmaranitaPatchFeature;
import org.betterx.betterend.world.features.bushes.BushFeature;
import org.betterx.betterend.world.features.bushes.BushWithOuterFeature;
import org.betterx.betterend.world.features.bushes.LargeAmaranitaFeature;
import org.betterx.betterend.world.features.bushes.Lumecorn;
import org.betterx.betterend.world.features.bushes.TenaneaBushFeature;
import org.betterx.betterend.world.features.terrain.ArchFeature;
import org.betterx.betterend.world.features.terrain.BigAuroraCrystalFeature;
import org.betterx.betterend.world.features.terrain.DesertLakeFeature;
import org.betterx.betterend.world.features.terrain.FallenPillarFeature;
import org.betterx.betterend.world.features.terrain.FloatingSpireFeature;
import org.betterx.betterend.world.features.terrain.GeyserFeature;
import org.betterx.betterend.world.features.terrain.IceStarFeature;
import org.betterx.betterend.world.features.terrain.ObsidianBoulderFeature;
import org.betterx.betterend.world.features.terrain.ObsidianPillarBasementFeature;
import org.betterx.betterend.world.features.terrain.OreLayerFeature;
import org.betterx.betterend.world.features.terrain.PondWithWaterfallFeature;
import org.betterx.betterend.world.features.terrain.SingleBlockFeature;
import org.betterx.betterend.world.features.terrain.SmaragdantCrystalFeature;
import org.betterx.betterend.world.features.terrain.SpireFeature;
import org.betterx.betterend.world.features.terrain.StalactiteFeature;
import org.betterx.betterend.world.features.terrain.SulphurHillFeature;
import org.betterx.betterend.world.features.terrain.SulphuricLakeFeature;
import org.betterx.betterend.world.features.terrain.SurfaceVentFeature;
import org.betterx.betterend.world.features.terrain.ThinArchFeature;
import org.betterx.betterend.world.features.terrain.caves.CaveChunkPopulatorFeature;
import org.betterx.betterend.world.features.terrain.caves.RoundCaveFeature;
import org.betterx.betterend.world.features.terrain.caves.StalactiteClusterFeature;
import org.betterx.betterend.world.features.terrain.caves.TunelCaveFeature;
import org.betterx.betterend.world.features.trees.DragonHelixTreeFeature;
import org.betterx.betterend.world.features.trees.DragonTreeFeature;
import org.betterx.betterend.world.features.trees.GiganticAmaranitaFeature;
import org.betterx.betterend.world.features.trees.HelixTreeFeature;
import org.betterx.betterend.world.features.trees.JellyshroomFeature;
import org.betterx.betterend.world.features.trees.LacugroveFeature;
import org.betterx.betterend.world.features.trees.LucerniaFeature;
import org.betterx.betterend.world.features.trees.MossyGlowshroomFeature;
import org.betterx.betterend.world.features.trees.PythadendronTreeFeature;
import org.betterx.betterend.world.features.trees.TenaneaFeature;
import org.betterx.betterend.world.features.trees.UmbrellaTreeFeature;

public class EndFeatures {
   public static final MapCodec<StalactiteFeature> STALACTITE_FEATURE = registerType("stalactite_feature", StalactiteFeature.CODEC);
   public static final MapCodec<BuildingListFeature> BUILDING_LIST_FEATURE = registerType("building_list_feature", BuildingListFeature.CODEC);
   public static final MapCodec<VineFeature> VINE_FEATURE = registerType("vine_feature", VineFeature.CODEC);
   public static final MapCodec<WallPlantFeature> WALL_PLANT_FEATURE = registerType("wall_plant_feature", WallPlantFeature.CODEC);
   public static final MapCodec<WallPlantOnLogFeature> WALL_PLANT_ON_LOG_FEATURE = registerType("wall_plant_on_log_feature", WallPlantOnLogFeature.CODEC);
   public static final MapCodec<GlowPillarFeature> GLOW_PILLAR_FEATURE = registerType("glow_pillar_feature", GlowPillarFeature.CODEC);
   public static final MapCodec<HydraluxFeature> HYDRALUX_FEATURE = registerType("hydralux_feature", HydraluxFeature.CODEC);
   public static final MapCodec<LanceleafFeature> LANCELEAF_FEATURE = registerType("lanceleaf_feature", LanceleafFeature.CODEC);
   public static final MapCodec<MengerSpongeFeature> MENGER_SPONGE_FEATURE = registerType("menger_sponge_feature", MengerSpongeFeature.CODEC);
   public static final MapCodec<SinglePlantFeature> SINGLE_PLANT_FEATURE = registerType("single_plant_feature", SinglePlantFeature.CODEC);
   public static final MapCodec<SingleInvertedScatterFeature> SINGLE_INVERTED_SCATTER_FEATURE = registerType(
      "single_inverted_scatter_feature", SingleInvertedScatterFeature.CODEC
   );
   public static final MapCodec<DoublePlantFeature> DOUBLE_PLANT_FEATURE = registerType("double_plant_feature", DoublePlantFeature.CODEC);
   public static final MapCodec<UnderwaterPlantFeature> UNDERWATER_PLANT_FEATURE = registerType("underwater_plant_feature", UnderwaterPlantFeature.CODEC);
   public static final MapCodec<ArchFeature> ARCH_FEATURE = registerType("arch_feature", ArchFeature.CODEC);
   public static final MapCodec<ThinArchFeature> THIN_ARCH_FEATURE = registerType("thin_arch_feature", ThinArchFeature.CODEC);
   public static final MapCodec<CharniaFeature> CHARNIA_FEATURE = registerType("charnia_feature", CharniaFeature.CODEC);
   public static final MapCodec<BlueVineFeature> BLUE_VINE_FEATURE = registerType("blue_vine_feature", BlueVineFeature.CODEC);
   public static final MapCodec<FilaluxFeature> FILALUX_FEATURE = registerType("filalux_feature", FilaluxFeature.CODEC);
   public static final MapCodec<EndLilyFeature> END_LILY_FEATURE = registerType("end_lily_feature", EndLilyFeature.CODEC);
   public static final MapCodec<EndLotusFeature> END_LOTUS_FEATURE = registerType("end_lotus_feature", EndLotusFeature.CODEC);
   public static final MapCodec<EndLotusLeafFeature> END_LOTUS_LEAF_FEATURE = registerType("end_lotus_leaf_feature", EndLotusLeafFeature.CODEC);
   public static final MapCodec<BushFeature> BUSH_FEATURE = registerType("bush_feature", BushFeature.CODEC);
   public static final MapCodec<SingleBlockFeature> SINGLE_BLOCK_FEATURE = registerType("single_block_feature", SingleBlockFeature.CODEC);
   public static final MapCodec<BushWithOuterFeature> BUSH_WITH_OUTER_FEATURE = registerType("bush_with_outer_feature", BushWithOuterFeature.CODEC);
   public static final MapCodec<StalactiteClusterFeature> STALACTITE_CLUSTER = registerType("stalactite_cluster", StalactiteClusterFeature.CODEC);
   public static final MapCodec<CaveChunkPopulatorFeature> CAVE_CHUNK_POPULATOR = registerType(
      "cave_chunk_populator", CaveChunkPopulatorFeature.CODEC
   );
   public static final MapCodec<OreLayerFeature> LAYERED_ORE_FEATURE = registerType("ore_layer", OreLayerFeature.CODEC);
   public static final MapCodec<IceStarFeature> ICE_STAR_FEATURE = registerType("ice_star", IceStarFeature.CODEC);
   public static final MapCodec<CrashedShipFeature> CRASHED_SHIP_FEATURE = registerType("crashed_ship", CrashedShipFeature.CODEC);
   public static final MossyGlowshroomFeature MOSSY_GLOWSHROOM_FEATURE = register(
      "mossy_glowshroom", MossyGlowshroomFeature.CODEC, new MossyGlowshroomFeature()
   );
   public static final PythadendronTreeFeature PYTHADENDRON_TREE_FEATURE = register(
      "pythadendron_tree", PythadendronTreeFeature.CODEC, new PythadendronTreeFeature()
   );
   public static final LacugroveFeature LACUGROVE_FEATURE = register("lacugrove", LacugroveFeature.CODEC, new LacugroveFeature());
   public static final DragonTreeFeature DRAGON_TREE_FEATURE = register("dragon_tree", DragonTreeFeature.CODEC, new DragonTreeFeature());
   public static final TenaneaFeature TENANEA_FEATURE = register("tenanea", TenaneaFeature.CODEC, new TenaneaFeature());
   public static final HelixTreeFeature HELIX_TREE_FEATURE = register("helix_tree", HelixTreeFeature.CODEC, new HelixTreeFeature());
   public static final DragonHelixTreeFeature DRAGON_HELIX_TREE_FEATURE = register(
      "dragon_helix_tree", DragonHelixTreeFeature.CODEC, new DragonHelixTreeFeature()
   );
   public static final UmbrellaTreeFeature UMBRELLA_TREE_FEATURE = register("umbrella_tree", UmbrellaTreeFeature.CODEC, new UmbrellaTreeFeature());
   public static final JellyshroomFeature JELLYSHROOM_FEATURE = register("jellyshroom", JellyshroomFeature.CODEC, new JellyshroomFeature());
   public static final GiganticAmaranitaFeature GIGANTIC_AMARANITA_FEATURE = register(
      "gigantic_amaranita", GiganticAmaranitaFeature.CODEC, new GiganticAmaranitaFeature()
   );
   public static final LucerniaFeature LUCERNIA_FEATURE = register("lucernia", LucerniaFeature.CODEC, new LucerniaFeature());
   public static final TenaneaBushFeature TENANEA_BUSH_FEATURE = register("tenanea_bush", TenaneaBushFeature.CODEC, new TenaneaBushFeature());
   public static final Lumecorn LUMECORN_FEATURE = register("lumecorn", Lumecorn.CODEC, new Lumecorn());
   public static final LargeAmaranitaFeature LARGE_AMARANITA_FEATURE = register("large_amaranita", LargeAmaranitaFeature.CODEC, new LargeAmaranitaFeature());
   public static final AmaranitaPatchFeature AMARANITA_PATCH_FEATURE = register("amaranita_patch", AmaranitaPatchFeature.CODEC, new AmaranitaPatchFeature());
   public static final NeonCactusFeature NEON_CACTUS_FEATURE = register("neon_cactus", NeonCactusFeature.CODEC, new NeonCactusFeature());
   public static final DesertLakeFeature DESERT_LAKE_FEATURE = register("desert_lake", DesertLakeFeature.CODEC, new DesertLakeFeature());
   public static final SulphuricLakeFeature SULPHURIC_LAKE_FEATURE = register("sulphuric_lake", SulphuricLakeFeature.CODEC, new SulphuricLakeFeature());
   public static final SurfaceVentFeature SURFACE_VENT_FEATURE = register("surface_vent", SurfaceVentFeature.CODEC, new SurfaceVentFeature());
   public static final SulphurHillFeature SULPHUR_HILL_FEATURE = register("sulphur_hill", SulphurHillFeature.CODEC, new SulphurHillFeature());
   public static final ObsidianPillarBasementFeature OBSIDIAN_PILLAR_FEATURE = register(
      "obsidian_pillar_basement", ObsidianPillarBasementFeature.CODEC, new ObsidianPillarBasementFeature()
   );
   public static final ObsidianBoulderFeature OBSIDIAN_BOULDER_FEATURE = register(
      "obsidian_boulder", ObsidianBoulderFeature.CODEC, new ObsidianBoulderFeature()
   );
   public static final FallenPillarFeature FALLEN_PILLAR_FEATURE = register("fallen_pillar", FallenPillarFeature.CODEC, new FallenPillarFeature());
   public static final SilkMothNestFeature SILK_MOTH_NEST_FEATURE = register("silk_moth_nest", SilkMothNestFeature.CODEC, new SilkMothNestFeature());
   public static final SpireFeature SPIRE_FEATURE = register("spire", SpireFeature.CODEC, new SpireFeature());
   public static final FloatingSpireFeature FLOATING_SPIRE_FEATURE = register("floating_spire", FloatingSpireFeature.CODEC, new FloatingSpireFeature());
   public static final GeyserFeature GEYSER_FEATURE = register("geyser", GeyserFeature.CODEC, new GeyserFeature());
   public static final PondWithWaterfallFeature POND_WITH_WATERFALL_FEATURE = register(
      "pond_with_waterfall", PondWithWaterfallFeature.CODEC, new PondWithWaterfallFeature()
   );
   public static final BiomeIslandFeature OVERWORLD_ISLAND = register("overworld_island", BiomeIslandFeature.CODEC, new BiomeIslandFeature());
   public static final SmaragdantCrystalFeature SMARAGDANT_CRYSTAL_FEATURE = register(
      "smaragdant_crystal", SmaragdantCrystalFeature.CODEC, new SmaragdantCrystalFeature()
   );
   public static final BigAuroraCrystalFeature BIG_AURORA_CRYSTAL_FEATURE = register(
      "big_aurora_crystal", BigAuroraCrystalFeature.CODEC, new BigAuroraCrystalFeature()
   );
   public static final CavePumpkinFeature CAVE_PUMPKIN_FEATURE = register("cave_pumpkin", CavePumpkinFeature.CODEC, new CavePumpkinFeature());
   public static final RoundCaveFeature ROUND_CAVE_FEATURE = register("round_cave", RoundCaveFeature.CODEC, new RoundCaveFeature());
   public static final TunelCaveFeature TUNEL_CAVE_FEATURE = register("tunel_cave", TunelCaveFeature.CODEC, new TunelCaveFeature());

   public static <F extends Feature> MapCodec<F> registerType(String name, MapCodec<F> codec) {
      Identifier l = BetterEnd.C.mk(name);
      return FeatureManager.register(l, codec);
   }

   public static <F extends Feature> F register(String name, MapCodec<F> codec, F feature) {
      registerType(name, codec);
      return feature;
   }

   public static void addDefaultFeatures(EndBiomeBuilder builder, boolean hasCaves) {
      builder.feature(EndOreFeatures.THALLASIUM_ORE);
      builder.feature(EndOreFeatures.ENDER_ORE);
      builder.feature(EndTerrainFeatures.CRASHED_SHIP);
      if (hasCaves) {
         builder.feature(EndTerrainFeatures.ROUND_CAVE);
         builder.feature(EndTerrainFeatures.TUNEL_CAVE);
      }
   }

   public static void register() {
   }
}

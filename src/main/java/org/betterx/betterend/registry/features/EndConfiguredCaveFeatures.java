package org.betterx.betterend.registry.features;

import org.betterx.wover.feature.api.configured.FeatureContentManager;
import org.betterx.wover.feature.api.configured.FeatureKey;
import org.betterx.wover.feature.api.configured.configurators.WithFeature;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndFeatures;
import org.betterx.betterend.world.features.CavePumpkinFeature;
import org.betterx.betterend.world.features.VineFeature;
import org.betterx.betterend.world.features.bushes.BushFeature;
import org.betterx.betterend.world.features.terrain.BigAuroraCrystalFeature;
import org.betterx.betterend.world.features.terrain.SingleBlockFeature;
import org.betterx.betterend.world.features.terrain.SmaragdantCrystalFeature;
import org.betterx.betterend.world.features.terrain.StalactiteFeature;
import org.betterx.betterend.world.features.terrain.caves.StalactiteClusterFeature;

public class EndConfiguredCaveFeatures {
   public static final FeatureKey<WithFeature<SmaragdantCrystalFeature>> SMARAGDANT_CRYSTAL = FeatureContentManager.withFeature(
      BetterEnd.C.mk("smaragdant_crystal"), EndFeatures.SMARAGDANT_CRYSTAL_FEATURE
   );
   public static final FeatureKey<WithFeature<SingleBlockFeature>> SMARAGDANT_CRYSTAL_SHARD = lateBound(BetterEnd.C.mk("smaragdant_crystal_shard"));
   public static final FeatureKey<WithFeature<BigAuroraCrystalFeature>> BIG_AURORA_CRYSTAL = FeatureContentManager.withFeature(
      BetterEnd.C.mk("big_aurora_crystal"), EndFeatures.BIG_AURORA_CRYSTAL_FEATURE
   );
   public static final FeatureKey<WithFeature<BushFeature>> CAVE_BUSH = lateBound(BetterEnd.C.mk("cave_bush"));
   public static final FeatureKey<WithFeature<SingleBlockFeature>> CAVE_GRASS = lateBound(BetterEnd.C.mk("cave_grass"));
   public static final FeatureKey<WithFeature<VineFeature>> RUBINEA = lateBound(BetterEnd.C.mk("rubinea"));
   public static final FeatureKey<WithFeature<VineFeature>> MAGNULA = lateBound(BetterEnd.C.mk("magnula"));
   public static final FeatureKey<WithFeature<StalactiteFeature>> END_STONE_STALACTITE = lateBound(BetterEnd.C.mk("end_stone_stalactite"));
   public static final FeatureKey<WithFeature<StalactiteFeature>> END_STONE_STALAGMITE = lateBound(BetterEnd.C.mk("end_stone_stalagmite"));
   public static final FeatureKey<WithFeature<StalactiteFeature>> END_STONE_STALACTITE_CAVEMOSS = lateBound(BetterEnd.C.mk("end_stone_stalactite_cavemoss"));
   public static final FeatureKey<WithFeature<StalactiteFeature>> END_STONE_STALAGMITE_CAVEMOSS = lateBound(BetterEnd.C.mk("end_stone_stalagmite_cavemoss"));
   public static final FeatureKey<WithFeature<StalactiteFeature>> END_STONE_WITH_CAVEMOSS_STALACTITE = lateBound(
      BetterEnd.C.mk("end_stone_with_cavemoss_stalactite")
   );
   public static final FeatureKey<WithFeature<StalactiteFeature>> END_STONE_WITH_CAVEMOSS_STALAGMITE = lateBound(
      BetterEnd.C.mk("end_stone_with_cavemoss_stalagmite")
   );
   public static final FeatureKey<WithFeature<CavePumpkinFeature>> CAVE_PUMPKIN = FeatureContentManager.withFeature(
      BetterEnd.C.mk("cave_pumpkin"), EndFeatures.CAVE_PUMPKIN_FEATURE
   );
   public static final FeatureKey<WithFeature<StalactiteClusterFeature>> STALACTITE_CLUSTER_PLAIN = lateBound(BetterEnd.C.mk("stalactite_cluster_plain"));
   public static final FeatureKey<WithFeature<StalactiteClusterFeature>> STALACTITE_CLUSTER_CAVEMOSS = lateBound(BetterEnd.C.mk("stalactite_cluster_cavemoss"));
   private static <F extends Feature> FeatureKey<WithFeature<F>> lateBound(Identifier id) {
      return FeatureContentManager.withFeature(id, null);
   }
}

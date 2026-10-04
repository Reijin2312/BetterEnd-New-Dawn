package org.betterx.betterend.registry.features;

import org.betterx.wover.feature.api.configured.FeatureContentManager;
import org.betterx.wover.feature.api.configured.FeatureKey;
import org.betterx.wover.feature.api.configured.configurators.WeightedBlockPatch;
import org.betterx.wover.feature.api.configured.configurators.WithFeature;
import org.betterx.wover.feature.api.features.ConditionFeature;
import org.betterx.betterend.BetterEnd;

public class EndConfiguredBonemealFeature {
   public static final FeatureKey<WithFeature<ConditionFeature>> BONEMEAL_END_MOSS = FeatureContentManager.withFeature(
      BetterEnd.C.mk("bonemeal_end_moss"), null
   );
   public static final FeatureKey<WithFeature<ConditionFeature>> BONEMEAL_RUTISCUS = FeatureContentManager.withFeature(
      BetterEnd.C.mk("bonemeal_rutiscus"), null
   );
   public static final FeatureKey<WeightedBlockPatch> BONEMEAL_END_MYCELIUM = FeatureContentManager.bonemeal(BetterEnd.C.mk("bonemeal_end_mycelium"));
   public static final FeatureKey<WeightedBlockPatch> BONEMEAL_JUNGLE_MOSS = FeatureContentManager.bonemeal(BetterEnd.C.mk("bonemeal_jungle_moss"));
   public static final FeatureKey<WeightedBlockPatch> BONEMEAL_SANGNUM = FeatureContentManager.bonemeal(BetterEnd.C.mk("bonemeal_sangnum"));
   public static final FeatureKey<WeightedBlockPatch> BONEMEAL_MOSSY_DRAGON_BONE = FeatureContentManager.bonemeal(BetterEnd.C.mk("bonemeal_mossy_dragon_bone"));
   public static final FeatureKey<WeightedBlockPatch> BONEMEAL_MOSSY_OBSIDIAN = FeatureContentManager.bonemeal(BetterEnd.C.mk("bonemeal_mossy_obsidian"));
   public static final FeatureKey<WeightedBlockPatch> BONEMEAL_CAVE_MOSS = FeatureContentManager.bonemeal(BetterEnd.C.mk("bonemeal_cave_moss"));
   public static final FeatureKey<WeightedBlockPatch> BONEMEAL_CHORUS_NYLIUM = FeatureContentManager.bonemeal(BetterEnd.C.mk("bonemeal_chorus_nylium"));
   public static final FeatureKey<WeightedBlockPatch> BONEMEAL_CRYSTAL_MOSS = FeatureContentManager.bonemeal(BetterEnd.C.mk("bonemeal_crystal_moss"));
   public static final FeatureKey<WeightedBlockPatch> BONEMEAL_SHADOW_GRASS = FeatureContentManager.bonemeal(BetterEnd.C.mk("bonemeal_shadow_grass"));
   public static final FeatureKey<WeightedBlockPatch> BONEMEAL_PINK_MOSS = FeatureContentManager.bonemeal(BetterEnd.C.mk("bonemeal_pink_moss"));
   public static final FeatureKey<WeightedBlockPatch> BONEMEAL_AMBER_MOSS = FeatureContentManager.bonemeal(BetterEnd.C.mk("bonemeal_amber_moss"));
}

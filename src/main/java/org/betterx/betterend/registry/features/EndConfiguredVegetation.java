package org.betterx.betterend.registry.features;

import org.betterx.wover.feature.api.configured.FeatureContentManager;
import org.betterx.wover.feature.api.configured.FeatureKey;
import org.betterx.wover.feature.api.configured.configurators.WithFeature;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndFeatures;
import org.betterx.betterend.world.features.bushes.LargeAmaranitaFeature;
import org.betterx.betterend.world.features.bushes.Lumecorn;
import org.betterx.betterend.world.features.bushes.TenaneaBushFeature;
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

public class EndConfiguredVegetation {
   public static final FeatureKey<WithFeature<DragonTreeFeature>> DRAGON_TREE = FeatureContentManager.withFeature(
      BetterEnd.C.mk("dragon_tree"), EndFeatures.DRAGON_TREE_FEATURE
   );
   public static final FeatureKey<WithFeature<GiganticAmaranitaFeature>> GIGANTIC_AMARANITA = FeatureContentManager.withFeature(
      BetterEnd.C.mk("gigantic_amaranita"), EndFeatures.GIGANTIC_AMARANITA_FEATURE
   );
   public static final FeatureKey<WithFeature<HelixTreeFeature>> HELIX_TREE = FeatureContentManager.withFeature(
      BetterEnd.C.mk("helix_tree"), EndFeatures.HELIX_TREE_FEATURE
   );
   public static final FeatureKey<WithFeature<DragonHelixTreeFeature>> DRAGON_HELIX_TREE = FeatureContentManager.withFeature(
      BetterEnd.C.mk("dragon_helix_tree"), EndFeatures.DRAGON_HELIX_TREE_FEATURE
   );
   public static final FeatureKey<WithFeature<JellyshroomFeature>> JELLYSHROOM = FeatureContentManager.withFeature(
      BetterEnd.C.mk("jellyshroom"), EndFeatures.JELLYSHROOM_FEATURE
   );
   public static final FeatureKey<WithFeature<LacugroveFeature>> LACUGROVE = FeatureContentManager.withFeature(
      BetterEnd.C.mk("lacugrove"), EndFeatures.LACUGROVE_FEATURE
   );
   public static final FeatureKey<WithFeature<LucerniaFeature>> LUCERNIA = FeatureContentManager.withFeature(
      BetterEnd.C.mk("lucernia"), EndFeatures.LUCERNIA_FEATURE
   );
   public static final FeatureKey<WithFeature<MossyGlowshroomFeature>> MOSSY_GLOWSHROOM = FeatureContentManager.withFeature(
      BetterEnd.C.mk("mossy_glowshroom"), EndFeatures.MOSSY_GLOWSHROOM_FEATURE
   );
   public static final FeatureKey<WithFeature<PythadendronTreeFeature>> PYTHADENDRON_TREE = FeatureContentManager.withFeature(
      BetterEnd.C.mk("pythadendron_tree"), EndFeatures.PYTHADENDRON_TREE_FEATURE
   );
   public static final FeatureKey<WithFeature<TenaneaFeature>> TENANEA = FeatureContentManager.withFeature(
      BetterEnd.C.mk("tenanea"), EndFeatures.TENANEA_FEATURE
   );
   public static final FeatureKey<WithFeature<UmbrellaTreeFeature>> UMBRELLA_TREE = FeatureContentManager.withFeature(
      BetterEnd.C.mk("umbrella_tree"), EndFeatures.UMBRELLA_TREE_FEATURE
   );
   public static final FeatureKey<WithFeature<LargeAmaranitaFeature>> LARGE_AMARANITA = FeatureContentManager.withFeature(
      BetterEnd.C.mk("large_amaranita"), EndFeatures.LARGE_AMARANITA_FEATURE
   );
   public static final FeatureKey<WithFeature<Lumecorn>> LUMECORN = FeatureContentManager.withFeature(BetterEnd.C.mk("lumecorn"), EndFeatures.LUMECORN_FEATURE);
   public static final FeatureKey<WithFeature<TenaneaBushFeature>> TENANEA_BUSH = FeatureContentManager.withFeature(
      BetterEnd.C.mk("tenanea_bush"), EndFeatures.TENANEA_BUSH_FEATURE
   );
}

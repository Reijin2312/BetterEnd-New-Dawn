package org.betterx.betterend.registry.features;

import org.betterx.wover.feature.api.configured.FeatureContentManager;
import org.betterx.wover.feature.api.configured.FeatureKey;
import org.betterx.wover.feature.api.configured.configurators.WithFeature;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndFeatures;
import org.betterx.betterend.world.features.terrain.SulphuricLakeFeature;

public class EndConfiguredLakeFeature {
   public static final FeatureKey<WithFeature<SulphuricLakeFeature>> SULPHURIC_LAKE = FeatureContentManager.withFeature(
      BetterEnd.C.mk("sulphuric_lake"), EndFeatures.SULPHURIC_LAKE_FEATURE
   );
}

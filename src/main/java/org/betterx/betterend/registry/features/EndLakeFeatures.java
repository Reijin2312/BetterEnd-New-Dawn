package org.betterx.betterend.registry.features;

import org.betterx.wover.feature.api.placed.BoundPlacedFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureManager;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import org.betterx.betterend.BetterEnd;

public class EndLakeFeatures {
   public static final PlacedFeatureKey DESERT_LAKE = (PlacedFeatureKey)PlacedFeatureManager.createKey(BetterEnd.C.mk("desert_lake"))
      .setDecoration(Decoration.LAKES);
   public static final BoundPlacedFeatureKey SULPHURIC_LAKE = (BoundPlacedFeatureKey)PlacedFeatureManager.createKey(EndConfiguredLakeFeature.SULPHURIC_LAKE)
      .setDecoration(Decoration.LAKES);
}

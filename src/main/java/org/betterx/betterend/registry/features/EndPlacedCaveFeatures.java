package org.betterx.betterend.registry.features;

import org.betterx.wover.feature.api.placed.PlacedFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureManager;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import org.betterx.betterend.BetterEnd;

public class EndPlacedCaveFeatures {
   public static final PlacedFeatureKey STALACTITE_CLUSTER_PLAIN = key("stalactite_cluster_plain");
   public static final PlacedFeatureKey STALACTITE_CLUSTER_CAVEMOSS = key("stalactite_cluster_cavemoss");
   public static final PlacedFeatureKey STALAGMITE_SCATTER = key("stalagmite_scatter");
   public static final PlacedFeatureKey STALACTITE_SCATTER = key("stalactite_scatter");
   public static final PlacedFeatureKey BIG_AURORA_CRYSTAL = key("big_aurora_crystal_placed");
   public static final PlacedFeatureKey SMARAGDANT_CRYSTAL = key("smaragdant_crystal_placed");
   public static final PlacedFeatureKey SMARAGDANT_SHARD_SCATTER = key("smaragdant_shard_scatter");
   public static final PlacedFeatureKey CAVE_LUSH_FLOOR_PATCH = key("cave_lush_floor_patch");
   public static final PlacedFeatureKey CAVE_LUSH_CEILING_PATCH = key("cave_lush_ceiling_patch");
   public static final PlacedFeatureKey CAVE_GRASS_SCATTER = key("cave_grass_scatter");
   public static final PlacedFeatureKey CAVE_BUSH_SCATTER = key("cave_bush_scatter");
   public static final PlacedFeatureKey CAVE_PUMPKIN_PLACED = key("cave_pumpkin_placed");

   private static PlacedFeatureKey key(String name) {
      return (PlacedFeatureKey)PlacedFeatureManager.createKey(BetterEnd.C.mk(name)).setDecoration(Decoration.UNDERGROUND_DECORATION);
   }
}

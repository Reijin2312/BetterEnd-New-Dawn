package org.betterx.betterend.registry.features;

import org.betterx.wover.feature.api.placed.PlacedFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureManager;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import org.betterx.betterend.BetterEnd;

public class EndOreFeatures {
   public static final PlacedFeatureKey THALLASIUM_ORE = (PlacedFeatureKey)PlacedFeatureManager.createKey(BetterEnd.C.mk("thallasium_ore"))
      .setDecoration(Decoration.UNDERGROUND_ORES);
   public static final PlacedFeatureKey ENDER_ORE = (PlacedFeatureKey)PlacedFeatureManager.createKey(BetterEnd.C.mk("ender_ore"))
      .setDecoration(Decoration.UNDERGROUND_ORES);
   public static final PlacedFeatureKey AMBER_ORE = (PlacedFeatureKey)PlacedFeatureManager.createKey(BetterEnd.C.mk("amber_ore"))
      .setDecoration(Decoration.UNDERGROUND_ORES);
   public static final PlacedFeatureKey DRAGON_BONE_BLOCK_ORE = (PlacedFeatureKey)PlacedFeatureManager.createKey(BetterEnd.C.mk("dragon_bone_ore"))
      .setDecoration(Decoration.UNDERGROUND_ORES);
   public static final PlacedFeatureKey VIOLECITE_LAYER = (PlacedFeatureKey)PlacedFeatureManager.createKey(BetterEnd.C.mk("violecite_layer"))
      .setDecoration(Decoration.UNDERGROUND_ORES);
   public static final PlacedFeatureKey FLAVOLITE_LAYER = (PlacedFeatureKey)PlacedFeatureManager.createKey(BetterEnd.C.mk("flavolite_layer"))
      .setDecoration(Decoration.UNDERGROUND_ORES);
}

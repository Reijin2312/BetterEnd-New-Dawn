package org.betterx.betterend.integration.byg.features;

import org.betterx.betterend.BetterEnd;
import org.betterx.wover.feature.api.placed.PlacedFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureManager;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;

import net.neoforged.neoforge.registries.RegisterEvent;

public class BYGFeatures {
    public static final NightshadeRedwoodTreeFeature NIGHTSHADE_REDWOOD_TREE_FEATURE = new NightshadeRedwoodTreeFeature();
    public static final BigEtherTreeFeature BIG_ETHER_TREE_FEATURE = new BigEtherTreeFeature();
    public static final OldBulbisTreeFeature OLD_BULBIS_TREE_FEATURE = new OldBulbisTreeFeature();

    public static final PlacedFeatureKey OLD_BULBIS_TREE = PlacedFeatureManager.createKey(BetterEnd.C.mk("old_bulbis_tree"));
    public static final PlacedFeatureKey IVIS_SPROUT = PlacedFeatureManager.createKey(BetterEnd.C.mk("ivis_sprout"));
    public static final PlacedFeatureKey IVIS_VINE = PlacedFeatureManager.createKey(BetterEnd.C.mk("ivis_vine"));
    public static final PlacedFeatureKey IVIS_MOSS = PlacedFeatureManager.createKey(BetterEnd.C.mk("ivis_moss"));
    public static final PlacedFeatureKey IVIS_MOSS_WOOD = PlacedFeatureManager.createKey(BetterEnd.C.mk("ivis_moss_wood"));
    public static final PlacedFeatureKey NIGHTSHADE_MOSS = PlacedFeatureManager.createKey(BetterEnd.C.mk("nightshade_moss"));
    public static final PlacedFeatureKey NIGHTSHADE_MOSS_WOOD = PlacedFeatureManager.createKey(BetterEnd.C.mk("nightshade_moss_wood"));
    public static final PlacedFeatureKey NIGHTSHADE_REDWOOD_TREE = PlacedFeatureManager.createKey(BetterEnd.C.mk("nightshade_redwood_tree"));
    public static final PlacedFeatureKey BIG_ETHER_TREE = PlacedFeatureManager.createKey(BetterEnd.C.mk("big_ether_tree"));

    private static boolean registered = false;

    private static void register(RegisterEvent.RegisterHelper<Feature> helper) {
        if (registered) return;
        registered = true;

        helper.register(BetterEnd.C.mk("nightshade_redwood_tree"), NIGHTSHADE_REDWOOD_TREE_FEATURE);
        helper.register(BetterEnd.C.mk("big_ether_tree"), BIG_ETHER_TREE_FEATURE);
        helper.register(BetterEnd.C.mk("old_bulbis_tree_feature"), OLD_BULBIS_TREE_FEATURE);
    }

    public static void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.FEATURE)) return;
        event.register(Registries.FEATURE, BYGFeatures::register);
    }

    public static void register() {
    }
}

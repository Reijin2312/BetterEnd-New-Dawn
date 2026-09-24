package org.betterx.betterend.integration.byg.biomes;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import org.betterx.betterend.world.biome.EndBiomeKey;

import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;

public class BYGBiomes {
    public static final MaterialCondition BYG_WATER_CHECK = MaterialRules.waterBlockCheck(-1, 0);

    public static final EndBiomeKey<OldBulbisGardens, ?> OLD_BULBIS_GARDENS = EndBiomeBuilder.createKey("bulbis_gardens");
    public static final EndBiomeKey<NightshadeRedwoods, ?> NIGHTSHADE_REDWOODS = EndBiomeBuilder.createKey("nightshade_forest");

    public static void register() {
        BetterEnd.LOGGER.info("Registered " + OLD_BULBIS_GARDENS);
    }

    public static void addBiomes() {
    }
}

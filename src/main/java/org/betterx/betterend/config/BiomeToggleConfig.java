package org.betterx.betterend.config;

import de.ambertation.wunderlib.configs.ConfigFile;
import org.betterx.betterend.BetterEnd;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.LinkedHashMap;
import java.util.Map;

public class BiomeToggleConfig extends ConfigFile {
    public static final Group BIOMES = new Group(BetterEnd.C.namespace, "biomes", 2600);
    private final Map<ResourceKey<Biome>, BooleanValue> biomes = new LinkedHashMap<>();

    public BiomeToggleConfig() {
        super(BetterEnd.C, "biomes_toggle");
    }

    @SafeVarargs
    public final void registerBiomes(ResourceKey<Biome>... biomeKeys) {
        for (ResourceKey<Biome> biomeKey : biomeKeys) {
            registerBiome(biomeKey);
        }
    }

    public BooleanValue registerBiome(ResourceKey<Biome> biomeKey) {
        return biomes.computeIfAbsent(
                biomeKey,
                key -> new BooleanValue("biomes", key.location().getPath(), true).setGroup(BIOMES)
        );
    }

    public boolean isEnabled(ResourceKey<Biome> biomeKey) {
        BooleanValue value = biomes.get(biomeKey);
        return value == null || value.get();
    }
}

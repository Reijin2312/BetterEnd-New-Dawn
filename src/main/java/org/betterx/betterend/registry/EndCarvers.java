package org.betterx.betterend.registry;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.world.carvers.EndCaveCarver;
import org.betterx.betterend.world.carvers.EndCaveCarverConfiguration;
import org.betterx.betterend.world.carvers.EndTunnelCarver;
import org.betterx.betterend.world.carvers.EndTunnelCarverConfiguration;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

/**
 * Registers the two BetterEnd {@link WorldCarver}s into the NeoForge carver registry and
 * exposes the {@link ResourceKey}s of their configured variants (which live in the
 * {@link Registries#CONFIGURED_CARVER} datapack registry, populated by {@code CarverProvider}).
 */
public class EndCarvers {
    public static final EndCaveCarver END_ROUND_CAVE = new EndCaveCarver(EndCaveCarverConfiguration.CODEC);
    public static final EndTunnelCarver END_TUNNEL_CAVE = new EndTunnelCarver(EndTunnelCarverConfiguration.CODEC);

    public static final ResourceKey<ConfiguredWorldCarver<?>> ROUND_CAVE = configuredKey("round_cave");
    public static final ResourceKey<ConfiguredWorldCarver<?>> TUNNEL_CAVE = configuredKey("tunnel_cave");

    private static ResourceKey<ConfiguredWorldCarver<?>> configuredKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_CARVER, BetterEnd.C.mk(name));
    }

    /**
     * Kept as a compatibility hook for callers that only need the instances initialized.
     */
    public static void ensureStaticallyLoaded() {
    }

    public static void onRegister(net.neoforged.neoforge.registries.RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.CARVER)) {
            event.register(Registries.CARVER, helper -> {
                helper.register(BetterEnd.C.mk("end_round_cave"), END_ROUND_CAVE);
                helper.register(BetterEnd.C.mk("end_tunnel_cave"), END_TUNNEL_CAVE);
            });
        }
    }
}

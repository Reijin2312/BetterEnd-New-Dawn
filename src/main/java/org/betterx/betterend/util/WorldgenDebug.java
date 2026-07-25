package org.betterx.betterend.util;

/** Compatibility no-op for upstream worldgen diagnostics. */
public final class WorldgenDebug {
    private WorldgenDebug() {}

    public static void log(String format, Object... args) {
        // Diagnostics are intentionally disabled in production generation.
    }
}

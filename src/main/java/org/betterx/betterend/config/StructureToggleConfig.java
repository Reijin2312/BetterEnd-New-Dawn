package org.betterx.betterend.config;

import de.ambertation.wunderlib.configs.ConfigFile;
import org.betterx.betterend.BetterEnd;
import org.betterx.wover.structure.api.StructureKey;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.LinkedHashMap;
import java.util.Map;

public class StructureToggleConfig extends ConfigFile {
    public static final Group STRUCTURES = new Group(BetterEnd.C.namespace, "structures", 2601);
    private final Map<ResourceKey<Structure>, BooleanValue> structures = new LinkedHashMap<>();

    public StructureToggleConfig() {
        super(BetterEnd.C, "structures_toggle");
    }

    public final void registerStructures(StructureKey<?, ?, ?>... structureKeys) {
        for (StructureKey<?, ?, ?> structureKey : structureKeys) {
            registerStructure(structureKey.key());
        }
    }

    public BooleanValue registerStructure(ResourceKey<Structure> structureKey) {
        return structures.computeIfAbsent(
                structureKey,
                key -> new BooleanValue("structures", key.identifier().getPath(), true).setGroup(STRUCTURES)
        );
    }

    public boolean isEnabled(Holder<Structure> structureHolder) {
        return structureHolder.unwrapKey().map(this::isEnabled).orElse(true);
    }

    public boolean isEnabled(ResourceKey<Structure> structureKey) {
        BooleanValue value = structures.get(structureKey);
        return value == null || value.get();
    }
}

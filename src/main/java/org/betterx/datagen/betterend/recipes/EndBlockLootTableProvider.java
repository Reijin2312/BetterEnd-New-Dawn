package org.betterx.datagen.betterend.recipes;

import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverLootTableProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;

public class EndBlockLootTableProvider extends WoverLootTableProvider {
    public EndBlockLootTableProvider(ModCore modCore) {
        super(modCore, "BetterEnd Block Loot", LootContextParamSets.BLOCK);
    }

    @Override
    protected void boostrap(
            @NotNull HolderLookup.Provider lookup,
            @NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer
    ) {
        // Ensure blocks are loaded before generating loot tables
        EndBlocks.ensureStaticallyLoaded();

        // Use BlockRegistry.bootstrapBlockLoot() which automatically handles
        // all blocks implementing BlockLootProvider (including BaseOreBlock)
        EndBlocks.getBlockRegistry().bootstrapBlockLoot(lookup, biConsumer);
    }
}

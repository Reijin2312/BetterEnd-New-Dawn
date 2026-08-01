package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.BaseBlock;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootTable;

import net.minecraft.world.level.block.state.BlockBehaviour;

import org.jetbrains.annotations.NotNull;

public class RunedFlavolite extends BaseBlock.Stone {
    public static final BooleanProperty ACTIVATED = BlockProperties.ACTIVE;
    private final boolean unbreakable;

    public RunedFlavolite(boolean unbreakable) {
        super(BlockBehaviour.Properties.ofLegacyCopy(EndBlocks.FLAVOLITE.polished)
                                 .strength(
                                         unbreakable ? -1 : 1,
                                         unbreakable
                                                 ? Blocks.BEDROCK.getExplosionResistance()
                                                 : Blocks.OBSIDIAN.getExplosionResistance()
                                 )
                                 .lightLevel(state -> state.getValue(ACTIVATED) ? 8 : 0));
        this.unbreakable = unbreakable;
        this.registerDefaultState(stateDefinition.any().setValue(ACTIVATED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(ACTIVATED);
    }

    @Override
    public boolean dropFromExplosion(Explosion explosion) {
        return !BlocksHelper.isInvulnerableUnsafe(this.defaultBlockState());
    }

    @Override
    public LootTable.Builder registerBlockLoot(
            @NotNull Identifier location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        return unbreakable ? null : super.registerBlockLoot(location, provider, tableKey);
    }
}

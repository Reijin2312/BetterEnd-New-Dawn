package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourObsidian;
import org.betterx.bclib.blocks.BaseBlock;
import org.betterx.wover.loot.api.BlockLootProvider;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import net.minecraft.world.level.block.state.BlockBehaviour;

import org.jetbrains.annotations.NotNull;

public class MossyObsidian extends BaseBlock implements BehaviourObsidian, BlockLootProvider {
    public MossyObsidian() {
        super(BlockBehaviour.Properties.ofLegacyCopy(Blocks.OBSIDIAN).strength(3).randomTicks());
    }

    @Override
    public LootTable.Builder registerBlockLoot(
            @NotNull Identifier location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        return provider.dropWithSilkTouch(this, Blocks.OBSIDIAN, ConstantValue.exactly(1.0F));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (random.nextInt(16) == 0 && !canSurvive(state, world, pos)) {
            world.setBlockAndUpdate(pos, Blocks.OBSIDIAN.defaultBlockState());
        }
    }

    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader worldView, BlockPos pos) {
        BlockPos blockPos = pos.above();
        BlockState blockState = worldView.getBlockState(blockPos);
        if (blockState.is(Blocks.SNOW) && blockState.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        } else if (blockState.getFluidState().getAmount() == 8) {
            return false;
        } else {
            int i = LightEngine.getLightBlockInto(
                    state,
                    blockState,
                    Direction.UP,
                    blockState.getLightBlock()
            );
            return i < 5;
        }
    }
}

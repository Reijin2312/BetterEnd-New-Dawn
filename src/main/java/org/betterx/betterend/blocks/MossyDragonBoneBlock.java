package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourStone;
import org.betterx.bclib.blocks.BaseRotatedPillarBlock;
import org.betterx.betterend.registry.EndBlocks;
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

@SuppressWarnings("deprecation")
public class MossyDragonBoneBlock extends BaseRotatedPillarBlock implements BehaviourStone, BlockLootProvider {
    public MossyDragonBoneBlock() {
        super(BlockBehaviour.Properties.ofLegacyCopy(Blocks.BONE_BLOCK).strength(0.5F).randomTicks());
    }


    @Override
    public LootTable.Builder registerBlockLoot(
            @NotNull Identifier location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        return provider.dropWithSilkTouch(this, EndBlocks.DRAGON_BONE_BLOCK, ConstantValue.exactly(1.0F));
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (random.nextInt(16) == 0 && !canSurvive(state, world, pos)) {
            world.setBlockAndUpdate(pos, Blocks.BONE_BLOCK.defaultBlockState().setValue(AXIS, state.getValue(AXIS)));
        }
    }

    @Override
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

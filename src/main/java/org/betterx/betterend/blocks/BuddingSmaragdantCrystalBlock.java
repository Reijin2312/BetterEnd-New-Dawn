package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourGlass;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.blocks.basis.LitPillarBlock;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootTable;

import org.betterx.wover.loot.api.LootLookupProvider;
import org.jetbrains.annotations.NotNull;

public class BuddingSmaragdantCrystalBlock extends LitPillarBlock implements BehaviourGlass {
    public BuddingSmaragdantCrystalBlock() {
        super(BehaviourBuilders
                .createGlass()
                .lightLevel((bs) -> 15)
                .strength(1F)
                .noOcclusion()
                .sound(SoundType.AMETHYST)
                .randomTicks()
                .pushReaction(PushReaction.DESTROY));
    }

    @Override
    public LootTable.Builder registerBlockLoot(
            @NotNull Identifier location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void randomTick(BlockState blockState, ServerLevel world, BlockPos pos, RandomSource random) {
        Direction dir = BlocksHelper.randomDirection(random);
        BlockPos side = pos.relative(dir);
        BlockState sideState = world.getBlockState(side);
        if (random.nextInt(20) == 0) {
            if (canShardGrowAtState(sideState)) {
                BlockState shard = EndBlocks.SMARAGDANT_CRYSTAL_SHARD.defaultBlockState()
                                                                     .setValue(
                                                                             SmaragdantCrystalShardBlock.WATERLOGGED,
                                                                             sideState.getFluidState()
                                                                                      .getType() == Fluids.WATER
                                                                     )
                                                                     .setValue(SmaragdantCrystalShardBlock.FACING, dir);
                world.setBlockAndUpdate(side, shard);
            }
        }
    }

    public static boolean canShardGrowAtState(BlockState blockState) {
        return blockState.isAir() || blockState.is(Blocks.WATER) && blockState.getFluidState().getAmount() == 8;
    }
}

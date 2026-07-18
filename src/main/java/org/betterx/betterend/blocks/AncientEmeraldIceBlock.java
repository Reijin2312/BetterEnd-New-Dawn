package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourIce;
import org.betterx.bclib.blocks.BaseBlock;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndParticles;
import org.betterx.wover.loot.api.BlockLootProvider;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

import net.minecraft.world.level.block.state.BlockBehaviour;

import org.jetbrains.annotations.NotNull;

public class AncientEmeraldIceBlock extends BaseBlock implements BehaviourIce, BlockLootProvider {
    public AncientEmeraldIceBlock() {
        super(BlockBehaviour.Properties.ofLegacyCopy(Blocks.BLUE_ICE).randomTicks());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        Direction dir = BlocksHelper.randomDirection(random);

        if (random.nextBoolean()) {
            int x = MHelper.randRange(-2, 2, random);
            int y = MHelper.randRange(-2, 2, random);
            int z = MHelper.randRange(-2, 2, random);
            BlockPos p = pos.offset(x, y, z);
            if (world.getBlockState(p).is(Blocks.WATER)) {
                world.setBlockAndUpdate(p, EndBlocks.EMERALD_ICE.defaultBlockState());
                makeParticles(world, p, random);
            }
        }

        pos = pos.relative(dir);
        state = world.getBlockState(pos);
        if (state.is(Blocks.WATER)) {
            world.setBlockAndUpdate(pos, EndBlocks.EMERALD_ICE.defaultBlockState());
            makeParticles(world, pos, random);
        } else if (state.is(EndBlocks.EMERALD_ICE)) {
            world.setBlockAndUpdate(pos, EndBlocks.DENSE_EMERALD_ICE.defaultBlockState());
            makeParticles(world, pos, random);
        }
    }

    private void makeParticles(ServerLevel world, BlockPos pos, RandomSource random) {
        world.sendParticles(
                EndParticles.SNOWFLAKE,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                20,
                0.5,
                0.5,
                0.5,
                0
        );
    }

    @Override
    public LootTable.Builder registerBlockLoot(
            @NotNull Identifier location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        return provider.dropWithSilkTouch(this);
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        super.stepOn(level, blockPos, blockState, entity);
        entity.setIsInPowderSnow(true);
    }
}

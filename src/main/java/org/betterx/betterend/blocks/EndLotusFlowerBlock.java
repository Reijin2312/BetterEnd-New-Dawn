package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourPlant;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.blocks.basis.EndPlantBlock;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


import com.google.common.collect.Lists;

import java.util.List;

public class EndLotusFlowerBlock extends EndPlantBlock implements BehaviourPlant {
    private static final VoxelShape SHAPE_OUTLINE = Block.box(2, 0, 2, 14, 14, 14);
    private static final VoxelShape SHAPE_COLLISION = Block.box(0, 0, 0, 16, 2, 16);

    public EndLotusFlowerBlock() {
        super(BehaviourBuilders.createPlant(MapColor.COLOR_PINK).lightLevel((bs) -> 15));
    }

    @Override
    public boolean isTerrain(BlockState state) {
        return state.is(EndBlocks.END_LOTUS_STEM);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE_OUTLINE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE_COLLISION;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        int count = MHelper.randRange(1, 2, MHelper.RANDOM_SOURCE);
        return Lists.newArrayList(new ItemStack(EndBlocks.END_LOTUS_SEED, count));
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean includeData) {
        return new ItemStack(EndBlocks.END_LOTUS_SEED);
    }
}

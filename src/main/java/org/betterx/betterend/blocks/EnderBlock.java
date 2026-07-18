package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.blocks.BaseBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;


public class EnderBlock extends BaseBlock.Stone {

    public EnderBlock() {
        super(BehaviourBuilders
                .createStone(MapColor.WARPED_WART_BLOCK)
                .strength(5F, 6F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE)
        );
    }

    public int getColor(BlockState state, BlockGetter world, BlockPos pos) {
        return 0xFF005548;
    }
}

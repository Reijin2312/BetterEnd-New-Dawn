package org.betterx.betterend.blocks;

import org.betterx.betterend.blocks.basis.LitPillarBlock;

import net.minecraft.world.level.block.Blocks;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class NeonCactusBlock extends LitPillarBlock {
    public NeonCactusBlock() {
        super(BlockBehaviour.Properties.ofLegacyCopy(Blocks.CACTUS).lightLevel((bs) -> 15));
    }
}

package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourStone;
import org.betterx.bclib.blocks.BaseBlock;

import net.minecraft.world.level.block.Blocks;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class MissingTileBlock extends BaseBlock implements BehaviourStone {
    public MissingTileBlock() {
        super(BlockBehaviour.Properties.ofLegacyCopy(Blocks.END_STONE));
    }
}

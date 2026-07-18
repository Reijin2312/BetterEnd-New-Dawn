package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourStone;
import org.betterx.bclib.blocks.BaseBlock;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class AmberBlock extends BaseBlock implements BehaviourStone {
    public AmberBlock() {
        super(BlockBehaviour.Properties.ofLegacyCopy(Blocks.DIAMOND_BLOCK).mapColor(MapColor.COLOR_YELLOW));
    }
}

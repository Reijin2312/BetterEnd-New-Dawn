package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourCompostable;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.blocks.BaseBlock;

import net.minecraft.world.level.block.SoundType;

public class AmaranitaCapBlock extends BaseBlock.Wood implements BehaviourCompostable {
    public AmaranitaCapBlock() {
        super(BehaviourBuilders.createWood().sound(SoundType.WOOD));
    }
}

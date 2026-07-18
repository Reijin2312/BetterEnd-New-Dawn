package org.betterx.betterend.interfaces;

import net.minecraft.world.level.block.Block;


public interface PottablePlant {
    boolean canPlantOn(Block block);

    default boolean canBePotted() {
        return true;
    }

    default String getPottedState() {
        return "";
    }
}

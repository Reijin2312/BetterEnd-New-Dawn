package org.betterx.betterend.complexmaterials;

import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SlotMap;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.bclib.items.boat.BoatTypeOverride;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class EndWoodenComplexMaterial extends WoodenComplexMaterial {
    private Block bark;
    private Block log;
    private boolean raft;

    public EndWoodenComplexMaterial(String name, MapColor woodColor, MapColor planksColor, Block furnitureCloth) {
        super(BetterEnd.C, name, name, woodColor, planksColor);
        this.setFurnitureCloth(furnitureCloth);
    }

    public EndWoodenComplexMaterial init() {
        return (EndWoodenComplexMaterial) super.init(
                EndBlocks.getBlockRegistry(),
                EndItems.getItemRegistry()
        );
    }

    public EndWoodenComplexMaterial useRaft() {
        this.raft = true;
        return this;
    }

    @Override
    protected SlotMap<WoodenComplexMaterial> createMaterialSlots() {
        SlotMap<WoodenComplexMaterial> slots = super.createMaterialSlots()
                    .add(raft ? WoodSlots.RAFT : WoodSlots.BOAT)
                    .add(raft ? WoodSlots.CHEST_RAFT : WoodSlots.CHEST_BOAT)
                    .add(WoodSlots.HANGING_SIGN)
                    .add(WoodSlots.TABURET)
                    .add(WoodSlots.BAR_STOOL)
                    .add(WoodSlots.CHAIR)
                    .add(WoodSlots.WALL);
        return slots;
    }

    @Override
    protected BoatTypeOverride supplyBoatType() {
        return BoatTypeOverride.create(C, getBaseName(), getBlock(WoodSlots.PLANKS), raft);
    }

    public boolean isTreeLog(Block block) {
        return block == getLog() || block == getBark();
    }

    public boolean isTreeLog(BlockState state) {
        return isTreeLog(state.getBlock());
    }

    public Block getLog() {
        if (log == null) {
            log = getBlock("log");
        }
        return log;
    }

    public Block getBark() {
        if (bark == null) {
            bark = getBlock("bark");
        }
        return bark;
    }
}
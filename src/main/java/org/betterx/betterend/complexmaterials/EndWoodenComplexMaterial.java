package org.betterx.betterend.complexmaterials;

import org.betterx.bclib.complexmaterials.WoodenComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SlotMap;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.item.api.ItemRegistry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class EndWoodenComplexMaterial extends WoodenComplexMaterial {
    private Block bark;
    private Block log;

    public EndWoodenComplexMaterial(String name, MapColor woodColor, MapColor planksColor, Block furnitureCloth) {
        super(BetterEnd.C, name, name, woodColor, planksColor);
        this.setFurnitureCloth(furnitureCloth);
    }

    public EndWoodenComplexMaterial init() {
        return (EndWoodenComplexMaterial) super.init(
                EndBlocks.getBlockRegistry(),
                ItemRegistry.forMod(BetterEnd.C)
        );
    }

    @Override
    protected SlotMap<WoodenComplexMaterial> createMaterialSlots() {
        return super.createMaterialSlots()
                    .add(WoodSlots.HANGING_SIGN)
                    .add(WoodSlots.TABURET)
                    .add(WoodSlots.BAR_STOOL)
                    .add(WoodSlots.CHAIR)
                    .add(WoodSlots.WALL)
                ;
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

package org.betterx.betterend.complexmaterials;

import org.betterx.bclib.complexmaterials.StoneComplexMaterial;
import org.betterx.bclib.complexmaterials.entry.SlotMap;
import org.betterx.bclib.complexmaterials.set.stone.StoneSlots;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.item.api.ItemRegistry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class VanillaVariantStoneMaterial extends StoneComplexMaterial {
    public VanillaVariantStoneMaterial(
            String baseName,
            Block sourceBlock,
            MapColor color
    ) {
        super(BetterEnd.C, baseName, "", sourceBlock, color);
    }

    public VanillaVariantStoneMaterial init() {
        return (VanillaVariantStoneMaterial) super.init(
                EndBlocks.getBlockRegistry(),
                ItemRegistry.forMod(BetterEnd.C)
        );
    }

    @Override
    protected SlotMap<StoneComplexMaterial> createMaterialSlots() {
        return SlotMap.of(
                StoneSlots.SOURCE,
                StoneSlots.CRACKED,
                StoneSlots.CRACKED_SLAB,
                StoneSlots.CRACKED_STAIRS,
                StoneSlots.CRACKED_WALL,
                StoneSlots.WEATHERED,
                StoneSlots.WEATHERED_SLAB,
                StoneSlots.WEATHERED_STAIRS,
                StoneSlots.WEATHERED_WALL
        );
    }
}

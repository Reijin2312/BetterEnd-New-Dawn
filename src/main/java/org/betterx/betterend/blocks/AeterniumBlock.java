package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.blocks.BaseBlock;
import org.betterx.wover.block.api.CustomBlockItemProvider;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class AeterniumBlock extends BaseBlock.Metal implements CustomBlockItemProvider {
    public AeterniumBlock() {
        super(BehaviourBuilders
                .createMetal(MapColor.COLOR_GRAY)
                .strength(65F, 1200F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.NETHERITE_BLOCK)
        );
    }

    @Override
    public BlockItem getCustomBlockItem(Identifier blockID, Item.Properties settings) {
        return new BlockItem(this, settings.fireResistant());
    }
}

package org.betterx.betterend.item.material;

import org.betterx.betterend.registry.EndTags;
import org.betterx.wover.tag.api.TagManager;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

public enum EndToolMaterial {
    THALLASIUM(ToolMaterial.IRON.incorrectBlocksForDrops(), 2, 320, 7.0F, 1.5F, 12, "ingots/thallasium"),
    TERMINITE(ToolMaterial.DIAMOND.incorrectBlocksForDrops(), 3, 1230, 8.5F, 3.0F, 14, "ingots/terminite"),
    AETERNIUM(EndTags.INCORRECT_FOR_AETERNIUM_TOOL, 5, 2196, 10.0F, 4.5F, 18, "ingots/aeternium");

    private final ToolMaterial toolMaterial;
    private final int level;
    public final TagKey<Block> incorrectBlocksForDrops;

    EndToolMaterial(
            TagKey<Block> incorrectBlocksForDrops,
            int level,
            int uses,
            float speed,
            float damage,
            int enchantibility,
            String repairTag
    ) {
        this.incorrectBlocksForDrops = incorrectBlocksForDrops;
        this.level = level;
        this.toolMaterial = new ToolMaterial(
                incorrectBlocksForDrops,
                uses,
                speed,
                damage,
                enchantibility,
                TagManager.ITEMS.makeCommonTag(repairTag)
        );
    }

    public int getLevel() {
        return level;
    }

    public ToolMaterial toolMaterial() {
        return toolMaterial;
    }
}

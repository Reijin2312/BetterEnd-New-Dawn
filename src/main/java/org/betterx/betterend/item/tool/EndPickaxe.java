package org.betterx.betterend.item.tool;

import org.betterx.bclib.items.tool.BasePickaxeItem;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class EndPickaxe extends BasePickaxeItem {
    private final ToolMaterial toolMaterial;

    public EndPickaxe(ToolMaterial material, int attackDamage, float attackSpeed, Properties settings) {
        super(material, attackDamage, attackSpeed, settings);
        this.toolMaterial = material;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(Blocks.END_STONE) && this.toolMaterial.speed() > ToolMaterial.IRON.speed()) {
            Tool tool = stack.get(DataComponents.TOOL);
            return tool.getMiningSpeed(state) * 3;
        }
        return super.getDestroySpeed(stack, state);
    }
}

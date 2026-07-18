package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourWaterPlant;
import org.betterx.bclib.interfaces.SurvivesOnSpecialGround;
import org.betterx.betterend.blocks.basis.EndUnderwaterPlantBlock;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CharniaBlock extends EndUnderwaterPlantBlock implements BehaviourWaterPlant {
    public CharniaBlock() {
        super(
                BehaviourBuilders.createWaterPlant()
        );
    }

    public void appendHoverText(
            ItemStack itemStack,
            Item.TooltipContext tooltipContext,
            TooltipDisplay tooltipDisplay,
            Consumer<Component> consumer,
            TooltipFlag tooltipFlag
    ) {
        List<Component> lines = new ArrayList<>();
        SurvivesOnSpecialGround.appendHoverTextUnderwater(lines);
        lines.forEach(consumer);
    }

    @Override
    protected boolean isTerrain(BlockState state) {
        return state.isSolid();
    }
}

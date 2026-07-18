package org.betterx.betterend.integration.jade;

import org.betterx.bclib.blocks.BasePlantWithAgeBlock;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.basis.EndPlantWithAgeBlock;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;

public enum EndPlantProvider implements IBlockComponentProvider {
    INSTANCE;

    public static final Identifier ID = BetterEnd.C.mk("plant_growth");
    private static final float MAX_AGE = 3.0F;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (config.get(JadeIds.MC_CROP_PROGRESS)) {
            // Jade already renders crop progress for AGE_* plants, avoid duplicate line.
            return;
        }

        if (!(accessor.getBlock() instanceof EndPlantWithAgeBlock)) {
            return;
        }

        BlockState state = accessor.getBlockState();
        if (!state.hasProperty(BasePlantWithAgeBlock.AGE)) {
            return;
        }

        float growthValue = state.getValue(BasePlantWithAgeBlock.AGE) / MAX_AGE;
        addMaturityTooltip(tooltip, growthValue);
    }

    private static void addMaturityTooltip(ITooltip tooltip, float growthValue) {
        growthValue *= 100.0F;

        if (growthValue < 100.0F) {
            String percentString = String.format("%.0f%%", growthValue);
            tooltip.add(Component.translatable("tooltip.jade.crop_growth", percentString));
        } else {
            tooltip.add(Component.translatable("tooltip.jade.crop_mature"));
        }
    }

    @Override
    public Identifier getUid() {
        return ID;
    }
}

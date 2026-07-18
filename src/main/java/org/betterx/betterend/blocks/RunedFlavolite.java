package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.BaseBlock;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;

import net.minecraft.world.level.block.state.BlockBehaviour;

import com.google.common.collect.Lists;

import java.util.List;

public class RunedFlavolite extends BaseBlock.Stone {
    public static final BooleanProperty ACTIVATED = BlockProperties.ACTIVE;

    public RunedFlavolite(boolean unbreakable) {
        super(BlockBehaviour.Properties.ofLegacyCopy(EndBlocks.FLAVOLITE.polished)
                                 .strength(
                                         unbreakable ? -1 : 1,
                                         unbreakable
                                                 ? Blocks.BEDROCK.getExplosionResistance()
                                                 : Blocks.OBSIDIAN.getExplosionResistance()
                                 )
                                 .lightLevel(state -> state.getValue(ACTIVATED) ? 8 : 0));
        this.registerDefaultState(stateDefinition.any().setValue(ACTIVATED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(ACTIVATED);
    }

    @Override
    public boolean dropFromExplosion(Explosion explosion) {
        return !BlocksHelper.isInvulnerableUnsafe(this.defaultBlockState());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (BlocksHelper.isInvulnerableUnsafe(this.defaultBlockState())) {
            return Lists.newArrayList();
        }
        return super.getDrops(state, builder);
    }
}

package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.BaseBlock;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class UmbrellaTreeClusterEmptyBlock extends BaseBlock {
    public static final BooleanProperty NATURAL = EndBlockProperties.NATURAL;

    public UmbrellaTreeClusterEmptyBlock() {
        super(BlockBehaviour.Properties
                .ofLegacyCopy(Blocks.NETHER_WART_BLOCK)
                .mapColor(MapColor.COLOR_PURPLE)
                .randomTicks()
        );
        registerDefaultState(stateDefinition.any().setValue(NATURAL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(NATURAL);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (state.getValue(NATURAL) && random.nextInt(16) == 0) {
            BlocksHelper.setWithUpdate(
                    world,
                    pos,
                    EndBlocks.UMBRELLA_TREE_CLUSTER.defaultBlockState().setValue(UmbrellaTreeClusterBlock.NATURAL, true)
            );
        }
    }
}

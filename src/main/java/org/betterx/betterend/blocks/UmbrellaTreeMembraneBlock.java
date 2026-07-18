package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourLeaves;
import org.betterx.bclib.client.render.BCLRenderLayer;
import org.betterx.bclib.interfaces.RenderLayerProvider;
import org.betterx.bclib.interfaces.RuntimeBlockModelProvider;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;

import net.minecraft.world.level.block.state.BlockBehaviour;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public class UmbrellaTreeMembraneBlock extends SlimeBlock implements RenderLayerProvider, RuntimeBlockModelProvider, BehaviourLeaves {
    public static final IntegerProperty COLOR = EndBlockProperties.COLOR;
    private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(0);

    public UmbrellaTreeMembraneBlock() {
        super(BlockBehaviour.Properties.ofLegacyCopy(Blocks.SLIME_BLOCK));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        double px = ctx.getClickedPos().getX() * 0.1;
        double py = ctx.getClickedPos().getY() * 0.1;
        double pz = ctx.getClickedPos().getZ() * 0.1;
        return this.defaultBlockState().setValue(COLOR, MHelper.floor(NOISE.eval(px, py, pz) * 3.5 + 4));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(COLOR);
    }

    @Override
    public BCLRenderLayer getRenderLayer() {
        return BCLRenderLayer.TRANSLUCENT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (state.getValue(COLOR) > 0) {
            return Lists.newArrayList(new ItemStack(this));
        } else {
            return MHelper.RANDOM.nextInt(4) == 0
                    ? Lists.newArrayList(new ItemStack(EndBlocks.UMBRELLA_TREE_SAPLING))
                    : Collections
                            .emptyList();
        }
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state) {
        return state.getValue(COLOR) > 0;
    }

    public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
        if (state.getValue(COLOR) > 0) {
            return super.skipRendering(state, stateFrom, direction);
        } else {
            return false;
        }
    }

    @Override
    public Object getItemModel(Identifier resourceLocation) {
        return getBlockModel(resourceLocation, defaultBlockState());
    }
}

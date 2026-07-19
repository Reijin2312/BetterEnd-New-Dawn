package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourIce;
import org.betterx.bclib.client.render.BCLRenderLayer;
import org.betterx.bclib.interfaces.RenderLayerProvider;
import org.betterx.bclib.interfaces.RuntimeBlockModelProvider;
import org.betterx.wover.enchantment.api.EnchantmentUtils;
import org.betterx.wover.loot.api.BlockLootProvider;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

import net.minecraft.world.level.block.state.BlockBehaviour;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmeraldIceBlock extends HalfTransparentBlock implements RenderLayerProvider, RuntimeBlockModelProvider, BehaviourIce, BlockLootProvider {
    public EmeraldIceBlock() {
        super(BlockBehaviour.Properties.ofLegacyCopy(Blocks.ICE));
    }

    @Override
    public BCLRenderLayer getRenderLayer() {
        return BCLRenderLayer.TRANSLUCENT;
    }

    @Override
    public void playerDestroy(
            Level world,
            Player player,
            BlockPos pos,
            BlockState state,
            @Nullable BlockEntity blockEntity,
            ItemStack stack
    ) {
        super.playerDestroy(world, player, pos, state, blockEntity, stack);
        if (EnchantmentUtils.getItemEnchantmentLevel(world, Enchantments.SILK_TOUCH, stack) == 0) {
            if (world.dimension() == Level.NETHER) {
                world.removeBlock(pos, false);
                return;
            }

            BlockState belowState = world.getBlockState(pos.below());
            if (belowState.blocksMotion() || belowState.liquid()) {
                world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
            }
        }

    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (world.getBrightness(LightLayer.BLOCK, pos) > 11 - state.getLightDampening()) {
            this.melt(state, world, pos);
        }

    }

    protected void melt(BlockState state, Level world, BlockPos pos) {
        if (world.dimension() == Level.NETHER) {
            world.removeBlock(pos, false);
        } else {
            world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
            world.neighborChanged(pos, Blocks.WATER, null);
        }
    }

    @Override
    public Object getItemModel(Identifier resourceLocation) {
        return getBlockModel(resourceLocation, defaultBlockState());
    }

    @Override
    public LootTable.Builder registerBlockLoot(
            @NotNull Identifier location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        return provider.dropWithSilkTouch(this);
    }
}

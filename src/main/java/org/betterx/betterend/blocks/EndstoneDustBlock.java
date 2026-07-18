package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourSand;
import org.betterx.ui.ColorUtil;
import org.betterx.wover.block.api.BlockTagProvider;
import org.betterx.wover.loot.api.BlockLootProvider;
import org.betterx.wover.loot.api.LootLookupProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

import net.minecraft.world.level.block.state.BlockBehaviour;

import org.jetbrains.annotations.NotNull;

public class EndstoneDustBlock extends FallingBlock implements BlockTagProvider, BehaviourSand, BlockLootProvider {
    private static final int COLOR = ColorUtil.color(226, 239, 168);

    public static final MapCodec<EndstoneDustBlock> CODEC = MapCodec.unit(EndstoneDustBlock::new);

    public EndstoneDustBlock() {
        super(BlockBehaviour.Properties
                .ofLegacyCopy(Blocks.SAND)
                .mapColor(Blocks.END_STONE.defaultMapColor())
        );
    }

    @Override
    protected MapCodec<? extends FallingBlock> codec() {
        return CODEC;
    }

    @Override
    public LootTable.Builder registerBlockLoot(
            @NotNull Identifier location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        return provider.drop(this);
    }

    public int getDustColor(BlockState state, BlockGetter world, BlockPos pos) {
        return COLOR;
    }

    @Override
    public void registerBlockTags(Identifier location, TagBootstrapContext<Block> context) {
        context.add(this, CommonBlockTags.END_STONES);
    }
}

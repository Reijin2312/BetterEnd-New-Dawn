package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.behaviours.interfaces.BehaviourStone;
import org.betterx.bclib.client.models.BCLModels;
import org.betterx.bclib.interfaces.BlockColorProvider;
import org.betterx.bclib.interfaces.CustomColorProvider;
import org.betterx.bclib.interfaces.ItemColorProvider;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.block.api.model.DatagenModelDispatch;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


import org.jetbrains.annotations.NotNull;

public class StoneLanternBlock extends EndLanternBlock implements CustomColorProvider, BehaviourStone {
    private static final VoxelShape SHAPE_CEIL = box(3, 1, 3, 13, 16, 13);
    private static final VoxelShape SHAPE_FLOOR = box(3, 0, 3, 13, 15, 13);
    private final Block baseBlock;

    public StoneLanternBlock(Block source) {
        super(BlockBehaviour.Properties.ofLegacyCopy(source).lightLevel((bs) -> 15));
        this.baseBlock = source;
    }

    @Override
    public BlockColorProvider getProvider() {
        return ((CustomColorProvider) EndBlocks.AURORA_CRYSTAL).getProvider();
    }

    @Override
    public ItemColorProvider getItemProvider() {
        return ((CustomColorProvider) EndBlocks.AURORA_CRYSTAL).getItemProvider();
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return state.getValue(IS_FLOOR) ? SHAPE_FLOOR : SHAPE_CEIL;
    }

    @Override
    public void provideBlockModels(Object modelGenerator) {
        WoverBlockModelGenerators generator = (WoverBlockModelGenerators) modelGenerator;
        //get id of this block from registry
        final var id = BuiltInRegistries.BLOCK.getKey(this);
        final var mapping = new TextureMapping()
                .put(BCLModels.GLASS, TextureMapping.getBlockTexture(EndBlocks.AURORA_CRYSTAL))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(this, "_top"))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(this, "_side"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(this, "_bottom"));

        final var floorModel = BCLModels.STONE_LANTERN_FLOOR.createWithSuffix(this, "_floor", mapping, generator.modelOutput());
        final var ceilModel = BCLModels.STONE_LANTERN_CEIL.create(this, mapping, generator.modelOutput());

        final Object properties = DatagenModelDispatch.propertyDispatchInitial(IS_FLOOR);
        DatagenModelDispatch.propertyDispatchSelect(properties, true, BlockModelGenerators.plainVariant(floorModel));
        DatagenModelDispatch.propertyDispatchSelect(properties, false, BlockModelGenerators.plainVariant(ceilModel));
        generator.acceptBlockState(DatagenModelDispatch.dispatchWith(this, properties));
    }
}

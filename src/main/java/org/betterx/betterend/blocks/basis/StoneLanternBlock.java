package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.behaviours.interfaces.BehaviourStone;
import org.betterx.bclib.client.models.BCLModels;
import org.betterx.bclib.interfaces.CustomColorProvider;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;

public class StoneLanternBlock extends EndLanternBlock implements CustomColorProvider, BehaviourStone {
    private static final VoxelShape SHAPE_CEIL = box(3, 1, 3, 13, 16, 13);
    private static final VoxelShape SHAPE_FLOOR = box(3, 0, 3, 13, 15, 13);
    private final Block baseBlock;

    public StoneLanternBlock(Block source) {
        super(BlockBehaviour.Properties.ofFullCopy(source).lightLevel((bs) -> 15));
        this.baseBlock = source;
    }

    @Override
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public BlockColor getProvider() {
        return ((CustomColorProvider) EndBlocks.AURORA_CRYSTAL).getProvider();
    }

    @Override
    public ItemColor getItemProvider() {
        return ((CustomColorProvider) EndBlocks.AURORA_CRYSTAL).getItemProvider();
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return state.getValue(IS_FLOOR) ? SHAPE_FLOOR : SHAPE_CEIL;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void provideBlockModels(WoverBlockModelGenerators generator) {
        //get id of this block from registry
        final var id = BuiltInRegistries.BLOCK.getKey(this);
        final boolean isVanilla = id.getNamespace().equals("minecraft");
        final var mapping = new TextureMapping()
                .put(BCLModels.GLASS, TextureMapping.getBlockTexture(EndBlocks.AURORA_CRYSTAL))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(this, "_top"))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(this, "_side"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(this, "_bottom"));

        final var floorModel = BCLModels.STONE_LANTERN_FLOOR.createWithSuffix(this, "_floor", mapping, generator.modelOutput());
        final var ceilModel = BCLModels.STONE_LANTERN_CEIL.create(this, mapping, generator.modelOutput());

        generator.acceptBlockState(MultiVariantGenerator
                .multiVariant(this)
                .with(PropertyDispatch
                        .property(IS_FLOOR)
                        .select(true, Variant.variant().with(VariantProperties.MODEL, floorModel))
                        .select(false, Variant.variant().with(VariantProperties.MODEL, ceilModel))));
    }
}

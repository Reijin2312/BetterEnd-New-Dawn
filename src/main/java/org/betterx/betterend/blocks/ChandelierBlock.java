package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.BaseAttachedBlock;
import org.betterx.bclib.client.render.BCLRenderLayer;
import org.betterx.bclib.interfaces.RenderLayerProvider;
import org.betterx.betterend.client.models.EndModels;
import org.betterx.wover.block.api.model.BlockModelProvider;
import org.betterx.wover.block.api.model.DatagenModelDispatch;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;

import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraft.world.level.block.state.BlockBehaviour;

import com.google.common.collect.Maps;

import java.util.EnumMap;

public class ChandelierBlock extends BaseAttachedBlock.Metal implements RenderLayerProvider, BlockModelProvider {
    private static final EnumMap<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap(Direction.class);

    public ChandelierBlock(Block source) {
        super(BlockBehaviour.Properties.ofLegacyCopy(source)
                                 .lightLevel((bs) -> 15)
                                 .noCollision()
                                 .noOcclusion()
                                 .requiresCorrectToolForDrops());
    }

    @Override
    public BCLRenderLayer getRenderLayer() {
        return BCLRenderLayer.CUTOUT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return BOUNDING_SHAPES.get(state.getValue(FACING));
    }

    @Override
    public void provideBlockModels(Object modelGenerator) {
        WoverBlockModelGenerators generator = (WoverBlockModelGenerators) modelGenerator;
        final var baseTexture = TextureMapping.getBlockTexture(this);
        final var mapping = new TextureMapping()
                .put(EndModels.WALL, baseTexture.withSuffix("_wall"))
                .put(EndModels.FLOOR, baseTexture.withSuffix("_floor"))
                .put(EndModels.CEIL, baseTexture.withSuffix("_ceil"));

        final var modelCeil = EndModels.CHANDELIER_CEIL.createWithSuffix(this, "_ceil", mapping, generator.modelOutput());
        final var modelWall = EndModels.CHANDELIER_WALL.createWithSuffix(this, "_wall", mapping, generator.modelOutput());
        final var modelFloor = EndModels.CHANDELIER_FLOOR.createWithSuffix(this, "_floor", mapping, generator.modelOutput());

        final Object prop = DatagenModelDispatch.propertyDispatchInitial(FACING);
        DatagenModelDispatch.propertyDispatchSelect(prop, Direction.DOWN, BlockModelGenerators.plainVariant(modelCeil));
        DatagenModelDispatch.propertyDispatchSelect(prop, Direction.UP, BlockModelGenerators.plainVariant(modelFloor));
        DatagenModelDispatch.propertyDispatchSelect(prop, Direction.EAST, BlockModelGenerators
                        .plainVariant(modelWall)
                        .with(VariantMutator.Y_ROT.withValue(Quadrant.R270)));
        DatagenModelDispatch.propertyDispatchSelect(prop, Direction.SOUTH, BlockModelGenerators.plainVariant(modelWall));
        DatagenModelDispatch.propertyDispatchSelect(prop, Direction.WEST, BlockModelGenerators
                        .plainVariant(modelWall)
                        .with(VariantMutator.Y_ROT.withValue(Quadrant.R90)));
        DatagenModelDispatch.propertyDispatchSelect(prop, Direction.NORTH, BlockModelGenerators
                        .plainVariant(modelWall)
                        .with(VariantMutator.Y_ROT.withValue(Quadrant.R180)));

        generator.acceptBlockState(DatagenModelDispatch.dispatchWith(this, prop));
        generator.delegateItemModel(this, modelCeil);
    }

    static {
        BOUNDING_SHAPES.put(Direction.UP, Block.box(5, 0, 5, 11, 13, 11));
        BOUNDING_SHAPES.put(Direction.DOWN, Block.box(5, 3, 5, 11, 16, 11));
        BOUNDING_SHAPES.put(Direction.NORTH, Shapes.box(0.0, 0.0, 0.5, 1.0, 1.0, 1.0));
        BOUNDING_SHAPES.put(Direction.SOUTH, Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 0.5));
        BOUNDING_SHAPES.put(Direction.WEST, Shapes.box(0.5, 0.0, 0.0, 1.0, 1.0, 1.0));
        BOUNDING_SHAPES.put(Direction.EAST, Shapes.box(0.0, 0.0, 0.0, 0.5, 1.0, 1.0));
    }
}

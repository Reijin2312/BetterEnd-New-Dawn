package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourMetal;
import org.betterx.bclib.client.models.BCLModels;
import org.betterx.bclib.client.render.BCLRenderLayer;
import org.betterx.bclib.interfaces.RenderLayerProvider;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.basis.EndLanternBlock;
import org.betterx.wover.block.api.model.DatagenModelDispatch;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class BulbVineLanternBlock extends EndLanternBlock implements RenderLayerProvider, BehaviourMetal {
    private static final VoxelShape SHAPE_CEIL = Block.box(4, 4, 4, 12, 16, 12);
    private static final VoxelShape SHAPE_FLOOR = Block.box(4, 0, 4, 12, 12, 12);

    public BulbVineLanternBlock() {
        this(BehaviourBuilders.createMetal(MapColor.COLOR_LIGHT_GRAY)
                              .strength(1)
                              .lightLevel((bs) -> 15)
                              .requiresCorrectToolForDrops()
                              .sound(SoundType.LANTERN));
    }

    public BulbVineLanternBlock(Properties settings) {
        super(settings);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return state.getValue(IS_FLOOR) ? SHAPE_FLOOR : SHAPE_CEIL;
    }

    @Override
    public BCLRenderLayer getRenderLayer() {
        return BCLRenderLayer.CUTOUT;
    }

    protected String getMetalTexture(Identifier blockId) {
        String name = blockId.getPath();
        name = name.substring(0, name.indexOf('_'));
        return name + "_bulb_vine_lantern_metal";
    }

    protected String getGlowTexture() {
        return "bulb_vine_lantern_bulb";
    }

    @Override
    public void provideBlockModels(Object modelGenerator) {
        WoverBlockModelGenerators generator = (WoverBlockModelGenerators) modelGenerator;
        //get id of this block from registry
        final var id = BuiltInRegistries.BLOCK.getKey(this);

        final var mapping = new TextureMapping()
                .put(BCLModels.GLOW, BetterEnd.C.mk("bulb_vine_lantern_bulb").withPrefix("block/"))
                .put(BCLModels.METAL, BetterEnd.C.mk(getMetalTexture(id)).withPrefix("block/"));

        final var floorModel = BCLModels.BULB_LANTERN_FLOOR.createWithSuffix(this, "_floor", mapping, generator.modelOutput());
        final var ceilModel = BCLModels.BULB_LANTERN_CEIL.create(this, mapping, generator.modelOutput());

        final Object properties = DatagenModelDispatch.propertyDispatchInitial(IS_FLOOR);
        DatagenModelDispatch.propertyDispatchSelect(properties, true, BlockModelGenerators.plainVariant(floorModel));
        DatagenModelDispatch.propertyDispatchSelect(properties, false, BlockModelGenerators.plainVariant(ceilModel));
        generator.acceptBlockState(DatagenModelDispatch.dispatchWith(this, properties));
    }
}

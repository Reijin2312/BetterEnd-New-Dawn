package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.blocks.BaseAttachedBlock;
import org.betterx.bclib.client.render.BCLRenderLayer;
import org.betterx.bclib.interfaces.RenderLayerProvider;
import org.betterx.bclib.interfaces.tools.AddMineablePickaxe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.google.common.collect.Maps;

import java.util.EnumMap;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class SmaragdantCrystalShardBlock extends BaseAttachedBlock implements AddMineablePickaxe, RenderLayerProvider, SimpleWaterloggedBlock, LiquidBlockContainer {
    private static final EnumMap<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap(Direction.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public SmaragdantCrystalShardBlock() {
        super(BehaviourBuilders.createStone(MapColor.COLOR_GREEN)
                               .lightLevel((bs) -> 15)
                               .sound(SoundType.AMETHYST_CLUSTER)
                               .requiresCorrectToolForDrops()
                               .noCollision()
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        super.createBlockStateDefinition(stateManager);
        stateManager.add(WATERLOGGED);
    }

    @Override
    public BCLRenderLayer getRenderLayer() {
        return BCLRenderLayer.CUTOUT;
    }

    @Override
    public boolean canPlaceLiquid(
            @Nullable LivingEntity player,
            BlockGetter blockGetter,
            BlockPos blockPos,
            BlockState blockState,
            Fluid fluid
    ) {
        return !blockState.getValue(WATERLOGGED);
    }

    @Override
    public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState) {
        return !state.getValue(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        if (state != null) {
            LevelReader worldView = ctx.getLevel();
            BlockPos blockPos = ctx.getClickedPos();
            boolean water = worldView.getFluidState(blockPos).getType() == Fluids.WATER;
            return state.setValue(WATERLOGGED, water);
        }
        return null;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return BOUNDING_SHAPES.get(state.getValue(FACING));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos blockPos = pos.relative(direction.getOpposite());
        return world.getBlockState(blockPos).isFaceSturdy(world, blockPos, direction);
    }

    static {
        BOUNDING_SHAPES.put(Direction.UP, Shapes.box(0.125, 0.0, 0.125, 0.875F, 0.875F, 0.875F));
        BOUNDING_SHAPES.put(Direction.DOWN, Shapes.box(0.125, 0.125, 0.125, 0.875F, 1.0, 0.875F));
        BOUNDING_SHAPES.put(Direction.NORTH, Shapes.box(0.125, 0.125, 0.125, 0.875F, 0.875F, 1.0));
        BOUNDING_SHAPES.put(Direction.SOUTH, Shapes.box(0.125, 0.125, 0.0, 0.875F, 0.875F, 0.875F));
        BOUNDING_SHAPES.put(Direction.WEST, Shapes.box(0.125, 0.125, 0.125, 1.0, 0.875F, 0.875F));
        BOUNDING_SHAPES.put(Direction.EAST, Shapes.box(0.0, 0.125, 0.125, 0.875F, 0.875F, 0.875F));
    }
}

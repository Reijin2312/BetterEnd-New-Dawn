package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.blocks.BaseAttachedBlock;
import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.client.render.BCLRenderLayer;
import org.betterx.bclib.interfaces.RenderLayerProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.google.common.collect.Maps;

import java.util.EnumMap;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.level.material.MapColor;

@SuppressWarnings("deprecation")
public class FurBlock extends BaseAttachedBlock implements SimpleWaterloggedBlock, RenderLayerProvider {
    private static final EnumMap<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap(Direction.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public FurBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    /** Compatibility constructors retained for the older BetterEnd block registry. */
    public FurBlock(MapColor color, Block sapling, int dropChance) {
        this(color, sapling, 0, dropChance, false);
    }

    public FurBlock(MapColor color, Block sapling, int light, int dropChance, boolean wet) {
        this(BehaviourBuilders.createPlant(color)
                .replaceable()
                .lightLevel(state -> light)
                .ignitedByLava()
                .sound(wet ? SoundType.WET_GRASS : SoundType.GRASS));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        // Keep FACING (added by BaseAttachedBlock) and add WATERLOGGED so submerged fur/outer-leaves can be flooded.
        super.createBlockStateDefinition(stateManager);
        stateManager.add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        // Reuse BaseAttachedBlock's FACING/attachment selection, then waterlog if placed into water.
        BlockState state = super.getStateForPlacement(ctx);
        if (state != null) {
            boolean water = ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER;
            return state.setValue(WATERLOGGED, water);
        }
        return null;
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @NotNull BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction neighborDirection,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource randomSource
    ) {
        boolean water = state.getValue(WATERLOGGED);
        if (water) {
            scheduledTickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        // Preserve BaseAttachedBlock's attachment/survival behavior; when it can no longer survive, leave the
        // water behind if this block was waterlogged instead of stranding a dry air pocket.
        if (!canSurvive(state, level, pos)) {
            return water ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return BOUNDING_SHAPES.get(state.getValue(FACING));
    }

    @Override
    public BCLRenderLayer getRenderLayer() {
        return BCLRenderLayer.CUTOUT;
    }

    static {
        BOUNDING_SHAPES.put(Direction.UP, Shapes.box(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
        BOUNDING_SHAPES.put(Direction.DOWN, Shapes.box(0.0, 0.5, 0.0, 1.0, 1.0, 1.0));
        BOUNDING_SHAPES.put(Direction.NORTH, Shapes.box(0.0, 0.0, 0.5, 1.0, 1.0, 1.0));
        BOUNDING_SHAPES.put(Direction.SOUTH, Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 0.5));
        BOUNDING_SHAPES.put(Direction.WEST, Shapes.box(0.5, 0.0, 0.0, 1.0, 1.0, 1.0));
        BOUNDING_SHAPES.put(Direction.EAST, Shapes.box(0.0, 0.0, 0.0, 0.5, 1.0, 1.0));
    }

}

package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.BasePathBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class EndPathBlock extends BasePathBlock.Stone {
    private final Block source;
    public EndPathBlock(Block source) { super(source); this.source = source; }
    private BlockState revertedState() { return source.defaultBlockState(); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        return !defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos())
                ? Block.pushEntitiesUp(defaultBlockState(), revertedState(), context.getLevel(), context.getClickedPos())
                : super.getStateForPlacement(context);
    }
    @Override public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTicks, BlockPos pos, Direction direction, BlockPos neighbourPos, BlockState neighbour, RandomSource random) {
        if (direction == Direction.UP && !state.canSurvive(level, pos)) scheduledTicks.scheduleTick(pos, this, 1);
        return super.updateShape(state, level, scheduledTicks, pos, direction, neighbourPos, neighbour, random);
    }
    @Override public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState reverted = Block.pushEntitiesUp(state, revertedState(), level, pos);
        level.setBlockAndUpdate(pos, reverted);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(null, reverted));
    }
}

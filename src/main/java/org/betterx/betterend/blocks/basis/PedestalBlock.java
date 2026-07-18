package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.blocks.BaseBlockNotFull;
import org.betterx.betterend.blocks.EndBlockProperties;
import org.betterx.betterend.blocks.EndBlockProperties.PedestalState;
import org.betterx.betterend.blocks.InfusionPedestal;
import org.betterx.betterend.blocks.entities.InfusionPedestalEntity;
import org.betterx.betterend.blocks.entities.PedestalBlockEntity;
import org.betterx.betterend.client.models.EndModels;
import org.betterx.betterend.registry.EndTags;
import org.betterx.betterend.rituals.InfusionRitual;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.block.api.BlockTagProvider;
import org.betterx.wover.block.api.model.BlockModelProvider;
import org.betterx.wover.block.api.model.DatagenModelDispatch;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.RandomSource;


import com.google.common.collect.Lists;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PedestalBlock extends BaseBlockNotFull implements EntityBlock, BlockTagProvider, BlockModelProvider {
    public final static EnumProperty<PedestalState> STATE = EndBlockProperties.PEDESTAL_STATE;
    public static final BooleanProperty HAS_ITEM = EndBlockProperties.HAS_ITEM;
    public static final BooleanProperty HAS_LIGHT = BlockProperties.HAS_LIGHT;

    private static final VoxelShape SHAPE_DEFAULT;
    private static final VoxelShape SHAPE_COLUMN;
    private static final VoxelShape SHAPE_PILLAR;
    private static final VoxelShape SHAPE_PEDESTAL_TOP;
    private static final VoxelShape SHAPE_COLUMN_TOP;
    private static final VoxelShape SHAPE_BOTTOM;

    protected final Block parent;
    protected float height = 1.0F;

    public PedestalBlock(Block parent) {
        super(BlockBehaviour.Properties.ofLegacyCopy(parent).lightLevel(getLuminance(parent.defaultBlockState())));
        this.registerDefaultState(
                stateDefinition
                        .any()
                        .setValue(STATE, PedestalState.DEFAULT)
                        .setValue(HAS_ITEM, false)
                        .setValue(HAS_LIGHT, false)
        );
        this.parent = parent;
    }

    private static ToIntFunction<BlockState> getLuminance(BlockState parent) {
        final int light = parent.getLightEmission();
        if (light > 0) {
            return state -> light;
        }
        return state -> state.getValue(HAS_LIGHT) ? 12 : 0;
    }

    public float getHeight(BlockState state) {
        if (state.getBlock() instanceof PedestalBlock && state.getValue(STATE) == PedestalState.PEDESTAL_TOP) {
            return this.height - 0.2F;
        }
        return this.height;
    }

    @Override
    public @NotNull InteractionResult useItemOn(
            ItemStack itemStack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (!state.is(this) || !isPlaceable(state)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof PedestalBlockEntity pedestal) {
            if (pedestal.isEmpty()) {
                if (itemStack.isEmpty()) return InteractionResult.CONSUME;
                pedestal.setItem(0, itemStack);
                level.blockEntityChanged(pos);
                checkRitual(level, player, pos);
                return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            } else {
                ItemStack stack = pedestal.getItem(0);
                if (player.addItem(stack)) {
                    pedestal.removeItemNoUpdate(0);
                    level.blockEntityChanged(pos);
                    checkRitual(level, player, pos);
                    return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
                }
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    public void destroy(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        MutableBlockPos posMutable = new MutableBlockPos();
        for (Point point : InfusionRitual.getMap()) {
            posMutable.set(blockPos).move(point.x, 0, point.y);
            BlockState state = levelAccessor.getBlockState(posMutable);
            if (state.getBlock() instanceof InfusionPedestal) {
                BlockEntity blockEntity = levelAccessor.getBlockEntity(posMutable);
                if (blockEntity instanceof InfusionPedestalEntity pedestal) {
                    if (pedestal.hasRitual()) {
                        pedestal.getRitual().setDirty();
                    }
                }
                break;
            }
        }
    }

    public void checkRitual(Level world, Player player, BlockPos pos) {
        MutableBlockPos posMutable = new MutableBlockPos();
        for (Point point : InfusionRitual.getMap()) {
            posMutable.set(pos).move(point.x, 0, point.y);
            BlockState state = world.getBlockState(posMutable);
            if (state.getBlock() instanceof InfusionPedestal) {
                ((InfusionPedestal) state.getBlock()).checkRitual(world, player, posMutable);
                break;
            }
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState upState = world.getBlockState(pos.above());
        BlockState downState = world.getBlockState(pos.below());
        boolean upSideSolid = upState.isFaceSturdy(world, pos.above(), Direction.DOWN) || upState.is(BlockTags.WALLS);
        boolean hasPedestalOver = upState.getBlock() instanceof PedestalBlock;
        boolean hasPedestalUnder = downState.getBlock() instanceof PedestalBlock;
        if (!hasPedestalOver && hasPedestalUnder && upSideSolid) {
            return defaultBlockState().setValue(STATE, PedestalState.COLUMN_TOP);
        } else if (!hasPedestalOver && !hasPedestalUnder && upSideSolid) {
            return defaultBlockState().setValue(STATE, PedestalState.COLUMN);
        } else if (hasPedestalUnder && hasPedestalOver) {
            return defaultBlockState().setValue(STATE, PedestalState.PILLAR);
        } else if (hasPedestalUnder) {
            return defaultBlockState().setValue(STATE, PedestalState.PEDESTAL_TOP);
        } else if (hasPedestalOver) {
            return defaultBlockState().setValue(STATE, PedestalState.BOTTOM);
        }
        return defaultBlockState();
    }

    @Override
    public @NotNull BlockState updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos posFrom,
            BlockState newState,
            RandomSource random
    ) {
        BlockState updated = getUpdatedState(state, world, scheduledTickAccess, pos, direction, posFrom, newState, random);
        if (!updated.is(this)) return updated;
        if (!isPlaceable(updated) && world instanceof LevelAccessor levelAccessor) {
            moveStoredStack(levelAccessor, updated, pos);
        }
        return updated;
    }

    private BlockState getUpdatedState(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos posFrom,
            BlockState newState,
            RandomSource random
    ) {
        if (!state.is(this)) return state.updateShape(world, scheduledTickAccess, pos, direction, posFrom, newState, random);
        if (direction != Direction.UP && direction != Direction.DOWN) return state;
        BlockState upState = world.getBlockState(pos.above());
        BlockState downState = world.getBlockState(pos.below());
        boolean upSideSolid = upState.isFaceSturdy(world, pos.above(), Direction.DOWN) || upState.is(BlockTags.WALLS);
        boolean hasPedestalOver = upState.getBlock() instanceof PedestalBlock;
        boolean hasPedestalUnder = downState.getBlock() instanceof PedestalBlock;
        if (direction == Direction.UP) {
            upSideSolid = newState.isFaceSturdy(world, posFrom, Direction.DOWN) || newState.is(BlockTags.WALLS);
            hasPedestalOver = newState.getBlock() instanceof PedestalBlock;
        } else {
            hasPedestalUnder = newState.getBlock() instanceof PedestalBlock;
        }
        BlockState updatedState;
        if (!hasPedestalOver && hasPedestalUnder && upSideSolid) {
            updatedState = state.setValue(STATE, PedestalState.COLUMN_TOP);
        } else if (!hasPedestalOver && !hasPedestalUnder && upSideSolid) {
            updatedState = state.setValue(STATE, PedestalState.COLUMN);
        } else if (hasPedestalUnder && hasPedestalOver) {
            updatedState = state.setValue(STATE, PedestalState.PILLAR);
        } else if (hasPedestalUnder) {
            updatedState = state.setValue(STATE, PedestalState.PEDESTAL_TOP);
        } else if (hasPedestalOver) {
            updatedState = state.setValue(STATE, PedestalState.BOTTOM);
        } else {
            updatedState = state.setValue(STATE, PedestalState.DEFAULT);
        }
        if (!isPlaceable(updatedState)) {
            updatedState = updatedState.setValue(HAS_ITEM, false).setValue(HAS_LIGHT, false);
        }
        return updatedState;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
        List<ItemStack> drop = Lists.newArrayList(super.getDrops(state, builder));
        if (state.is(this)) {
            if (isPlaceable(state)) {
                BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
                if (blockEntity instanceof PedestalBlockEntity pedestal) {
                    if (!pedestal.isEmpty()) {
                        drop.add(pedestal.getItem(0));
                    }
                }
            } else {
                return drop;
            }
        }
        return drop;
    }

    private void moveStoredStack(LevelAccessor world, BlockState state, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof PedestalBlockEntity pedestal && state.is(this)) {
            ItemStack stack = pedestal.removeItemNoUpdate(0);
            if (!stack.isEmpty()) {
                moveStoredStack(blockEntity, world, stack, pos.above());
            }
        }
    }

    private void moveStoredStack(BlockEntity blockEntity, LevelAccessor world, ItemStack stack, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (!state.is(this)) {
            dropStoredStack(blockEntity, stack, pos);
        } else if (state.getValue(STATE).equals(PedestalState.PILLAR)) {
            moveStoredStack(blockEntity, world, stack, pos.above());
        } else if (!isPlaceable(state)) {
            dropStoredStack(blockEntity, stack, pos);
        } else if (blockEntity instanceof PedestalBlockEntity pedestal) {
            if (pedestal.isEmpty()) {
                pedestal.setItem(0, stack);
            } else {
                dropStoredStack(blockEntity, stack, pos);
            }
        } else {
            dropStoredStack(blockEntity, stack, pos);
        }
    }

    private void dropStoredStack(BlockEntity blockEntity, ItemStack stack, BlockPos pos) {
        if (blockEntity != null && blockEntity.getLevel() != null) {
            Level world = blockEntity.getLevel();
            Block.popResource(world, getDropPos(world, pos), stack);
        }
    }

    private BlockPos getDropPos(LevelAccessor world, BlockPos pos) {
        BlockPos dropPos;
        if (world.getBlockState(pos).isAir()) {
            return pos;
        }
        if (world.getBlockState(pos.above()).isAir()) {
            return pos.above();
        }
        for (int i = 2; i < Direction.values().length; i++) {
            dropPos = pos.relative(Direction.from3DDataValue(i));
            if (world.getBlockState(dropPos).isAir()) {
                return dropPos.immutable();
            }
        }
        return getDropPos(world, pos.above());
    }

    public boolean isPlaceable(BlockState state) {
        if (!state.is(this)) return false;
        PedestalState currentState = state.getValue(STATE);
        return currentState == PedestalState.DEFAULT || currentState == PedestalState.PEDESTAL_TOP;
    }

    @Override
    public @NotNull VoxelShape getShape(
            BlockState state,
            @NotNull BlockGetter world,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        if (state.is(this)) {
            return switch (state.getValue(STATE)) {
                case BOTTOM -> SHAPE_BOTTOM;
                case PEDESTAL_TOP -> SHAPE_PEDESTAL_TOP;
                case COLUMN_TOP -> SHAPE_COLUMN_TOP;
                case PILLAR -> SHAPE_PILLAR;
                case COLUMN -> SHAPE_COLUMN;
                default -> SHAPE_DEFAULT;
            };
        }
        return super.getShape(state, world, pos, context);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(STATE, HAS_ITEM, HAS_LIGHT);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new PedestalBlockEntity(blockPos, blockState);
    }

    public boolean hasUniqueEntity() {
        return false;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return state.getBlock() instanceof PedestalBlock;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, @NotNull Level world, @NotNull BlockPos pos, Direction direction) {
        return state.getValue(HAS_ITEM) ? 15 : 0;
    }
    private static Map<PedestalState, ModelTemplate> pedestalModels() {
        return Map.of(
                PedestalState.DEFAULT, EndModels.PEDESTAL_DEFAULT,
                PedestalState.PEDESTAL_TOP, EndModels.PEDESTAL_TOP,
                PedestalState.COLUMN_TOP, EndModels.PEDESTAL_COLUMN_TOP,
                PedestalState.COLUMN, EndModels.PEDESTAL_COLUMN,
                PedestalState.BOTTOM, EndModels.PEDESTAL_BOTTOM,
                PedestalState.PILLAR, EndModels.PEDESTAL_PILLAR
        );
    }

    @Override
    public void provideBlockModels(Object modelGenerator) {
        WoverBlockModelGenerators generator = (WoverBlockModelGenerators) modelGenerator;
        provideBlockModel(generator, createTextureMapping(), this);
    }

    public static void provideBlockModel(
            WoverBlockModelGenerators generator,
            TextureMapping mapping,
            Block pedestalBlock
    ) {
        provideBlockModel(generator, mapping, pedestalBlock, pedestalModels());
    }

    public static void provideBlockModel(
            WoverBlockModelGenerators generator,
            TextureMapping mapping,
            Block pedestalBlock,
            Map<PedestalState, ModelTemplate> pdestalModels
    ) {
        final Identifier id = TextureMapping.getBlockTexture(pedestalBlock);
        final Object properties = DatagenModelDispatch.propertyDispatchInitial(STATE);

        for (var entry : pdestalModels.entrySet()) {
            final String suffix = "_" + entry.getKey();
            Identifier model = entry
                    .getValue()
                    .createWithSuffix(pedestalBlock, suffix, mapping, generator.modelOutput());
            DatagenModelDispatch.propertyDispatchSelect(properties, entry.getKey(), BlockModelGenerators.plainVariant(model));
        }

        generator.acceptBlockState(DatagenModelDispatch.dispatchWith(pedestalBlock, properties));
        generator.delegateItemModel(pedestalBlock, id.withSuffix("_default"));
    }

    protected TextureMapping createTextureMapping() {
        final var parentTexture = TextureMapping.getBlockTexture(parent);
        return new TextureMapping()
                .put(TextureSlot.TOP, parentTexture.withSuffix("_top"))
                .put(TextureSlot.BOTTOM, parentTexture.withSuffix("_bottom"))
                .put(EndModels.BASE, parentTexture.withSuffix("_base"))
                .put(EndModels.PILLAR, parentTexture.withSuffix("_pillar"));
    }

    static {
        VoxelShape basinUp = Block.box(2, 3, 2, 14, 4, 14);
        VoxelShape basinDown = Block.box(0, 0, 0, 16, 3, 16);
        VoxelShape columnTopUp = Block.box(1, 14, 1, 15, 16, 15);
        VoxelShape columnTopDown = Block.box(2, 13, 2, 14, 14, 14);
        VoxelShape pedestalTop = Block.box(1, 8, 1, 15, 10, 15);
        VoxelShape pedestalDefault = Block.box(1, 12, 1, 15, 14, 15);
        VoxelShape pillar = Block.box(3, 0, 3, 13, 8, 13);
        VoxelShape pillarDefault = Block.box(3, 0, 3, 13, 12, 13);
        VoxelShape columnTop = Shapes.or(columnTopDown, columnTopUp);
        VoxelShape basin = Shapes.or(basinDown, basinUp);
        SHAPE_PILLAR = Block.box(3, 0, 3, 13, 16, 13);
        SHAPE_DEFAULT = Shapes.or(basin, pillarDefault, pedestalDefault);
        SHAPE_PEDESTAL_TOP = Shapes.or(pillar, pedestalTop);
        SHAPE_COLUMN_TOP = Shapes.or(SHAPE_PILLAR, columnTop);
        SHAPE_COLUMN = Shapes.or(basin, SHAPE_PILLAR, columnTop);
        SHAPE_BOTTOM = Shapes.or(basin, SHAPE_PILLAR);
    }

    @Override
    public void registerBlockTags(Identifier location, TagBootstrapContext<Block> context) {
        context.add(EndTags.PEDESTALS, this);
    }
}

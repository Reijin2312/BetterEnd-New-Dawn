package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.blocks.BaseBlockNotFull;
import org.betterx.bclib.client.render.BCLRenderLayer;
import org.betterx.bclib.interfaces.RenderLayerProvider;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.wover.loot.api.LootLookupProvider;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class LumecornBlock extends BaseBlockNotFull.Wood implements RenderLayerProvider {
    public static final EnumProperty<EndBlockProperties.LumecornShape> SHAPE = EnumProperty.create(
            "shape",
            EndBlockProperties.LumecornShape.class
    );
    private static final VoxelShape SHAPE_BOTTOM = Block.box(6, 0, 6, 10, 16, 10);
    private static final VoxelShape SHAPE_TOP = Block.box(6, 0, 6, 10, 8, 10);

    public LumecornBlock() {
        super(BehaviourBuilders
                .createWood()
                .strength(0.5F)
                .lightLevel(state -> state.getValue(SHAPE).getLight())
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SHAPE);
    }

    @Override
    public BCLRenderLayer getRenderLayer() {
        return BCLRenderLayer.CUTOUT;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return state.getValue(SHAPE) == EndBlockProperties.LumecornShape.LIGHT_TOP ? SHAPE_TOP : SHAPE_BOTTOM;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        EndBlockProperties.LumecornShape shape = state.getValue(SHAPE);
        if (shape == EndBlockProperties.LumecornShape.BOTTOM_BIG || shape == EndBlockProperties.LumecornShape.BOTTOM_SMALL) {
            return world.getBlockState(pos.below()).is(CommonBlockTags.END_STONES);
        } else if (shape == EndBlockProperties.LumecornShape.LIGHT_TOP) {
            return world.getBlockState(pos.below()).is(this);
        } else {
            return world.getBlockState(pos.below()).is(this) && world.getBlockState(pos.above()).is(this);
        }
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction facing,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        if (!canSurvive(state, world, pos)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            return state;
        }
    }

    @Override
    public LootTable.Builder registerBlockLoot(
            @NotNull Identifier location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        final LootItemCondition.Builder seedBearing =
                shapeIs(EndBlockProperties.LumecornShape.BOTTOM_BIG)
                        .or(shapeIs(EndBlockProperties.LumecornShape.BOTTOM_SMALL))
                        .or(shapeIs(EndBlockProperties.LumecornShape.MIDDLE));

        return LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(seedBearing)
                        .add(LootItem.lootTableItem(EndBlocks.LUMECORN_SEED)
                                     .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))))
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(seedBearing.invert())
                        .when(LootItemRandomChanceCondition.randomChance(0.5F))
                        .add(LootItem.lootTableItem(EndItems.LUMECORN_ROD)));
    }

    private LootItemCondition.Builder shapeIs(EndBlockProperties.LumecornShape shape) {
        return LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(this)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SHAPE, shape));
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean includeData) {
        EndBlockProperties.LumecornShape shape = blockState.getValue(SHAPE);
        if (shape == EndBlockProperties.LumecornShape.BOTTOM_BIG || shape == EndBlockProperties.LumecornShape.BOTTOM_SMALL || shape == EndBlockProperties.LumecornShape.MIDDLE) {
            return new ItemStack(EndBlocks.LUMECORN_SEED);
        }
        return new ItemStack(EndItems.LUMECORN_ROD);
    }
}

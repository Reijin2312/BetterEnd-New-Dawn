package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.BaseBlockNotFull;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.bclib.client.render.BCLRenderLayer;
import org.betterx.bclib.interfaces.RenderLayerProvider;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraft.world.level.block.state.BlockBehaviour;

import org.jetbrains.annotations.NotNull;

public class CavePumpkinBlock extends BaseBlockNotFull implements RenderLayerProvider {
    public static final BooleanProperty SMALL = BlockProperties.SMALL;
    private static final VoxelShape SHAPE_SMALL;
    private static final VoxelShape SHAPE_BIG;

    public CavePumpkinBlock() {
        super(BlockBehaviour.Properties.ofLegacyCopy(Blocks.PUMPKIN).lightLevel(state -> state.getValue(SMALL) ? 10 : 15));
        registerDefaultState(defaultBlockState().setValue(SMALL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SMALL);
    }

    @Override
    public BCLRenderLayer getRenderLayer() {
        return BCLRenderLayer.CUTOUT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return state.getValue(SMALL) ? SHAPE_SMALL : SHAPE_BIG;
    }

    @Override
    public LootTable.Builder registerBlockLoot(
            @NotNull Identifier location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        final LootItemCondition.Builder small = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(this)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SMALL, true));

        return LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(small)
                        .add(LootItem.lootTableItem(EndBlocks.CAVE_PUMPKIN_SEED)))
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(small.invert())
                        .add(LootItem.lootTableItem(this)));
    }

    static {
        VoxelShape lantern = Block.box(1, 0, 1, 15, 13, 15);
        VoxelShape cap = Block.box(0, 12, 0, 16, 15, 16);
        VoxelShape top = Block.box(5, 15, 5, 11, 16, 11);
        SHAPE_BIG = Shapes.or(lantern, cap, top);

        lantern = Block.box(5, 7, 5, 11, 13, 11);
        cap = Block.box(4, 12, 4, 12, 15, 12);
        top = Block.box(6, 15, 6, 10, 16, 10);
        SHAPE_SMALL = Shapes.or(lantern, cap, top);
    }
}

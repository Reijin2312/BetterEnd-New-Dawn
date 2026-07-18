package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourShearablePlant;
import org.betterx.bclib.blocks.BaseAttachedBlock;
import org.betterx.bclib.client.render.BCLRenderLayer;
import org.betterx.bclib.interfaces.RenderLayerProvider;
import org.betterx.wover.block.api.BlockTagProvider;
import org.betterx.wover.block.api.model.BlockModelProvider;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.loot.api.BlockLootProvider;
import org.betterx.wover.loot.api.LootLookupProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;


import com.google.common.collect.Maps;

import java.util.EnumMap;
import org.jetbrains.annotations.NotNull;

public class FurBlock extends BaseAttachedBlock implements RenderLayerProvider, BehaviourShearablePlant, BlockTagProvider, BlockLootProvider, BlockModelProvider {
    private static final EnumMap<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap(Direction.class);
    private final Block drop;
    private final int dropChance;

    public FurBlock(MapColor color, Block drop, int light, int dropChance, boolean wet) {
        super(BehaviourBuilders
                .createPlant(color)
                .replaceable()
                .lightLevel(bs -> light)
                .ignitedByLava()
                .sound(wet ? SoundType.WET_GRASS : SoundType.GRASS)
        );

        this.drop = drop;
        this.dropChance = dropChance;
    }

    public FurBlock(MapColor color, Block drop, int dropChance) {
        this(color, drop, 0, dropChance, false);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return BOUNDING_SHAPES.get(state.getValue(FACING));
    }

    @Override
    public LootTable.Builder registerBlockLoot(
            @NotNull Identifier location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        final float dropRate = dropChance < 1 ? 1.0F : 1.0F / dropChance;
        final float[] LEAVES_SAPLING_CHANCES = new float[]{
                dropRate,
                cappedChance(1.25F * dropRate),
                cappedChance(1.6666667F * dropRate),
                cappedChance(2.0F * dropRate)
        };
        return provider.dropLeaves(this, drop, LEAVES_SAPLING_CHANCES);
    }

    private static float cappedChance(float chance) {
        return Math.min(1.0F, chance);
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

    @Override
    public void registerBlockTags(Identifier location, TagBootstrapContext<Block> context) {
        context.add(this, BlockTags.LEAVES);
    }

    @Override
    public void provideBlockModels(Object modelGenerator) {
    WoverBlockModelGenerators generator = (WoverBlockModelGenerators) modelGenerator;
        generator.createCubeModel(this);
        generator.createFlatItem(this);
    }
}

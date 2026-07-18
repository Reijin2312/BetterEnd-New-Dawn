package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.betterend.blocks.basis.EndPlantBlock;
import org.betterx.betterend.interfaces.survives.SurvivesOnShadowGrass;
import org.betterx.wover.loot.api.BlockLootProvider;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NeedlegrassBlock extends EndPlantBlock implements SurvivesOnShadowGrass, BlockLootProvider {
    public NeedlegrassBlock() {
        super(BehaviourBuilders
                .createGrass(MapColor.COLOR_BLACK)
                .ignitedByLava()
                .offsetType(OffsetType.XZ)
        );
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean checkInside) {
        if (entity instanceof LivingEntity) {
            entity.hurt(world.damageSources().cactus(), 0.1F);
        }
    }

    @Override
    public @Nullable LootTable.Builder registerBlockLoot(
            @NotNull Identifier location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        return LootTable.lootTable().withPool(
                LootPool.lootPool()
                        .when(provider.hasSilkTouch())
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(this).apply(ApplyExplosionDecay.explosionDecay()))
        ).withPool(
                LootPool.lootPool()
                        .when(provider.hasSilkTouch())
                        .setRolls(UniformGenerator.between(0, 2))
                        .add(LootItem.lootTableItem(Items.STICK)
                                     .apply(ApplyExplosionDecay.explosionDecay())
                        )
        );
    }

    @Override
    protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public boolean isTerrain(BlockState state) {
        return SurvivesOnShadowGrass.super.isTerrain(state);
    }
}

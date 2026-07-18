package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.client.render.BCLRenderLayer;
import org.betterx.bclib.interfaces.BlockColorProvider;
import org.betterx.bclib.interfaces.CustomColorProvider;
import org.betterx.bclib.interfaces.ItemColorProvider;
import org.betterx.bclib.interfaces.RenderLayerProvider;
import org.betterx.bclib.interfaces.tools.AddMineableHammer;
import org.betterx.bclib.interfaces.tools.AddMineablePickaxe;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.registry.EndItems;
import org.betterx.ui.ColorUtil;
import org.betterx.wover.loot.api.BlockLootProvider;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;

public class AuroraCrystalBlock extends TransparentBlock implements BlockLootProvider, RenderLayerProvider, CustomColorProvider, AddMineablePickaxe, AddMineableHammer {
    public static final Vec3i[] COLORS;
    private static final int MIN_DROP = 1;
    private static final int MAX_DROP = 4;

    public AuroraCrystalBlock() {
        super(BehaviourBuilders
                .createGlass()
                .strength(1F)
                .lightLevel((bs) -> 15)
        );
    }

    @Override
    @Deprecated
    public VoxelShape getVisualShape(
            BlockState blockState,
            BlockGetter blockGetter,
            BlockPos blockPos,
            CollisionContext collisionContext
    ) {
        return this.getCollisionShape(blockState, blockGetter, blockPos, collisionContext);
    }

    @Override
    public BlockColorProvider getProvider() {
        return (state, world, pos, tintIndex) -> {
            if (pos == null) {
                pos = BlockPos.ZERO;
            }

            long i = (long) pos.getX() + (long) pos.getY() + (long) pos.getZ();
            double delta = i * 0.1;
            int index = MHelper.floor(delta);
            int index2 = (index + 1) & 3;
            delta -= index;
            index &= 3;

            Vec3i color1 = COLORS[index];
            Vec3i color2 = COLORS[index2];

            int r = MHelper.floor(Mth.lerp(delta, color1.getX(), color2.getX()));
            int g = MHelper.floor(Mth.lerp(delta, color1.getY(), color2.getY()));
            int b = MHelper.floor(Mth.lerp(delta, color1.getZ(), color2.getZ()));

            return ColorUtil.color(r, g, b);
        };
    }

    @Override
    public ItemColorProvider getItemProvider() {
        return (stack, tintIndex) -> {
            return ColorUtil.color(COLORS[3].getX(), COLORS[3].getY(), COLORS[3].getZ());
        };
    }

    @Override
    public BCLRenderLayer getRenderLayer() {
        return BCLRenderLayer.TRANSLUCENT;
    }

    @Override
    public LootTable.Builder registerBlockLoot(
            @NotNull Identifier location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        return provider.dropOre(this, EndItems.CRYSTAL_SHARDS, UniformGenerator.between(MIN_DROP, MAX_DROP));
    }

    static {
        COLORS = new Vec3i[]{
                new Vec3i(247, 77, 161),
                new Vec3i(120, 184, 255),
                new Vec3i(120, 255, 168),
                new Vec3i(243, 58, 255)
        };
    }
}

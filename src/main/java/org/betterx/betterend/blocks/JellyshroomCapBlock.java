package org.betterx.betterend.blocks;

import org.betterx.bclib.client.models.ModelsHelper;
import org.betterx.bclib.client.render.BCLRenderLayer;
import org.betterx.bclib.interfaces.BlockColorProvider;
import org.betterx.bclib.interfaces.CustomColorProvider;
import org.betterx.bclib.interfaces.ItemColorProvider;
import org.betterx.bclib.interfaces.RenderLayerProvider;
import org.betterx.bclib.interfaces.RuntimeBlockModelProvider;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.client.models.Patterns;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.ui.ColorUtil;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;

import net.minecraft.world.level.block.state.BlockBehaviour;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class JellyshroomCapBlock extends SlimeBlock implements RenderLayerProvider, RuntimeBlockModelProvider, CustomColorProvider {
    public static final IntegerProperty COLOR = EndBlockProperties.COLOR;
    private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(0);
    private final Vec3i colorStart;
    private final Vec3i colorEnd;
    private final int coloritem;

    public JellyshroomCapBlock(int r1, int g1, int b1, int r2, int g2, int b2) {
        super(BlockBehaviour.Properties.ofLegacyCopy(Blocks.SLIME_BLOCK));
        colorStart = new Vec3i(r1, g1, b1);
        colorEnd = new Vec3i(r2, g2, b2);
        coloritem = ColorUtil.color((r1 + r2) >> 1, (g1 + g2) >> 1, (b1 + b2) >> 1);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        double px = ctx.getClickedPos().getX() * 0.1;
        double py = ctx.getClickedPos().getY() * 0.1;
        double pz = ctx.getClickedPos().getZ() * 0.1;
        return this.defaultBlockState().setValue(COLOR, MHelper.floor(NOISE.eval(px, py, pz) * 3.5 + 4));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(COLOR);
    }

    @Override
    public BCLRenderLayer getRenderLayer() {
        return BCLRenderLayer.TRANSLUCENT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Lists.newArrayList(new ItemStack(this));
    }

    @Override
    public BlockModel getItemModel(Identifier resourceLocation) {
        return getBlockModel(resourceLocation, defaultBlockState());
    }

    @Override
    public @Nullable BlockModel getBlockModel(Identifier resourceLocation, BlockState blockState) {
        Optional<String> pattern = Patterns.createJson(Patterns.BLOCK_COLORED, "jellyshroom_cap");
        return ModelsHelper.fromPattern(pattern);
    }

    @Override
    public BlockColorProvider getProvider() {
        return (state, world, pos, tintIndex) -> {
            float delta = (float) state.getValue(COLOR) / 7F;
            int r = Mth.floor(Mth.lerp(delta, colorStart.getX() / 255F, colorEnd.getX() / 255F) * 255F);
            int g = Mth.floor(Mth.lerp(delta, colorStart.getY() / 255F, colorEnd.getY() / 255F) * 255F);
            int b = Mth.floor(Mth.lerp(delta, colorStart.getZ() / 255F, colorEnd.getZ() / 255F) * 255F);
            return ColorUtil.color(r, g, b);
        };
    }

    @Override
    public ItemColorProvider getItemProvider() {
        return (stack, tintIndex) -> {
            return coloritem;
        };
    }
}

package org.betterx.betterend.client.render;

import de.ambertation.wunderlib.ui.ColorHelper;
import org.betterx.betterend.blocks.EternalPedestal;
import org.betterx.betterend.blocks.basis.PedestalBlock;
import org.betterx.betterend.blocks.entities.PedestalBlockEntity;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;


import org.jspecify.annotations.Nullable;

public class PedestalItemRenderer<T extends PedestalBlockEntity> implements BlockEntityRenderer<T, PedestalItemRenderer.PedestalRenderState> {
    private static final int MAX_GEM_AGE = 314;
    private final ItemModelResolver itemModelResolver;

    public PedestalItemRenderer(BlockEntityRendererProvider.Context ctx) {
        this.itemModelResolver = ctx.itemModelResolver();
    }

    @Override
    public PedestalRenderState createRenderState() {
        return new PedestalRenderState();
    }

    @Override
    public void extractRenderState(
            T blockEntity,
            PedestalRenderState state,
            float partialTick,
            Vec3 cameraPos,
            ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);
        state.itemRenderState.clear();
        state.renderItem = false;

        if (blockEntity.isEmpty()) {
            return;
        }

        BlockState blockState = blockEntity.getBlockState();
        if (!(blockState.getBlock() instanceof PedestalBlock pedestal)) {
            return;
        }

        ItemStack activeItem = blockEntity.getItem(0);
        if (activeItem.isEmpty()) {
            return;
        }

        state.renderItem = true;
        state.isBlockItem = activeItem.getItem() instanceof BlockItem;
        state.isEndCrystal = activeItem.is(Items.END_CRYSTAL);
        state.isEternalCrystal = activeItem.is(EndItems.ETERNAL_CRYSTAL);
        state.hasEternalBeam = blockState.is(EndBlocks.ETERNAL_PEDESTAL) && blockState.getValue(EternalPedestal.ACTIVATED);
        state.pedestalHeight = pedestal.getHeight(blockState);
        state.blockY = blockEntity.getBlockPos().getY();
        state.age = blockEntity.getLevel() == null
                ? 0
                : (int) (blockEntity.getLevel().getGameTime() % MAX_GEM_AGE);
        state.partialTick = partialTick;
        state.animationTime = state.age + partialTick;

        this.itemModelResolver.updateForTopItem(
                state.itemRenderState,
                activeItem,
                ItemDisplayContext.GROUND,
                blockEntity.getLevel(),
                null,
                (int) blockEntity.getBlockPos().asLong()
        );
    }

    @Override
    public void submit(
            PedestalRenderState state,
            PoseStack matrices,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState cameraRenderState
    ) {
        if (!state.renderItem || state.itemRenderState.isEmpty()) {
            return;
        }

        matrices.pushPose();
        matrices.translate(0.5D, state.pedestalHeight, 0.5D);

        if (state.isBlockItem) {
            matrices.scale(1.5F, 1.5F, 1.5F);
        } else {
            matrices.scale(1.25F, 1.25F, 1.25F);
        }

        if (state.hasEternalBeam) {
            float[] colors = ColorHelper.toFloatArrayRGBA(EternalCrystalRenderer.colors(state.age));
            BeamRenderer.renderLightBeam(
                    matrices,
                    submitNodeCollector,
                    state.age,
                    state.partialTick,
                    -state.blockY,
                    1024 - state.blockY,
                    colors,
                    0.25F,
                    0.13F,
                    0.16F
            );
            float altitude = Mth.sin(state.animationTime / 10.0F) * 0.1F + 0.1F;
            matrices.translate(0.0D, altitude, 0.0D);
        }

        if (state.isEndCrystal) {
            EndCrystalRenderer.render(
                    state.age,
                    MAX_GEM_AGE,
                    state.partialTick,
                    matrices,
                    submitNodeCollector,
                    state.lightCoords,
                    OverlayTexture.NO_OVERLAY
            );
        } else if (state.isEternalCrystal) {
            EternalCrystalRenderer.render(
                    state.age,
                    state.partialTick,
                    matrices,
                    submitNodeCollector,
                    state.lightCoords,
                    OverlayTexture.NO_OVERLAY
            );
        } else {
            float rotation = state.animationTime / 25.0F + 6.0F;
            matrices.mulPose(Axis.YP.rotation(rotation));
            state.itemRenderState.submit(matrices, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        }

        matrices.popPose();
    }

    public static class PedestalRenderState extends BlockEntityRenderState {
        public final ItemStackRenderState itemRenderState = new ItemStackRenderState();
        public boolean renderItem;
        public boolean isBlockItem;
        public boolean isEndCrystal;
        public boolean isEternalCrystal;
        public boolean hasEternalBeam;
        public float pedestalHeight;
        public int blockY;
        public int age;
        public float partialTick;
        public float animationTime;
    }
}

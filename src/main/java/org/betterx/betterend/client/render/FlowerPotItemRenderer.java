package org.betterx.betterend.client.render;

import org.betterx.betterend.blocks.entities.FlowerPotBlockEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import org.jspecify.annotations.Nullable;

public class FlowerPotItemRenderer<T extends FlowerPotBlockEntity>
        implements BlockEntityRenderer<T, FlowerPotItemRenderer.FlowerPotRenderState> {
    private final ItemModelResolver itemModelResolver;

    public FlowerPotItemRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public FlowerPotRenderState createRenderState() {
        return new FlowerPotRenderState();
    }

    @Override
    public void extractRenderState(
            T blockEntity,
            FlowerPotRenderState state,
            float partialTick,
            Vec3 cameraPos,
            ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);
        state.soil.clear();
        state.plant.clear();

        blockEntity.getSoilBlock().ifPresent(block -> itemModelResolver.updateForTopItem(
                state.soil,
                new ItemStack(block),
                ItemDisplayContext.GROUND,
                blockEntity.getLevel(),
                null,
                (int) blockEntity.getBlockPos().asLong()
        ));
        blockEntity.getPlantBlock().ifPresent(block -> itemModelResolver.updateForTopItem(
                state.plant,
                new ItemStack(block),
                ItemDisplayContext.GROUND,
                blockEntity.getLevel(),
                null,
                (int) blockEntity.getBlockPos().asLong()
        ));
    }

    @Override
    public void submit(
            FlowerPotRenderState state,
            PoseStack matrices,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState cameraRenderState
    ) {
        submitItem(state.soil, matrices, submitNodeCollector, state, 0.5, 0.5F);
        submitItem(state.plant, matrices, submitNodeCollector, state, 0.75, 0.5F);
    }

    private static void submitItem(
            ItemStackRenderState item,
            PoseStack matrices,
            SubmitNodeCollector submitNodeCollector,
            FlowerPotRenderState state,
            double yOffset,
            float scale
    ) {
        if (item.isEmpty()) {
            return;
        }
        matrices.pushPose();
        matrices.translate(0.5, yOffset, 0.5);
        matrices.scale(scale, scale, scale);
        item.submit(matrices, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        matrices.popPose();
    }

    public static class FlowerPotRenderState extends BlockEntityRenderState {
        public final ItemStackRenderState soil = new ItemStackRenderState();
        public final ItemStackRenderState plant = new ItemStackRenderState();
    }
}

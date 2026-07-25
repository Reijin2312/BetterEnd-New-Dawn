package org.betterx.betterend.client.render;

import org.betterx.betterend.blocks.FlowerPotBlock;
import org.betterx.betterend.blocks.entities.FlowerPotBlockEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FlowerPotItemRenderer<T extends FlowerPotBlockEntity> implements BlockEntityRenderer<T> {
    public FlowerPotItemRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            T blockEntity,
            float tickDelta,
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            int light,
            int overlay
    ) {
        Level world = blockEntity.getLevel();
        if (world == null) return;

        BlockState state = world.getBlockState(blockEntity.getBlockPos());
        if (!(state.getBlock() instanceof FlowerPotBlock)) return;

        blockEntity.getSoilBlock().ifPresent(soil -> renderStack(
                soil, world, blockEntity, matrices, vertexConsumers, light, overlay, 0.5, 0.5F
        ));
        blockEntity.getPlantBlock().ifPresent(plant -> renderStack(
                plant, world, blockEntity, matrices, vertexConsumers, light, overlay, 0.75, 0.5F
        ));
    }

    private void renderStack(
            Block block,
            Level world,
            T blockEntity,
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            int light,
            int overlay,
            double yOffset,
            float scale
    ) {
        ItemStack stack = new ItemStack(block);
        Minecraft minecraft = Minecraft.getInstance();
        BakedModel model = minecraft.getItemRenderer().getModel(stack, world, null, blockEntity.getBlockPos().hashCode());

        matrices.pushPose();
        matrices.translate(0.5, yOffset, 0.5);
        matrices.scale(scale, scale, scale);
        minecraft.getItemRenderer()
                 .render(
                         stack,
                         ItemDisplayContext.GROUND,
                         false,
                         matrices,
                         vertexConsumers,
                         light,
                         overlay,
                         model
                 );
        matrices.popPose();
    }
}

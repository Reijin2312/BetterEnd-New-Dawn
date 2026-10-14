package org.betterx.betterend.client.render;

import de.ambertation.wunderlib.ui.ColorHelper;
import org.betterx.betterend.blocks.EternalPedestal;
import org.betterx.betterend.blocks.basis.PedestalBlock;
import org.betterx.betterend.blocks.entities.EternalPedestalEntity;
import org.betterx.betterend.blocks.entities.PedestalBlockEntity;
import org.betterx.betterend.client.effects.EternalHint;
import org.betterx.betterend.client.effects.InfusionHint;
import org.betterx.betterend.recipe.builders.InfusionRecipe;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.rituals.InfusionRitual;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class PedestalItemRenderer<T extends PedestalBlockEntity> implements BlockEntityRenderer<T> {
    private static final int SOCKET_MISSING_RGB = 0xFFE9A0;
    private static final int SOCKET_PLACED_RGB = 0xC9A94E;
    private static final int SOCKET_NORTH_MISSING_RGB = 0xC58BFF;
    private static final int SOCKET_NORTH_PLACED_RGB = 0x8A5FC9;
    private static final int FRAME_MISSING_RGB = 0xB98BE8;
    private static final float CRYSTAL_GHOST_ALPHA = 0.55F;

    public PedestalItemRenderer(BlockEntityRendererProvider.Context ctx) {
        super();
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

        renderInfusionHint(world, blockEntity.getBlockPos(), tickDelta, matrices, vertexConsumers);
        renderEternalHint(world, blockEntity, tickDelta, matrices, vertexConsumers, light);

        BlockState state = world.getBlockState(blockEntity.getBlockPos());
        if (!(state.getBlock() instanceof PedestalBlock)) return;
        if (!state.getValue(PedestalBlock.HAS_ITEM) || blockEntity.isEmpty()) return;

        ItemStack activeItem = blockEntity.getItem(0);

        matrices.pushPose();
        Minecraft minecraft = Minecraft.getInstance();
        BakedModel model = minecraft.getItemRenderer().getModel(activeItem, world, null, 0);
        Vector3f translate = model.getTransforms().ground.translation;
        PedestalBlock pedestal = (PedestalBlock) state.getBlock();
        matrices.translate(translate.x() + 0.5, translate.y() + pedestal.getHeight(state), translate.z() + 0.5);
        if (activeItem.getItem() instanceof BlockItem) {
            matrices.scale(1.5F, 1.5F, 1.5F);
        } else {
            matrices.scale(1.25F, 1.25F, 1.25F);
        }
        int age = getGemAge();
        if (state.is(EndBlocks.ETERNAL_PEDESTAL) && state.getValue(EternalPedestal.ACTIVATED)) {
            float[] colors = ColorHelper.toFloatArrayRGBA(EternalCrystalRenderer.colors(age));
            int y = blockEntity.getBlockPos().getY();

            BeamRenderer.renderLightBeam(
                    matrices,
                    vertexConsumers,
                    age,
                    tickDelta,
                    -y,
                    1024 - y,
                    colors,
                    0.25F,
                    0.13F,
                    0.16F
            );
            float altitude = Mth.sin((age + tickDelta) / 10.0F) * 0.1F + 0.1F;
            matrices.translate(0.0D, altitude, 0.0D);
        }
        if (activeItem.getItem() == Items.END_CRYSTAL) {
            EndCrystalRenderer.render(age, 314, tickDelta, matrices, vertexConsumers, light);
        } else if (activeItem.getItem() == EndItems.ETERNAL_CRYSTAL) {
            EternalCrystalRenderer.render(age, tickDelta, matrices, vertexConsumers, light);
        } else {
            float rotation = (age + tickDelta) / 25.0F + 6.0F;
            matrices.mulPose(Axis.YP.rotation(rotation));
            minecraft.getItemRenderer()
                     .render(
                             activeItem,
                             ItemDisplayContext.GROUND,
                             false,
                             matrices,
                             vertexConsumers,
                             light,
                             overlay,
                             model
                     );
        }
        matrices.popPose();
    }

    private static void renderInfusionHint(
            Level world,
            BlockPos pedestalPos,
            float partialTick,
            PoseStack matrices,
            MultiBufferSource buffers
    ) {
        if (!InfusionHint.isActiveAt(pedestalPos)) return;

        float[] outline = InfusionHint.outline(partialTick);
        if (outline == null || outline[0] <= 0.0F) return;

        boolean[] present = InfusionRitual.socketsPresent(world, pedestalPos);
        VoxelShape shape = world.getBlockState(pedestalPos)
                                .getShape(world, pedestalPos, CollisionContext.empty());
        VertexConsumer lines = buffers.getBuffer(RenderType.lines());

        for (int i = 0; i < present.length; i++) {
            boolean north = i == InfusionRecipe.CatalystSlot.NORTH.index;
            int rgb = north
                    ? (present[i] ? SOCKET_NORTH_PLACED_RGB : SOCKET_NORTH_MISSING_RGB)
                    : (present[i] ? SOCKET_PLACED_RGB : SOCKET_MISSING_RGB);
            float alpha = outline[0] * (present[i] ? 0.35F : outline[1]);
            BlockPos offset = InfusionRitual.socketPos(BlockPos.ZERO, i);
            renderShapeOutline(matrices, lines, shape, offset, rgb, alpha);
        }
    }

    private static void renderEternalHint(
            Level world,
            PedestalBlockEntity blockEntity,
            float partialTick,
            PoseStack matrices,
            MultiBufferSource buffers,
            int light
    ) {
        if (!(blockEntity instanceof EternalPedestalEntity)) return;

        BlockPos anchor = EternalHint.anchorPedestal(world);
        if (anchor == null || !anchor.equals(blockEntity.getBlockPos())) return;

        float intensity = EternalHint.intensity(partialTick);
        if (intensity <= 0.0F) return;

        BlockPos here = blockEntity.getBlockPos();
        VertexConsumer lines = buffers.getBuffer(RenderType.lines());
        for (BlockPos framePos : EternalHint.missingFrame(world)) {
            renderShapeOutline(
                    matrices,
                    lines,
                    Shapes.block(),
                    framePos.subtract(here),
                    FRAME_MISSING_RGB,
                    intensity * 0.75F
            );
        }

        int age = getGemAge();
        for (BlockPos crystalPos : EternalHint.pedestalPositions()) {
            BlockPos offset = crystalPos.subtract(here);
            matrices.pushPose();
            matrices.translate(
                    offset.getX() + 0.5,
                    offset.getY() + EternalHint.CRYSTAL_HEIGHT,
                    offset.getZ() + 0.5
            );
            EternalCrystalRenderer.render(
                    age,
                    partialTick,
                    matrices,
                    buffers,
                    light,
                    intensity * CRYSTAL_GHOST_ALPHA
            );
            matrices.popPose();
        }
    }

    private static void renderShapeOutline(
            PoseStack matrices,
            VertexConsumer lines,
            VoxelShape shape,
            BlockPos offset,
            int rgb,
            float alpha
    ) {
        float red = ((rgb >> 16) & 0xFF) / 255.0F;
        float green = ((rgb >> 8) & 0xFF) / 255.0F;
        float blue = (rgb & 0xFF) / 255.0F;
        float clampedAlpha = Mth.clamp(alpha, 0.0F, 1.0F);
        for (AABB box : shape.toAabbs()) {
            LevelRenderer.renderLineBox(
                    matrices,
                    lines,
                    box.move(offset.getX(), offset.getY(), offset.getZ()),
                    red,
                    green,
                    blue,
                    clampedAlpha
            );
        }
    }

    public static int getGemAge() {
        return (int) (Minecraft.getInstance().level.getGameTime() % 314);
    }
}

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
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;

public class PedestalItemRenderer<T extends PedestalBlockEntity> implements BlockEntityRenderer<T, PedestalItemRenderer.PedestalRenderState> {
    private static final int MAX_GEM_AGE = 314;
    private static final int SOCKET_MISSING_RGB = 0xFFE9A0;
    private static final int SOCKET_PLACED_RGB = 0xC9A94E;
    private static final int SOCKET_NORTH_MISSING_RGB = 0xC58BFF;
    private static final int SOCKET_NORTH_PLACED_RGB = 0x8A5FC9;
    private static final int FRAME_MISSING_RGB = 0xB98BE8;
    private static final float CRYSTAL_GHOST_ALPHA = 0.55F;
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
        extractHints(blockEntity, state, partialTick);
        state.itemRenderState.clear();
        state.renderItem = false;
        state.age = blockEntity.getLevel() == null ? 0 : (int) (blockEntity.getLevel().getGameTime() % MAX_GEM_AGE);
        state.partialTick = partialTick;
        state.animationTime = state.age + partialTick;

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
        submitHints(state, matrices, submitNodeCollector);

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
            matrices.mulPose(new org.joml.Matrix4f().rotation(Axis.YP.rotation(rotation)));
            state.itemRenderState.submit(matrices, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        }

        matrices.popPose();
    }

    private static void extractHints(PedestalBlockEntity blockEntity, PedestalRenderState state, float partialTick) {
        state.outlines.clear();
        state.crystalOffsets.clear();
        state.crystalGhostAlpha = 0.0F;
        var level = blockEntity.getLevel();
        if (level == null) return;
        BlockPos here = blockEntity.getBlockPos();

        if (InfusionHint.isActiveAt(here)) {
            float[] outline = InfusionHint.outline(partialTick);
            if (outline != null && outline[0] > 0.0F) {
                boolean[] present = InfusionRitual.socketsPresent(level, here);
                VoxelShape shape = level.getBlockState(here).getShape(level, here, CollisionContext.empty());
                for (int i = 0; i < present.length; i++) {
                    boolean north = i == InfusionRecipe.CatalystSlot.NORTH.index;
                    int rgb = north ? (present[i] ? SOCKET_NORTH_PLACED_RGB : SOCKET_NORTH_MISSING_RGB)
                                    : (present[i] ? SOCKET_PLACED_RGB : SOCKET_MISSING_RGB);
                    float alpha = outline[0] * (present[i] ? 0.35F : outline[1]);
                    state.outlines.add(new HintOutline(shape, InfusionRitual.socketPos(BlockPos.ZERO, i), rgb, alpha));
                }
            }
        }

        if (!(blockEntity instanceof EternalPedestalEntity)) return;
        BlockPos anchor = EternalHint.anchorPedestal(level);
        if (anchor == null || !anchor.equals(here)) return;
        float intensity = EternalHint.intensity(partialTick);
        if (intensity <= 0.0F) return;
        for (BlockPos framePos : EternalHint.missingFrame(level)) {
            state.outlines.add(new HintOutline(Shapes.block(), framePos.subtract(here), FRAME_MISSING_RGB, intensity * 0.75F));
        }
        for (BlockPos crystalPos : EternalHint.pedestalPositions()) state.crystalOffsets.add(crystalPos.subtract(here));
        state.crystalGhostAlpha = intensity * CRYSTAL_GHOST_ALPHA;
    }

    private static void submitHints(PedestalRenderState state, PoseStack matrices, SubmitNodeCollector collector) {
        for (HintOutline outline : state.outlines) {
            collector.submitCustomGeometry(matrices, RenderTypes.linesTranslucent(),
                    (pose, vertices) -> renderShapeOutline(pose, vertices, outline));
        }
        if (state.crystalGhostAlpha <= 0.0F) return;
        for (BlockPos offset : state.crystalOffsets) {
            matrices.pushPose();
            matrices.translate(offset.getX() + 0.5, offset.getY() + EternalHint.CRYSTAL_HEIGHT, offset.getZ() + 0.5);
            EternalCrystalRenderer.render(state.age, state.partialTick, matrices, collector, state.lightCoords,
                    OverlayTexture.NO_OVERLAY, state.crystalGhostAlpha);
            matrices.popPose();
        }
    }

    private static void renderShapeOutline(PoseStack.Pose pose, VertexConsumer vertices, HintOutline outline) {
        BlockPos offset = outline.offset();
        int color = ARGB.color(Mth.clamp(outline.alpha(), 0.0F, 1.0F), outline.rgb());
        outline.shape().forAllEdges((x1, y1, z1, x2, y2, z2) -> {
            float nx = (float) (x2 - x1), ny = (float) (y2 - y1), nz = (float) (z2 - z1);
            float length = Mth.sqrt(nx * nx + ny * ny + nz * nz);
            if (length > 0.0F) { nx /= length; ny /= length; nz /= length; }
            vertices.addVertex(pose, (float) x1 + offset.getX(), (float) y1 + offset.getY(), (float) z1 + offset.getZ())
                    .setColor(color).setNormal(pose, nx, ny, nz).setLineWidth(2.0F);
            vertices.addVertex(pose, (float) x2 + offset.getX(), (float) y2 + offset.getY(), (float) z2 + offset.getZ())
                    .setColor(color).setNormal(pose, nx, ny, nz).setLineWidth(2.0F);
        });
    }

    private record HintOutline(VoxelShape shape, BlockPos offset, int rgb, float alpha) {}

    public static class PedestalRenderState extends BlockEntityRenderState {
        public final ItemStackRenderState itemRenderState = new ItemStackRenderState();
        public final List<HintOutline> outlines = new ArrayList<>();
        public final List<BlockPos> crystalOffsets = new ArrayList<>();
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
        public float crystalGhostAlpha;
    }
}

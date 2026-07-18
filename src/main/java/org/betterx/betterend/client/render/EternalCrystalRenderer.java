package org.betterx.betterend.client.render;

import de.ambertation.wunderlib.ui.ColorHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.AuroraCrystalBlock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;

// TODO make crystals bright
public class EternalCrystalRenderer {
    private static final RenderType RENDER_LAYER;
    private static final ModelPart[] SHARDS;
    private static final ModelPart CORE;

    public static void render(
            int age,
            float tickDelta,
            PoseStack matrices,
            SubmitNodeCollector submitNodeCollector,
            int light,
            int overlay
    ) {
        int color = colors(age);
        float rotation = (age + tickDelta) / 25.0F + 6.0F;
        matrices.pushPose();
        matrices.scale(0.6F, 0.6F, 0.6F);
        matrices.mulPose(Axis.YP.rotation(rotation));

        submitNodeCollector.submitModelPart(
                CORE,
                matrices,
                RENDER_LAYER,
                light,
                overlay,
                null,
                color,
                null
        );

        for (int i = 0; i < 4; i++) {
            matrices.pushPose();
            float offset = Mth.sin(rotation * 2 + i) * 0.15F;
            matrices.translate(0, offset, 0);
            submitNodeCollector.submitModelPart(
                    SHARDS[i],
                    matrices,
                    RENDER_LAYER,
                    light,
                    overlay,
                    null,
                    color,
                    null
            );
            matrices.popPose();
        }

        matrices.popPose();
    }

    public static int colors(int age) {
        double delta = age * 0.01;
        int index = MHelper.floor(delta);
        int index2 = (index + 1) & 3;
        delta -= index;
        index &= 3;

        Vec3i color1 = AuroraCrystalBlock.COLORS[index];
        Vec3i color2 = AuroraCrystalBlock.COLORS[index2];

        int r = MHelper.floor(Mth.lerp(delta, color1.getX(), color2.getX()));
        int g = MHelper.floor(Mth.lerp(delta, color1.getY(), color2.getY()));
        int b = MHelper.floor(Mth.lerp(delta, color1.getZ(), color2.getZ()));

        return ColorHelper.color(r, g, b);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild(
                "SHARDS_0",
                CubeListBuilder.create().texOffs(2, 4).addBox(-5.0f, 1.0f, -3.0f, 2.0f, 8.0f, 2.0f),
                PartPose.ZERO
        );

        modelPartData.addOrReplaceChild(
                "SHARDS_1",
                CubeListBuilder.create().texOffs(2, 4).addBox(3.0f, -1.0f, -1.0f, 2.0f, 8.0f, 2.0f),
                PartPose.ZERO
        );

        modelPartData.addOrReplaceChild(
                "SHARDS_2",
                CubeListBuilder.create().texOffs(2, 4).addBox(-1.0f, 0.0f, -5.0f, 2.0f, 4.0f, 2.0f),
                PartPose.ZERO
        );

        modelPartData.addOrReplaceChild(
                "SHARDS_3",
                CubeListBuilder.create().texOffs(2, 4).addBox(0.0f, 3.0f, 4.0f, 2.0f, 6.0f, 2.0f),
                PartPose.ZERO
        );

        modelPartData.addOrReplaceChild(
                "CORE",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                PartPose.ZERO
        );

        return LayerDefinition.create(modelData, 16, 16);
    }

    static {
        RENDER_LAYER = RenderTypes.itemEntityTranslucentCull(BetterEnd.C.mk("textures/entity/eternal_crystal.png"));
        SHARDS = new ModelPart[4];

        ModelPart root = getTexturedModelData().bakeRoot();
        SHARDS[0] = root.getChild("SHARDS_0");
        SHARDS[1] = root.getChild("SHARDS_1");
        SHARDS[2] = root.getChild("SHARDS_2");
        SHARDS[3] = root.getChild("SHARDS_3");
        CORE = root.getChild("CORE");
    }
}

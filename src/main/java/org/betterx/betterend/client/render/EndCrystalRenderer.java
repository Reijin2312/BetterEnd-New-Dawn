package org.betterx.betterend.client.render;

import org.betterx.betterend.BetterEnd;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Constants;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

import org.joml.Quaternionf;

public class EndCrystalRenderer {
    private static final Identifier CRYSTAL_TEXTURE = BetterEnd.C.mk(
            "textures/entity/end_crystal/end_crystal.png");
    private static final Identifier CRYSTAL_BEAM_TEXTURE = BetterEnd.C.mk(
            "textures/entity/end_crystal/end_crystal_beam.png");
    private static final RenderType END_CRYSTAL;
    private static final ModelPart CORE;
    private static final ModelPart FRAME;
    private static final int AGE_CYCLE = 240;
    private static final float SINE_45_DEGREES;
    private static final Quaternionf ROTATOR;

    public static void render(
            int age,
            int maxAge,
            float tickDelta,
            PoseStack matrices,
            SubmitNodeCollector submitNodeCollector,
            int light,
            int overlay
    ) {
        float k = (float) AGE_CYCLE / maxAge;
        float rotation = (age * k + tickDelta) * 3.0F;
        matrices.pushPose();
        matrices.scale(0.8F, 0.8F, 0.8F);
        matrices.translate(0.0D, -0.5D, 0.0D);
        matrices.mulPose(Axis.YP.rotationDegrees(rotation));
        matrices.translate(0.0D, 0.8F, 0.0D);
        //matrices.mulPose(new Quaternion(new Vector3f(SINE_45_DEGREES, 0.0F, SINE_45_DEGREES), 60.0F, true));
        matrices.mulPose(ROTATOR);
        submitNodeCollector.submitModelPart(FRAME, matrices, END_CRYSTAL, light, overlay, null);
        matrices.scale(0.875F, 0.875F, 0.875F);
        //matrices.mulPose(new Quaternion(new Vector3f(SINE_45_DEGREES, 0.0F, SINE_45_DEGREES), 60.0F, true));
        matrices.mulPose(ROTATOR);
        matrices.mulPose(Axis.YP.rotationDegrees(rotation));
        submitNodeCollector.submitModelPart(FRAME, matrices, END_CRYSTAL, light, overlay, null);
        matrices.scale(0.875F, 0.875F, 0.875F);
        //matrices.mulPose(new Quaternion(new Vector3f(SINE_45_DEGREES, 0.0F, SINE_45_DEGREES), 60.0F, true));
        matrices.mulPose(ROTATOR);
        matrices.mulPose(Axis.YP.rotationDegrees(rotation));
        submitNodeCollector.submitModelPart(CORE, matrices, END_CRYSTAL, light, overlay, null);
        matrices.popPose();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild(
                "FRAME",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f),
                PartPose.ZERO
        );

        modelPartData.addOrReplaceChild(
                "CORE",
                CubeListBuilder.create().texOffs(32, 0).addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f),
                PartPose.ZERO
        );

        return LayerDefinition.create(modelData, 64, 32);
    }

    static {
        END_CRYSTAL = RenderTypes.entityCutoutNoCull(CRYSTAL_TEXTURE);
        RenderTypes.entitySmoothCutout(CRYSTAL_BEAM_TEXTURE);
        SINE_45_DEGREES = (float) Math.sin(0.7853981633974483D);

        ModelPart root = getTexturedModelData().bakeRoot();
        FRAME = root.getChild("FRAME");
        CORE = root.getChild("CORE");

        ROTATOR = new Quaternionf().setAngleAxis(
                60.0f * Constants.DEG_TO_RAD,
                SINE_45_DEGREES,
                0.0F,
                SINE_45_DEGREES
        );
    }
}

package org.betterx.betterend.item.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

public class ArmoredElytraModel extends EntityModel<HumanoidRenderState> {
    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild(
                PartNames.LEFT_WING,
                CubeListBuilder.create().texOffs(22, 0).addBox(-10.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f),
                PartPose.ZERO
        );

        modelPartData.addOrReplaceChild(
                PartNames.RIGHT_WING,
                CubeListBuilder.create().mirror().texOffs(22, 0).addBox(0.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f),
                PartPose.ZERO
        );

        return LayerDefinition.create(modelData, 64, 32);
    }

    public ArmoredElytraModel(ModelPart modelPart) {
        super(modelPart);
        leftWing = modelPart.getChild(PartNames.LEFT_WING);
        rightWing = modelPart.getChild(PartNames.RIGHT_WING);
    }

    @Override
    public void setupAnim(HumanoidRenderState state) {
        super.setupAnim(state);
        leftWing.y = state.isCrouching ? 3.0F : 0.0F;
        leftWing.xRot = state.elytraRotX;
        leftWing.yRot = state.elytraRotY;
        leftWing.zRot = state.elytraRotZ;
        rightWing.x = -leftWing.x;
        rightWing.yRot = -leftWing.yRot;
        rightWing.y = leftWing.y;
        rightWing.xRot = leftWing.xRot;
        rightWing.zRot = -leftWing.zRot;
    }
}

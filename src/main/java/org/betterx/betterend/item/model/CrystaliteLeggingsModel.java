package org.betterx.betterend.item.model;

import org.betterx.betterend.registry.EndEntitiesRenders;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.google.common.collect.Lists;

import java.util.Collections;

public class CrystaliteLeggingsModel extends HumanoidModel<HumanoidRenderState> {
    public static LayerDefinition getTexturedModelData() {
        float scale = 1.0f;
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        // Humanoid model tries to retrieve all parts in it's constructor,
        // so we need to add empty Nodes
        PartDefinition head = modelPartData.addOrReplaceChild(PartNames.HEAD, CubeListBuilder.create(), PartPose.ZERO);
        head.addOrReplaceChild(PartNames.HAT, CubeListBuilder.create(), PartPose.ZERO);
        // modelPartData.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild(PartNames.RIGHT_ARM, CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild(PartNames.LEFT_ARM, CubeListBuilder.create(), PartPose.ZERO);
        // modelPartData.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        // modelPartData.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation deformation = new CubeDeformation(scale);
        modelPartData.addOrReplaceChild(
                PartNames.BODY,
                CubeListBuilder.create().texOffs(16, 16).addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, deformation),
                PartPose.ZERO
        );

        modelPartData.addOrReplaceChild(
                PartNames.LEFT_LEG,
                CubeListBuilder.create().texOffs(0, 32).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, deformation),
                PartPose.offset(1.9f, 12.0f, 0.0f)
        );

        modelPartData.addOrReplaceChild(
                PartNames.RIGHT_LEG,
                CubeListBuilder.create().texOffs(0, 16).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, deformation),
                PartPose.offset(-1.9f, 12.0f, 0.0f)
        );

        return LayerDefinition.create(modelData, 64, 48);
    }

    final ModelPart myBody;
    final ModelPart myLeftLeg;
    final ModelPart myRightLeg;

    public static CrystaliteLeggingsModel createModel(EntityModelSet entityModelSet) {
        return new CrystaliteLeggingsModel(entityModelSet == null
                ? getTexturedModelData().bakeRoot()
                : entityModelSet.bakeLayer(
                        EndEntitiesRenders.CRYSTALITE_LEGGINGS));
    }

    public CrystaliteLeggingsModel(ModelPart modelPart) {
        super(modelPart, RenderTypes::entityTranslucent);

        myBody = modelPart.getChild(PartNames.BODY);
        myLeftLeg = modelPart.getChild(PartNames.LEFT_LEG);
        myRightLeg = modelPart.getChild(PartNames.RIGHT_LEG);
    }

    protected Iterable<ModelPart> headParts() {
        return Collections::emptyIterator;
    }

    protected Iterable<ModelPart> bodyParts() {
        return Lists.newArrayList(myBody, myRightLeg, myLeftLeg);
    }
}

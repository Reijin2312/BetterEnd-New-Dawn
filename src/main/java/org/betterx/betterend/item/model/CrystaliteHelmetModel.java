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

public class CrystaliteHelmetModel extends HumanoidModel<HumanoidRenderState> {
    final ModelPart myHat;

    public static LayerDefinition getTexturedModelData() {
        final float scale = 1.0f;
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        // TODO: see if we need to subclass HumanoidModel
        // Humanoid model tries to retrieve all parts in it's constructor,
        // so we need to add empty Nodes
        PartDefinition head = modelPartData.addOrReplaceChild(PartNames.HEAD, CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild(PartNames.BODY, CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild(PartNames.RIGHT_ARM, CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild(PartNames.LEFT_ARM, CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild(PartNames.RIGHT_LEG, CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild(PartNames.LEFT_LEG, CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation deformation_hat = new CubeDeformation(scale + 0.5f);
        head.addOrReplaceChild(
                PartNames.HAT,
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, deformation_hat),
                PartPose.ZERO
        );

        return LayerDefinition.create(modelData, 64, 48);
    }

    public static CrystaliteHelmetModel createModel(EntityModelSet entityModelSet) {
        return new CrystaliteHelmetModel(entityModelSet == null
                ? getTexturedModelData().bakeRoot()
                : entityModelSet.bakeLayer(
                        EndEntitiesRenders.CRYSTALITE_HELMET));
    }


    public CrystaliteHelmetModel(ModelPart modelPart) {
        super(modelPart, RenderTypes::entityTranslucent);

        myHat = this.hat;
    }

    protected Iterable<ModelPart> headParts() {
        return Collections::emptyIterator;
    }

    protected Iterable<ModelPart> bodyParts() {
        return Lists.newArrayList(myHat);
    }
}

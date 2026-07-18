package org.betterx.betterend.item.model;

import org.betterx.bclib.client.render.HumanoidArmorRenderer;
import org.betterx.betterend.registry.EndEntitiesRenders;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.entity.HumanoidArm;

import com.google.common.collect.Lists;

import java.util.Collections;

public class CrystaliteChestplateModel extends HumanoidModel<HumanoidRenderState> implements HumanoidArmorRenderer.CopyExtraState {

    public ModelPart leftShoulder;
    public ModelPart rightShoulder;
    private final boolean thinArms;

    public static LayerDefinition getRegularTexturedModelData() {
        return getTexturedModelData(1.0f, false);
    }

    public static LayerDefinition getThinTexturedModelData() {
        return getTexturedModelData(1.0f, true);
    }

    private static LayerDefinition getTexturedModelData(float scale, boolean thinArms) {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        // Humanoid model tries to retrieve all parts in it's constructor,
        // so we need to add empty Nodes
        PartDefinition head = modelPartData.addOrReplaceChild(PartNames.HEAD, CubeListBuilder.create(), PartPose.ZERO);
        head.addOrReplaceChild(PartNames.HAT, CubeListBuilder.create(), PartPose.ZERO);
        // modelPartData.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild(PartNames.RIGHT_ARM, CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild(PartNames.LEFT_ARM, CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild(PartNames.RIGHT_LEG, CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild(PartNames.LEFT_LEG, CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation deformation = new CubeDeformation(scale + 0.25F);
        PartDefinition body = modelPartData.addOrReplaceChild(
                PartNames.BODY,
                CubeListBuilder.create().texOffs(16, 16).addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, deformation),
                PartPose.ZERO
        );

        if (thinArms) {
            deformation = new CubeDeformation(scale + 0.45F);
            PartDefinition leftShoulder = modelPartData.addOrReplaceChild(
                    "leftShoulder",
                    CubeListBuilder.create()
                                   .mirror()
                                   .texOffs(40, 32)
                                   .addBox(-1.0f, -2.5f, -2.0f, 4.0f, 12.0f, 4.0f, deformation),
                    PartPose.offset(5.0f, 2.0f, 0.0f)
            );

            PartDefinition rightShoulder = modelPartData.addOrReplaceChild(
                    "rightShoulder",
                    CubeListBuilder.create()
                                   .texOffs(40, 16)
                                   .addBox(-3.0f, -2.5f, -2.0f, 4.0f, 12.0f, 4.0f, deformation),
                    PartPose.offset(-5.0f, 2.0f, 10.0f)
            );
        } else {
            deformation = new CubeDeformation(scale + 0.45F);
            PartDefinition leftShoulder = modelPartData.addOrReplaceChild(
                    "leftShoulder",
                    CubeListBuilder.create()
                                   .mirror()
                                   .texOffs(40, 32)
                                   .addBox(-1.0f, -2.5f, -2.0f, 4.0f, 12.0f, 4.0f, deformation),
                    PartPose.offset(5.0f, 2.0f, 0.0f)
            );

            PartDefinition rightShoulder = modelPartData.addOrReplaceChild(
                    "rightShoulder",
                    CubeListBuilder.create()
                                   .texOffs(40, 16)
                                   .addBox(-3.0f, -2.5f, -2.0f, 4.0f, 12.0f, 4.0f, deformation),
                    PartPose.offset(-5.0f, 2.0f, 10.0f)
            );
        }
        return LayerDefinition.create(modelData, 64, 48);
    }

    final ModelPart localBody;

    public static CrystaliteChestplateModel createRegularModel(EntityModelSet entityModelSet) {
        return new CrystaliteChestplateModel(
                entityModelSet == null
                        ? getRegularTexturedModelData().bakeRoot()
                        : entityModelSet
                                .bakeLayer(EndEntitiesRenders.CRYSTALITE_CHESTPLATE),
                false
        );
    }

    public static CrystaliteChestplateModel createThinModel(EntityModelSet entityModelSet) {
        return new CrystaliteChestplateModel(
                entityModelSet == null
                        ? getThinTexturedModelData().bakeRoot()
                        : entityModelSet
                                .bakeLayer(EndEntitiesRenders.CRYSTALITE_CHESTPLATE_THIN),
                true
        );
    }

    protected CrystaliteChestplateModel(ModelPart modelPart, boolean thinArms) {
        super(modelPart, RenderTypes::entityTranslucent);
        this.thinArms = thinArms;
        localBody = modelPart.getChild(PartNames.BODY);
        this.leftShoulder = modelPart.getChild("leftShoulder");
        this.rightShoulder = modelPart.getChild("rightShoulder");
    }


    @Override
    public void copyPropertiesFrom(HumanoidModel<?> parentModel) {
        copyPart(leftArm, this.leftShoulder);
        copyPart(rightArm, this.rightShoulder);
    }

    @Override
    public void setupAnim(HumanoidRenderState renderState) {
        super.setupAnim(renderState);
        copyPropertiesFrom(this);
    }

    protected Iterable<ModelPart> headParts() {
        return Collections::emptyIterator;
    }

    protected Iterable<ModelPart> bodyParts() {
        return Lists.newArrayList(localBody, leftShoulder, rightShoulder);
    }

    public void translateToHand(HumanoidArm arm, PoseStack matrices) {
        ModelPart modelPart = this.getArm(arm);
        if (this.thinArms) {
            float f = 0.5F * (float) (arm == HumanoidArm.RIGHT ? 1 : -1);
            modelPart.x += f;
            modelPart.translateAndRotate(matrices);
            modelPart.x -= f;
        } else {
            modelPart.translateAndRotate(matrices);
        }
    }

    private static void copyPart(ModelPart from, ModelPart to) {
        to.x = from.x;
        to.y = from.y;
        to.z = from.z;
        to.xRot = from.xRot;
        to.yRot = from.yRot;
        to.zRot = from.zRot;
        to.xScale = from.xScale;
        to.yScale = from.yScale;
        to.zScale = from.zScale;
        to.visible = from.visible;
        to.skipDraw = from.skipDraw;
    }
}

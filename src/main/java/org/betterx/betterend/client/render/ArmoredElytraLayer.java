package org.betterx.betterend.client.render;

import org.betterx.bclib.items.elytra.BCLElytraItem;
import org.betterx.betterend.item.model.ArmoredElytraModel;
import org.betterx.betterend.registry.EndEntitiesRenders;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.PlayerSkin;


public class ArmoredElytraLayer<S extends HumanoidRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
    private static final Identifier VANILLA_WINGS = Identifier.withDefaultNamespace("textures/entity/elytra.png");
    private final ArmoredElytraModel elytraModel;

    public ArmoredElytraLayer(RenderLayerParent<S, M> renderLayerParent, EntityModelSet entityModelSet) {
        super(renderLayerParent);
        ArmoredElytraModel model;
        try {
            model = new ArmoredElytraModel(entityModelSet.bakeLayer(EndEntitiesRenders.ARMORED_ELYTRA));
        } catch (IllegalArgumentException ex) {
            model = null;
        }
        this.elytraModel = model;
    }

    @Override
    public void submit(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int light,
            S state,
            float yRot,
            float xRot
    ) {
        if (elytraModel == null) {
            return;
        }

        ItemStack itemStack = state.chestEquipment;
        if (itemStack.isEmpty()) {
            return;
        }
        // 1.21.11+: vanilla WingsLayer handles equippable items with GLIDER.
        // Keep this layer only for legacy custom elytras without GLIDER.
        if (itemStack.has(DataComponents.GLIDER)) {
            return;
        }

        boolean isVanillaElytra = itemStack.is(Items.ELYTRA);
        boolean isCustomElytra = itemStack.getItem() instanceof BCLElytraItem;
        if (!isVanillaElytra && !isCustomElytra) {
            return;
        }

        Identifier wingsTexture = getDefaultWingsTexture(itemStack);
        if (isVanillaElytra && state instanceof AvatarRenderState avatarState) {
            PlayerSkin playerSkin = avatarState.skin;
            if (playerSkin.elytra() != null) {
                wingsTexture = playerSkin.elytra().texturePath();
            } else if (playerSkin.cape() != null && avatarState.showCape) {
                wingsTexture = playerSkin.cape().texturePath();
            }
        }

        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 0.125D);
        elytraModel.setupAnim(state);
        submitNodeCollector.submitModel(
                elytraModel,
                state,
                poseStack,
                RenderTypes.armorCutoutNoCull(wingsTexture),
                light,
                OverlayTexture.NO_OVERLAY,
                -1,
                null,
                state.outlineColor,
                null
        );
        poseStack.popPose();
    }

    private Identifier getDefaultWingsTexture(ItemStack itemStack) {
        if (itemStack.getItem() instanceof BCLElytraItem elytraItem) {
            return elytraItem.getModelTexture();
        }
        return VANILLA_WINGS;
    }
}

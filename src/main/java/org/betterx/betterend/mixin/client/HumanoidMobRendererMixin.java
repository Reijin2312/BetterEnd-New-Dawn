package org.betterx.betterend.mixin.client;

import org.betterx.betterend.client.render.ArmoredElytraLayer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidMobRenderer.class)
public abstract class HumanoidMobRendererMixin {
    @Unique
    private boolean betterend$armoredElytraLayerAdded;

    @Inject(
            method = "<init>*",
            at = @At("RETURN")
    )
    public void be_addCustomLayer(CallbackInfo ci) {
        if (betterend$armoredElytraLayerAdded) {
            return;
        }
        betterend$armoredElytraLayerAdded = true;
        addArmoredElytraLayer();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void addArmoredElytraLayer() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return;
        }
        EntityModelSet modelSet = minecraft.getEntityModels();
        if (modelSet == null) {
            return;
        }

        LivingEntityRenderer renderer = (LivingEntityRenderer) (Object) this;
        RenderLayerParent parent = (RenderLayerParent) (Object) this;
        renderer.addLayer(new ArmoredElytraLayer(parent, modelSet));
    }
}

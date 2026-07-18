package org.betterx.betterend.item.model;

import org.betterx.bclib.client.render.HumanoidArmorRenderer;
import org.betterx.betterend.BetterEnd;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelType;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class CrystaliteArmorRenderer extends HumanoidArmorRenderer {
    private final static Identifier FIRST_LAYER = BetterEnd.C.mk(
            "textures/models/armor/crystalite_layer_1.png");
    private final static Identifier SECOND_LAYER = BetterEnd.C.mk(
            "textures/models/armor/crystalite_layer_2.png");
    private final static CrystaliteHelmetModel HELMET_MODEL = CrystaliteHelmetModel.createModel(null);
    private final static CrystaliteChestplateModel CHEST_MODEL = CrystaliteChestplateModel.createRegularModel(null);
    private final static CrystaliteChestplateModel CHEST_MODEL_SLIM = CrystaliteChestplateModel.createThinModel(null);
    private final static CrystaliteLeggingsModel LEGGINGS_MODEL = CrystaliteLeggingsModel.createModel(null);
    private final static CrystaliteBootsModel BOOTS_MODEL = CrystaliteBootsModel.createModel(null);
    private static CrystaliteArmorRenderer INSTANCE = null;

    public static CrystaliteArmorRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrystaliteArmorRenderer();
        }
        return INSTANCE;
    }

    public Identifier textureForSlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS ? SECOND_LAYER : FIRST_LAYER;
    }

    public HumanoidModel<?> modelFor(@Nullable LivingEntity entity, EquipmentSlot slot) {
        return getModelForSlot(entity, slot);
    }

    public HumanoidModel<?> modelFor(EquipmentSlot slot, @Nullable Model originalModel) {
        if (slot == EquipmentSlot.HEAD) return HELMET_MODEL;
        if (slot == EquipmentSlot.LEGS) return LEGGINGS_MODEL;
        if (slot == EquipmentSlot.FEET) return BOOTS_MODEL;
        if (slot == EquipmentSlot.CHEST) {
            return isSlimPlayerModel(originalModel) ? CHEST_MODEL_SLIM : CHEST_MODEL;
        }
        return null;
    }

    @NotNull
    @Override
    protected Identifier getTextureForSlot(EquipmentSlot slot, boolean innerLayer) {
        return innerLayer ? SECOND_LAYER : FIRST_LAYER;
    }

    @Override
    protected HumanoidModel<?> getModelForSlot(@Nullable LivingEntity entity, EquipmentSlot slot) {
        if (slot == EquipmentSlot.HEAD) return HELMET_MODEL;
        if (slot == EquipmentSlot.LEGS) return LEGGINGS_MODEL;
        if (slot == EquipmentSlot.FEET) return BOOTS_MODEL;
        if (slot == EquipmentSlot.CHEST) {
            if (entity instanceof AbstractClientPlayer acp && acp.getSkin().model() == PlayerModelType.SLIM) {
                return CHEST_MODEL_SLIM;
            } else {
                return CHEST_MODEL;
            }
        }
        return null;
    }

    private static boolean isSlimPlayerModel(@Nullable Model model) {
        if (model == null) {
            return false;
        }
        Class<?> type = model.getClass();
        while (type != null && type != Object.class) {
            try {
                Field slimField = type.getDeclaredField("slim");
                if (slimField.getType() != boolean.class) {
                    return false;
                }
                slimField.setAccessible(true);
                return slimField.getBoolean(model);
            } catch (NoSuchFieldException ignored) {
                type = type.getSuperclass();
            } catch (IllegalAccessException | SecurityException ignored) {
                return false;
            }
        }
        return false;
    }
}

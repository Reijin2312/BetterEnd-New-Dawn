package org.betterx.betterend.item;

import org.betterx.betterend.item.material.EndArmorTier;
import org.betterx.betterend.registry.EndAttributes;
import org.betterx.wover.complex.api.equipment.ArmorSlot;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorType;

public class CrystaliteHelmet extends CrystaliteArmor {
    private static ItemAttributeModifiers defaultAttributes() {
        return EndArmorItem.startAttributeBuilder(
                ArmorSlot.HELMET_SLOT,
                EndArmorTier.CRYSTALITE
        ).add(
                EndAttributes.BLINDNESS_RESISTANCE,
                new AttributeModifier(
                        EndArmorItem.BASE_BLINDNESS_RESISTANCE,
                        1.0,
                        AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.HEAD
        ).build();
    }

    public CrystaliteHelmet() {
        this(defaultAttributes());
    }

    private CrystaliteHelmet(ItemAttributeModifiers attributes) {
        super(
                ArmorType.HELMET,
                EndArmorItem.createDefaultEndArmorSettings(
                        ArmorSlot.HELMET_SLOT,
                        EndArmorTier.CRYSTALITE,
                        attributes
                ),
                attributes
        );
    }
}

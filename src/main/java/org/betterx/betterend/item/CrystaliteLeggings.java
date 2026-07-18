package org.betterx.betterend.item;

import org.betterx.betterend.item.material.EndArmorTier;
import org.betterx.wover.complex.api.equipment.ArmorSlot;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorType;

public class CrystaliteLeggings extends CrystaliteArmor {
    private static ItemAttributeModifiers defaultAttributes() {
        return EndArmorItem.startAttributeBuilder(
                ArmorSlot.LEGGINGS_SLOT,
                EndArmorTier.CRYSTALITE
        ).add(
                Attributes.MAX_HEALTH,
                new AttributeModifier(
                        EndArmorItem.MAX_HEALTH_BOOST,
                        4.0,
                        AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.LEGS
        ).build();
    }

    public CrystaliteLeggings() {
        this(defaultAttributes());
    }

    private CrystaliteLeggings(ItemAttributeModifiers attributes) {
        super(
                ArmorType.LEGGINGS,
                EndArmorItem.createDefaultEndArmorSettings(
                        ArmorSlot.LEGGINGS_SLOT,
                        EndArmorTier.CRYSTALITE,
                        attributes
                ),
                attributes
        );
    }
}

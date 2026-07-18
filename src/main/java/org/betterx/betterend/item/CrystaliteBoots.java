package org.betterx.betterend.item;

import org.betterx.betterend.effects.EndStatusEffects;
import org.betterx.betterend.interfaces.MobEffectApplier;
import org.betterx.betterend.item.material.EndArmorTier;
import org.betterx.wover.complex.api.equipment.ArmorSlot;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.function.Consumer;

public class CrystaliteBoots extends CrystaliteArmor implements MobEffectApplier {
    private static ItemAttributeModifiers defaultAttributes() {
        return EndArmorItem.startAttributeBuilder(
                ArmorSlot.BOOTS_SLOT,
                EndArmorTier.CRYSTALITE
        ).build();
    }

    public CrystaliteBoots() {
        this(defaultAttributes());
    }

    private CrystaliteBoots(ItemAttributeModifiers attributes) {
        super(
                ArmorType.BOOTS,
                EndArmorItem.createDefaultEndArmorSettings(
                        ArmorSlot.BOOTS_SLOT,
                        EndArmorTier.CRYSTALITE,
                        attributes
                ),
                attributes
        );
    }

    @Override
    public void applyEffect(LivingEntity owner) {
        if ((owner.tickCount & 63) == 0) {
            owner.addEffect(new MobEffectInstance(EndStatusEffects.CRYSTALITE_MOVE_SPEED));
        }
    }

    @Override
    public void appendHoverText(
            ItemStack itemStack,
            Item.TooltipContext tooltipContext,
            TooltipDisplay tooltipDisplay,
            Consumer<Component> consumer,
            TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
        consumer.accept(Component.empty());
        consumer.accept(BOOTS_DESC);
    }
}

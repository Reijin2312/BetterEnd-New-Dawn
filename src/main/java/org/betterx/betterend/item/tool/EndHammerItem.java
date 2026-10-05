package org.betterx.betterend.item.tool;

import org.betterx.bclib.interfaces.ItemModelProvider;
import org.betterx.betterend.BetterEnd;
import org.betterx.wover.item.api.ItemTagProvider;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class EndHammerItem extends DiggerItem implements ItemModelProvider, ItemTagProvider {
    public final static ResourceLocation ATTACK_KNOCKBACK_MODIFIER_ID = BetterEnd.C.mk("base_knockback");

    public static ItemAttributeModifiers createAttributes(
            Tier tier,
            float attackDamage,
            float attackSpeed,
            float knockback
    ) {
        return ItemAttributeModifiers
                .builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                BASE_ATTACK_DAMAGE_ID,
                                attackDamage + tier.getAttackDamageBonus(),
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(
                                BASE_ATTACK_SPEED_ID,
                                attackSpeed,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_KNOCKBACK,
                        new AttributeModifier(
                                ATTACK_KNOCKBACK_MODIFIER_ID,
                                knockback,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    public EndHammerItem(Tier material, float attackDamage, float attackSpeed, float knockback, Properties settings) {
        super(
                material,
                BlockTags.MINEABLE_WITH_PICKAXE,
                settings.attributes(createAttributes(material, attackDamage, attackSpeed, knockback))
        );
    }

    @Override
    public void registerItemTags(ResourceLocation location, ItemTagBootstrapContext context) {
        context.add(this,
                CommonItemTags.HAMMERS,
                ItemTags.MINING_ENCHANTABLE,
                ItemTags.MINING_LOOT_ENCHANTABLE,
                ItemTags.DURABILITY_ENCHANTABLE);
    }
}

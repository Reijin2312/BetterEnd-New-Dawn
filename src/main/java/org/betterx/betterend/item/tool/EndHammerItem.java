package org.betterx.betterend.item.tool;

import org.betterx.bclib.interfaces.ItemModelProvider;
import org.betterx.betterend.BetterEnd;
import org.betterx.wover.item.api.ItemTagProvider;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonItemTags;
import org.betterx.wover.tag.api.predefined.MineableTags;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class EndHammerItem extends Item implements ItemModelProvider, ItemTagProvider {
    public final static Identifier ATTACK_KNOCKBACK_MODIFIER_ID = BetterEnd.C.mk("base_knockback");

    public static ItemAttributeModifiers createAttributes(
            ToolMaterial tier,
            float attackDamage,
            float attackSpeed,
            float knockback
    ) {
        return ItemAttributeModifiers
                .builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                Item.BASE_ATTACK_DAMAGE_ID,
                                attackDamage + tier.attackDamageBonus(),
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(
                                Item.BASE_ATTACK_SPEED_ID,
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

    public EndHammerItem(ToolMaterial material, float attackDamage, float attackSpeed, float knockback, Properties settings) {
        super(settings
                .durability(material.durability())
                .repairable(material.repairItems())
                .enchantable(material.enchantmentValue())
                .attributes(createAttributes(material, attackDamage, attackSpeed, knockback)));
    }

    @Override
    public boolean canDestroyBlock(
            ItemStack stack,
            BlockState state,
            Level world,
            BlockPos pos,
            LivingEntity miner
    ) {
        return state.is(MineableTags.HAMMER)
                || state.is(Blocks.DIAMOND_BLOCK)
                || state.is(Blocks.EMERALD_BLOCK)
                || state.is(Blocks.LAPIS_BLOCK)
                || state.is(Blocks.REDSTONE_BLOCK);
    }

    @Override
    public void registerItemTags(Identifier location, ItemTagBootstrapContext context) {
        context.add(this, CommonItemTags.HAMMERS);
    }
}

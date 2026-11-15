package org.betterx.betterend.item;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.interfaces.BetterEndElytra;
import org.betterx.betterend.interfaces.MultiModelItem;
import org.betterx.wover.complex.api.equipment.ArmorSlot;
import org.betterx.wover.complex.api.equipment.ArmorTier;
import org.betterx.wover.item.api.ItemTagProvider;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.ArmorType;


public class ArmoredElytra extends EndArmorItem implements MultiModelItem, BetterEndElytra, ItemTagProvider {
    private final Identifier wingTexture;
    private final Item repairItem;
    private final double movementFactor;
    private final float toughness;
    private final int defense;

    private static ResourceKey<EquipmentAsset> getElytraAssetKey(String name) {
        return ResourceKey.create(EquipmentAssets.ROOT_ID, BetterEnd.C.mk(name));
    }

    private static ArmorMaterial getElytraArmorMaterial(ArmorTier material, String name) {
        ArmorMaterial base = material.armorMaterial;
        return new ArmorMaterial(
                base.durability(),
                base.defense(),
                base.enchantmentValue(),
                SoundEvents.ARMOR_EQUIP_ELYTRA,
                base.toughness(),
                base.knockbackResistance(),
                base.repairIngredient(),
                getElytraAssetKey(name)
        );
    }

    private static ItemAttributeModifiers defaultAttributes(
            ArmorTier material,
            float defenseDivider,
            float toughnessDivider
    ) {
        final float defense = material.armorMaterial
                .defense()
                .getOrDefault(ArmorType.CHESTPLATE, 0) / defenseDivider;

        final float toughness = material.armorMaterial
                .toughness() / toughnessDivider;

        return EndArmorItem.startAttributeBuilder(
                ArmorSlot.CHESTPLATE_SLOT,
                material,
                defense,
                toughness,
                0.5f
        ).build();
    }

    private static Properties defaultSettings(
            ArmorTier material,
            int durability,
            ItemAttributeModifiers attributes,
            boolean fireproof
    ) {
        final Properties props = EndArmorItem.createDefaultEndArmorSettings(
                                                     ArmorSlot.CHESTPLATE_SLOT, material, attributes
                                             ).rarity(Rarity.EPIC)
                                              .durability(durability)
                                              .component(DataComponents.GLIDER, Unit.INSTANCE);
        if (fireproof) {
            props.fireResistant();
        }
        return props;
    }

    public ArmoredElytra(
            String name,
            ArmorTier material,
            Item repairItem,
            int durability,
            double movementFactor,
            float defenseDivider,
            float toughnessDivider,
            boolean fireproof
    ) {
        this(
                name,
                material,
                repairItem,
                durability,
                movementFactor,
                defenseDivider,
                toughnessDivider,
                fireproof,
                defaultAttributes(material, defenseDivider, toughnessDivider)
        );
    }

    private ArmoredElytra(
            String name,
            ArmorTier material,
            Item repairItem,
            int durability,
            double movementFactor,
            float defenseDivider,
            float toughnessDivider,
            boolean fireproof,
            ItemAttributeModifiers attributes
    ) {
        super(
                getElytraArmorMaterial(material, name),
                ArmorType.CHESTPLATE,
                defaultSettings(material, durability, attributes, fireproof),
                durability,
                attributes
        );
        this.wingTexture = BetterEnd.C.mk("textures/entity/" + name + ".png");
        this.repairItem = repairItem;
        this.movementFactor = movementFactor;
        this.defense = (int) (material.armorMaterial
                .defense()
                .getOrDefault(ArmorType.CHESTPLATE, 0) / defenseDivider);

        this.toughness = material.armorMaterial
                .toughness() / toughnessDivider;

    }

    @Override
    public double getMovementFactor() {
        return movementFactor;
    }

    @Override
    public Identifier getModelTexture() {
        return wingTexture;
    }

    public boolean isValidRepairItem(ItemStack itemStack, ItemStack itemStack2) {
        return itemStack2.getItem() == repairItem;
    }

    public int getDefense() {
        return defense;
    }

    public float getToughness() {
        return toughness;
    }

    @Override
    public void registerModelPredicate() {
        // In 1.21+, item model conditions are data-driven and "broken" is a built-in property.
    }

    @Override
    public void registerItemTags(Identifier location, ItemTagBootstrapContext context) {
        context.add(this, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.EQUIPPABLE_ENCHANTABLE);
    }
}

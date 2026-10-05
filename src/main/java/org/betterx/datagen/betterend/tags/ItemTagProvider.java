package org.betterx.datagen.betterend.tags;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.complexmaterials.MaterialManager;
import org.betterx.betterend.item.tool.EndHammerItem;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndTags;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.TagManager;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemTagProvider extends WoverTagProvider.ForItems {
    public ItemTagProvider(ModCore modCore) {
        super(modCore);
    }

    public static final TagKey<Item> CAPE_SLOT = TagManager.ITEMS.makeTag(BetterEnd.TRINKETS_CORE, "chest/cape");

    @Override
    public void prepareTags(ItemTagBootstrapContext context) {
        EndItems.getModItems().forEach(item -> {
            if (EndHammerItem.class.isInstance(item)) {
                context.add(CommonItemTags.HAMMERS, item);
            }
        });

        context.add(ItemTags.BEACON_PAYMENT_ITEMS, EndItems.AETERNIUM_INGOT);
        context.add(CommonItemTags.FURNACES, Items.FURNACE, Items.BLAST_FURNACE, Items.SMOKER);

        context.add(EndTags.ALLOYING_IRON, Items.IRON_ORE, Items.DEEPSLATE_IRON_ORE, Items.RAW_IRON);
        context.add(EndTags.ALLOYING_GOLD, Items.GOLD_ORE, Items.DEEPSLATE_GOLD_ORE, Items.RAW_GOLD);
        context.add(EndTags.ALLOYING_COPPER, Items.COPPER_ORE, Items.DEEPSLATE_COPPER_ORE, Items.RAW_COPPER);

        context.add(ItemTags.FISHES, EndItems.END_FISH_RAW, EndItems.END_FISH_COOKED);
        context.add(ItemTags.ARMOR_ENCHANTABLE,
                EndItems.AETERNIUM_HELMET, EndItems.AETERNIUM_CHESTPLATE, EndItems.AETERNIUM_LEGGINGS, EndItems.AETERNIUM_BOOTS,
                EndItems.CRYSTALITE_HELMET, EndItems.CRYSTALITE_CHESTPLATE, EndItems.CRYSTALITE_LEGGINGS, EndItems.CRYSTALITE_BOOTS,
                EndBlocks.THALLASIUM.helmet, EndBlocks.THALLASIUM.chestplate, EndBlocks.THALLASIUM.leggings, EndBlocks.THALLASIUM.boots,
                EndBlocks.TERMINITE.helmet, EndBlocks.TERMINITE.chestplate, EndBlocks.TERMINITE.leggings, EndBlocks.TERMINITE.boots);
        context.add(ItemTags.DURABILITY_ENCHANTABLE,
                EndItems.AETERNIUM_HELMET, EndItems.AETERNIUM_CHESTPLATE, EndItems.AETERNIUM_LEGGINGS, EndItems.AETERNIUM_BOOTS,
                EndItems.CRYSTALITE_HELMET, EndItems.CRYSTALITE_CHESTPLATE, EndItems.CRYSTALITE_LEGGINGS, EndItems.CRYSTALITE_BOOTS,
                EndBlocks.THALLASIUM.helmet, EndBlocks.THALLASIUM.chestplate, EndBlocks.THALLASIUM.leggings, EndBlocks.THALLASIUM.boots,
                EndBlocks.TERMINITE.helmet, EndBlocks.TERMINITE.chestplate, EndBlocks.TERMINITE.leggings, EndBlocks.TERMINITE.boots);
        context.add(ItemTags.HEAD_ARMOR_ENCHANTABLE,
                EndItems.AETERNIUM_HELMET, EndItems.CRYSTALITE_HELMET, EndBlocks.THALLASIUM.helmet, EndBlocks.TERMINITE.helmet);
        context.add(ItemTags.CHEST_ARMOR_ENCHANTABLE,
                EndItems.AETERNIUM_CHESTPLATE, EndItems.CRYSTALITE_CHESTPLATE, EndBlocks.THALLASIUM.chestplate, EndBlocks.TERMINITE.chestplate);
        context.add(ItemTags.LEG_ARMOR_ENCHANTABLE,
                EndItems.AETERNIUM_LEGGINGS, EndItems.CRYSTALITE_LEGGINGS, EndBlocks.THALLASIUM.leggings, EndBlocks.TERMINITE.leggings);
        context.add(ItemTags.FOOT_ARMOR_ENCHANTABLE,
                EndItems.AETERNIUM_BOOTS, EndItems.CRYSTALITE_BOOTS, EndBlocks.THALLASIUM.boots, EndBlocks.TERMINITE.boots);

/*
TERMINITE = DIAMOND
AETERNIUM > NETHERITE
 */
        context.add(EndTags.ANVIL_AETERNIUM_TOOL, EndItems.AETERNIUM_HAMMER);
        context.add(ItemTags.SPEARS, EndItems.AETERNIUM_SPEAR, EndBlocks.THALLASIUM.spear, EndBlocks.TERMINITE.spear);

        context.add(EndTags.ANVIL_NETHERITE_TOOL, EndTags.ANVIL_AETERNIUM_TOOL);
        context.add(EndTags.ANVIL_NETHERITE_TOOL, EndItems.NETHERITE_HAMMER);

        context.add(EndTags.ANVIL_DIAMOND_TOOL, EndTags.ANVIL_NETHERITE_TOOL);
        context.add(EndTags.ANVIL_DIAMOND_TOOL, EndItems.DIAMOND_HAMMER, EndBlocks.TERMINITE.hammer);

        context.add(EndTags.ANVIL_IRON_TOOL, EndTags.ANVIL_DIAMOND_TOOL);
        context.add(EndTags.ANVIL_IRON_TOOL, EndItems.IRON_HAMMER, EndItems.GOLDEN_HAMMER, EndBlocks.THALLASIUM.hammer);

        MaterialManager.stream().forEach(m -> m.registerItemTags(context));

        context.add(CAPE_SLOT, EndItems.CRYSTALITE_ELYTRA, EndItems.ARMORED_ELYTRA);
    }
}

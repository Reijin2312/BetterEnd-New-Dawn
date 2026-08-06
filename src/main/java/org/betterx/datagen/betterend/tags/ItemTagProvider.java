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

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
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

/*
TERMINITE = DIAMOND
AETERNIUM > NETHERITE
 */
        context.add(EndTags.ANVIL_AETERNIUM_TOOL, EndItems.AETERNIUM_HAMMER);

        context.add(EndTags.ANVIL_NETHERITE_TOOL, EndTags.ANVIL_AETERNIUM_TOOL);
        context.add(EndTags.ANVIL_NETHERITE_TOOL, EndItems.NETHERITE_HAMMER);

        context.add(EndTags.ANVIL_DIAMOND_TOOL, EndTags.ANVIL_NETHERITE_TOOL);
        context.add(EndTags.ANVIL_DIAMOND_TOOL, EndItems.DIAMOND_HAMMER, EndBlocks.TERMINITE.hammer);

        context.add(EndTags.ANVIL_IRON_TOOL, EndTags.ANVIL_DIAMOND_TOOL);
        context.add(EndTags.ANVIL_IRON_TOOL, EndItems.IRON_HAMMER, EndItems.GOLDEN_HAMMER, EndBlocks.THALLASIUM.hammer);

        MaterialManager.stream().forEach(m -> m.registerItemTags(context));
        registerSulfurCubeArchetypes(context);

        context.add(CAPE_SLOT, EndItems.CRYSTALITE_ELYTRA, EndItems.ARMORED_ELYTRA);
    }

    private static void registerSulfurCubeArchetypes(ItemTagBootstrapContext context) {
        addSulfurCubeArchetype(context, "bouncy", "amaranita_cap,amaranita_hymenophore,amaranita_hyphae,amaranita_stem,dragon_tree_bark,dragon_tree_log,dragon_tree_planks,dragon_tree_stripped_bark,dragon_tree_stripped_log,end_lotus_bark,end_lotus_log,end_lotus_planks,end_lotus_stripped_bark,end_lotus_stripped_log,filalux_lantern,helix_tree_bark,helix_tree_log,helix_tree_planks,helix_tree_stripped_bark,helix_tree_stripped_log,jellyshroom_bark,jellyshroom_log,jellyshroom_planks,jellyshroom_stripped_bark,jellyshroom_stripped_log,lacugrove_bark,lacugrove_log,lacugrove_planks,lacugrove_stripped_bark,lacugrove_stripped_log,lucernia_bark,lucernia_log,lucernia_planks,lucernia_stripped_bark,lucernia_stripped_log,mossy_glowshroom_bark,mossy_glowshroom_cap,mossy_glowshroom_log,mossy_glowshroom_planks,mossy_glowshroom_stripped_bark,mossy_glowshroom_stripped_log,pythadendron_bark,pythadendron_log,pythadendron_planks,pythadendron_stripped_bark,pythadendron_stripped_log,tenanea_bark,tenanea_log,tenanea_planks,tenanea_stripped_bark,tenanea_stripped_log,umbrella_tree_bark,umbrella_tree_cluster,umbrella_tree_cluster_empty,umbrella_tree_log,umbrella_tree_planks,umbrella_tree_stripped_bark,umbrella_tree_stripped_log");
        addSulfurCubeArchetype(context, "fast_flat", "hydralux_petal_block,hydralux_petal_block_black,hydralux_petal_block_blue,hydralux_petal_block_brown,hydralux_petal_block_cyan,hydralux_petal_block_gray,hydralux_petal_block_green,hydralux_petal_block_light_blue,hydralux_petal_block_light_gray,hydralux_petal_block_lime,hydralux_petal_block_magenta,hydralux_petal_block_orange,hydralux_petal_block_pink,hydralux_petal_block_purple,hydralux_petal_block_red,hydralux_petal_block_white,hydralux_petal_block_yellow");
        addSulfurCubeArchetype(context, "fast_sliding", "ancient_emerald_ice,dense_emerald_ice,dense_snow,emerald_ice");
        addSulfurCubeArchetype(context, "regular", "dragon_bone_block");
        addSulfurCubeArchetype(context, "slow_bouncy", "amber_block,amber_moss,amber_ore,aurora_crystal,azure_jadestone,azure_jadestone_bricks,azure_jadestone_pillar,azure_jadestone_polished,azure_jadestone_tiles,brimstone,budding_smaragdant_crystal,cave_moss,charcoal_block,chorus_nylium,crystal_moss,end_moss,end_mycelium,end_stone_brick_cracked,end_stone_brick_weathered,end_stone_smelter,ender_block,ender_ore,flavolite,flavolite_bricks,flavolite_pillar,flavolite_polished,flavolite_runed,flavolite_runed_eternal,flavolite_tiles,jungle_moss,missing_tile,mossy_dragon_bone,mossy_obsidian,pallidium_full,pallidium_heavy,pallidium_thin,pallidium_tiny,pink_moss,rutiscus,sandy_jadestone,sandy_jadestone_bricks,sandy_jadestone_pillar,sandy_jadestone_polished,sandy_jadestone_tiles,sangnum,shadow_grass,smaragdant_crystal,smaragdant_crystal_bricks,smaragdant_crystal_pillar,smaragdant_crystal_polished,smaragdant_crystal_tiles,sulphuric_rock,sulphuric_rock_bricks,sulphuric_rock_pillar,sulphuric_rock_polished,sulphuric_rock_tiles,umbralith,umbralith_bricks,umbralith_pillar,umbralith_polished,umbralith_tiles,violecite,violecite_bricks,violecite_pillar,violecite_polished,violecite_tiles,virid_jadestone,virid_jadestone_bricks,virid_jadestone_pillar,virid_jadestone_polished,virid_jadestone_tiles");
        addSulfurCubeArchetype(context, "slow_flat", "aeternium_block,terminite_block,terminite_tile,thallasium_block,thallasium_ore,thallasium_tile");
        addSulfurCubeArchetype(context, "slow_sliding", "amaranita_lantern,blue_vine_lantern,glowing_pillar_luminophor,jellyshroom_cap_purple,mossy_glowshroom_hymenophore,umbrella_tree_membrane");
    }

    private static void addSulfurCubeArchetype(
            ItemTagBootstrapContext context,
            String archetype,
            String itemPaths
    ) {
        TagKey<Item> tag = TagManager.ITEMS.makeTag(
                Identifier.withDefaultNamespace("sulfur_cube_archetype/" + archetype)
        );
        for (String path : itemPaths.split(",")) {
            Identifier itemId = BetterEnd.C.mk(path);
            Item item = BuiltInRegistries.ITEM.getValue(itemId);
            if (item == null || item == Items.AIR) {
                throw new IllegalStateException("Missing BetterEnd sulfur cube archetype item: " + itemId);
            }
            context.add(tag, item);
        }
    }
}

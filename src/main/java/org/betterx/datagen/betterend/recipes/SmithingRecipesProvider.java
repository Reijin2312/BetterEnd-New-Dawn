package org.betterx.datagen.betterend.recipes;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndTemplates;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverRecipeProvider;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class SmithingRecipesProvider extends WoverRecipeProvider {
    public SmithingRecipesProvider(ModCore modCore) {
        super(modCore, "BetterEnd - Smithing Recipes");
    }

    @Override
    protected void bootstrap(net.minecraft.data.worldgen.BootstrapContextAccess provider, RecipeOutput context) {
        RecipeBuilder.smithing(BetterEnd.C.mk("aeternium_pickaxe"), EndItems.AETERNIUM_PICKAXE)
                     .template(EndTemplates.LEATHER_HANDLE_ATTACHMENT)
                     .base(EndItems.AETERNIUM_PICKAXE_HEAD)
                     .addon(EndItems.LEATHER_WRAPPED_STICK)
                     .build(context);
        RecipeBuilder.smithing(BetterEnd.C.mk("aeternium_axe"), EndItems.AETERNIUM_AXE)
                     .template(EndTemplates.LEATHER_HANDLE_ATTACHMENT)
                     .base(EndItems.AETERNIUM_AXE_HEAD)
                     .addon(EndItems.LEATHER_WRAPPED_STICK)
                     .build(context);
        RecipeBuilder.smithing(BetterEnd.C.mk("aeternium_shovel"), EndItems.AETERNIUM_SHOVEL)
                     .template(EndTemplates.LEATHER_HANDLE_ATTACHMENT)
                     .base(EndItems.AETERNIUM_SHOVEL_HEAD)
                     .addon(EndItems.LEATHER_WRAPPED_STICK)
                     .build(context);
        RecipeBuilder.smithing(BetterEnd.C.mk("aeternium_spear"), EndItems.AETERNIUM_SPEAR)
                     .template(EndTemplates.LEATHER_HANDLE_ATTACHMENT)
                     .base(EndItems.AETERNIUM_SPEAR_TIP)
                     .addon(EndItems.LEATHER_WRAPPED_STICK)
                     .build(context);
        RecipeBuilder.smithing(BetterEnd.C.mk("aeternium_hoe"), EndItems.AETERNIUM_HOE)
                     .template(EndTemplates.LEATHER_HANDLE_ATTACHMENT)
                     .base(EndItems.AETERNIUM_HOE_HEAD)
                     .addon(EndItems.LEATHER_WRAPPED_STICK)
                     .build(context);
        RecipeBuilder.smithing(BetterEnd.C.mk("aeternium_hammer"), EndItems.AETERNIUM_HAMMER)
                     .template(EndTemplates.LEATHER_HANDLE_ATTACHMENT)
                     .base(EndItems.AETERNIUM_HAMMER_HEAD)
                     .addon(EndItems.LEATHER_WRAPPED_STICK)
                     .build(context);

        RecipeBuilder.smithing(BetterEnd.C.mk("aeternium_sword"), EndItems.AETERNIUM_SWORD)
                     .template(EndTemplates.TOOL_ASSEMBLY)
                     .base(EndItems.AETERNIUM_SWORD_BLADE)
                     .addon(EndItems.AETERNIUM_SWORD_HANDLE)
                     .build(context);

        RecipeBuilder.smithing(BetterEnd.C.mk("aeternium_helmet"), EndItems.AETERNIUM_HELMET)
                     .template(EndTemplates.PLATE_UPGRADE)
                     .base(EndBlocks.TERMINITE.helmet)
                     .addon(EndItems.AETERNIUM_FORGED_PLATE)
                     .build(context);
        RecipeBuilder.smithing(BetterEnd.C.mk("aeternium_chestplate"), EndItems.AETERNIUM_CHESTPLATE)
                     .template(EndTemplates.PLATE_UPGRADE)
                     .base(EndBlocks.TERMINITE.chestplate)
                     .addon(EndItems.AETERNIUM_FORGED_PLATE)
                     .build(context);
        RecipeBuilder.smithing(BetterEnd.C.mk("aeternium_leggings"), EndItems.AETERNIUM_LEGGINGS)
                     .template(EndTemplates.PLATE_UPGRADE)
                     .base(EndBlocks.TERMINITE.leggings)
                     .addon(EndItems.AETERNIUM_FORGED_PLATE)
                     .build(context);
        RecipeBuilder.smithing(BetterEnd.C.mk("aeternium_boots"), EndItems.AETERNIUM_BOOTS)
                     .template(EndTemplates.PLATE_UPGRADE)
                     .base(EndBlocks.TERMINITE.boots)
                     .addon(EndItems.AETERNIUM_FORGED_PLATE)
                     .build(context);

        RecipeBuilder.smithing(BetterEnd.C.mk("aeternium_sword_handle"), EndItems.AETERNIUM_SWORD_HANDLE)
                     .template(EndTemplates.TERMINITE_UPGRADE)
                     .base(EndItems.LEATHER_WRAPPED_STICK)
                     .addon(EndBlocks.TERMINITE.ingot)
                     .build(context);

        RecipeBuilder.smithing(BetterEnd.C.mk("thallasium_anvil_updrade"), EndBlocks.TERMINITE.anvilBlock)
                     .template(EndTemplates.TERMINITE_UPGRADE)
                     .base(EndBlocks.THALLASIUM.anvilBlock)
                     .addon(EndBlocks.TERMINITE.ingot)
                     .build(context);

        RecipeBuilder.smithing(BetterEnd.C.mk("terminite_anvil_updrade"), EndBlocks.AETERNIUM_ANVIL)
                     .template(EndTemplates.AETERNIUM_UPGRADE)
                     .base(EndBlocks.TERMINITE.anvilBlock)
                     .addon(EndItems.AETERNIUM_INGOT)
                     .build(context);


        RecipeBuilder.smithing(BetterEnd.C.mk("armored_elytra"), EndItems.ARMORED_ELYTRA)
                     .template(EndTemplates.AETERNIUM_UPGRADE)
                     .base(Items.ELYTRA)
                     .addon(EndItems.AETERNIUM_INGOT)
                     .build(context);


        RecipeBuilder.smithing(BetterEnd.C.mk("netherite_hammer"), EndItems.NETHERITE_HAMMER)
                     .template(EndTemplates.NETHERITE_UPGRADE)
                     .base(EndItems.DIAMOND_HAMMER)
                     .addon(Items.NETHERITE_INGOT)
                     .build(context);


        RecipeBuilder.copySmithingTemplate(
                BetterEnd.C.mk("copy_plate_upgrade"),
                RecipeBuilder.CopySmithingTemplateCostLevel.REGULAR,
                EndTemplates.PLATE_UPGRADE,
                Items.IRON_INGOT
        ).build(context);

        RecipeBuilder.copySmithingTemplate(
                BetterEnd.C.mk("copy_leather_handle_attachment"),
                RecipeBuilder.CopySmithingTemplateCostLevel.REGULAR,
                EndTemplates.LEATHER_HANDLE_ATTACHMENT,
                Items.LEATHER
        ).build(context);

        RecipeBuilder.copySmithingTemplate(
                BetterEnd.C.mk("copy_handle_attachment"),
                RecipeBuilder.CopySmithingTemplateCostLevel.CHEAP,
                EndTemplates.HANDLE_ATTACHMENT,
                Items.DIAMOND
        ).build(context);

        RecipeBuilder.copySmithingTemplate(
                BetterEnd.C.mk("copy_terminite_upgrade"),
                RecipeBuilder.CopySmithingTemplateCostLevel.CHEAP,
                EndTemplates.TERMINITE_UPGRADE,
                EndBlocks.TERMINITE.ingot
        ).build(context);

        RecipeBuilder.copySmithingTemplate(
                BetterEnd.C.mk("copy_thallasium_upgrade"),
                RecipeBuilder.CopySmithingTemplateCostLevel.CHEAP,
                EndTemplates.THALLASIUM_UPGRADE,
                EndBlocks.THALLASIUM.ingot
        ).build(context);

        RecipeBuilder.copySmithingTemplate(
                BetterEnd.C.mk("copy_aeternium_upgrade"),
                RecipeBuilder.CopySmithingTemplateCostLevel.REGULAR,
                EndTemplates.AETERNIUM_UPGRADE,
                Blocks.LAPIS_BLOCK
        ).build(context);

        RecipeBuilder.copySmithingTemplate(
                BetterEnd.C.mk("copy_tool_assembly"),
                RecipeBuilder.CopySmithingTemplateCostLevel.CHEAP,
                EndTemplates.TOOL_ASSEMBLY,
                Blocks.IRON_BLOCK
        ).build(context);

        RecipeBuilder.copySmithingTemplate(
                BetterEnd.C.mk("copy_netherite_upgrade"),
                RecipeBuilder.CopySmithingTemplateCostLevel.REGULAR,
                EndTemplates.NETHERITE_UPGRADE,
                Blocks.NETHERRACK
        ).build(context);
    }
}

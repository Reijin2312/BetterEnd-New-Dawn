package org.betterx.datagen.betterend.recipes;

import org.betterx.bclib.recipes.BCLRecipeBuilder;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.item.material.EndToolMaterial;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndTags;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverRecipeProvider;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

public class AnvilRecipesProvider extends WoverRecipeProvider {
    public AnvilRecipesProvider(ModCore modCore) {
        super(modCore, "BetterEnd - Anvil Recipes");
    }

    protected void bootstrap(net.minecraft.data.worldgen.BootstrapContextAccess provider, RecipeOutput context) {
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("ender_pearl_to_dust"), EndItems.ENDER_DUST)
                        .setPrimaryInputAndUnlock(Items.ENDER_PEARL)
                        .setAnvilLevel(EndToolMaterial.THALLASIUM.getLevel())
                        .setAllowedTools(EndTags.ANVIL_IRON_TOOL)
                        .setDamage(5)
                        .build(context);

        BCLRecipeBuilder.anvil(BetterEnd.C.mk("ender_shard_to_dust"), EndItems.ENDER_DUST)
                        .setPrimaryInputAndUnlock(EndItems.ENDER_SHARD)
                        .setAnvilLevel(EndToolMaterial.THALLASIUM.getLevel())
                        .setAllowedTools(EndTags.ANVIL_IRON_TOOL)
                        .setDamage(3)
                        .build(context);

        final int anvilLevel = EndToolMaterial.AETERNIUM.getLevel();
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_axe_head"), EndItems.AETERNIUM_AXE_HEAD)
                        .setPrimaryInputAndUnlock(EndItems.AETERNIUM_INGOT)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_NETHERITE_TOOL)
                        .setDamage(6)
                        .build(context);
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_pickaxe_head"), EndItems.AETERNIUM_PICKAXE_HEAD)
                        .setPrimaryInputAndUnlock(EndItems.AETERNIUM_INGOT)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_NETHERITE_TOOL)
                        .setDamage(6)
                        .build(context);
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_shovel_head"), EndItems.AETERNIUM_SHOVEL_HEAD)
                        .setPrimaryInputAndUnlock(EndItems.AETERNIUM_INGOT)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_NETHERITE_TOOL)
                        .setDamage(6)
                        .build(context);
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_hoe_head"), EndItems.AETERNIUM_HOE_HEAD)
                        .setPrimaryInputAndUnlock(EndItems.AETERNIUM_INGOT)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_NETHERITE_TOOL)
                        .setDamage(6)
                        .build(context);
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_hammer_head"), EndItems.AETERNIUM_HAMMER_HEAD)
                        .setPrimaryInputAndUnlock(EndItems.AETERNIUM_INGOT)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_NETHERITE_TOOL)
                        .setDamage(6)
                        .build(context);
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_sword_blade"), EndItems.AETERNIUM_SWORD_BLADE)
                        .setPrimaryInputAndUnlock(EndItems.AETERNIUM_INGOT)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_DIAMOND_TOOL)
                        .setDamage(6)
                        .build(context);
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_spear_tip"), EndItems.AETERNIUM_SPEAR_TIP)
                        .setPrimaryInputAndUnlock(EndItems.AETERNIUM_INGOT)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_DIAMOND_TOOL)
                        .setDamage(6)
                        .build(context);
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_forged_plate"), EndItems.AETERNIUM_FORGED_PLATE)
                        .setPrimaryInputAndUnlock(EndItems.AETERNIUM_INGOT)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_NETHERITE_TOOL)
                        .setDamage(6)
                        .build(context);
    }
}

package org.betterx.datagen.betterend.recipes;

import org.betterx.bclib.recipes.BCLRecipeBuilder;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverRecipeProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class AlloyingRecipesProvider extends WoverRecipeProvider {
    public AlloyingRecipesProvider(ModCore modCore) {
        super(modCore, "BetterEnd - Alloying Recipes");
    }

    public void bootstrap(HolderLookup.Provider provider, RecipeOutput context) {
        BCLRecipeBuilder.alloying(BetterEnd.C.mk("additional_iron_ore"), Items.IRON_INGOT)
                        .setInput(Items.IRON_ORE, Items.IRON_ORE)
                        .outputCount(3)
                        .setExperience(2.1F)
                        .build(context);
        BCLRecipeBuilder.alloying(BetterEnd.C.mk("additional_iron_deepslate_ore"), Items.IRON_INGOT)
                        .setInput(Items.DEEPSLATE_IRON_ORE, Items.DEEPSLATE_IRON_ORE)
                        .outputCount(3)
                        .setExperience(2.1F)
                        .build(context);
        BCLRecipeBuilder.alloying(BetterEnd.C.mk("additional_iron_raw"), Items.IRON_INGOT)
                        .setInput(Items.RAW_IRON, Items.RAW_IRON)
                        .outputCount(3)
                        .setExperience(2.1F)
                        .build(context);

        BCLRecipeBuilder.alloying(BetterEnd.C.mk("additional_gold_ore"), Items.GOLD_INGOT)
                        .setInput(Items.GOLD_ORE, Items.GOLD_ORE)
                        .outputCount(3)
                        .setExperience(3F)
                        .build(context);
        BCLRecipeBuilder.alloying(BetterEnd.C.mk("additional_gold_deepslate_ore"), Items.GOLD_INGOT)
                        .setInput(Items.DEEPSLATE_GOLD_ORE, Items.DEEPSLATE_GOLD_ORE)
                        .outputCount(3)
                        .setExperience(3F)
                        .build(context);
        BCLRecipeBuilder.alloying(BetterEnd.C.mk("additional_gold_raw"), Items.GOLD_INGOT)
                        .setInput(Items.RAW_GOLD, Items.RAW_GOLD)
                        .outputCount(3)
                        .setExperience(3F)
                        .build(context);

        BCLRecipeBuilder.alloying(BetterEnd.C.mk("additional_copper_ore"), Items.COPPER_INGOT)
                        .setInput(Items.COPPER_ORE, Items.COPPER_ORE)
                        .outputCount(3)
                        .setExperience(3F)
                        .build(context);
        BCLRecipeBuilder.alloying(BetterEnd.C.mk("additional_copper_deepslate_ore"), Items.COPPER_INGOT)
                        .setInput(Items.DEEPSLATE_COPPER_ORE, Items.DEEPSLATE_COPPER_ORE)
                        .outputCount(3)
                        .setExperience(3F)
                        .build(context);
        BCLRecipeBuilder.alloying(BetterEnd.C.mk("additional_copper_raw"), Items.COPPER_INGOT)
                        .setInput(Items.RAW_COPPER, Items.RAW_COPPER)
                        .outputCount(3)
                        .setExperience(3F)
                        .build(context);

        BCLRecipeBuilder.alloying(BetterEnd.C.mk("additional_netherite"), Items.NETHERITE_SCRAP)
                        .setInput(Blocks.ANCIENT_DEBRIS, Blocks.ANCIENT_DEBRIS)
                        .outputCount(3)
                        .setExperience(6F)
                        .setSmeltTime(1000)
                        .build(context);
        BCLRecipeBuilder.alloying(BetterEnd.C.mk("terminite_ingot"), EndBlocks.TERMINITE.ingot)
                        .setInput(Items.IRON_INGOT, EndItems.ENDER_DUST)
                        .outputCount(1)
                        .setExperience(2.5F)
                        .setSmeltTime(450)
                        .build(context);
        BCLRecipeBuilder.alloying(BetterEnd.C.mk("aeternium_ingot"), EndItems.AETERNIUM_INGOT)
                        .setInput(EndBlocks.TERMINITE.ingot, Items.NETHERITE_INGOT)
                        .outputCount(1)
                        .setExperience(4.5F)
                        .setSmeltTime(850)
                        .build(context);
        BCLRecipeBuilder.alloying(BetterEnd.C.mk("terminite_ingot_thallasium"), EndBlocks.TERMINITE.ingot)
                        .setInput(EndBlocks.THALLASIUM.ingot, EndItems.ENDER_DUST)
                        .outputCount(1)
                        .setExperience(2.5F)
                        .setSmeltTime(450)
                        .build(context);
    }
}

package org.betterx.betterend.integration;

import org.betterx.bclib.integration.ModIntegration;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class DyeDepotIntegration extends ModIntegration {
    public DyeDepotIntegration() {
        super(BetterEnd.DYE_DEPOT);
    }

    @Override
    public void init() {
        RecipeBuilder.BOOTSTRAP_RECIPES.subscribe(this::registerRecipes);
    }

    private void registerRecipes(RecipeOutput context) {
        for (DyeColor color : DyeColor.values()) {
            if (color.getId() < 16) {
                continue;
            }
            ItemLike dye = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(
                    "dye_depot",
                    color.getName() + "_dye"
            ));
            if (dye == null || dye == Items.AIR) {
                continue;
            }

            registerColoredRecipe(
                    context,
                    "hydralux_petal_block",
                    EndBlocks.HYDRALUX_PETAL_BLOCK,
                    color,
                    dye,
                    true
            );
            registerColoredRecipe(
                    context,
                    "iron_bulb_lantern",
                    EndBlocks.IRON_BULB_LANTERN,
                    color,
                    dye,
                    false
            );
            if (EndBlocks.THALLASIUM != null) {
                registerColoredRecipe(
                        context,
                        "thallasium_bulb_lantern",
                        EndBlocks.THALLASIUM.bulb_lantern,
                        color,
                        dye,
                        false
                );
            }
            if (EndBlocks.TERMINITE != null) {
                registerColoredRecipe(
                        context,
                        "terminite_bulb_lantern",
                        EndBlocks.TERMINITE.bulb_lantern,
                        color,
                        dye,
                        false
                );
            }
        }
    }

    private void registerColoredRecipe(
            RecipeOutput context,
            String baseName,
            ItemLike source,
            DyeColor color,
            ItemLike dye,
            boolean craftEight
    ) {
        Identifier blockId = BetterEnd.C.mk(baseName + "_" + color.getName());
        Block result = BuiltInRegistries.BLOCK.getValue(blockId);
        if (result == Blocks.AIR) {
            return;
        }
        if (craftEight) {
            RecipeBuilder.crafting(blockId, result)
                         .outputCount(8)
                         .shape("###", "#D#", "###")
                         .addMaterial('#', source)
                         .addMaterial('D', dye)
                         .build(context);
        } else {
            RecipeBuilder.crafting(blockId, result)
                         .shapeless()
                         .addMaterial('#', source)
                         .addMaterial('D', dye)
                         .build(context);
        }
    }
}

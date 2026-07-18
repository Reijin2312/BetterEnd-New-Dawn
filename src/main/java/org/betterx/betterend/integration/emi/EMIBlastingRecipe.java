package org.betterx.betterend.integration.emi;

import org.betterx.bclib.integration.emi.EMIAbstractAlloyingRecipe;
import org.betterx.bclib.integration.emi.EMIPlugin;
import org.betterx.betterend.BetterEnd;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.*;

import dev.emi.emi.api.EmiRegistry;

public class EMIBlastingRecipe extends EMIAbstractAlloyingRecipe<SingleRecipeInput, BlastingRecipe> {
    public EMIBlastingRecipe(RecipeHolder<BlastingRecipe> recipe) {
        super(recipe, Identifier.fromNamespaceAndPath(
                "emi",
                recipe.id().getNamespace() + "/" + recipe.id().getPath() + "/allloying"
        ), 1, false);
    }

    @Override
    protected int getSmeltTime() {
        return recipe.getCookingTime();
    }

    @Override
    protected float getExperience() {
        return recipe.getExperience();
    }


    static void addAllRecipes(EmiRegistry emiRegistry, RecipeManager manager) {
        EMIPlugin.addAllRecipes(
                emiRegistry, manager, BetterEnd.LOGGER,
                RecipeType.BLASTING, EMIBlastingRecipe::new
        );
    }
}

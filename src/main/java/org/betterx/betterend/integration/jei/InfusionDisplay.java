package org.betterx.betterend.integration.jei;

import org.betterx.betterend.recipe.builders.InfusionRecipe;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class InfusionDisplay {
    public final RecipeHolder<InfusionRecipe> recipe;
    public final int time;

    public InfusionDisplay(RecipeHolder<InfusionRecipe> recipe) {
        this.recipe = recipe;
        this.time = recipe.value().getInfusionTime();
    }

    public List<Ingredient> getIngredients() {
        return recipe.value().getIngredients();
    }

    public Identifier getId() {
        return recipe.id().identifier();
    }
}

package org.betterx.betterend.integration.jei;

import org.betterx.bclib.recipes.AlloyingRecipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;

import java.util.List;

public class AlloyingDisplay {
    public final RecipeHolder<? extends Recipe<?>> recipe;
    private final float experience;
    private final int time;

    private AlloyingDisplay(RecipeHolder<? extends Recipe<?>> recipe, float experience, int time) {
        this.recipe = recipe;
        this.experience = experience;
        this.time = time;
    }

    public static AlloyingDisplay fromAlloying(RecipeHolder<AlloyingRecipe> recipe) {
        return new AlloyingDisplay(recipe, recipe.value().getExperience(), recipe.value().getSmeltTime());
    }

    public static AlloyingDisplay fromBlasting(RecipeHolder<BlastingRecipe> recipe) {
        return new AlloyingDisplay(recipe, recipe.value().experience(), recipe.value().cookingTime());
    }

    public float getExperience() {
        return experience;
    }

    public int getTime() {
        return time;
    }

    public List<Ingredient> getIngredients() {
        if (recipe.value() instanceof AlloyingRecipe alloyingRecipe) {
            return alloyingRecipe.getIngredients();
        }
        if (recipe.value() instanceof BlastingRecipe blastingRecipe) {
            return List.of(blastingRecipe.input());
        }
        return List.of();
    }

    public ItemStack getResultItem(HolderLookup.Provider provider) {
        if (recipe.value() instanceof AlloyingRecipe alloyingRecipe) {
            return alloyingRecipe.getResultItem(provider);
        }
        if (recipe.value() instanceof BlastingRecipe blastingRecipe) {
            ItemStack sampleInput = blastingRecipe.input()
                                                  .items()
                                                  .findFirst()
                                                  .map(item -> new ItemStack(item.value()))
                                                  .orElse(ItemStack.EMPTY);
            if (sampleInput.isEmpty()) {
                return ItemStack.EMPTY;
            }
            return blastingRecipe.assemble(new SingleRecipeInput(sampleInput), provider);
        }
        return ItemStack.EMPTY;
    }

    public Identifier getId() {
        return recipe.id().identifier();
    }
}

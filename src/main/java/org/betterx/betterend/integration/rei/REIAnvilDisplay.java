package org.betterx.betterend.integration.rei;

import org.betterx.bclib.recipes.AnvilRecipe;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.Collections;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class REIAnvilDisplay extends BasicDisplay implements SimpleGridMenuDisplay {

    private final RecipeHolder<AnvilRecipe> recipe;

    public REIAnvilDisplay(RecipeHolder<AnvilRecipe> recipe) {
        super(
                EntryIngredients.ofIngredients(recipe.value().getIngredients()),
                Collections.singletonList(EntryIngredients.of(recipe
                        .value()
                        .getResultItem(Minecraft.getInstance().level.registryAccess())))
        );
        this.recipe = recipe;

        inputs.get(1).forEach(entryStack -> {
            if (entryStack.getValue() instanceof ItemStack itemStack) {
                itemStack.setCount(recipe.value().getInputCount());
            }
        });
    }

    public int getDamage() {
        return recipe.value().getDamage();
    }

    public int getAnvilLevel() {
        return recipe.value().getAnvilLevel();
    }

    @Override
    public @NotNull Optional<Identifier> getDisplayLocation() {
        return Optional.ofNullable(recipe).map(RecipeHolder::id);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return REIPlugin.SMITHING;
    }

    @Override
    public int getWidth() {
        return 2;
    }

    @Override
    public int getHeight() {
        return 1;
    }
}

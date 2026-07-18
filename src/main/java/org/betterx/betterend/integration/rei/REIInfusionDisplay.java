package org.betterx.betterend.integration.rei;

import org.betterx.betterend.recipe.builders.InfusionRecipe;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.Collections;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class REIInfusionDisplay extends BasicDisplay implements SimpleGridMenuDisplay {

    private final RecipeHolder<InfusionRecipe> recipe;
    private final int time;

    public REIInfusionDisplay(RecipeHolder<InfusionRecipe> recipe) {
        super(
                EntryIngredients.ofIngredients(recipe.value().getIngredients()),
                Collections.singletonList(EntryIngredients.of(recipe
                        .value()
                        .getResultItem(Minecraft.getInstance().level.registryAccess())))
        );
        this.recipe = recipe;
        this.time = recipe.value().getInfusionTime();
    }

    public int getInfusionTime() {
        return this.time;
    }

    @Override
    public @NotNull Optional<Identifier> getDisplayLocation() {
        return Optional.ofNullable(recipe).map(RecipeHolder::id);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return REIPlugin.INFUSION;
    }

    // @Override
    // public @NotNull List<List<EntryStack>> getRequiredEntries() {
    // 	return this.input;
    // }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    // @Override
    // public List<List<EntryStack>> getOrganisedInputEntries(ContainerInfo<AbstractContainerMenu> containerInfo, AbstractContainerMenu container) {
    //	return this.input;
    //}
}

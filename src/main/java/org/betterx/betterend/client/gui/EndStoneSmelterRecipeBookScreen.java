package org.betterx.betterend.client.gui;

import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;


import java.util.List;

public class EndStoneSmelterRecipeBookScreen extends RecipeBookComponent<EndStoneSmelterMenu> {
    private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(
            Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled"),
            Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled"),
            Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled_highlighted"),
            Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled_highlighted")
    );

    private final Component recipeFilterName;

    public EndStoneSmelterRecipeBookScreen(
            EndStoneSmelterMenu menu,
            Component recipeFilterName,
            List<RecipeBookComponent.TabInfo> tabs
    ) {
        super(menu, tabs);
        this.recipeFilterName = recipeFilterName;
    }

    @Override
    protected WidgetSprites getFilterButtonTextures() {
        return FILTER_SPRITES;
    }

    @Override
    protected boolean isCraftingSlot(Slot slot) {
        return switch (slot.index) {
            case EndStoneSmelterMenu.INGREDIENT_SLOT_A,
                 EndStoneSmelterMenu.INGREDIENT_SLOT_B,
                 EndStoneSmelterMenu.FUEL_SLOT,
                 EndStoneSmelterMenu.RESULT_SLOT -> true;
            default -> false;
        };
    }

    @Override
    protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay recipeDisplay, ContextMap context) {
        ghostSlots.setResult(this.menu.getResultSlot(), context, recipeDisplay.result());
        if (recipeDisplay instanceof FurnaceRecipeDisplay furnaceRecipeDisplay) {
            ghostSlots.setInput(this.menu.getInputSlotA(), context, furnaceRecipeDisplay.ingredient());
            Slot fuelSlot = this.menu.getFuelSlot();
            if (fuelSlot.getItem().isEmpty()) {
                ghostSlots.setInput(fuelSlot, context, furnaceRecipeDisplay.fuel());
            }
        }
    }

    @Override
    protected Component getRecipeFilterName() {
        return this.recipeFilterName;
    }

    @Override
    protected void selectMatchingRecipes(RecipeCollection recipeCollection, StackedItemContents stackedItemContents) {
        recipeCollection.selectRecipes(stackedItemContents, recipeDisplay -> recipeDisplay instanceof FurnaceRecipeDisplay);
    }
}

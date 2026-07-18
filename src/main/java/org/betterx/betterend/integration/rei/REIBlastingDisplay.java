package org.betterx.betterend.integration.rei;

import org.betterx.betterend.blocks.entities.EndStoneSmelterBlockEntity;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class REIBlastingDisplay extends BasicDisplay implements SimpleGridMenuDisplay {
    private static final List<EntryStack> fuel;

    private final RecipeHolder<BlastingRecipe> recipe;
    private final float xp;
    private final double cookingTime;


    public REIBlastingDisplay(RecipeHolder<BlastingRecipe> recipe) {
        this(recipe, recipe.value().getExperience(), recipe.value().getCookingTime());
    }

    protected REIBlastingDisplay(RecipeHolder<BlastingRecipe> recipe, float xp, double cookingTime) {
        super(
                EntryIngredients.ofIngredients(recipe.value().getIngredients()),
                Collections.singletonList(EntryIngredients.of(recipe
                        .value()
                        .getResultItem(Minecraft.getInstance().level.registryAccess())))
        );
        this.recipe = recipe;
        this.xp = xp;
        this.cookingTime = cookingTime;
    }


    public static List<EntryStack> getFuel() {
        return fuel;
    }

    @Override
    public @NotNull Optional<Identifier> getDisplayLocation() {
        return Optional.ofNullable(recipe).map(RecipeHolder::id);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return REIPlugin.ALLOYING;
    }

    public float getXp() {
        return this.xp;
    }

    public double getCookingTime() {
        return this.cookingTime;
    }

    public Optional<RecipeHolder<?>> getOptionalRecipe() {
        return Optional.ofNullable(recipe);
    }

    @Override
    public int getWidth() {
        return 2;
    }

    @Override
    public int getHeight() {
        return 1;
    }

    static {
        fuel = EndStoneSmelterBlockEntity.availableFuels()
                                         .keySet()
                                         .stream()
                                         .map(Item::getDefaultInstance)
                                         .map(EntryStacks::of)
                                         .map(e -> e.setting(
                                                 EntryStack.Settings.TOOLTIP_APPEND_EXTRA,
                                                 stack -> Collections.singletonList(Component.translatable(
                                                         "category.rei.smelting.fuel").withStyle(ChatFormatting.YELLOW))
                                         ))
                                         .collect(Collectors.toList());
    }
}

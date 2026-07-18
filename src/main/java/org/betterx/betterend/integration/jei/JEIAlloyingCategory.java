package org.betterx.betterend.integration.jei;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;

public class JEIAlloyingCategory implements IRecipeCategory<AlloyingDisplay> {
    private static final Identifier GUI_TEXTURE = BetterEnd.C.mk("textures/gui/smelter_gui.png");
    private static final int WIDTH = 124;
    private static final int HEIGHT = 62;
    private static final DecimalFormat DF = new DecimalFormat("###.##");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated fire;
    private final Component title;

    public JEIAlloyingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(GUI_TEXTURE, 26, 12, WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(
                VanillaTypes.ITEM_STACK,
                new ItemStack(EndBlocks.END_STONE_SMELTER)
        );
        this.title = Component.translatable("betterend.jei.container.alloying");

        this.fire = guiHelper.drawableBuilder(GUI_TEXTURE, 177, 0, 14, 14)
                             .buildAnimated(200, IDrawableAnimated.StartDirection.TOP, true);
        this.arrow = guiHelper.drawableBuilder(GUI_TEXTURE, 176, 15, 24, 17)
                              .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public @NotNull IRecipeType<AlloyingDisplay> getRecipeType() {
        return JEIPlugin.ALLOYING_RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return title;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlloyingDisplay display, IFocusGroup focuses) {
        List<Ingredient> ingredients = display.getIngredients();
        if (!ingredients.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 19, 5)
                   .add(ingredients.get(0));
        }
        if (ingredients.size() > 1 && !ingredients.get(1).isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 41, 5)
                   .add(ingredients.get(1));
        }

        List<ItemStack> fuels = JEIPlugin.ALLOYING_FUELS.isEmpty() ? List.of() : JEIPlugin.ALLOYING_FUELS;
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 30, 41)
               .addItemStacks(fuels);

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            ItemStack result = display.getResultItem(minecraft.level.registryAccess());
            if (!result.isEmpty()) {
                builder.addSlot(RecipeIngredientRole.OUTPUT, 103, 23)
                       .add(result);
            }
        }
    }

    @Override
    public void draw(
            AlloyingDisplay display,
            IRecipeSlotsView recipeSlotsView,
            GuiGraphics guiGraphics,
            double mouseX,
            double mouseY
    ) {
        background.draw(guiGraphics, 0, 0);
        fire.draw(guiGraphics, 32, 25);
        arrow.draw(guiGraphics, 64, 23);

        Component text = Component.translatable(
                "betterend.jei.alloying.experience_time",
                DF.format(display.getExperience()),
                DF.format(display.getTime() / 20.0D)
        );
        guiGraphics.drawString(Minecraft.getInstance().font, text, 55, 49, 0xFF404040, false);
    }
}

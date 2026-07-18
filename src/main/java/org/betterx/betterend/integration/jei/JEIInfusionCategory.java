package org.betterx.betterend.integration.jei;

import org.betterx.betterend.BetterEnd;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JEIInfusionCategory implements IRecipeCategory<InfusionDisplay> {
    private static final Identifier ICON_TEXTURE = BetterEnd.C.mk("textures/gui/infusion_16x16_pixel_optimized.png");
    private static final Identifier BACKGROUND_TEXTURE = BetterEnd.C.mk("textures/gui/jei_infusion.png");
    private static final int WIDTH = 150;
    private static final int HEIGHT = 104;

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public JEIInfusionCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, 0, 0, WIDTH, HEIGHT);
        this.icon = guiHelper.drawableBuilder(ICON_TEXTURE, 0, 0, 16, 16)
                             .setTextureSize(16, 16)
                             .build();
        this.title = Component.translatable("betterend.jei.container.infusion");
    }

    @Override
    public @NotNull IRecipeType<InfusionDisplay> getRecipeType() {
        return JEIPlugin.INFUSION_RECIPE_TYPE;
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
    public void setRecipe(IRecipeLayoutBuilder builder, InfusionDisplay display, IFocusGroup focuses) {
        List<Ingredient> inputs = display.getIngredients();

        final int cx = 33;
        final int cy = 38;

        if (!inputs.isEmpty()) addInputSlot(builder, cx + 8, cy + 12, inputs.get(0));
        if (inputs.size() > 1) addInputSlot(builder, cx + 8, cy - 16, inputs.get(1));
        if (inputs.size() > 2) addInputSlot(builder, cx + 32, cy - 12, inputs.get(2));
        if (inputs.size() > 3) addInputSlot(builder, cx + 36, cy + 12, inputs.get(3));
        if (inputs.size() > 4) addInputSlot(builder, cx + 32, cy + 36, inputs.get(4));
        if (inputs.size() > 5) addInputSlot(builder, cx + 8, cy + 40, inputs.get(5));
        if (inputs.size() > 6) addInputSlot(builder, cx - 16, cy + 36, inputs.get(6));
        if (inputs.size() > 7) addInputSlot(builder, cx - 20, cy + 12, inputs.get(7));
        if (inputs.size() > 8) addInputSlot(builder, cx - 16, cy - 12, inputs.get(8));

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            ItemStack result = display.recipe.value().getResultItem(minecraft.level.registryAccess());
            if (!result.isEmpty()) {
                builder.addSlot(RecipeIngredientRole.OUTPUT, cx + 88, cy + 12).add(result);
            }
        }
    }

    @Override
    public void draw(
            InfusionDisplay display,
            IRecipeSlotsView recipeSlotsView,
            GuiGraphics guiGraphics,
            double mouseX,
            double mouseY
    ) {
        background.draw(guiGraphics, 0, 0);
        Component timeText = Component.translatable("betterend.jei.infusion.time&val", display.time);
        guiGraphics.drawString(Minecraft.getInstance().font, timeText, 100, 92, 0xFF404040, false);
    }

    private static void addInputSlot(IRecipeLayoutBuilder builder, int x, int y, Ingredient ingredient) {
        var slot = builder.addSlot(RecipeIngredientRole.INPUT, x, y);
        if (!isEmptyCatalystPlaceholder(ingredient)) {
            slot.add(ingredient);
        }
    }

    private static boolean isEmptyCatalystPlaceholder(Ingredient ingredient) {
        var items = ingredient.items().toList();
        return items.size() == 1 && items.get(0).value() == Items.BARRIER;
    }
}

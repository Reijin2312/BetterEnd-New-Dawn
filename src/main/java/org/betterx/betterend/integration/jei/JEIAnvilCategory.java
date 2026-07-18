package org.betterx.betterend.integration.jei;

import org.betterx.bclib.recipes.AnvilRecipe;
import org.betterx.betterend.blocks.basis.EndAnvilBlock;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class JEIAnvilCategory implements IRecipeCategory<AnvilRecipe> {
    private static final int WIDTH = 120;
    private static final int HEIGHT = 45;

    private final IDrawable background;
    private final IDrawable icon;
    private final List<ItemStack> allAnvils;

    public JEIAnvilCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(
                VanillaTypes.ITEM_STACK,
                new ItemStack(EndBlocks.THALLASIUM.anvilBlock)
        );

        this.allAnvils = new ArrayList<>();
        this.allAnvils.add(new ItemStack(Blocks.ANVIL));
        this.allAnvils.addAll(EndBlocks.getModBlocks()
                                       .stream()
                                       .filter(EndAnvilBlock.class::isInstance)
                                       .map(Block::asItem)
                                       .map(ItemStack::new)
                                       .toList());
    }

    @Override
    public @NotNull IRecipeType<AnvilRecipe> getRecipeType() {
        return JEIPlugin.ANVIL_RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("betterend.jei.container.anvil");
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
    public void createRecipeExtras(IRecipeExtrasBuilder builder, AnvilRecipe recipe, IFocusGroup focuses) {
        builder.addDrawable(background, 0, 0);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AnvilRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 5)
               .addItemStacks(toCountedStacks(recipe.getMainIngredient(), recipe.getInputCount()));

        builder.addSlot(RecipeIngredientRole.INPUT, 25, 5)
               .addItemStacks(getHammerStacks(recipe));

        int anvilLevel = recipe.getAnvilLevel();
        List<ItemStack> validAnvils = allAnvils.stream()
                                               .filter(stack -> {
                                                   Block block = Block.byItem(stack.getItem());
                                                   if (block instanceof EndAnvilBlock endAnvil) {
                                                       return endAnvil.getCraftingLevel() >= anvilLevel;
                                                   }
                                                   return anvilLevel <= 1;
                                               })
                                               .toList();
        builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 15, 25).addItemStacks(validAnvils);

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 85, 6)
                   .add(recipe.getResultItem(minecraft.level.registryAccess()));
        }
    }

    @Override
    public void draw(
            AnvilRecipe recipe,
            IRecipeSlotsView recipeSlotsView,
            GuiGraphics guiGraphics,
            double mouseX,
            double mouseY
    ) {
        Component damageText = Component.translatable("betterend.jei.damage.amount&dmg", recipe.getDamage());
        guiGraphics.drawString(Minecraft.getInstance().font, damageText, 45, 28, 0xFF404040, false);

        Component levelText = Component.translatable("betterend.jei.recipe_level.anvil", recipe.getAnvilLevel());
        guiGraphics.drawString(Minecraft.getInstance().font, levelText, 45, 18, 0xFF404040, false);
    }

    private static List<ItemStack> toCountedStacks(Ingredient ingredient, int count) {
        List<ItemStack> out = new ArrayList<>();
        ingredient.items().forEach(item -> {
            ItemStack stack = new ItemStack(item.value(), count);
            out.add(stack);
        });
        return out;
    }

    private static List<ItemStack> getHammerStacks(AnvilRecipe recipe) {
        return StreamSupport.stream(AnvilRecipe.getAllHammers().spliterator(), false)
                            .map(Holder::value)
                            .filter(recipe::canUse)
                            .map(ItemStack::new)
                            .toList();
    }
}

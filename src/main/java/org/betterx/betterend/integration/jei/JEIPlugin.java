package org.betterx.betterend.integration.jei;

import org.betterx.bclib.recipes.AlloyingRecipe;
import org.betterx.bclib.recipes.AnvilRecipe;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.basis.EndAnvilBlock;
import org.betterx.betterend.blocks.entities.EndStoneSmelterBlockEntity;
import org.betterx.betterend.recipe.builders.InfusionRecipe;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.recipe.types.IRecipeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    public static final IRecipeType<AlloyingDisplay> ALLOYING_RECIPE_TYPE =
            IRecipeType.create(BetterEnd.MOD_ID, "alloying", AlloyingDisplay.class);
    public static final IRecipeType<AnvilRecipe> ANVIL_RECIPE_TYPE =
            IRecipeType.create(BetterEnd.MOD_ID, "anvil", AnvilRecipe.class);
    public static final IRecipeType<InfusionDisplay> INFUSION_RECIPE_TYPE =
            IRecipeType.create(BetterEnd.MOD_ID, "infusion", InfusionDisplay.class);

    public static List<ItemStack> ALLOYING_FUELS = List.of();

    @Override
    public @NotNull Identifier getPluginUid() {
        return BetterEnd.C.mk("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new JEIAlloyingCategory(guiHelper));
        registration.addRecipeCategories(new JEIAnvilCategory(guiHelper));
        registration.addRecipeCategories(new JEIInfusionCategory(guiHelper));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        Minecraft minecraft = Minecraft.getInstance();
        RecipeManager recipeManager = resolveRecipeManager(minecraft);
        if (recipeManager == null) return;

        if (ALLOYING_FUELS.isEmpty()) {
            ALLOYING_FUELS = EndStoneSmelterBlockEntity.availableFuels()
                                                       .keySet()
                                                       .stream()
                                                       .map(ItemStack::new)
                                                       .toList();
        }

        List<AlloyingDisplay> alloyingDisplays = collectRecipes(recipeManager, AlloyingRecipe.class)
                .stream()
                .map(AlloyingDisplay::fromAlloying)
                .toList();
        registration.addRecipes(ALLOYING_RECIPE_TYPE, alloyingDisplays);

        List<AlloyingDisplay> blastingDisplays = collectRecipes(recipeManager, BlastingRecipe.class)
                .stream()
                .map(AlloyingDisplay::fromBlasting)
                .toList();
        registration.addRecipes(ALLOYING_RECIPE_TYPE, blastingDisplays);

        List<InfusionDisplay> infusionDisplays = collectRecipes(recipeManager, InfusionRecipe.class)
                .stream()
                .map(InfusionDisplay::new)
                .toList();
        registration.addRecipes(INFUSION_RECIPE_TYPE, infusionDisplays);

        List<AnvilRecipe> anvilRecipes = collectRecipes(recipeManager, AnvilRecipe.class)
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(ANVIL_RECIPE_TYPE, anvilRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        List<ItemStack> anvils = EndBlocks.getModBlocks()
                .stream()
                .filter(EndAnvilBlock.class::isInstance)
                .map(Block::asItem)
                .map(ItemStack::new)
                .toList();

        registration.addCraftingStation(ANVIL_RECIPE_TYPE, new ItemStack(Blocks.ANVIL));
        registration.addCraftingStation(ALLOYING_RECIPE_TYPE, new ItemStack(EndBlocks.END_STONE_SMELTER));
        registration.addCraftingStation(INFUSION_RECIPE_TYPE, new ItemStack(EndBlocks.INFUSION_PEDESTAL));

        for (ItemStack stack : anvils) {
            registration.addCraftingStation(ANVIL_RECIPE_TYPE, stack);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Recipe<?>> List<RecipeHolder<T>> collectRecipes(
            RecipeManager recipeManager,
            Class<T> recipeClass
    ) {
        return recipeManager.getRecipes()
                            .stream()
                            .filter(recipeHolder -> recipeClass.isInstance(recipeHolder.value()))
                            .map(recipeHolder -> (RecipeHolder<T>) recipeHolder)
                            .toList();
    }

    @Nullable
    private static RecipeManager resolveRecipeManager(Minecraft minecraft) {
        // 1.21.11 client level no longer exposes full custom recipes via recipeAccess().
        // For singleplayer/dev this remains available from the integrated server.
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server != null) {
            return server.getRecipeManager();
        }

        if (minecraft.level != null && minecraft.level.recipeAccess() instanceof RecipeManager recipeManager) {
            return recipeManager;
        }

        return null;
    }
}

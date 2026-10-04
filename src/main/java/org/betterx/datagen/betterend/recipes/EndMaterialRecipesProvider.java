package org.betterx.datagen.betterend.recipes;

import org.betterx.betterend.complexmaterials.MaterialManager;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverRecipeProvider;

import net.minecraft.data.recipes.RecipeOutput;

public class EndMaterialRecipesProvider extends WoverRecipeProvider {
    public EndMaterialRecipesProvider(ModCore modCore) {
        super(modCore, "BetterEnd - Material Recipes");
    }

    @Override
    protected void bootstrap(net.minecraft.data.worldgen.BootstrapContextAccess provider, RecipeOutput context) {
        MaterialManager.stream().forEach(m -> m.registerRecipes(context));
    }
}

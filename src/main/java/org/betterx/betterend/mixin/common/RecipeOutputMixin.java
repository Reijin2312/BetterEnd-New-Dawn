package org.betterx.betterend.mixin.common;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RecipeOutput.class)
public interface RecipeOutputMixin {
    @Unique
    default Identifier getRecipeIdentifier(Identifier id) {
        return id;
    }
}

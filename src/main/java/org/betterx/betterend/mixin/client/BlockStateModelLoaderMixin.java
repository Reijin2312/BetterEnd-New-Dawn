package org.betterx.betterend.mixin.client;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.world.generator.GeneratorOptions;

import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(BlockStateModelLoader.class)
public abstract class BlockStateModelLoaderMixin {
    private static final ResourceLocation VANILLA_PLANT_FILE =
            ResourceLocation.withDefaultNamespace("blockstates/chorus_plant.json");
    private static final ResourceLocation VANILLA_FLOWER_FILE =
            ResourceLocation.withDefaultNamespace("blockstates/chorus_flower.json");
    private static final ResourceLocation CUSTOM_PLANT_FILE =
            BetterEnd.C.mk("blockstates/custom_chorus_plant.json");
    private static final ResourceLocation CUSTOM_FLOWER_FILE =
            BetterEnd.C.mk("blockstates/custom_chorus_flower.json");

    /**
     * Replace only the resource stack while retaining the vanilla definition ID.
     * Rewriting idToFile makes the loader associate a BetterEnd resource ID with
     * the vanilla ChorusPlantBlock state definition and produces missing-variant
     * warnings for every state during resource reloads.
     */
    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static Map<ResourceLocation, List<?>> betterend$replaceChorusDefinitions(
            Map<ResourceLocation, List<?>> resources
    ) {
        if (!GeneratorOptions.changeChorusPlant()) {
            return resources;
        }

        Map<ResourceLocation, List<?>> replaced = new HashMap<>(resources);
        replace(replaced, VANILLA_PLANT_FILE, CUSTOM_PLANT_FILE);
        replace(replaced, VANILLA_FLOWER_FILE, CUSTOM_FLOWER_FILE);
        return replaced;
    }

    private static void replace(
            Map<ResourceLocation, List<?>> resources,
            ResourceLocation vanillaFile,
            ResourceLocation customFile
    ) {
        List<?> custom = resources.get(customFile);
        if (custom != null && !custom.isEmpty()) {
            resources.put(vanillaFile, custom);
        }
    }
}

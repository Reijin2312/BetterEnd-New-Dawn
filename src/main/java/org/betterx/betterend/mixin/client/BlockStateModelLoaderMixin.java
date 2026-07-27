package org.betterx.betterend.mixin.client;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.world.generator.GeneratorOptions;

import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(BlockStateModelLoader.class)
public abstract class BlockStateModelLoaderMixin {
    private static final Identifier VANILLA_PLANT_FILE =
            Identifier.withDefaultNamespace("blockstates/chorus_plant.json");
    private static final Identifier VANILLA_FLOWER_FILE =
            Identifier.withDefaultNamespace("blockstates/chorus_flower.json");
    private static final Identifier CUSTOM_PLANT_FILE =
            BetterEnd.C.mk("blockstates/custom_chorus_plant.json");
    private static final Identifier CUSTOM_FLOWER_FILE =
            BetterEnd.C.mk("blockstates/custom_chorus_flower.json");

    @Redirect(
            method = "lambda$loadBlockStates$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/FileToIdConverter;listMatchingResourceStacks(Lnet/minecraft/server/packs/resources/ResourceManager;)Ljava/util/Map;"
            ),
            remap = false
    )
    private static Map<Identifier, List<Resource>> betterend$replaceChorusDefinitions(
            FileToIdConverter converter,
            ResourceManager resourceManager
    ) {
        Map<Identifier, List<Resource>> resources = converter.listMatchingResourceStacks(resourceManager);
        if (!GeneratorOptions.changeChorusPlant()) {
            return resources;
        }

        Map<Identifier, List<Resource>> replaced = new HashMap<>(resources);
        replace(replaced, VANILLA_PLANT_FILE, CUSTOM_PLANT_FILE);
        replace(replaced, VANILLA_FLOWER_FILE, CUSTOM_FLOWER_FILE);
        return replaced;
    }

    private static void replace(
            Map<Identifier, List<Resource>> resources,
            Identifier vanillaFile,
            Identifier customFile
    ) {
        List<Resource> custom = resources.get(customFile);
        if (custom != null && !custom.isEmpty()) {
            resources.put(vanillaFile, custom);
        }
    }
}

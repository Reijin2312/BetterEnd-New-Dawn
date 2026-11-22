package org.betterx.betterend.mixin.client;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.world.generator.GeneratorOptions;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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

    @WrapOperation(
            // Mojang's synthetic lambda name is not preserved in Fabric's intermediary namespace.
            // Target the stable intermediary name used by this Minecraft version instead.
            method = "method_65717",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/FileToIdConverter;listMatchingResourceStacks(Lnet/minecraft/server/packs/resources/ResourceManager;)Ljava/util/Map;"
            )
    )
    private static Map<Identifier, List<Resource>> betterend$replaceChorusDefinitions(
            FileToIdConverter converter,
            ResourceManager resourceManager,
            Operation<Map<Identifier, List<Resource>>> original
    ) {
        Map<Identifier, List<Resource>> resources = original.call(converter, resourceManager);
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

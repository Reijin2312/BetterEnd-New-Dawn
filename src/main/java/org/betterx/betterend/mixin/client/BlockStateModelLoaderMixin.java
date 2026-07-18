package org.betterx.betterend.mixin.client;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.world.generator.GeneratorOptions;

import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.resources.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BlockStateModelLoader.class)
public abstract class BlockStateModelLoaderMixin {
    @ModifyArg(
            method = "loadBlockStates",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/FileToIdConverter;fileToId(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/resources/Identifier;"
            ),
            require = 0,
            remap = false
    )
    private static Identifier be_switchModelOnLoadLegacy(Identifier loc) {
        return be_replaceChorusModelId(loc);
    }

    @ModifyArg(
            method = "lambda$loadBlockStates$1(Ljava/util/Map$Entry;Ljava/util/function/Function;)Lnet/minecraft/client/resources/model/BlockStateModelLoader$LoadedModels;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/FileToIdConverter;fileToId(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/resources/Identifier;"
            ),
            require = 0,
            remap = false
    )
    private static Identifier be_switchModelOnLoad21111(Identifier loc) {
        return be_replaceChorusModelId(loc);
    }

    @Unique
    private static Identifier be_replaceChorusModelId(Identifier loc) {
        // This is always a block state id because it comes from BLOCKSTATE_LISTER.
        if (GeneratorOptions.changeChorusPlant() && be_changeModel(loc)) {
            String path = loc.getPath().replace("chorus", "custom_chorus");
            return BetterEnd.C.mk(path);
        }
        return loc;
    }

    @Unique
    private static boolean be_changeModel(Identifier id) {
        if (id.getNamespace().equals("minecraft")) {
            if (id.getPath().equals("chorus_plant") || id.getPath().equals("chorus_flower")) {
                return true;
            }
            return false;
        }
        return false;
    }
}

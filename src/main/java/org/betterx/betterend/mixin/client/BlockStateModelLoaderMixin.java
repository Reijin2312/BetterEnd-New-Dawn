package org.betterx.betterend.mixin.client;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.world.generator.GeneratorOptions;

import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.resources.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockStateModelLoader.class)
public abstract class BlockStateModelLoaderMixin {
    @Redirect(
            method = "lambda$loadBlockStates$2",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/FileToIdConverter;fileToId(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/resources/Identifier;"
            )
    )
    private static Identifier be_switchModelOnLoad(
            net.minecraft.resources.FileToIdConverter converter,
            Identifier loc
    ) {
        return be_replaceChorusModelId(converter.fileToId(loc));
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

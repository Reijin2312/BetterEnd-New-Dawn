package org.betterx.betterend.mixin.common;

import org.betterx.betterend.world.generator.GeneratorOptions;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public class LevelMixin {

    @Inject(method = "getRespawnData", at = @At("RETURN"), cancellable = true, remap = false)
    private void be_getSharedSpawnPos(CallbackInfoReturnable<LevelData.RespawnData> info) {
        if (GeneratorOptions.changeSpawn()) {
            final ServerLevel server = (ServerLevel) (Object) this;
            if (server.dimension() == Level.END) {
                final LevelData.RespawnData current = info.getReturnValue();
                final float yaw = current == null ? 0.0F : current.yaw();
                final float pitch = current == null ? 0.0F : current.pitch();
                info.setReturnValue(LevelData.RespawnData.of(server.dimension(), GeneratorOptions.getSpawn(), yaw, pitch));
            }
        }
    }
}

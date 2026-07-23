package org.betterx.betterend.mixin.common;

import org.betterx.betterend.world.generator.GeneratorOptions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.phys.Vec3;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin {
    @WrapOperation(
            method = "getPortalDestination",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/Vec3;atBottomCenterOf(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/world/phys/Vec3;"
            )
    )
    Vec3 be_changeSpawnInEnd(Vec3i instance, Operation<Vec3> original) {
        if (GeneratorOptions.changeSpawn() && instance == ServerLevel.END_SPAWN_POINT) {
            BlockPos spawn = GeneratorOptions.getSpawn();
            return original.call(spawn);
        }

        return original.call(instance);
    }
}

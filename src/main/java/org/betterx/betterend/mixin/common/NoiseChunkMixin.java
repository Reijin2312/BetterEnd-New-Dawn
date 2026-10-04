package org.betterx.betterend.mixin.common;

import org.betterx.betterend.interfaces.BETargetChecker;
import org.betterx.betterend.world.generator.TerrainGenerator;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.ScopedDensityBuffer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/** Adapts the 26.2 island generator to Minecraft 26.3's density volumes. */
@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseChunkMixin {
    @Shadow
    @Final
    private Holder<NoiseGeneratorSettings> settings;

    @WrapOperation(
            method = "doFill",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/densityfunction/DensitySampler$Bound;sampleVolume(Lnet/minecraft/world/level/levelgen/densityfunction/DensityVolume;)Lnet/minecraft/world/level/levelgen/densityfunction/ScopedDensityBuffer;"
            )
    )
    private ScopedDensityBuffer be_fillBetterEndDensity(
            DensitySampler.Bound sampler,
            DensityVolume volume,
            Operation<ScopedDensityBuffer> original
    ) {
        if (!BETargetChecker.class.cast(settings.value()).be_isTarget()) {
            return original.call(sampler, volume);
        }

        ScopedDensityBuffer buffer = sampler.context().acquireBuffer(volume);
        TerrainGenerator.fillDensityVolume(buffer, volume, settings.value().noiseSettings());
        return buffer;
    }
}

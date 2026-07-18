package org.betterx.betterend.mixin.common;

import net.minecraft.world.level.biome.BiomeSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(targets = "com.teamabnormals.blueprint.common.world.modification.ModdedBiomeSource", remap = false)
public interface BlueprintModdedBiomeSourceAccessor {
    @Accessor("originalSource")
    BiomeSource be_getOriginalSource();
}

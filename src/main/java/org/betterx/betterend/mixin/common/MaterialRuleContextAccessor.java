package org.betterx.betterend.mixin.common;

import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MaterialRuleContext.class)
public interface MaterialRuleContextAccessor {
    @Accessor(value = "randomState", remap = false)
    RandomState be_getRandomState();
}

package org.betterx.betterend.mixin.common;

import org.betterx.betterend.events.PlayerAdvancementsCallback;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {
    @Shadow
    private ServerPlayer player;

    @Inject(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementRewards;grant(Lnet/minecraft/server/level/ServerPlayer;)V", shift = Shift.AFTER))
    public void be_award(
            AdvancementHolder advancementHolder,
            String criterionName,
            CallbackInfoReturnable<Boolean> cir
    ) {
        PlayerAdvancementsCallback.fire(
                player,
                advancementHolder,
                criterionName
        );
    }
}

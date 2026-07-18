package org.betterx.betterend.mixin.client;

import org.betterx.betterend.events.ItemTooltipCallback;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import org.jetbrains.annotations.Nullable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "getTooltipLines", at = @At("RETURN"))
    private void be_getTooltip(
            Item.TooltipContext tooltipContext,
            @Nullable Player player,
            TooltipFlag tooltipFlag,
            CallbackInfoReturnable<List<Component>> info
    ) {
        ItemTooltipCallback.fire(player, ItemStack.class.cast(this), tooltipFlag, info.getReturnValue());
    }
}

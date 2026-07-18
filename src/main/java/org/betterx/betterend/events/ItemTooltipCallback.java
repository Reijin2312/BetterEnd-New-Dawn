package org.betterx.betterend.events;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

public interface ItemTooltipCallback {
    List<ItemTooltipCallback> LISTENERS = new ArrayList<>();

    static void register(ItemTooltipCallback callback) {
        LISTENERS.add(callback);
    }

    static void fire(Player player, ItemStack stack, TooltipFlag context, List<Component> lines) {
        for (ItemTooltipCallback callback : LISTENERS) {
            callback.getTooltip(player, stack, context, lines);
        }
    }

    /**
     * Called when an item stack's tooltip is rendered. Text added to {@code lines} will be
     * rendered with the tooltip.
     *
     * @param lines the list containing the lines of text displayed on the stack's tooltip
     */
    void getTooltip(Player player, ItemStack stack, TooltipFlag context, List<Component> lines);
}

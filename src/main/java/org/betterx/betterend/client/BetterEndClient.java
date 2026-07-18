package org.betterx.betterend.client;

import org.betterx.bclib.integration.modmenu.ModMenu;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.config.screen.ConfigScreen;
import org.betterx.betterend.events.ItemTooltipCallback;
import org.betterx.betterend.interfaces.MultiModelItem;
import org.betterx.betterend.item.CrystaliteArmor;
import org.betterx.betterend.registry.*;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import net.fabricmc.api.ClientModInitializer;

public class BetterEndClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EndBlockEntityRenders.register();
        EndScreens.register();
        EndParticles.register();
        EndEntitiesRenders.register();
        EndModelProviders.register();
        MultiModelItem.register();
        registerTooltips();


        ModMenu.addModMenuScreen(BetterEnd.C.modId, ConfigScreen::new);

    }

    public static void registerTooltips() {
        ItemTooltipCallback.register((player, stack, context, lines) -> {
            if (stack.getItem() instanceof CrystaliteArmor) {
                boolean hasSet = false;
                if (player != null) {
                    hasSet = CrystaliteArmor.hasFullSet(player);
                }
                MutableComponent setDesc = Component.translatable("tooltip.armor.crystalite_set");

                setDesc.setStyle(Style.EMPTY.applyFormats(
                        hasSet ? ChatFormatting.BLUE : ChatFormatting.DARK_GRAY,
                        ChatFormatting.ITALIC
                ));
                lines.add(Component.empty());
                lines.add(setDesc);
            }
        });
    }
}

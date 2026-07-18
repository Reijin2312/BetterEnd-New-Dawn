package org.betterx.betterend.integration;

import org.betterx.bclib.api.v2.ModIntegrationAPI;
import org.betterx.bclib.integration.ModIntegration;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.events.PlayerAdvancementsCallback;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class Integrations {
    public static final ModIntegration BYG = registerBygIntegration();
    public static final ModIntegration FLAMBOYANT_REFABRICATED = ModIntegrationAPI.register(new FlamboyantRefabricatedIntegration());
    public static final ModIntegration DYE_DEPOT = ModIntegrationAPI.register(new DyeDepotIntegration());
    private static final Identifier GUIDEBOOK_ID = BetterEnd.C.mk("guidebook");

    public static void init() {
        if (BetterEnd.PATCHOULI.isLoaded() && BetterEnd.ENABLE_GUIDEBOOK) {
            Identifier advId = Identifier.withDefaultNamespace("end/enter_end_gateway");

            PlayerAdvancementsCallback.register((player, advancement, criterionName) -> {
                if (advId.equals(advancement.id())) {
                    Item guideBook = BuiltInRegistries.ITEM.getValue(GUIDEBOOK_ID);
                    if (guideBook != Items.AIR) {
                        player.addItem(new ItemStack(guideBook));
                    }
                }
            });
        }
    }

    private static ModIntegration registerBygIntegration() {
        try {
            Class<?> clazz = Class.forName("org.betterx.betterend.integration.byg.BYGIntegration");
            Object instance = clazz.getDeclaredConstructor().newInstance();
            if (instance instanceof ModIntegration integration) {
                return ModIntegrationAPI.register(integration);
            }
        } catch (ClassNotFoundException ignored) {
            return null;
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException("Failed to register BYG integration reflectively", ex);
        }
        return null;
    }
}

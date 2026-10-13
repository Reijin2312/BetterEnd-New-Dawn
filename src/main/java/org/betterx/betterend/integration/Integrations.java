package org.betterx.betterend.integration;

import org.betterx.bclib.api.v2.ModIntegrationAPI;
import org.betterx.bclib.integration.ModIntegration;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.config.Configs;
import org.betterx.betterend.events.PlayerAdvancementsCallback;
import org.betterx.betterend.integration.byg.BYGIntegration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class Integrations {
    public static final ModIntegration BYG = ModIntegrationAPI.register(new BYGIntegration());
    public static final ModIntegration FLAMBOYANT_REFABRICATED = ModIntegrationAPI.register(new FlamboyantRefabricatedIntegration());
    public static final ModIntegration DYE_DEPOT = ModIntegrationAPI.register(new DyeDepotIntegration());
    private static final ResourceLocation GUIDEBOOK_ID = BetterEnd.C.mk("guidebook");

    public static void init() {
        if (BetterEnd.PATCHOULI.isLoaded() && BetterEnd.ENABLE_GUIDEBOOK) {
            ResourceLocation advId = ResourceLocation.withDefaultNamespace("end/enter_end_gateway");

            PlayerAdvancementsCallback.register((player, advancement, criterionName) -> {
                if (advId.equals(advancement.id()) && Configs.GENERATOR_CONFIG.giveGuideBook.get()) {
                    Item guideBook = BuiltInRegistries.ITEM.get(GUIDEBOOK_ID);
                    if (guideBook != Items.AIR) {
                        player.addItem(new ItemStack(guideBook));
                    }
                }
            });
        }
    }
}

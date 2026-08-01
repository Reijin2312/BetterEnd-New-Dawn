package org.betterx.betterend.client;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.client.render.BetterEndRenderPipelines;
import org.betterx.betterend.config.screen.ConfigScreen;
import org.betterx.betterend.events.ItemTooltipCallback;
import org.betterx.betterend.interfaces.MultiModelItem;
import org.betterx.betterend.item.CrystaliteArmor;
import org.betterx.betterend.network.RitualUpdate;
import org.betterx.betterend.registry.*;
import org.betterx.betterend.rituals.EternalRitual;
import org.betterx.betterend.world.generator.GeneratorOptions;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.Map;

@EventBusSubscriber(modid = BetterEnd.MOD_ID, value = Dist.CLIENT)
public class BetterEndClient {
    private static final Identifier CHECK_FLOWER_ID = Identifier.withDefaultNamespace("chorus_flower");
    private static final Identifier CHECK_PLANT_ID = Identifier.withDefaultNamespace("chorus_plant");
    private static final Identifier TO_LOAD_FLOWER_ID = BetterEnd.C.mk("custom_chorus_flower");
    private static final Identifier TO_LOAD_PLANT_ID = BetterEnd.C.mk("custom_chorus_plant");

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            RitualUpdate.registerClientProcessor((center, axis, active, willActivate, client) ->
                    EternalRitual.updateActiveStateOnPedestals(
                            center, axis, active, willActivate, ((Minecraft) client).level, null
                    )
            );
            EndModelProviders.register();
            MultiModelItem.register();
            registerTooltips();

            IConfigScreenFactory factory = (modContainer, parent) -> new ConfigScreen(parent);
            ModList.get()
                   .getModContainerById(BetterEnd.MOD_ID)
                   .ifPresent(container -> container.registerExtensionPoint(IConfigScreenFactory.class, factory));

            NeoForge.EVENT_BUS.addListener(BetterEndClient::onItemTooltip);

        });
    }

    @SubscribeEvent
    public static void registerScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
        EndScreens.register(event);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        EndParticles.registerProviders(event);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        EndEntitiesRenders.registerRenderers(event);
        EndBlockEntityRenders.registerRenderers(event);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        EndEntitiesRenders.registerLayerDefinitions(event);
    }

    @SubscribeEvent
    public static void registerRenderPipelines(RegisterRenderPipelinesEvent event) {
        BetterEndRenderPipelines.register(event);
    }

    @SubscribeEvent
    public static void onModifyBakedModels(ModelEvent.ModifyBakingResult event) {
        if (!GeneratorOptions.changeChorusPlant()) {
            return;
        }

        Map<Identifier, ItemModel> models = event.getBakingResult().itemStackModels();
        ItemModel flowerModel = models.get(TO_LOAD_FLOWER_ID);
        ItemModel plantModel = models.get(TO_LOAD_PLANT_ID);

        if (flowerModel != null) {
            models.put(CHECK_FLOWER_ID, flowerModel);
        }
        if (plantModel != null) {
            models.put(CHECK_PLANT_ID, plantModel);
        }
    }

    private static void onItemTooltip(ItemTooltipEvent event) {
        ItemTooltipCallback.fire(event.getEntity(), event.getItemStack(), event.getFlags(), event.getToolTip());
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

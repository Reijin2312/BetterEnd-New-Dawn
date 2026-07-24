package org.betterx.betterend;

import de.ambertation.wunderlib.network.ClientBoundPacketHandler;
import org.betterx.betterend.advancements.BECriteria;
import org.betterx.betterend.api.BetterEndPlugin;
import org.betterx.betterend.commands.CommandRegistry;
import org.betterx.betterend.config.Configs;
import org.betterx.betterend.effects.EndPotions;
import org.betterx.betterend.effects.EndStatusEffects;
import org.betterx.betterend.integration.Integrations;
import org.betterx.betterend.integration.byg.features.BYGFeatures;
import org.betterx.betterend.network.RitualUpdate;
import org.betterx.betterend.recipe.builders.InfusionRecipe;
import org.betterx.betterend.registry.*;
import org.betterx.betterend.tab.CreativeTabs;
import org.betterx.betterend.util.BonemealPlants;
import org.betterx.betterend.util.LootTableUtil;
import org.betterx.betterend.world.generator.EndLandBiomeDecider;
import org.betterx.betterend.world.generator.GeneratorOptions;
import org.betterx.datagen.betterend.BetterEndDatagen;
import org.betterx.wover.core.api.Logger;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.generator.api.biomesource.end.BiomeDecider;
import org.betterx.wover.state.api.WorldConfig;

import net.minecraft.resources.ResourceLocation;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(BetterEnd.MOD_ID)
public class BetterEnd {
    public static final String MOD_ID = "betterend";
    public static final ModCore C = ModCore.create(MOD_ID);
    public static final Logger LOGGER = C.LOG;

    public static final ModCore BYG = ModCore.create("byg");
    public static final ModCore NOURISH = ModCore.create("nourish");
    public static final ModCore FLAMBOYANT = ModCore.create("flamboyant");
    public static final ModCore DYE_DEPOT = ModCore.create("dye_depot");
    public static final ModCore PATCHOULI = ModCore.create("patchouli");
    public static final ModCore HYDROGEN = ModCore.create("hydrogen");
    public static final ModCore TRINKETS_CORE = ModCore.create("trinkets");
    public static final boolean ENABLE_GUIDEBOOK = false;
    public static final ResourceLocation BYG_ADDITIONS_PACK = C.addDatapack(BYG);
    public static final ResourceLocation NOURISH_ADDITIONS_PACK = C.addDatapack(NOURISH);
    public static final ResourceLocation FLAMBOYANT_ADDITIONS_PACK = C.addDatapack(FLAMBOYANT);
    public static final ResourceLocation PATCHOULI_ADDITIONS_PACK = ENABLE_GUIDEBOOK
            ? C.addDatapack(PATCHOULI)
            : null;

    public BetterEnd(IEventBus modBus) {
        C.registerDatapackListener(modBus);
        modBus.addListener(EndSounds::register);
        modBus.addListener(RegisterEvent.class, EndEntities::onRegister);
        modBus.addListener(net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent.class, EndEntities::onRegisterAttributes);
        modBus.addListener(RegisterEvent.class, EndParticles::onRegister);
        modBus.addListener(RegisterEvent.class, EndPoiTypes::onRegister);
        modBus.addListener(RegisterEvent.class, this::registerFeatures);
        modBus.addListener(RegisterEvent.class, EndMenuTypes::onRegister);
        modBus.addListener(RegisterEvent.class, EndBlockEntities::register);
        modBus.addListener(RegisterEvent.class, this::ensureBlocksLoaded);
        modBus.addListener(RegisterEvent.class, this::ensureItemsLoaded);
        modBus.addListener(RegisterEvent.class, EndEnchantments::onRegister);
        modBus.addListener(RegisterEvent.class, EndStatusEffects::onRegister);
        modBus.addListener(RegisterEvent.class, EndPotions::onRegister);
        modBus.addListener(RegisterEvent.class, BECriteria::onRegister);
        modBus.addListener(RegisterEvent.class, CreativeTabs::onRegister);
        if (BYG.isLoaded()) {
            modBus.addListener(RegisterEvent.class, BYGFeatures::onRegister);
        }

        // Гарантируем, что блоки/предметы подготавливаются до фактической регистрации, даже если порядок
        // обработчиков событий изменится (иначе остаются незарегистрованные intrusive holders).
        EndBlocks.ensureRegistered();
        org.betterx.wover.block.api.BlockRegistry.hook(modBus);
        org.betterx.wover.item.api.ItemRegistry.hook(modBus);
        if (ModCore.isDatagen()) {
            BetterEndDatagen datagen = new BetterEndDatagen();
            modBus.addListener(datagen::onGatherData);
        }
        initialize();
    }

    private void initialize() {
        WorldConfig.registerMod(C);

        EndNumericProviders.register();
        EndPortals.loadPortals();
        EndMenuTypes.ensureStaticallyLoaded();
        // Registrations are handled via RegisterEvent listeners to avoid early registry access
        EndBiomes.register();
        EndTags.register();
        EndPotions.register();
        InfusionRecipe.register();
        EndStructures.register();
        GeneratorOptions.init();
        LootTableUtil.init();
        CommandRegistry.register();
        EndParticles.ensureStaticallyLoadedServerside();
        java.util.ServiceLoader.load(BetterEndPlugin.class).forEach(BetterEndPlugin::register);
        Integrations.init();
        Configs.saveConfigs();
        CreativeTabs.register();

        if (GeneratorOptions.useNewGenerator()) {
            BiomeDecider.registerHighPriorityDecider(C.mk("end_land"), new EndLandBiomeDecider());
        }

        ClientBoundPacketHandler.register(RitualUpdate.CHANNEL, RitualUpdate.Payload::new);
    }

    private static boolean bonemealInitialized = false;

    private void ensureBlocksLoaded(RegisterEvent event) {
        if (event.getRegistryKey().equals(net.minecraft.core.registries.Registries.BLOCK)) {
            try {
                Class.forName("org.betterx.betterend.registry.EndBlocks");
            } catch (ClassNotFoundException ignored) {
            }
            if (!bonemealInitialized) {
                BonemealPlants.init();
                bonemealInitialized = true;
            }
        }
    }

    private void ensureItemsLoaded(RegisterEvent event) {
        if (event.getRegistryKey().equals(net.minecraft.core.registries.Registries.ITEM)) {
            try {
                Class.forName("org.betterx.betterend.registry.EndItems");
            } catch (ClassNotFoundException ignored) {
            }
            EndItems.ensureStaticallyLoaded();
            EndItems.registerCompostableFoods();
        }
    }

    private void registerFeatures(RegisterEvent event) {
        if (event.getRegistryKey().equals(net.minecraft.core.registries.Registries.FEATURE)) {
            EndFeatures.onRegister(event);
        }
    }
}

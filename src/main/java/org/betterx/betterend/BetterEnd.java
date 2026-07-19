package org.betterx.betterend;

import de.ambertation.wunderlib.network.ClientBoundPacketHandler;
import org.betterx.betterend.advancements.BECriteria;
import org.betterx.betterend.api.BetterEndPlugin;
import org.betterx.betterend.commands.CommandRegistry;
import org.betterx.betterend.config.Configs;
import org.betterx.betterend.effects.EndPotions;
import org.betterx.betterend.integration.Integrations;
import org.betterx.betterend.network.RitualUpdate;
import org.betterx.betterend.recipe.builders.InfusionRecipe;
import org.betterx.betterend.registry.*;
import org.betterx.betterend.tab.CreativeTabs;
import org.betterx.betterend.util.BonemealPlants;
import org.betterx.betterend.util.LootTableUtil;
import org.betterx.betterend.world.generator.EndLandBiomeDecider;
import org.betterx.betterend.blocks.FlowerPotBlock;
import org.betterx.betterend.world.generator.GeneratorOptions;
import org.betterx.wover.core.api.Logger;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.generator.api.biomesource.end.BiomeDecider;
import org.betterx.wover.state.api.WorldConfig;

import net.minecraft.resources.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class BetterEnd implements ModInitializer {
    public static final ModCore C = ModCore.create("betterend");
    public static final ModCore TRINKETS_CORE = ModCore.create("trinkets");
    public static final String MOD_ID = C.namespace;
    public static final Logger LOGGER = C.LOG;

    public static final ModCore BYG = ModCore.create("byg");
    public static final ModCore NOURISH = ModCore.create("nourish");
    public static final ModCore FLAMBOYANT = ModCore.create("flamboyant");
    public static final ModCore HYDROGEN = ModCore.create("hydrogen");
    public static final ModCore DYE_DEPOT = ModCore.create("dye_depot");
    public static final ModCore PATCHOULI = ModCore.create("patchouli");
    public static final boolean ENABLE_GUIDEBOOK = false;
    public static final Identifier BYG_ADDITIONS_PACK = C.addDatapack(BYG);
    public static final Identifier NOURISH_ADDITIONS_PACK = C.addDatapack(NOURISH);
    public static final Identifier FLAMBOYANT_ADDITIONS_PACK = C.addDatapack(FLAMBOYANT);
    public static final Identifier PATCHOULI_ADDITIONS_PACK = ENABLE_GUIDEBOOK ? C.addDatapack(PATCHOULI) : null;

    @Override
    public void onInitialize() {
        WorldConfig.registerMod(C);

        EndNumericProviders.register();
        EndPortals.loadPortals();
        EndSounds.register();
        EndEntities.register();
        // 1.21.11 requires block ids during construction. Blocks are now
        // initialized lazily, so dependent registries (block entities and POIs)
        // must not read their fields before this point.
        EndBlocks.ensureStaticallyLoaded();
        EndItems.ensureStaticallyLoaded();
        // Fabric initializes some client/post-init hooks while the lazy registries are
        // still being populated. Rebuild the pot lookup only after both blocks and
        // their BlockItems are complete, otherwise the first partial snapshot is cached.
        FlowerPotBlock.refreshPottableLists();
        EndMenuTypes.ensureStaticallyLoaded();
        EndBlockEntities.register();
        EndPoiTypes.register();
        EndFeatures.register();
        EndBiomes.register();
        EndTags.register();
        EndTemplates.ensureStaticallyLoaded();
        EndEnchantments.ensureStaticallyLoaded();
        EndPotions.register();
        InfusionRecipe.register();
        EndStructures.register();
        BonemealPlants.init();
        GeneratorOptions.init();
        LootTableUtil.init();
        CommandRegistry.register();
        EndParticles.ensureStaticallyLoadedServerside();
        BECriteria.register();
        FabricLoader.getInstance()
                    .getEntrypoints("betterend", BetterEndPlugin.class)
                    .forEach(BetterEndPlugin::register);
        Integrations.init();
        Configs.saveConfigs();
        CreativeTabs.register();

        if (GeneratorOptions.useNewGenerator()) {
            BiomeDecider.registerHighPriorityDecider(C.mk("end_land"), new EndLandBiomeDecider());
        }

        ClientBoundPacketHandler.register(RitualUpdate.CHANNEL, RitualUpdate.Payload::new);

    }
}

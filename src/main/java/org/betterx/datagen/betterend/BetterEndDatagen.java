package org.betterx.datagen.betterend;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.datagen.betterend.advancement.EndAdvancementDataProvider;
import org.betterx.datagen.betterend.recipes.*;
import org.betterx.datagen.betterend.tags.BiomeTagProvider;
import org.betterx.datagen.betterend.tags.BlockTagProvider;
import org.betterx.datagen.betterend.tags.ItemTagProvider;
import org.betterx.datagen.betterend.tags.NourishItemTagProvider;
import org.betterx.datagen.betterend.worldgen.EndBiomeModificationProvider;
import org.betterx.datagen.betterend.worldgen.EndBiomesProvider;
import org.betterx.datagen.betterend.worldgen.StructureDataProvider;
import org.betterx.datagen.betterend.worldgen.features.*;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;

import net.minecraft.core.RegistrySetBuilder;

public class BetterEndDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        EndBiomesProvider.loadAllBiomeConfigs();
        EndBlocks.ensureStaticallyLoaded();
        EndItems.ensureStaticallyLoaded();

        globalPack.addMultiProvider(EndBiomesProvider::new);
        globalPack.addProvider(BlockTagProvider::new);
        globalPack.addProvider(ItemTagProvider::new);
        globalPack.addProvider(BiomeTagProvider::new);
        globalPack.addRegistryProvider(JukeboxRegistryProvider::new);
        globalPack.addMultiProvider(StructureDataProvider::new);
        globalPack.addMultiProvider(VegetationFeaturesProvider::new);
        globalPack.addMultiProvider(OreFeatureProvider::new);
        globalPack.addMultiProvider(LakeFeatureProvider::new);
        globalPack.addMultiProvider(TerrainFeatureProvider::new);
        globalPack.addMultiProvider(CaveFeatureProvider::new);
        globalPack.addProvider(EndBiomeModificationProvider::new);
        globalPack.addProvider(EndCraftingRecipesProvider::new);
        globalPack.addProvider(EndFurnaceRecipeProvider::new);
        globalPack.addProvider(SmithingRecipesProvider::new);
        globalPack.addProvider(AlloyingRecipesProvider::new);
        globalPack.addProvider(AnvilRecipesProvider::new);
        globalPack.addProvider(InfusionRecipesProvider::new);
        globalPack.addProvider(EndMaterialRecipesProvider::new);
        globalPack.addProvider(EndEnchantmentProvider::new);
        globalPack.addProvider(EndChestLootTableProvider::new);
        globalPack.addProvider(EndBlockLootTableProvider::new);
        globalPack.addProvider(EndModelProvider::new);
        globalPack.addProvider(EndEquipmentAssetProvider::new);
        globalPack.addProvider(modCore -> (output, registries) ->
                new EndAdvancementDataProvider(output, registries));

        // Keep default behavior aligned with 1.21: BYG worldgen content is not part of the
        // main BetterEnd pack by default. Otherwise BYG biomes can spawn as empty biomes when
        // BYG itself is not installed.
//        addDatapack(BetterEnd.BYG_ADDITIONS_PACK)
//                .addMultiProvider(BYGFeatureProvider::new)
//                .addProvider(BYGBlockTagsProvider::new)
//                .addMultiProvider(BYGBiomeProvider::new);

        //Add providers for the nourish integration
        addDatapack(BetterEnd.NOURISH_ADDITIONS_PACK)
                .addProvider(NourishItemTagProvider::new);

        //Add providers for the patchouli integration
        if (BetterEnd.ENABLE_GUIDEBOOK && BetterEnd.PATCHOULI_ADDITIONS_PACK != null) {
            addDatapack(BetterEnd.PATCHOULI_ADDITIONS_PACK)
                    .addProvider(PatchouliBookProvider::new);
        }
    }

    @Override
    protected ModCore modCore() {
        return BetterEnd.C;
    }

    @Override
    protected void onBuildRegistry(RegistrySetBuilder registryBuilder) {
        super.onBuildRegistry(registryBuilder);
        EndRegistrySupplier.INSTANCE.bootstrapRegistries(registryBuilder);

    }
}

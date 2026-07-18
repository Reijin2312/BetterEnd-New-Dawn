package org.betterx.datagen.betterend;

import org.betterx.betterend.BetterEnd;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverDataProvider;

import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EndEquipmentAssetProvider implements WoverDataProvider<DataProvider> {
    public EndEquipmentAssetProvider(ModCore modCore) {
    }

    @Override
    public DataProvider getProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new DataProvider() {
            private final PackOutput.PathProvider equipmentPathProvider =
                    output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "equipment");

            @Override
            public CompletableFuture<?> run(CachedOutput cache) {
                Map<Identifier, EquipmentClientInfo> equipment = new LinkedHashMap<>();
                addArmor(equipment, "thallasium");
                addArmor(equipment, "terminite");
                addArmor(equipment, "aeternium");
                addArmor(equipment, "crystalite");

                return DataProvider.saveAll(
                        cache,
                        EquipmentClientInfo.CODEC,
                        equipmentPathProvider::json,
                        equipment
                );
            }

            @Override
            public String getName() {
                return "Equipment Asset Definitions - BetterEnd";
            }
        };
    }

    private static void addArmor(Map<Identifier, EquipmentClientInfo> equipment, String name) {
        Identifier id = BetterEnd.C.mk(name);
        equipment.put(id, EquipmentClientInfo.builder().addHumanoidLayers(id).build());
    }
}

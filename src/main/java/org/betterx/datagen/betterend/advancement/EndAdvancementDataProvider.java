package org.betterx.datagen.betterend.advancement;

import org.betterx.bclib.api.v2.advancement.AdvancementManager;
import org.betterx.bclib.api.v3.datagen.AdvancementDataProvider;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.advancements.BECriteria;
import org.betterx.betterend.complexmaterials.MetalMaterial;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.EndTemplates;

import net.minecraft.advancements.AdvancementRequirements.Strategy;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.criterion.ChangeDimensionTrigger;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EndAdvancementDataProvider extends AdvancementDataProvider {
    public EndAdvancementDataProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registryLookup
    ) {
        super(List.of(BetterEnd.MOD_ID), output, registryLookup);
    }

    @Override
    @SuppressWarnings("removal")
    protected void bootstrap(HolderLookup.Provider lookup) {
        final HolderLookup.RegistryLookup<Structure> structures = lookup.lookupOrThrow(Registries.STRUCTURE);
        final HolderLookup.RegistryLookup<Biome> biomeLookup = lookup.lookupOrThrow(Registries.BIOME);
        Identifier root = AdvancementManager.Builder
                .create(BetterEnd.C.mk("root"))
                .startDisplay(EndBlocks.END_MYCELIUM)
                .frame(AdvancementType.TASK)
                .hideFromChat()
                .background(Identifier.withDefaultNamespace("textures/gui/advancements/backgrounds/end.png"))
                .endDisplay()
                .addCriterion(
                        "welcome",
                        PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.location())
                )
                .requirements(Strategy.OR)
                .build();

        Identifier enterEnd = AdvancementManager.Builder
                .create(BetterEnd.C.mk("enter_end"))
                .startDisplay(EndBlocks.CAVE_MOSS)
                .endDisplay()
                .parent(root)
                .addCriterion(
                        "entered_end",
                        ChangeDimensionTrigger
                                .TriggerInstance
                                .changedDimensionTo(Level.END)
                )
                .requirements(Strategy.OR)
                .build();

        Holder<Structure> portalHolder = structures.getOrThrow(EndStructures.ETERNAL_PORTAL.key());
        Identifier portal = AdvancementManager.Builder
                .create(BetterEnd.C.mk("portal"))
                .parent(enterEnd)
                .startDisplay(EndBlocks.ETERNAL_PEDESTAL)
                .frame(AdvancementType.GOAL)
                .endDisplay()
                .addAtStructureCriterion("eternal_portal", portalHolder)
                .requirements(Strategy.OR)
                .build();

        Identifier portalOn = AdvancementManager.Builder
                .create(BetterEnd.C.mk("portal_on"))
                .parent(portal)
                .startDisplay(EndItems.ETERNAL_CRYSTAL)
                .endDisplay()
                .addCriterion("turn_on", BECriteria.PORTAL_ON_TRIGGER)
                .requirements(Strategy.OR)
                .build();

        Identifier portalTravel = AdvancementManager.Builder
                .create(BetterEnd.C.mk("portal_travel"))
                .parent(portalOn)
                .startDisplay(Items.GRASS_BLOCK)
                .frame(AdvancementType.CHALLENGE)
                .endDisplay()
                .addCriterion("travel", BECriteria.PORTAL_TRAVEL_TRIGGER)
                .requirements(Strategy.OR)
                .build();

        final var biomes = biomeLookup
                .listElementIds()
                .filter(id -> id.identifier().getNamespace().equals(BetterEnd.C.modId))
                .toList();

        if (!biomes.isEmpty()) {
            Identifier allTheBiomes = AdvancementManager.Builder
                    .create(BetterEnd.C.mk("all_the_biomes"))
                    .parent(enterEnd)
                    .startDisplay(EndItems.AETERNIUM_BOOTS)
                    .frame(AdvancementType.CHALLENGE)
                    .endDisplay()
                    .addVisitBiomesCriterion(biomes
                            .stream()
                            .sorted(Comparator.comparing(ResourceKey::identifier))
                            .map(key -> (Holder<Biome>) biomeLookup.get(key).orElseThrow())
                            .toList())
                    .requirements(Strategy.AND)
                    .rewardXP(1500)
                    .build();


            Holder<Structure> villageHolder = structures.getOrThrow(EndStructures.END_VILLAGE.key());
            Identifier village = AdvancementManager.Builder
                    .create(BetterEnd.C.mk("village"))
                    .parent(allTheBiomes)
                    .startDisplay(EndBlocks.TENANEA.getBlock(WoodSlots.DOOR))
                    .frame(AdvancementType.GOAL)
                    .endDisplay()
                    .addAtStructureCriterion("end_village", villageHolder)
                    .requirements(Strategy.OR)
                    .build();
        }

        Identifier allElytras = AdvancementManager.Builder
                .create(BetterEnd.C.mk("all_elytras"))
                .parent(enterEnd)
                .startDisplay(EndItems.CRYSTALITE_ELYTRA)
                .frame(AdvancementType.GOAL)
                .endDisplay()
                .addInventoryChangedCriterion("vanilla", Items.ELYTRA)
                .addInventoryChangedCriterion("crystalite", EndItems.CRYSTALITE_ELYTRA)
                .addInventoryChangedCriterion("armored", EndItems.ARMORED_ELYTRA)
                .requirements(Strategy.AND)
                .build();

        Identifier infusion = AdvancementManager.Builder
                .create(BetterEnd.C.mk("infusion"))
                .parent(enterEnd)
                .startDisplay(EndBlocks.INFUSION_PEDESTAL)
                .endDisplay()
                .addInventoryChangedCriterion("infusion_pedestal", EndBlocks.INFUSION_PEDESTAL)
                .requirements(Strategy.OR)
                .build();

        Identifier infusionFinished = AdvancementManager.Builder
                .create(BetterEnd.C.mk("infusion_finished"))
                .parent(infusion)
                .startDisplay(Items.ENDER_EYE)
                .frame(AdvancementType.GOAL)
                .endDisplay()
                .addCriterion("finished", BECriteria.INFUSION_FINISHED_TRIGGER)
                .requirements(Strategy.OR)
                .build();


        Identifier allTheTemplates = AdvancementManager.Builder
                .create(BetterEnd.C.mk("all_the_templates"))
                .parent(enterEnd)
                .startDisplay(EndTemplates.TOOL_ASSEMBLY)
                .frame(AdvancementType.CHALLENGE)
                .endDisplay()
                .addInventoryChangedAnyCriterion("got_handle", EndTemplates.HANDLE_ATTACHMENT)
                .addInventoryChangedAnyCriterion("got_tool", EndTemplates.TOOL_ASSEMBLY)
                .addInventoryChangedAnyCriterion("got_leather", EndTemplates.LEATHER_HANDLE_ATTACHMENT)
                .addInventoryChangedAnyCriterion("got_plate", EndTemplates.PLATE_UPGRADE)
                .addInventoryChangedAnyCriterion("got_terminite", EndTemplates.TERMINITE_UPGRADE)
                .addInventoryChangedAnyCriterion("got_aeternium", EndTemplates.AETERNIUM_UPGRADE)
                .addInventoryChangedAnyCriterion("got_thallasium", EndTemplates.THALLASIUM_UPGRADE)
                .addInventoryChangedAnyCriterion("got_netherite", EndTemplates.NETHERITE_UPGRADE)
                .requirements(Strategy.AND)
                .rewardXP(1500)
                .build();

        Identifier hammer = AdvancementManager.Builder
                .create(BetterEnd.C.mk("hammer"))
                .parent(enterEnd)
                .startDisplay(EndItems.DIAMOND_HAMMER)
                .endDisplay()
                .addInventoryChangedCriterion("got_diamond_hammer", EndItems.DIAMOND_HAMMER)
                .addInventoryChangedCriterion("got_thallasium_hammer", EndBlocks.THALLASIUM.hammer)
                .addInventoryChangedCriterion("got_terminite_hammer", EndBlocks.TERMINITE.hammer)
                .requirements(Strategy.OR)
                .build();

        Identifier thallasiumAnvil = AdvancementManager.Builder
                .create(BetterEnd.C.mk("thallasium_anvil"))
                .parent(hammer)
                .startDisplay(EndBlocks.THALLASIUM.anvilBlock)
                .endDisplay()
                .addInventoryChangedCriterion("got_thallasium_anvil", EndBlocks.THALLASIUM.anvilBlock)
                .requirements(Strategy.OR)
                .build();

        Identifier thallasiumPlate = AdvancementManager.Builder
                .create(BetterEnd.C.mk("thallasium_plate"))
                .parent(thallasiumAnvil)
                .startDisplay(EndBlocks.THALLASIUM.forgedPlate)
                .endDisplay()
                .addInventoryChangedCriterion("got_thallasium_plate", EndBlocks.THALLASIUM.forgedPlate)
                .requirements(Strategy.OR)
                .build();

        Identifier terminiteAnvil = AdvancementManager.Builder
                .create(BetterEnd.C.mk("terminite_anvil"))
                .parent(thallasiumAnvil)
                .startDisplay(EndBlocks.TERMINITE.anvilBlock)
                .endDisplay()
                .addInventoryChangedCriterion("got_terminite_anvil", EndBlocks.TERMINITE.anvilBlock)
                .requirements(Strategy.OR)
                .build();

        Identifier terminitePlate = AdvancementManager.Builder
                .create(BetterEnd.C.mk("terminite_plate"))
                .parent(terminiteAnvil)
                .startDisplay(EndBlocks.TERMINITE.forgedPlate)
                .endDisplay()
                .addInventoryChangedCriterion("got_erminite_plate", EndBlocks.TERMINITE.forgedPlate)
                .requirements(Strategy.OR)
                .build();

        Identifier aeterniumAnvil = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_anvil"))
                .parent(terminiteAnvil)
                .startDisplay(EndBlocks.AETERNIUM_ANVIL)
                .frame(AdvancementType.CHALLENGE)
                .endDisplay()
                .addInventoryChangedCriterion("got_aeternium_anvil", EndBlocks.AETERNIUM_ANVIL)
                .requirements(Strategy.OR)
                .rewardXP(500)
                .build();

        Identifier aeterniumHammerHead = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_hammer_head"))
                .parent(aeterniumAnvil)
                .startDisplay(EndItems.AETERNIUM_HAMMER_HEAD)
                .endDisplay()
                .addInventoryChangedCriterion("got_aeternium_hammer_head", EndItems.AETERNIUM_HAMMER_HEAD)
                .requirements(Strategy.OR)
                .build();

        Identifier aeterniumHammer = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_hammer"))
                .parent(aeterniumHammerHead)
                .startDisplay(EndItems.AETERNIUM_HAMMER)
                .endDisplay()
                .addInventoryChangedCriterion("got_aeternium_hammer", EndItems.AETERNIUM_HAMMER)
                .requirements(Strategy.OR)
                .build();

        Identifier aeterniumPlate = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_plate"))
                .parent(aeterniumHammer)
                .startDisplay(EndItems.AETERNIUM_FORGED_PLATE)
                .frame(AdvancementType.GOAL)
                .endDisplay()
                .addInventoryChangedCriterion("got_aeternium_plate", EndItems.AETERNIUM_FORGED_PLATE)
                .requirements(Strategy.OR)
                .rewardXP(200)
                .build();

        Identifier thallasiumArmor = addArmor(EndBlocks.THALLASIUM)
                .parent(thallasiumPlate)
                .requirements(Strategy.OR)
                .build();

        Identifier thallasiumHead = addToolHeads(EndBlocks.THALLASIUM)
                .parent(thallasiumAnvil)
                .requirements(Strategy.OR)
                .build();

        Identifier thallasium = addTools(EndBlocks.THALLASIUM)
                .parent(thallasiumHead)
                .requirements(Strategy.OR)
                .build();

        Identifier terminiteHead = addToolHeads(EndBlocks.TERMINITE)
                .parent(terminiteAnvil)
                .requirements(Strategy.OR)
                .build();

        Identifier terminite = addTools(EndBlocks.TERMINITE)
                .parent(terminiteHead)
                .requirements(Strategy.OR)
                .build();

        Identifier terminiteArmor = addArmor(EndBlocks.TERMINITE)
                .parent(terminitePlate)
                .requirements(Strategy.OR)
                .build();


        Identifier aeterniumHead = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_tool_head"))
                .startDisplay(EndItems.AETERNIUM_PICKAXE_HEAD)
                .frame(AdvancementType.GOAL)
                .endDisplay()
                .parent(aeterniumHammer)
                .addInventoryChangedCriterion("got_aeternium_pickaxe_head", EndItems.AETERNIUM_PICKAXE_HEAD)
                .addInventoryChangedCriterion("got_aeternium_hoe_head", EndItems.AETERNIUM_HOE_HEAD)
                .addInventoryChangedCriterion("got_aeternium_axe_head", EndItems.AETERNIUM_AXE_HEAD)
                .addInventoryChangedCriterion("got_aeternium_shovel_head", EndItems.AETERNIUM_SHOVEL_HEAD)
                .addInventoryChangedCriterion(
                        "got_aeternium_sword_head",
                        EndItems.AETERNIUM_SWORD_BLADE,
                        EndItems.AETERNIUM_SWORD_HANDLE
                )
                .requirements(Strategy.AND)
                .rewardXP(200)
                .build();

        Identifier aeternium = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_tool"))
                .startDisplay(EndItems.AETERNIUM_PICKAXE)
                .frame(AdvancementType.CHALLENGE)
                .endDisplay()
                .parent(aeterniumHead)
                .addInventoryChangedCriterion("got_aeternium_pickaxe", EndItems.AETERNIUM_PICKAXE)
                .addInventoryChangedCriterion("got_aeternium_hoe", EndItems.AETERNIUM_HOE)
                .addInventoryChangedCriterion("got_aeternium_axe", EndItems.AETERNIUM_AXE)
                .addInventoryChangedCriterion("got_aeternium_shovel", EndItems.AETERNIUM_SHOVEL)
                .addInventoryChangedCriterion("got_aeternium_sword", EndItems.AETERNIUM_SWORD)
                .requirements(Strategy.AND)
                .rewardXP(2000)
                .build();

        Identifier aeterniumArmor = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_armor"))
                .startDisplay(EndItems.AETERNIUM_CHESTPLATE)
                .frame(AdvancementType.CHALLENGE)
                .endDisplay()
                .parent(aeterniumPlate)
                .addInventoryChangedCriterion("got_aeternium_helmet", EndItems.AETERNIUM_HELMET)
                .addInventoryChangedCriterion("got_aeternium_chestplate", EndItems.AETERNIUM_CHESTPLATE)
                .addInventoryChangedCriterion("got_aeternium_leggings", EndItems.AETERNIUM_LEGGINGS)
                .addInventoryChangedCriterion("got_aeternium_boots", EndItems.AETERNIUM_BOOTS)
                .requirements(Strategy.AND)
                .rewardXP(2000)
                .build();
    }

    AdvancementManager.Builder addTools(MetalMaterial mat) {
        return AdvancementManager.Builder
                .create(BetterEnd.C.mk(mat.name + "_tool"))
                .startDisplay(mat.pickaxe)
                .endDisplay()
                .addInventoryChangedCriterion("got_" + mat.name + "_pickaxe", mat.pickaxe)
                .addInventoryChangedCriterion("got_" + mat.name + "_hoe", mat.hoe)
                .addInventoryChangedCriterion("got_" + mat.name + "_axe", mat.axe)
                .addInventoryChangedCriterion("got_" + mat.name + "_shovel", mat.shovel)
                .addInventoryChangedCriterion("got_" + mat.name + "_sword", mat.sword);
    }

    AdvancementManager.Builder addToolHeads(MetalMaterial mat) {
        return AdvancementManager.Builder
                .create(BetterEnd.C.mk(mat.name + "_tool_head"))
                .startDisplay(mat.pickaxeHead)
                .endDisplay()
                .addInventoryChangedCriterion("got_" + mat.name + "_pickaxe_head", mat.pickaxeHead)
                .addInventoryChangedCriterion("got_" + mat.name + "_hoe_head", mat.hoeHead)
                .addInventoryChangedCriterion("got_" + mat.name + "_axe_head", mat.axeHead)
                .addInventoryChangedCriterion("got_" + mat.name + "_shovel_head", mat.shovelHead)
                .addInventoryChangedCriterion("got_" + mat.name + "_sword_head", mat.swordBlade, mat.swordHandle);
    }

    AdvancementManager.Builder addArmor(MetalMaterial mat) {
        return AdvancementManager.Builder
                .create(BetterEnd.C.mk(mat.name + "_armor"))
                .startDisplay(mat.chestplate)
                .endDisplay()
                .addInventoryChangedCriterion("got_" + mat.name + "_helmet", mat.helmet)
                .addInventoryChangedCriterion("got_" + mat.name + "_chestplate", mat.chestplate)
                .addInventoryChangedCriterion("got_" + mat.name + "_leggings", mat.leggings)
                .addInventoryChangedCriterion("got_" + mat.name + "_boots", mat.boots);
    }
}

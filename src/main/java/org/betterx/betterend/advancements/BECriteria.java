package org.betterx.betterend.advancements;

import org.betterx.betterend.BetterEnd;

import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

import java.util.Optional;
import net.neoforged.neoforge.registries.RegisterEvent;

public class BECriteria {
    public static Identifier PORTAL_ON_ID = BetterEnd.C.mk("portal_on");
    public static Identifier PORTAL_TRAVEL_ID = BetterEnd.C.mk("portal_travel");
    public static Identifier INFUSION_FINISHED_ID = BetterEnd.C.mk("infusion_finished");


    public static PlayerTrigger PORTAL_ON;
    public static PlayerTrigger PORTAL_TRAVEL;
    public static PlayerTrigger INFUSION_FINISHED;

    public static Criterion<PlayerTrigger.TriggerInstance> PORTAL_ON_TRIGGER;
    public static Criterion<PlayerTrigger.TriggerInstance> PORTAL_TRAVEL_TRIGGER;
    public static Criterion<PlayerTrigger.TriggerInstance> INFUSION_FINISHED_TRIGGER;
    private static boolean initialized = false;

    public static void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.TRIGGER_TYPE)) return;
        event.register(Registries.TRIGGER_TYPE, BECriteria::register);
    }

    public static void register(RegisterEvent.RegisterHelper<CriterionTrigger<?>> helper) {
        if (initialized) return;
        initialized = true;
        PORTAL_ON = new PlayerTrigger();
        PORTAL_TRAVEL = new PlayerTrigger();
        INFUSION_FINISHED = new PlayerTrigger();

        helper.register(PORTAL_ON_ID, PORTAL_ON);
        helper.register(PORTAL_TRAVEL_ID, PORTAL_TRAVEL);
        helper.register(INFUSION_FINISHED_ID, INFUSION_FINISHED);

        PORTAL_ON_TRIGGER = PORTAL_ON.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
        PORTAL_TRAVEL_TRIGGER = PORTAL_TRAVEL.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
        INFUSION_FINISHED_TRIGGER = INFUSION_FINISHED.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
    }
}

package org.betterx.betterend.effects;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.wover.potions.api.BrewingBuilder;
import org.betterx.wover.potions.api.PotionManager;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Objects;
import org.jetbrains.annotations.ApiStatus;

public class EndPotions {
    private static final ResourceKey<Potion> END_VEIL_KEY = ResourceKey.create(Registries.POTION, BetterEnd.C.mk("end_veil"));
    private static final ResourceKey<Potion> LONG_END_VEIL_KEY = ResourceKey.create(Registries.POTION, BetterEnd.C.mk("long_end_veil"));
    public static Holder<Potion> END_VEIL;
    public static Holder<Potion> LONG_END_VEIL;


    @ApiStatus.Internal
    public static void register() {
        PotionManager.BOOTSTRAP_POTIONS.subscribe(EndPotions::bootstrap);
    }

    public static void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.POTION)) return;
        event.register(Registries.POTION, helper -> {
            helper.register(
                    END_VEIL_KEY.identifier(),
                    new Potion(BetterEnd.MOD_ID + "_end_veil", new MobEffectInstance(EndStatusEffects.END_VEIL, 3600))
            );
            helper.register(
                    LONG_END_VEIL_KEY.identifier(),
                    new Potion(BetterEnd.MOD_ID + "_long_end_veil", new MobEffectInstance(EndStatusEffects.END_VEIL, 9600))
            );
            END_VEIL = BuiltInRegistries.POTION.get(END_VEIL_KEY).orElseThrow();
            LONG_END_VEIL = BuiltInRegistries.POTION.get(LONG_END_VEIL_KEY).orElseThrow();
        });
    }

    private static void bootstrap(BrewingBuilder builder) {
        builder.addMix(Potions.AWKWARD, EndItems.ENDER_DUST, Objects.requireNonNull(END_VEIL));
        builder.addMix(END_VEIL, Items.REDSTONE, Objects.requireNonNull(LONG_END_VEIL));
        builder.addMix(Potions.AWKWARD, EndBlocks.MURKWEED.asItem(), Potions.NIGHT_VISION);
    }
}

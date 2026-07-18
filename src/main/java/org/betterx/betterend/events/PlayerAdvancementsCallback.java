package org.betterx.betterend.events;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public interface PlayerAdvancementsCallback {
    List<PlayerAdvancementsCallback> LISTENERS = new ArrayList<>();
    static void register(PlayerAdvancementsCallback callback) {
        LISTENERS.add(callback);
    }
    static void fire(ServerPlayer player, AdvancementHolder advancement, String criterionName) {
        for (PlayerAdvancementsCallback callback : LISTENERS) {
            callback.onAdvancementComplete(player, advancement, criterionName);
        }
    }

    void onAdvancementComplete(ServerPlayer player, AdvancementHolder advancement, String criterionName);
}

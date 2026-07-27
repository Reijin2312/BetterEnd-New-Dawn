package org.betterx.betterend.registry;

import org.betterx.betterend.BetterEnd;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;

import net.neoforged.neoforge.registries.RegisterEvent;
import net.minecraft.core.registries.BuiltInRegistries;

public class EndSounds {
    // Music
    public static Holder<SoundEvent> MUSIC_FOREST;
    public static Holder<SoundEvent> MUSIC_WATER;
    public static Holder<SoundEvent> MUSIC_DARK;
    public static Holder<SoundEvent> MUSIC_OPENSPACE;
    public static Holder<SoundEvent> MUSIC_CAVES;
    public static Holder<SoundEvent> MUSIC_ENDER_HOLLOW;

    // Ambient
    public static Holder<SoundEvent> AMBIENT_FOGGY_MUSHROOMLAND;
    public static Holder<SoundEvent> AMBIENT_CHORUS_FOREST;
    public static Holder<SoundEvent> AMBIENT_MEGALAKE;
    public static Holder<SoundEvent> AMBIENT_DUST_WASTELANDS;
    public static Holder<SoundEvent> AMBIENT_MEGALAKE_GROVE;
    public static Holder<SoundEvent> AMBIENT_BLOSSOMING_SPIRES;
    public static Holder<SoundEvent> AMBIENT_SULPHUR_SPRINGS;
    public static Holder<SoundEvent> AMBIENT_UMBRELLA_JUNGLE;
    public static Holder<SoundEvent> AMBIENT_GLOWING_GRASSLANDS;
    public static Holder<SoundEvent> AMBIENT_CAVES;
    public static Holder<SoundEvent> AMBIENT_AMBER_LAND;
    public static Holder<SoundEvent> UMBRA_VALLEY;

    // Entity
    public static Holder<SoundEvent> ENTITY_DRAGONFLY;
    public static Holder<SoundEvent> ENTITY_SHADOW_WALKER;
    public static Holder<SoundEvent> ENTITY_SHADOW_WALKER_DAMAGE;
    public static Holder<SoundEvent> ENTITY_SHADOW_WALKER_DEATH;

    // Records
    public static Holder.Reference<SoundEvent> RECORD_STRANGE_AND_ALIEN_SOUND;
    public static Holder.Reference<SoundEvent> RECORD_GRASPING_AT_STARS_SOUND;
    public static Holder.Reference<SoundEvent> RECORD_ENDSEEKER_SOUND;
    public static Holder.Reference<SoundEvent> RECORD_EO_DRACONA_SOUND;
    public static Holder.Reference<SoundEvent> RECORD_ENDER_HOLLOW_SOUND;
    public static Holder.Reference<SoundEvent> RECORD_MOONLIT_UNDERCURRENTS_SOUND;

    public static final ResourceKey<JukeboxSong> RECORD_STRANGE_AND_ALIEN = createSongKey("strange_and_alien");
    public static final ResourceKey<JukeboxSong> RECORD_GRASPING_AT_STARS = createSongKey("grasping_at_stars");
    public static final ResourceKey<JukeboxSong> RECORD_ENDSEEKER = createSongKey("endseeker");
    public static final ResourceKey<JukeboxSong> RECORD_EO_DRACONA = createSongKey("eo_dracona");
    public static final ResourceKey<JukeboxSong> RECORD_ENDER_HOLLOW = createSongKey("ender_hollow");
    public static final ResourceKey<JukeboxSong> RECORD_MOONLIT_UNDERCURRENTS = createSongKey("moonlit_undercurrents");

    public static void register(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.SOUND_EVENT)) return;
        event.register(Registries.SOUND_EVENT, helper -> {
            MUSIC_FOREST = register(helper, key("betterend.music.forest"));
            MUSIC_WATER = register(helper, key("betterend.music.water"));
            MUSIC_DARK = register(helper, key("betterend.music.dark"));
            MUSIC_OPENSPACE = register(helper, key("betterend.music.openspace"));
            MUSIC_CAVES = register(helper, key("betterend.music.caves"));
            MUSIC_ENDER_HOLLOW = register(helper, key("betterend.music.ender_hollow"));

            AMBIENT_FOGGY_MUSHROOMLAND = register(helper, key("betterend.ambient.foggy_mushroomland"));
            AMBIENT_CHORUS_FOREST = register(helper, key("betterend.ambient.chorus_forest"));
            AMBIENT_MEGALAKE = register(helper, key("betterend.ambient.megalake"));
            AMBIENT_DUST_WASTELANDS = register(helper, key("betterend.ambient.dust_wastelands"));
            AMBIENT_MEGALAKE_GROVE = register(helper, key("betterend.ambient.megalake_grove"));
            AMBIENT_BLOSSOMING_SPIRES = register(helper, key("betterend.ambient.blossoming_spires"));
            AMBIENT_SULPHUR_SPRINGS = register(helper, key("betterend.ambient.sulphur_springs"));
            AMBIENT_UMBRELLA_JUNGLE = register(helper, key("betterend.ambient.umbrella_jungle"));
            AMBIENT_GLOWING_GRASSLANDS = register(helper, key("betterend.ambient.glowing_grasslands"));
            AMBIENT_CAVES = register(helper, key("betterend.ambient.caves"));
            AMBIENT_AMBER_LAND = register(helper, key("betterend.ambient.amber_land"));
            UMBRA_VALLEY = register(helper, key("betterend.ambient.umbra_valley"));

            ENTITY_DRAGONFLY = register(helper, key("betterend.entity.dragonfly"));
            ENTITY_SHADOW_WALKER = register(helper, key("betterend.entity.shadow_walker"));
            ENTITY_SHADOW_WALKER_DAMAGE = register(helper, key("betterend.entity.shadow_walker_damage"));
            ENTITY_SHADOW_WALKER_DEATH = register(helper, key("betterend.entity.shadow_walker_death"));

            RECORD_STRANGE_AND_ALIEN_SOUND = registerRef(helper, key("betterend.record.strange_and_alien"));
            RECORD_GRASPING_AT_STARS_SOUND = registerRef(helper, key("betterend.record.grasping_at_stars"));
            RECORD_ENDSEEKER_SOUND = registerRef(helper, key("betterend.record.endseeker"));
            RECORD_EO_DRACONA_SOUND = registerRef(helper, key("betterend.record.eo_dracona"));
            RECORD_ENDER_HOLLOW_SOUND = registerRef(helper, key("betterend.record.ender_hollow"));
            RECORD_MOONLIT_UNDERCURRENTS_SOUND = registerRef(helper, key("betterend.record.moonlit_undercurrents"));
        });
    }

    private static ResourceKey<JukeboxSong> createSongKey(String string) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, BetterEnd.C.mk(string));
    }

    private static ResourceKey<SoundEvent> key(String id) {
        return ResourceKey.create(Registries.SOUND_EVENT, BetterEnd.C.mk(id));
    }

    private static Holder.Reference<SoundEvent> registerRef(RegisterEvent.RegisterHelper<SoundEvent> helper, ResourceKey<SoundEvent> key) {
        helper.register(key.location(), SoundEvent.createVariableRangeEvent(key.location()));
        return (Holder.Reference<SoundEvent>) BuiltInRegistries.SOUND_EVENT.getHolder(key).orElseThrow();
    }

    private static Holder<SoundEvent> register(RegisterEvent.RegisterHelper<SoundEvent> helper, ResourceKey<SoundEvent> key) {
        helper.register(key.location(), SoundEvent.createVariableRangeEvent(key.location()));
        return BuiltInRegistries.SOUND_EVENT.getHolder(key).orElseThrow();
    }
}

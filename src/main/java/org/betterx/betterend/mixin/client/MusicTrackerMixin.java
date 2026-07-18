package org.betterx.betterend.mixin.client;

import org.betterx.betterend.config.Configs;

import net.minecraft.sounds.Music;
import net.minecraft.world.level.Level;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public class MusicTrackerMixin {
    @Unique private static final float FADE_SPEED = 0.2f; // Units per second (0.2f -> Fade across 5 seconds)
    @Unique private static final float TICK_DELTA = 0.05f;
    // Note: Assume game is at a constant 20 tps since MC doesn't have getTPS()
    // The use of currentTimeMillis() is ditched since it is overly complex for this system
    // The difference from this constant will only be noticeable if the game's TPS is extremely low
    // If the game is lagging to that extent, smooth music blending is the least of the player's worries

    @Unique private final MusicManager be_thisObj = (MusicManager)(Object)this;
    @Unique private boolean be_waitChange = false;
    @Unique private float be_volume = 1.0f;

    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private RandomSource random;
    @Shadow private SoundInstance currentMusic;
    @Shadow private int nextSongDelay;

    @Unique
    private boolean be_isCorrectDimension() {
        return minecraft.player != null && minecraft.level != null
                && minecraft.level.dimension() == Level.END;
    }

    @Unique
    private boolean be_shouldChangeMusic(Music toMusic) {
        return currentMusic == null || !toMusic.sound().value().location().equals(currentMusic.getIdentifier());
    }

    @Inject(method = "startPlaying", at = @At("TAIL"))
    public void be_startPlaying(Music music, CallbackInfo ci) {
        be_volume = 0.0f; // Mostly to fix issues when the blending system becomes desynced due to other dims
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void be_onTick(CallbackInfo ci) {
        if (!Configs.CLIENT_CONFIG.blendBiomeMusic.get() || !be_isCorrectDimension()) {
            be_waitChange = false;
            be_volume = 1.0f;
            return;
        }

        Music targetMusic = minecraft.getSituationalMusic();
        if (targetMusic == null || !targetMusic.replaceCurrentMusic()) {
            be_waitChange = false;
            be_volume = 1.0f;
            return; // If the target music cannot replace the current, let vanilla handle it
        }

        if (currentMusic != null && !minecraft.getSoundManager().isActive(currentMusic)) {
            currentMusic = null;
            nextSongDelay = Math.min(
                    nextSongDelay,
                    Mth.nextInt(random, targetMusic.minDelay(), targetMusic.maxDelay())
            );
        }
        nextSongDelay = Math.min(nextSongDelay, targetMusic.maxDelay());

        if (currentMusic == null) {
            if (nextSongDelay-- <= 0) {
                be_waitChange = false;
                be_thisObj.startPlaying(targetMusic);
                if (currentMusic instanceof AbstractSoundInstanceAccessor accessor) {
                    accessor.setVolume(0.0f);
                }
            }
            ci.cancel();
            return;
        }

        boolean volumeChanged = false;
        if (be_waitChange || be_shouldChangeMusic(targetMusic)) {
            if (!be_waitChange) {
                nextSongDelay = random.nextInt(0, Math.max(targetMusic.minDelay() / 2, 1));
                be_waitChange = true;
            }
            if (be_volume > 0.0f) {
                // Fade out current music
                volumeChanged = true;
                be_volume -= FADE_SPEED * TICK_DELTA;
                if (be_volume <= 0.0f) {
                    be_volume = 0.0f;
                    minecraft.getSoundManager().stop(currentMusic);
                    currentMusic = null;
                }
            } else if (nextSongDelay > 0) {
                // In-between music delay
                nextSongDelay -= 1;
            } else {
                // Start new music
                be_waitChange = false;
                be_thisObj.startPlaying(targetMusic);
                if (currentMusic instanceof AbstractSoundInstanceAccessor accessor) {
                    accessor.setVolume(0.0f);
                }
            }
        } else if (be_volume < 1.0f) {
            // Fade in new music
            volumeChanged = true;
            be_volume += FADE_SPEED * TICK_DELTA;
        }

        if (volumeChanged) {
            be_volume = Mth.clamp(be_volume, 0.0f, 1.0f);
            if (currentMusic instanceof AbstractSoundInstanceAccessor accessor) {
                accessor.setVolume(be_volume);
            }
        }

        ci.cancel();
    }
}

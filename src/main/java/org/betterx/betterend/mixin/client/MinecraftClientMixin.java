package org.betterx.betterend.mixin.client;

import org.betterx.bclib.util.MHelper;

import net.minecraft.client.main.GameConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {
    @Unique
    private static Music END_MUSIC = null;

    @Shadow
    public LocalPlayer player;

    @Shadow
    public Screen screen;

    @Final
    @Shadow
    public Gui gui;

    @Final
    @Shadow
    public GameRenderer gameRenderer;

    @Shadow
    public ClientLevel level;

    @Unique
    private static Music be_getOrCacheEndMusic() {
        if (END_MUSIC == null) {
            END_MUSIC = new Music(
                    Musics.END.sound(),
                    Musics.END.minDelay(),
                    Musics.END.maxDelay(),
                    false // Don't replace current music
            );
        }
        return END_MUSIC;
    }

    @Inject(method = "<init>*", at = @At(value = "TAIL"))
    private void onInit(GameConfig args, CallbackInfo info) {
        be_getOrCacheEndMusic();
    }


    @Inject(method = "getSituationalMusic", at = @At("HEAD"), cancellable = true)
    private void be_getEndMusic(CallbackInfoReturnable<Music> info) {
        if (!(this.screen instanceof WinScreen) && this.player != null) {
            if (this.player.level().dimension() == Level.END) {
                if (this.gui.getBossOverlay().shouldPlayMusic() && MHelper.lengthSqr(
                        this.player.getX(),
                        this.player.getZ()
                ) < 250000) {
                    info.setReturnValue(Musics.END_BOSS);
                } else {
                    var camera = this.gameRenderer.getMainCamera();
                    if (camera == null) {
                        info.setReturnValue(be_getOrCacheEndMusic());
                    } else {
                        var backgroundMusic = camera.attributeProbe().getValue(EnvironmentAttributes.BACKGROUND_MUSIC, 1.0F);
                        boolean creativeFlying = this.player.getAbilities().instabuild && this.player.getAbilities().mayfly;
                        boolean underwater = this.player.isUnderWater();
                        info.setReturnValue(backgroundMusic.select(creativeFlying, underwater).orElse(be_getOrCacheEndMusic()));
                    }
                }
                info.cancel();
            }
        }
    }
}

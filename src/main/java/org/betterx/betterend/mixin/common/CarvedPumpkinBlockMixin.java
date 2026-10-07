package org.betterx.betterend.mixin.common;

import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(CarvedPumpkinBlock.class)
public class CarvedPumpkinBlockMixin {
    @ModifyArg(
            method = "getOrCreateSnowGolemBase",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/pattern/BlockInWorld;hasState(Ljava/util/function/Predicate;)Ljava/util/function/Predicate;"
            ),
            index = 0
    )
    private Predicate<BlockState> betterend$allowDenseSnowInSnowGolemBase(Predicate<BlockState> original) {
        return state -> original.test(state) || state.is(EndBlocks.DENSE_SNOW);
    }

    @ModifyArg(
            method = "getOrCreateSnowGolemFull",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/pattern/BlockInWorld;hasState(Ljava/util/function/Predicate;)Ljava/util/function/Predicate;",
                    ordinal = 1
            ),
            index = 0
    )
    private Predicate<BlockState> betterend$allowDenseSnowInSnowGolem(Predicate<BlockState> original) {
        return state -> original.test(state) || state.is(EndBlocks.DENSE_SNOW);
    }
}


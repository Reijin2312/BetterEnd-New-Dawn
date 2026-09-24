package org.betterx.betterend.mixin.common;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ChorusPlantFeature;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChorusPlantFeature.class)
public class ChorusPlantFeatureMixin {
    @Inject(method = "place", at = @At("HEAD"), cancellable = true, remap = false)
    private void be_place(
            WorldGenLevel structureWorldAccess,
            ChunkGenerator generator,
            RandomSource random,
            BlockPos blockPos,
            CallbackInfoReturnable<Boolean> info
    ) {
        if (structureWorldAccess.isEmptyBlock(blockPos) && structureWorldAccess.getBlockState(blockPos.below())
                                                                               .is(EndBlocks.CHORUS_NYLIUM)) {
            ChorusFlowerBlock.generatePlant(structureWorldAccess, blockPos, random, MHelper.randRange(8, 16, random));
            BlockState bottom = structureWorldAccess.getBlockState(blockPos);
            if (bottom.is(Blocks.CHORUS_PLANT)) {
                BlocksHelper.setWithoutUpdate(
                        structureWorldAccess,
                        blockPos,
                        bottom.setValue(PipeBlock.DOWN, true)
                );
            }
            info.setReturnValue(true);
        }
    }
}

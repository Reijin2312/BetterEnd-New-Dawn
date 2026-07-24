package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourPlant;
import org.betterx.bclib.blocks.BaseDoublePlantBlock;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.blocks.basis.EndPlantBlock;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class UmbrellaMossBlock extends EndPlantBlock implements BehaviourPlant {
    public UmbrellaMossBlock() {
        super(BehaviourBuilders.createGrass(MapColor.COLOR_ORANGE).ignitedByLava().lightLevel((state) -> 11));
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasEmissiveLighting(BlockGetter world, BlockPos pos) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public float getAmbientOcclusionLightLevel(BlockGetter world, BlockPos pos) {
        return 1F;
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return world.isEmptyBlock(pos.above());
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        int rot = world.random.nextInt(4);
        BlockState bs = EndBlocks.UMBRELLA_MOSS_TALL.defaultBlockState().setValue(BaseDoublePlantBlock.ROTATION, rot);
        BlocksHelper.setWithoutUpdate(world, pos, bs);
        BlocksHelper.setWithoutUpdate(world, pos.above(), bs.setValue(BaseDoublePlantBlock.TOP, true));
    }

    @Override
    public boolean isTerrain(BlockState state) {
        return state.is(CommonBlockTags.SOIL);
    }
}

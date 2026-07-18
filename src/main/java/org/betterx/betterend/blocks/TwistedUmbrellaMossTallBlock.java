package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourPlant;
import org.betterx.bclib.blocks.BaseDoublePlantBlock;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.interfaces.survives.SurvivesOnJungleMossOrMycelium;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.block.api.model.BlockModelProvider;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;


public class TwistedUmbrellaMossTallBlock extends BaseDoublePlantBlock implements BehaviourPlant, SurvivesOnJungleMossOrMycelium, BlockModelProvider {
    public TwistedUmbrellaMossTallBlock() {
        super(12);
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        ItemEntity item = new ItemEntity(
                world,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                new ItemStack(EndBlocks.TWISTED_UMBRELLA_MOSS)
        );
        world.addFreshEntity(item);
    }

    @Override
    public boolean isTerrain(BlockState state) {
        return SurvivesOnJungleMossOrMycelium.super.isTerrain(state);
    }

    @Override
    public void provideBlockModels(Object modelGenerator) {
    WoverBlockModelGenerators generator = (WoverBlockModelGenerators) modelGenerator;
        generator.createCubeModel(this);
        generator.createFlatItem(this, BetterEnd.C.mk("item/twisted_umbrella_moss_large"));
    }
}

package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourPlant;
import org.betterx.bclib.interfaces.SurvivesOnBlocks;
import org.betterx.bclib.interfaces.SurvivesOnTags;
import org.betterx.betterend.blocks.basis.EndPlantBlock;
import org.betterx.wover.block.api.model.BlockModelProvider;

import net.minecraft.world.level.block.Block;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import java.util.List;

public class TerrainPlantBlock extends EndPlantBlock implements SurvivesOnBlocks, SurvivesOnTags, BehaviourPlant, BlockModelProvider {
    private final List<Block> ground;
    private final List<TagKey<Block>> groundTags;

    public TerrainPlantBlock(Block... ground) {
        super(BehaviourBuilders.createPlant(ground.length == 0 ? MapColor.PLANT : ground[0].defaultMapColor())
                               .ignitedByLava()
                               .offsetType(OffsetType.XZ)
                               .replaceable());
        this.ground = List.of(ground);
        this.groundTags = List.of();
    }

    public TerrainPlantBlock(TagKey<Block> groundTag) {
        super(BehaviourBuilders.createPlant(MapColor.PLANT)
                               .ignitedByLava()
                               .offsetType(OffsetType.XZ)
                               .replaceable());
        this.ground = List.of();
        this.groundTags = List.of(groundTag);
    }

    @Override
    public List<Block> getSurvivableBlocks() {
        return ground;
    }

    @Override
    public List<TagKey<Block>> getSurvivableTags() {
        return groundTags;
    }

    @Override
    public String getSurvivableBlocksString() {
        return groundTags.isEmpty()
                ? SurvivesOnBlocks.super.getSurvivableBlocksString()
                : SurvivesOnTags.super.getSurvivableBlocksString();
    }

    @Override
    public boolean isSurvivable(BlockState state) {
        return groundTags.isEmpty()
                ? SurvivesOnBlocks.super.isSurvivable(state)
                : SurvivesOnTags.super.isSurvivable(state);
    }

    @Override
    public boolean isTerrain(BlockState state) {
        return isSurvivable(state);
    }

//    @Override
//    public void provideBlockModels(WoverBlockModelGenerators generator) {
//        generator.createCubeModel(this);
//        generator.createFlatItem(this);
//    }
}

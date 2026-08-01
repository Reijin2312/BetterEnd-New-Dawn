package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.BaseBlock;
import org.betterx.bclib.behaviours.interfaces.BehaviourCompostable;
import org.betterx.bclib.interfaces.tools.AddMineableHoe;
import org.betterx.betterend.client.models.EndModels;
import org.betterx.wover.block.api.model.BlockModelProvider;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.block.api.model.WoverBlockModelGeneratorsAccess;

import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;


public class GlowingHymenophoreBlock extends BaseBlock implements AddMineableHoe, BehaviourCompostable, BlockModelProvider {
    public GlowingHymenophoreBlock() {
        this(MapColor.NONE);
    }

    public GlowingHymenophoreBlock(MapColor mapColor) {
        super(Properties.of().mapColor(mapColor).strength(1.0F)
                        .lightLevel((bs) -> 15)
                        .sound(SoundType.SHROOMLIGHT)
        );
    }

    @Override
    public float compostingChance() {
        return 0.65F;
    }

    @Override
    public void provideBlockModels(Object modelGenerator) {
    WoverBlockModelGenerators generator = (WoverBlockModelGenerators) modelGenerator;
        provideUnshadedCubeModel(generator, this);
    }

    public static void provideUnshadedCubeModel(
            WoverBlockModelGenerators generator,
            Block glowingHymenophoreBlock
    ) {
        final var model = EndModels.CUBE_NO_SHADE.create(
                glowingHymenophoreBlock,
                TextureMapping.defaultTexture(glowingHymenophoreBlock),
                generator.modelOutput()
        );
        generator.acceptBlockState(
                WoverBlockModelGeneratorsAccess.createSimpleBlock(
                        glowingHymenophoreBlock,
                        model
                )
        );
        generator.delegateItemModel(glowingHymenophoreBlock, model);
    }
}

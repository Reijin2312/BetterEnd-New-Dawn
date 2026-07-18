package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.BaseBlock;
import org.betterx.bclib.interfaces.tools.AddMineableAxe;
import org.betterx.betterend.client.models.EndModels;
import org.betterx.wover.block.api.model.BlockModelProvider;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.block.api.model.WoverBlockModelGeneratorsAccess;

import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;


public class GlowingHymenophoreBlock extends BaseBlock.Wood implements AddMineableAxe, BlockModelProvider {
    public GlowingHymenophoreBlock() {
        super(Properties.of()
                        .lightLevel((bs) -> 15)
                        .sound(SoundType.WART_BLOCK)
        );
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
        generator.acceptBlockState(
                WoverBlockModelGeneratorsAccess.createSimpleBlock(
                        glowingHymenophoreBlock,
                        EndModels.CUBE_NO_SHADE.create(
                                glowingHymenophoreBlock,
                                TextureMapping.defaultTexture(glowingHymenophoreBlock),
                                generator.modelOutput()
                        )
                )
        );
    }
}

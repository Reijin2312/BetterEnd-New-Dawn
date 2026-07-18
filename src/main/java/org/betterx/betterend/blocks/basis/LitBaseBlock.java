package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.blocks.BaseBlock;
import org.betterx.bclib.interfaces.RuntimeBlockModelProvider;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;


import org.jetbrains.annotations.Nullable;

import java.io.StringReader;

public class LitBaseBlock extends BaseBlock implements RuntimeBlockModelProvider {
    private static final String PATTERN = "{\"parent\":\"betterend:block/cube_noshade\",\"textures\":{\"texture\":\"betterend:block/name\"}}";

    public LitBaseBlock(Properties settings) {
        super(settings);
    }


    @Nullable
    @Override
    public BlockModel getBlockModel(Identifier resourceLocation, BlockState blockState) {
        return BlockModel.fromStream(new StringReader(PATTERN.replace("name", resourceLocation.getPath())));
    }
}

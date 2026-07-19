package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.blocks.BaseBlock;
import org.betterx.bclib.client.models.ModelsHelper;
import org.betterx.bclib.interfaces.RuntimeBlockModelProvider;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;


import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class LitBaseBlock extends BaseBlock implements RuntimeBlockModelProvider {
    private static final String PATTERN = "{\"parent\":\"betterend:block/cube_noshade\",\"textures\":{\"texture\":\"betterend:block/name\"}}";

    public LitBaseBlock(Properties settings) {
        super(settings);
    }


    @Nullable
    @Override
    public Object getBlockModel(Identifier resourceLocation, BlockState blockState) {
        return ModelsHelper.fromPattern(Optional.of(PATTERN.replace("name", resourceLocation.getPath())));
    }
}

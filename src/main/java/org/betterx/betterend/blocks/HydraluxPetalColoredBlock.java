package org.betterx.betterend.blocks;

import org.betterx.bclib.interfaces.CustomColorProvider;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.client.models.EndModels;
import org.betterx.wover.block.api.model.BlockModelProvider;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class HydraluxPetalColoredBlock extends HydraluxPetalBlock implements CustomColorProvider, BlockModelProvider {
    public HydraluxPetalColoredBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public BlockColor getProvider() {
        return (state, world, pos, tintIndex) -> BlocksHelper.getBlockColor(this);
    }

    @Override
    public ItemColor getItemProvider() {
        return (stack, tintIndex) -> BlocksHelper.getBlockColor(this);
    }

//    @Override
//    @Environment(EnvType.CLIENT)
//    public @Nullable BlockModel getBlockModel(ResourceLocation resourceLocation, BlockState blockState) {
//        String path = "betterend:block/block_petal_colored";
//        Optional<String> pattern = Patterns.createJson(Patterns.BLOCK_PETAL_COLORED, path, path);
//        return ModelsHelper.fromPattern(pattern);
//    }

    private static ResourceLocation PETAL_MODEL;

    @Override
    @Environment(EnvType.CLIENT)
    public void provideBlockModels(WoverBlockModelGenerators generator) {
        final var modelLocation = BetterEnd.C.mk("block/hydralux_petal_block_colored");
        final var mapping = new TextureMapping().put(TextureSlot.TEXTURE, BetterEnd.C.mk("block/hydralux_petal_block_colored"));
        if (PETAL_MODEL == null)
            PETAL_MODEL = EndModels.PETAL_COLORED.create(modelLocation, mapping, generator.modelOutput());
        generator.acceptBlockState(BlockModelGenerators.createSimpleBlock(this, PETAL_MODEL));
        generator.delegateItemModel(this, modelLocation);
    }
}

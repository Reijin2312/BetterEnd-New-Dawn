package org.betterx.betterend.blocks;

import org.betterx.bclib.interfaces.BlockColorProvider;
import org.betterx.bclib.interfaces.CustomColorProvider;
import org.betterx.bclib.interfaces.ItemColorProvider;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.client.models.EndModels;
import org.betterx.wover.block.api.model.BlockModelProvider;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.block.api.model.WoverBlockModelGeneratorsAccess;

import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockBehaviour;


public class HydraluxPetalColoredBlock extends HydraluxPetalBlock implements CustomColorProvider, BlockModelProvider {
    public HydraluxPetalColoredBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public BlockColorProvider getProvider() {
        return (state, world, pos, tintIndex) -> BlocksHelper.getBlockColor(this);
    }

    @Override
    public ItemColorProvider getItemProvider() {
        return (stack, tintIndex) -> BlocksHelper.getBlockColor(this);
    }

//    @Override
//    public @Nullable BlockModel getBlockModel(Identifier resourceLocation, BlockState blockState) {
//        String path = "betterend:block/block_petal_colored";
//        Optional<String> pattern = Patterns.createJson(Patterns.BLOCK_PETAL_COLORED, path, path);
//        return ModelsHelper.fromPattern(pattern);
//    }

    private static Identifier PETAL_MODEL;

    @Override
    public void provideBlockModels(Object modelGenerator) {
        WoverBlockModelGenerators generator = (WoverBlockModelGenerators) modelGenerator;
        final var modelLocation = BetterEnd.C.mk("block/hydralux_petal_block_colored");
        final var mapping = new TextureMapping().put(TextureSlot.TEXTURE, new Material(BetterEnd.C.mk("block/hydralux_petal_block_colored")));
        if (PETAL_MODEL == null)
            PETAL_MODEL = EndModels.PETAL_COLORED.create(modelLocation, mapping, generator.modelOutput());
        generator.acceptBlockState(WoverBlockModelGeneratorsAccess.createSimpleBlock(this, PETAL_MODEL));
        generator.delegateItemModel(this, modelLocation);
    }
}

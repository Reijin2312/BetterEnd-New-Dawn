package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourMetal;
import org.betterx.bclib.behaviours.interfaces.BehaviourStone;
import org.betterx.bclib.behaviours.interfaces.BehaviourWood;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.basis.PedestalBlock;
import org.betterx.betterend.client.models.EndModels;

import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.world.level.block.Block;


public abstract class EndPedestal extends PedestalBlock {

    protected EndPedestal(Block parent) {
        super(parent);
    }


    @Override
    protected TextureMapping createTextureMapping() {
        final var parentTexture = BetterEnd.C.convertNamespace(TextureMapping.getBlockTexture(parent));
        final var polishedTexture = BetterEnd.C.convertNamespace(TextureMapping.getBlockTexture(parent, "_polished"));
        return new TextureMapping()
                .put(TextureSlot.TOP, polishedTexture)
                .put(TextureSlot.BOTTOM, polishedTexture)
                .put(EndModels.BASE, polishedTexture)
                .put(EndModels.PILLAR, parentTexture.withSuffix("_pillar_side"));
    }

    public static class Stone extends EndPedestal implements BehaviourStone {
        public Stone(Block parent) {
            super(parent);
        }

    }

    public static class Wood extends EndPedestal implements BehaviourWood {
        public Wood(Block parent) {
            super(parent);
        }

    }

    public static class Metal extends EndPedestal implements BehaviourMetal {
        public Metal(Block parent) {
            super(parent);
        }

    }
}

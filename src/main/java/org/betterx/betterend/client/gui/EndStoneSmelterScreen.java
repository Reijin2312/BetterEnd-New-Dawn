package org.betterx.betterend.client.gui;

import org.betterx.betterend.BetterEnd;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeBookCategories;


import java.util.List;

public class EndStoneSmelterScreen extends AbstractRecipeBookScreen<EndStoneSmelterMenu> {
    private static final Identifier BACKGROUND_TEXTURE = BetterEnd.C.mk("textures/gui/smelter_gui.png");
    private static final Component FILTER_NAME = Component.translatable("gui.recipebook.toggleRecipes.blastable");
    private static final List<RecipeBookComponent.TabInfo> TABS = List.of(
            new RecipeBookComponent.TabInfo(SearchRecipeBookCategory.BLAST_FURNACE),
            new RecipeBookComponent.TabInfo(Items.REDSTONE_ORE, RecipeBookCategories.BLAST_FURNACE_BLOCKS),
            new RecipeBookComponent.TabInfo(Items.IRON_SHOVEL, Items.GOLDEN_LEGGINGS, RecipeBookCategories.BLAST_FURNACE_MISC)
    );

    public EndStoneSmelterScreen(EndStoneSmelterMenu handler, Inventory inventory, Component title) {
        super(handler, new EndStoneSmelterRecipeBookScreen(handler, FILTER_NAME, TABS), inventory, title);
    }

    @Override
    public void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + 20, this.height / 2 - 49);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                BACKGROUND_TEXTURE,
                x,
                y,
                0.0F,
                0.0F,
                this.imageWidth,
                this.imageHeight,
                256,
                256
        );

        if (this.menu.isBurning()) {
            int litProgress = this.menu.getFuelProgress();
            guiGraphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    BACKGROUND_TEXTURE,
                    x + 56,
                    y + 36 + 12 - litProgress,
                    176,
                    12 - litProgress,
                    14,
                    litProgress + 1,
                    256,
                    256
            );
        }

        int smeltProgress = this.menu.getSmeltProgress();
        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                BACKGROUND_TEXTURE,
                x + 92,
                y + 34,
                176,
                14,
                smeltProgress + 1,
                16,
                256,
                256
        );
    }
}

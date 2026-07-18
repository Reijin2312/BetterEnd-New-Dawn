package org.betterx.betterend.complexmaterials;

import org.betterx.bclib.blocks.BaseSlabBlock;
import org.betterx.bclib.blocks.BaseStairsBlock;
import org.betterx.bclib.blocks.BaseWallBlock;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.EndPedestal;
import org.betterx.betterend.blocks.basis.LitBaseBlock;
import org.betterx.betterend.blocks.basis.LitPillarBlock;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.datagen.betterend.recipes.EndCraftingRecipesProvider;
import org.betterx.wover.recipe.api.CraftingRecipeBuilder;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class CrystalSubblocksMaterial implements MaterialManager.Material {
    public final Block polished;
    public final Block tiles;
    public final Block pillar;
    public final Block stairs;
    public final Block slab;
    public final Block wall;
    public final Block pedestal;
    public final Block bricks;
    public final Block brick_stairs;
    public final Block brick_slab;
    public final Block brick_wall;
    private final Block source;
    private final String name;

    public CrystalSubblocksMaterial(String name, Block source) {
        this.source = source;
        this.name = name;

        BlockBehaviour.Properties material = BlockBehaviour.Properties.ofLegacyCopy(source);
        polished = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_polished"), new LitBaseBlock(material));
        tiles = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_tiles"), new LitBaseBlock(material));
        pillar = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_pillar"), new LitPillarBlock(material));
        stairs = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_stairs"), new BaseStairsBlock.Stone(source));
        slab = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_slab"), new BaseSlabBlock.Stone(source));
        wall = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_wall"), new BaseWallBlock.Stone(source));
        pedestal = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_pedestal"), new EndPedestal.Stone(source));
        bricks = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_bricks"), new LitBaseBlock(material));
        brick_stairs = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_bricks_stairs"), new BaseStairsBlock.Stone(bricks));
        brick_slab = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_bricks_slab"), new BaseSlabBlock.Stone(bricks));
        brick_wall = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_bricks_wall"), new BaseWallBlock.Stone(bricks));


        MaterialManager.register(this);
    }

    @Override
    public void registerBlockTags(TagBootstrapContext<Block> context) {
        context.add(BlockTags.STONE_BRICKS, bricks);
        context.add(BlockTags.WALLS, wall, brick_wall);
        context.add(BlockTags.SLABS, slab, brick_slab);
    }

    @Override
    public void registerItemTags(ItemTagBootstrapContext context) {
        context.add(ItemTags.SLABS, slab.asItem(), brick_slab.asItem());
        context.add(ItemTags.STONE_BRICKS, bricks.asItem());
        context.add(ItemTags.STONE_CRAFTING_MATERIALS, source.asItem());
        context.add(ItemTags.STONE_TOOL_MATERIALS, source.asItem());
    }

    @Override
    public void registerRecipes(RecipeOutput context) {
        CraftingRecipeBuilder craftingRecipeBuilder18 = RecipeBuilder.crafting(BetterEnd.C.mk(name + "_bricks"), bricks);
        CraftingRecipeBuilder craftingRecipeBuilder28 = craftingRecipeBuilder18.outputCount(4);
        CraftingRecipeBuilder craftingRecipeBuilder9 = craftingRecipeBuilder28.shape("##", "##")
                                                                              .addMaterial('#', source);
        craftingRecipeBuilder9.group("end_bricks")
                              .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder17 = RecipeBuilder.crafting(BetterEnd.C.mk(name + "_polished"), polished);
        CraftingRecipeBuilder craftingRecipeBuilder27 = craftingRecipeBuilder17.outputCount(4);
        CraftingRecipeBuilder craftingRecipeBuilder8 = craftingRecipeBuilder27.shape("##", "##")
                                                                              .addMaterial('#', bricks);
        craftingRecipeBuilder8.group("end_tile")
                              .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder16 = RecipeBuilder.crafting(BetterEnd.C.mk(name + "_tiles"), tiles);
        CraftingRecipeBuilder craftingRecipeBuilder26 = craftingRecipeBuilder16.outputCount(4);
        CraftingRecipeBuilder craftingRecipeBuilder7 = craftingRecipeBuilder26.shape("##", "##")
                                                                              .addMaterial('#', polished);
        craftingRecipeBuilder7.group("end_small_tile")
                              .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder25 = RecipeBuilder.crafting(BetterEnd.C.mk(name + "_pillar"), pillar);
        CraftingRecipeBuilder craftingRecipeBuilder6 = craftingRecipeBuilder25.shape("#", "#")
                                                                              .addMaterial('#', slab);
        craftingRecipeBuilder6.group("end_pillar")
                              .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder15 = RecipeBuilder.crafting(BetterEnd.C.mk(name + "_stairs"), stairs);
        CraftingRecipeBuilder craftingRecipeBuilder24 = craftingRecipeBuilder15.outputCount(4);
        CraftingRecipeBuilder craftingRecipeBuilder5 = craftingRecipeBuilder24.shape("#  ", "## ", "###")
                                                                              .addMaterial('#', source);
        craftingRecipeBuilder5.group("end_stone_stairs")
                              .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder14 = RecipeBuilder.crafting(BetterEnd.C.mk(name + "_slab"), slab);
        CraftingRecipeBuilder craftingRecipeBuilder23 = craftingRecipeBuilder14.outputCount(6);
        CraftingRecipeBuilder craftingRecipeBuilder4 = craftingRecipeBuilder23.shape("###")
                                                                              .addMaterial('#', source);
        craftingRecipeBuilder4.group("end_stone_slabs")
                              .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder13 = RecipeBuilder.crafting(BetterEnd.C.mk(name + "_bricks_stairs"), brick_stairs);
        CraftingRecipeBuilder craftingRecipeBuilder22 = craftingRecipeBuilder13.outputCount(4);
        CraftingRecipeBuilder craftingRecipeBuilder3 = craftingRecipeBuilder22.shape("#  ", "## ", "###")
                                                                              .addMaterial('#', bricks);
        craftingRecipeBuilder3.group("end_stone_stairs")
                              .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder12 = RecipeBuilder.crafting(BetterEnd.C.mk(name + "_bricks_slab"), brick_slab);
        CraftingRecipeBuilder craftingRecipeBuilder21 = craftingRecipeBuilder12.outputCount(6);
        CraftingRecipeBuilder craftingRecipeBuilder2 = craftingRecipeBuilder21.shape("###")
                                                                              .addMaterial('#', bricks);
        craftingRecipeBuilder2.group("end_stone_slabs")
                              .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder11 = RecipeBuilder.crafting(BetterEnd.C.mk(name + "_wall"), wall);
        CraftingRecipeBuilder craftingRecipeBuilder20 = craftingRecipeBuilder11.outputCount(6);
        CraftingRecipeBuilder craftingRecipeBuilder1 = craftingRecipeBuilder20.shape("###", "###")
                                                                              .addMaterial('#', source);
        craftingRecipeBuilder1.group("end_wall")
                              .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder10 = RecipeBuilder.crafting(BetterEnd.C.mk(name + "_bricks_wall"), brick_wall);
        CraftingRecipeBuilder craftingRecipeBuilder19 = craftingRecipeBuilder10.outputCount(6);
        CraftingRecipeBuilder craftingRecipeBuilder = craftingRecipeBuilder19.shape("###", "###")
                                                                             .addMaterial('#', bricks);
        craftingRecipeBuilder.group("end_wall")
                             .build(context);

        EndCraftingRecipesProvider.registerPedestal(context, name + "_pedestal", pedestal, slab, pillar);
    }
}

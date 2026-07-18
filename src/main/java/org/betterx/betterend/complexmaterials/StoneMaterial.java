package org.betterx.betterend.complexmaterials;

import org.betterx.bclib.blocks.*;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.EndPedestal;
import org.betterx.betterend.blocks.FlowerPotBlock;
import org.betterx.betterend.blocks.basis.StoneLanternBlock;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.datagen.betterend.recipes.EndCraftingRecipesProvider;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;
import org.betterx.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;

public class StoneMaterial implements MaterialManager.Material {
    public final Block stone;
    public final String name;

    public final Block polished;
    public final Block tiles;
    public final Block pillar;
    public final Block stairs;
    public final Block slab;
    public final Block wall;
    public final Block button;
    public final Block pressurePlate;
    public final Block pedestal;
    public final Block lantern;

    public final Block bricks;
    public final Block brickStairs;
    public final Block brickSlab;
    public final Block brickWall;
    public final Block furnace;
    public final Block flowerPot;

    public static BlockBehaviour.Properties createMaterial(MapColor color) {
        return BlockBehaviour.Properties.ofLegacyCopy(Blocks.END_STONE).mapColor(color);
    }

    public StoneMaterial(String name, MapColor color) {
        BlockBehaviour.Properties material = createMaterial(color);

        this.name = name;
        stone = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name), new BaseBlock.Stone(material));
        polished = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_polished"), new BaseBlock.Stone(material));
        tiles = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_tiles"), new BaseBlock.Stone(material));
        pillar = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_pillar"), new BaseRotatedPillarBlock.Stone(material));
        stairs = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_stairs"), new BaseStairsBlock.Stone(stone));
        slab = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_slab"), new BaseSlabBlock.Stone(stone));
        wall = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_wall"), new BaseWallBlock.Stone(stone));
        button = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_button"), new BaseButtonBlock.Stone(stone, BlockSetType.STONE));
        pressurePlate = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_plate"),
                new BasePressurePlateBlock.Stone(stone, BlockSetType.STONE)
        );
        pedestal = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_pedestal"), new EndPedestal.Stone(stone));
        lantern = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_lantern"), new StoneLanternBlock(stone));

        bricks = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_bricks"), new BaseBlock.Stone(material));
        brickStairs = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_bricks_stairs"), new BaseStairsBlock.Stone(bricks));
        brickSlab = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_bricks_slab"), new BaseSlabBlock.Stone(bricks));
        brickWall = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_bricks_wall"), new BaseWallBlock.Stone(bricks));
        furnace = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_furnace"), new BaseFurnaceBlock.Stone(bricks));
        flowerPot = EndBlocks.registerBlock(EndBlocks.prepareBlockPath(name + "_flower_pot"), new FlowerPotBlock.Stone(bricks));

        MaterialManager.register(this);
    }

    public static void recipesForStoneMaterial(RecipeOutput context, StoneMaterial mat) {
        RecipeBuilder
                .stonecutting(
                        BetterEnd.C.mk(mat.name + "_bricks_stonecutting"),
                        mat.bricks
                )
                .input(mat.stone)
                .group(mat.name + "_stonecutting")
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterEnd.C.mk(mat.name + "_wall_stonecutting"),
                        mat.wall
                )
                .input(mat.stone)
                .group(mat.name + "_stonecutting")
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterEnd.C.mk(mat.name + "_slab_stonecutting"),
                        mat.slab
                )
                .input(mat.stone)
                .group(mat.name + "_stonecutting")
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterEnd.C.mk(mat.name + "_stairs_stonecutting"),
                        mat.stairs
                )
                .input(mat.stone)
                .group(mat.name + "_stonecutting")
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterEnd.C.mk(mat.name + "_tiles_stonecutting"),
                        mat.tiles
                )
                .input(mat.stone)
                .group(mat.name + "_stonecutting")
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterEnd.C.mk(mat.name + "_brick_slab_stonecutting"),
                        mat.brickSlab
                )
                .outputCount(2)
                .input(mat.stone)
                .group(mat.name + "_stonecutting")
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterEnd.C.mk(mat.name + "_brick_stair_stonecutting"),
                        mat.brickStairs
                )
                .input(mat.stone)
                .group(mat.name + "_stonecutting")
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterEnd.C.mk(mat.name + "_brick_wall_stonecutting"),
                        mat.brickWall
                )
                .input(mat.stone)
                .group(mat.name + "_stonecutting")
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterEnd.C.mk(mat.name + "_pillar_stonecutting"),
                        mat.pillar
                )
                .input(mat.stone)
                .group(mat.name + "_stonecutting")
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterEnd.C.mk(mat.name + "_polished_stonecutting"),
                        mat.polished
                )
                .input(mat.stone)
                .group(mat.name + "_stonecutting")
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterEnd.C.mk(mat.name + "_brick_slabs_from_" + mat.name + "_brick_stonecutting"),
                        mat.brickSlab
                )
                .outputCount(2)
                .input(mat.bricks)
                .group(mat.name + "_stonecutting")
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterEnd.C.mk(mat.name + "_brick_stair_from_" + mat.name + "_brick_stonecutting"),
                        mat.brickStairs
                )
                .input(mat.bricks)
                .group(mat.name + "_stonecutting")
                .build(context);

        RecipeBuilder
                .stonecutting(
                        BetterEnd.C.mk(mat.name + "_brick_wall_from_" + mat.name + "_brick_stonecutting"),
                        mat.brickWall
                )
                .input(mat.bricks)
                .group(mat.name + "_stonecutting")
                .build(context);
    }

    @Override
    public void registerRecipes(RecipeOutput context) {
        RecipeBuilder.crafting(BetterEnd.C.mk(name + "_bricks"), bricks)
                     .outputCount(4)
                     .shape("##", "##")
                     .addMaterial('#', stone)
                     .group("end_bricks")
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk(name + "_polished"), polished)
                     .outputCount(4)
                     .shape("##", "##")
                     .addMaterial('#', bricks)
                     .group("end_tile")
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk(name + "_tiles"), tiles)
                     .outputCount(4)
                     .shape("##", "##")
                     .addMaterial('#', polished)
                     .group("end_small_tile")
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk(name + "_pillar"), pillar)
                     .shape("#", "#")
                     .addMaterial('#', slab)
                     .group("end_pillar")
                     .build(context);

        RecipeBuilder.crafting(BetterEnd.C.mk(name + "_stairs"), stairs)
                     .outputCount(4)
                     .shape("#  ", "## ", "###")
                     .addMaterial('#', stone)
                     .group("end_stone_stairs")
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk(name + "_slab"), slab)
                     .outputCount(6)
                     .shape("###")
                     .addMaterial('#', stone)
                     .group("end_stone_slabs")
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk(name + "_bricks_stairs"), brickStairs)
                     .outputCount(4)
                     .shape("#  ", "## ", "###")
                     .addMaterial('#', bricks)
                     .group("end_stone_stairs")
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk(name + "_bricks_slab"), brickSlab)
                     .outputCount(6)
                     .shape("###")
                     .addMaterial('#', bricks)
                     .group("end_stone_slabs")
                     .build(context);

        RecipeBuilder.crafting(BetterEnd.C.mk(name + "_wall"), wall)
                     .outputCount(6)
                     .shape("###", "###")
                     .addMaterial('#', stone)
                     .group("end_wall")
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk(name + "_bricks_wall"), brickWall)
                     .outputCount(6)
                     .shape("###", "###")
                     .addMaterial('#', bricks)
                     .group("end_wall")
                     .build(context);

        RecipeBuilder.crafting(BetterEnd.C.mk(name + "_button"), button)
                     .shapeless()
                     .addMaterial('#', stone)
                     .group("end_stone_buttons")
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk(name + "_pressure_plate"), pressurePlate)
                     .shape("##")
                     .addMaterial('#', stone)
                     .group("end_stone_plates")
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk(name + "_lantern"), lantern)
                     .shape("S", "#", "S")
                     .addMaterial('#', EndItems.CRYSTAL_SHARDS)
                     .addMaterial('S', slab, brickSlab)
                     .group("end_stone_lanterns")
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk(name + "_furnace"), furnace)
                     .shape("###", "# #", "###")
                     .addMaterial('#', stone)
                     .group("end_stone_ITEM_FURNACES")
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk(name + "_flower_pot"), flowerPot)
                     .outputCount(3)
                     .shape("# #", " # ")
                     .addMaterial('#', bricks)
                     .group("end_pots")
                     .build(context);

        EndCraftingRecipesProvider.registerPedestal(context, name + "_pedestal", pedestal, slab, pillar);
        StoneMaterial.recipesForStoneMaterial(context, this);
    }

    @Override
    public void registerBlockTags(TagBootstrapContext<Block> context) {
        context.add(BlockTags.STONE_BRICKS, bricks);
        context.add(BlockTags.WALLS, wall, brickWall);
        context.add(BlockTags.SLABS, slab, brickSlab);
        context.add(pressurePlate, BlockTags.PRESSURE_PLATES, BlockTags.STONE_PRESSURE_PLATES);
        context.add(CommonBlockTags.END_STONES, stone);
        context.add(BlockTags.DRAGON_IMMUNE, stone, stairs, slab, wall);
        context.add(CommonBlockTags.END_STONES, stone);
        context.add(CommonBlockTags.END_STONES, stone);
    }

    @Override
    public void registerItemTags(ItemTagBootstrapContext context) {
        context.add(ItemTags.SLABS, slab.asItem(), brickSlab.asItem());
        context.add(ItemTags.STONE_BRICKS, bricks.asItem());
        context.add(ItemTags.STONE_CRAFTING_MATERIALS, stone.asItem());
        context.add(ItemTags.STONE_TOOL_MATERIALS, stone.asItem());
        context.add(CommonItemTags.FURNACES, furnace.asItem());
    }
}

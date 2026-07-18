package org.betterx.betterend.complexmaterials;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ColoredMaterial implements MaterialManager.Material {
    private static final Map<Integer, ItemLike> DYES = Maps.newHashMap();
    private static final Map<Integer, String> COLORS = Maps.newHashMap();
    private static final Map<Integer, Integer> COLOR_VALUES = Maps.newHashMap();
    private final Map<Integer, Block> colors = Maps.newHashMap();

    public ColoredMaterial(Function<BlockBehaviour.Properties, Block> constructor, Block source, boolean craftEight) {
        this(resolveBaseName(source), constructor, source, COLORS, DYES, COLOR_VALUES, craftEight);
    }

    public ColoredMaterial(
            String baseName,
            Function<BlockBehaviour.Properties, Block> constructor,
            Block source,
            boolean craftEight
    ) {
        this(baseName, constructor, source, COLORS, DYES, COLOR_VALUES, craftEight);
    }

    private List<MaterialManager.MaterialRecipe> RECIPES;

    public ColoredMaterial(
            String baseName,
            Function<BlockBehaviour.Properties, Block> constructor,
            Block source,
            Map<Integer, String> colors,
            Map<Integer, ItemLike> dyes,
            boolean craftEight
    ) {
        this(baseName, constructor, source, colors, dyes, null, craftEight);
    }

    public ColoredMaterial(
            Function<BlockBehaviour.Properties, Block> constructor,
            Block source,
            Map<Integer, String> colors,
            Map<Integer, ItemLike> dyes,
            boolean craftEight
    ) {
        this(resolveBaseName(source), constructor, source, colors, dyes, null, craftEight);
    }

    public ColoredMaterial(
            String baseName,
            Function<BlockBehaviour.Properties, Block> constructor,
            Block source,
            Map<Integer, String> colors,
            Map<Integer, ItemLike> dyes,
            Map<Integer, Integer> colorValues,
            boolean craftEight
    ) {
        if (ModCore.isDatagen()) {
            RECIPES = new ArrayList<>(colors.size());
            MaterialManager.register(this);
        }
        String id = requireBaseName(baseName, source);
        colors.forEach((color, name) -> {
            String blockName = id + "_" + name;
            String preparedBlockName = EndBlocks.prepareBlockPath(blockName);
            Block block = constructor.apply(BlockBehaviour.Properties
                    .ofLegacyCopy(source)
                    .mapColor(MapColor.COLOR_BLACK));
            EndBlocks.registerBlock(preparedBlockName, block);
            if (ModCore.isDatagen()) {
                ItemLike dye = dyes.get(color);
                if (dye != null && dye != Items.AIR) {
                    RECIPES.add(context -> {
                        if (craftEight) {
                            RecipeBuilder.crafting(BetterEnd.C.mk(blockName), block)
                                         .outputCount(8)
                                         .shape("###", "#D#", "###")
                                         .addMaterial('#', source)
                                         .addMaterial('D', dye)
                                         .build(context);
                        } else {
                            RecipeBuilder.crafting(BetterEnd.C.mk(blockName), block)
                                         .shapeless()
                                         .addMaterial('#', source)
                                         .addMaterial('D', dye)
                                         .build(context);
                        }
                    });
                }
            }
            this.colors.put(color, block);
            int tintColor = colorValues != null ? colorValues.getOrDefault(color, color) : color;
            BlocksHelper.addBlockColor(block, tintColor);
        });
    }

    public Block getByColor(DyeColor color) {
        return colors.get(color.getId());
    }

    public Block getByColor(int color) {
        return colors.get(color);
    }

    static {
        for (DyeColor color : DyeColor.values()) {
            int colorId = color.getId();
            COLORS.put(colorId, color.getName());
            DYES.put(colorId, resolveDyeItem(color));
            COLOR_VALUES.put(colorId, color.getMapColor().col);
        }
    }

    private static ItemLike resolveDyeItem(DyeColor color) {
        Item dye = DyeItem.byColor(color);
        if (dye != null && dye != Items.AIR) {
            return dye;
        }
        Item depotDye = BuiltInRegistries.ITEM
                .get(Identifier.fromNamespaceAndPath(
                "dye_depot",
                color.getName() + "_dye"
        ))
                .map(Holder.Reference::value)
                .orElse(Items.AIR);
        if (depotDye != Items.AIR) {
            return depotDye;
        }
        return dye == null ? Items.AIR : dye;
    }

    @Override
    public void registerRecipes(RecipeOutput context) {
        if (RECIPES != null) {
            RECIPES.forEach(r -> r.registerRecipes(context));
        }
    }

    @Override
    public void registerBlockTags(TagBootstrapContext<Block> context) {

    }

    @Override
    public void registerItemTags(ItemTagBootstrapContext context) {

    }

    private static String resolveBaseName(Block source) {
        Identifier key = BuiltInRegistries.BLOCK.getKey(source);
        if (key == null) {
            throw new IllegalStateException("ColoredMaterial base block is not registered yet: " + source);
        }
        return key.getPath();
    }

    private static String requireBaseName(String baseName, Block source) {
        if (baseName == null || baseName.isBlank()) {
            throw new IllegalArgumentException("ColoredMaterial base name is required for " + source);
        }
        return baseName;
    }
}

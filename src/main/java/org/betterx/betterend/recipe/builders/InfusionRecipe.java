package org.betterx.betterend.recipe.builders;

import org.betterx.bclib.interfaces.UnknownReceipBookCategory;
import org.betterx.bclib.recipes.BCLBaseRecipeBuilder;
import org.betterx.bclib.recipes.BCLRecipeManager;
import org.betterx.bclib.util.ItemUtil;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.rituals.InfusionRitual;
import org.betterx.wover.enchantment.api.EnchantmentUtils;
import org.betterx.wover.item.api.ItemStackHelper;
import org.betterx.wover.recipe.api.BaseRecipeBuilder;
import org.betterx.wover.recipe.api.BaseUnlockableRecipeBuilder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;


import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InfusionRecipe implements Recipe<InfusionRitual.InfusionInput>, UnknownReceipBookCategory {
    public final static String GROUP = "infusion";
    public final static RecipeType<InfusionRecipe> TYPE = BCLRecipeManager.registerType(BetterEnd.MOD_ID, GROUP);
    public final static RecipeSerializer<InfusionRecipe> SERIALIZER = BCLRecipeManager.registerSerializer(
            BetterEnd.MOD_ID,
            GROUP,
            new RecipeSerializer<>(Serializer.CODEC, Serializer.STREAM_CODEC)
    );

    private final Ingredient[] catalysts;
    final private Ingredient input;
    final private ItemStackTemplate output;
    final private int time;
    final private String group;
    private PlacementInfo placementInfo;
    // 1.21.11 disallows empty Ingredient instances; treat this sentinel as an empty catalyst slot.
    private static final Ingredient EMPTY_INGREDIENT = Ingredient.of(Items.BARRIER);

    private InfusionRecipe(Ingredient input, ItemStack output, Ingredient[] catalysts, int time, String group) {
        this(input, ItemStackTemplate.fromNonEmptyStack(output), catalysts, time, group);
    }

    private InfusionRecipe(Ingredient input, ItemStackTemplate output, Ingredient[] catalysts, int time, String group) {
        this.input = input;
        this.output = output;
        this.catalysts = catalysts;
        this.time = time;
        this.group = group;
    }

    private ItemStack createOutputStack() {
        return ItemStackHelper.callItemStackSetupIfPossible(this.output.create());
    }

    private ItemStack createOutputStack(HolderLookup.Provider provider) {
        return ItemStackHelper.callItemStackSetupIfPossible(this.output.create(), provider);
    }

    public static Builder create(String id, ItemLike output) {
        return create(BetterEnd.C.mk(id), output);
    }

    public static Builder create(Identifier id, ItemLike output) {
        return new BuilderImpl(id, output);
    }

    public static Builder create(String id, ItemStack output) {
        return create(BetterEnd.C.mk(id), output);
    }

    public static Builder create(Identifier id, ItemStack output) {
        return new BuilderImpl(id, output);
    }

    private static Builder create(Identifier id, ItemStackTemplate output) {
        return new BuilderImpl(id, output);
    }

    public static Builder create(
            String id,
            ResourceKey<Enchantment> enchantment,
            int level,
            HolderLookup.RegistryLookup<Enchantment> lookup
    ) {
        return create(BetterEnd.C.mk(id), enchantment, level, lookup);
    }

    public static Builder create(
            Identifier id,
            ResourceKey<Enchantment> enchantment,
            int level,
            HolderLookup.RegistryLookup<Enchantment> lookup
    ) {
        return create(id, createEnchantedBookTemplate(enchantment, level, lookup));
    }

    public static ItemStack createEnchantedBook(
            ResourceKey<Enchantment> enchantment,
            int level,
            HolderLookup.RegistryLookup<Enchantment> lookup
    ) {
        return createEnchantedBookTemplate(enchantment, level, lookup).create();
    }

    private static ItemStackTemplate createEnchantedBookTemplate(
            ResourceKey<Enchantment> enchantment,
            int level,
            HolderLookup.RegistryLookup<Enchantment> lookup
    ) {
        final Holder<Enchantment> holder = EnchantmentUtils.getEnchantment(lookup, enchantment);
        if (holder != null) {
            final ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
            enchantments.set(holder, level);
            return new ItemStackTemplate(
                    Items.ENCHANTED_BOOK,
                    DataComponentPatch.builder()
                                      .set(DataComponents.STORED_ENCHANTMENTS, enchantments.toImmutable())
                                      .build()
            );
        }
        return new ItemStackTemplate(Items.ENCHANTED_BOOK);
    }

    public int getInfusionTime() {
        return this.time;
    }

    @Override
    public boolean matches(InfusionRitual.InfusionInput inv, Level world) {
        boolean valid = this.input.test(inv.getItem(0));
        if (!valid) return false;
        for (int i = 0; i < 8; i++) {
            valid &= testCatalyst(this.catalysts[i], inv.getItem(i + 1));
        }
        return valid;
    }

    private static boolean testCatalyst(Ingredient ingredient, ItemStack stack) {
        if (EMPTY_INGREDIENT.equals(ingredient)) {
            return stack.isEmpty();
        }
        return ingredient.test(stack);
    }

    @Override
    public @NotNull ItemStack assemble(InfusionRitual.InfusionInput recipeInput) {
        return createOutputStack();
    }

    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> defaultedList = NonNullList.create();
        defaultedList.add(input);
        defaultedList.addAll(Arrays.asList(catalysts));
        return defaultedList;
    }

    public @NotNull ItemStack getResultItem(HolderLookup.Provider acc) {
        return this.createOutputStack(acc);
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(this.getIngredients());
        }
        return this.placementInfo;
    }

    @Override
    public @NotNull String group() {
        return this.group;
    }

    @Override
    public @NotNull RecipeSerializer<InfusionRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<InfusionRecipe> getType() {
        return TYPE;
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    public interface Builder extends BaseRecipeBuilder<Builder>, BaseUnlockableRecipeBuilder<Builder> {
        Builder group(@Nullable String group);

        Builder setPrimaryInput(ItemLike... inputs);
        Builder setPrimaryInput(TagKey<Item> input);
        Builder setPrimaryInputAndUnlock(TagKey<Item> input);
        Builder setPrimaryInputAndUnlock(ItemLike... inputs);

        Builder setTime(int time);
        Builder addCatalyst(CatalystSlot slot, ItemLike... items);
        Builder addCatalyst(CatalystSlot slot, ItemStack stack);
        Builder addCatalyst(CatalystSlot slot, TagKey<Item> tag);
    }

    public static class BuilderImpl extends BCLBaseRecipeBuilder<Builder, InfusionRecipe> implements Builder {
        private final Ingredient[] catalysts;
        private int time;

        protected BuilderImpl(Identifier id, ItemLike output) {
            super(id, output, false);
            this.catalysts = emptyCatalysts();
            this.time = 1;
        }

        protected BuilderImpl(Identifier id, ItemStack output) {
            super(id, output, false);
            this.catalysts = emptyCatalysts();
            this.time = 1;
        }

        protected BuilderImpl(Identifier id, ItemStackTemplate output) {
            super(id, output, false);
            this.catalysts = emptyCatalysts();
            this.time = 1;
        }

        private static Ingredient[] emptyCatalysts() {
            return new Ingredient[]{
                    EMPTY_INGREDIENT, EMPTY_INGREDIENT, EMPTY_INGREDIENT, EMPTY_INGREDIENT,
                    EMPTY_INGREDIENT, EMPTY_INGREDIENT, EMPTY_INGREDIENT, EMPTY_INGREDIENT
            };
        }


        public Builder setTime(int time) {
            this.time = time;
            return this;
        }

        public Builder addCatalyst(CatalystSlot slot, ItemLike... items) {
            this.catalysts[slot.index] = Ingredient.of(items);
            return this;
        }

        public Builder addCatalyst(CatalystSlot slot, ItemStack stack) {
            this.catalysts[slot.index] = stack.isEmpty() ? EMPTY_INGREDIENT : Ingredient.of(stack.getItem());
            return this;
        }

        public Builder addCatalyst(CatalystSlot slot, TagKey<Item> tag) {
            this.catalysts[slot.index] = Ingredient.of(BuiltInRegistries.ITEM.getOrThrow(tag));
            return this;
        }

        @Override
        protected void validate() {
            super.validate();
            if (time < 0) {
                throwIllegalStateException(
                        "Time should be positive, recipe {} will be ignored!"
                );
            }
        }

        @Override
        protected InfusionRecipe createRecipe(Identifier id) {
            return new InfusionRecipe(
                    this.primaryInput,
                    this.outputTemplate(),
                    this.catalysts,
                    this.time,
                    this.group
            );
        }
    }


    public enum CatalystSlot implements StringRepresentable {
        NORTH(0, "north"),
        NORTH_EAST(1, "north_east"),
        EAST(2, "east"),
        SOUTH_EAST(3, "south_east"),
        SOUTH(4, "south"),
        SOUTH_WEST(5, "south_west"),
        WEST(6, "west"),
        NORTH_WEST(7, "north_west");

        public static final Codec<CatalystSlot> CODEC = StringRepresentable.fromEnum(CatalystSlot::values);

        public final int index;
        public final String key;

        CatalystSlot(int index, String key) {
            this.index = index;
            this.key = key;
        }

        @Override
        public @NotNull String getSerializedName() {
            return key;
        }
    }

    public static class Serializer {
        public static @NotNull InfusionRecipe fromNetwork(RegistryFriendlyByteBuf packetBuffer) {
            final Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(packetBuffer);
            final ItemStackTemplate output = ItemStackTemplate.STREAM_CODEC.decode(packetBuffer);
            final String group = packetBuffer.readUtf();
            final int time = packetBuffer.readVarInt();
            final Ingredient[] catalysts = new Ingredient[8];
            for (int i = 0; i < 8; i++) {
                catalysts[i] = Ingredient.CONTENTS_STREAM_CODEC.decode(packetBuffer);
            }
            return new InfusionRecipe(input, output, catalysts, time, group);
        }

        public static void toNetwork(RegistryFriendlyByteBuf packetBuffer, InfusionRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(packetBuffer, recipe.input);
            ItemStackTemplate.STREAM_CODEC.encode(packetBuffer, recipe.output);
            packetBuffer.writeUtf(recipe.group);
            packetBuffer.writeVarInt(recipe.time);
            for (int i = 0; i < 8; i++) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(packetBuffer, recipe.catalysts[i]);
            }
        }

        public static final MapCodec<Ingredient[]> CODEC_CATALYSTS = RecordCodecBuilder.mapCodec(instance -> instance
                .group(
                        ItemUtil.CODEC_INGREDIENT_WITH_NBT
                                .lenientOptionalFieldOf(CatalystSlot.NORTH.key, EMPTY_INGREDIENT)
                                .forGetter(catalysts -> catalysts[CatalystSlot.NORTH.index]),
                        ItemUtil.CODEC_INGREDIENT_WITH_NBT
                                .lenientOptionalFieldOf(CatalystSlot.NORTH_EAST.key, EMPTY_INGREDIENT)
                                .forGetter(catalysts -> catalysts[CatalystSlot.NORTH_EAST.index]),
                        ItemUtil.CODEC_INGREDIENT_WITH_NBT
                                .lenientOptionalFieldOf(CatalystSlot.EAST.key, EMPTY_INGREDIENT)
                                .forGetter(catalysts -> catalysts[CatalystSlot.EAST.index]),
                        ItemUtil.CODEC_INGREDIENT_WITH_NBT
                                .lenientOptionalFieldOf(CatalystSlot.SOUTH_EAST.key, EMPTY_INGREDIENT)
                                .forGetter(catalysts -> catalysts[CatalystSlot.SOUTH_EAST.index]),
                        ItemUtil.CODEC_INGREDIENT_WITH_NBT
                                .lenientOptionalFieldOf(CatalystSlot.SOUTH.key, EMPTY_INGREDIENT)
                                .forGetter(catalysts -> catalysts[CatalystSlot.SOUTH.index]),
                        ItemUtil.CODEC_INGREDIENT_WITH_NBT
                                .lenientOptionalFieldOf(CatalystSlot.SOUTH_WEST.key, EMPTY_INGREDIENT)
                                .forGetter(catalysts -> catalysts[CatalystSlot.SOUTH_WEST.index]),
                        ItemUtil.CODEC_INGREDIENT_WITH_NBT
                                .lenientOptionalFieldOf(CatalystSlot.WEST.key, EMPTY_INGREDIENT)
                                .forGetter(catalysts -> catalysts[CatalystSlot.WEST.index]),
                        ItemUtil.CODEC_INGREDIENT_WITH_NBT
                                .lenientOptionalFieldOf(CatalystSlot.NORTH_WEST.key, EMPTY_INGREDIENT)
                                .forGetter(catalysts -> catalysts[CatalystSlot.NORTH_WEST.index])
                )
                .apply(instance, (n, ne, e, se, s, sw, w, nw) -> new Ingredient[]{n, ne, e, se, s, sw, w, nw}));

        public static final MapCodec<InfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemUtil.CODEC_INGREDIENT_WITH_NBT.fieldOf("input").forGetter(recipe -> recipe.input),
                ItemUtil.CODEC_ITEM_STACK_TEMPLATE_WITH_NBT.fieldOf("result").forGetter(recipe -> recipe.output),
                CODEC_CATALYSTS.fieldOf("catalysts").forGetter(recipe -> recipe.catalysts),
                Codec.INT.optionalFieldOf("time", 1).forGetter(recipe -> recipe.time),
                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group)
        ).apply(instance, InfusionRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, InfusionRecipe> STREAM_CODEC = StreamCodec.of(InfusionRecipe.Serializer::toNetwork, InfusionRecipe.Serializer::fromNetwork);

    }

    public static void register() {
        //we call this to make sure that TYPE is initialized
    }
}

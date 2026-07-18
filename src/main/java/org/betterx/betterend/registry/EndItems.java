package org.betterx.betterend.registry;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.items.BaseArmorItem;
import org.betterx.bclib.items.BaseDiscItem;
import org.betterx.bclib.items.BaseSpawnEggItem;
import org.betterx.bclib.items.ModelProviderItem;
import org.betterx.bclib.items.tool.BaseAxeItem;
import org.betterx.bclib.items.tool.BaseHoeItem;
import org.betterx.bclib.items.tool.BaseShovelItem;
import org.betterx.bclib.items.tool.BaseSwordItem;
import org.betterx.bclib.models.RecordItemModelProvider;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.item.*;
import org.betterx.betterend.item.material.EndArmorMaterial;
import org.betterx.betterend.item.material.EndArmorTier;
import org.betterx.betterend.item.material.EndToolMaterial;
import org.betterx.betterend.item.tool.EndHammerItem;
import org.betterx.betterend.item.tool.EndPickaxe;
import org.betterx.betterend.util.DebugHelpers;
import org.betterx.wover.complex.api.equipment.ArmorSlot;
import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class EndItems {
    private static ItemRegistry ITEMS_REGISTRY;

    // Materials //
    public final static Item ENDER_DUST = registerEndItem("ender_dust");
    public final static Item ENDER_SHARD = registerEndItem("ender_shard");
    public final static Item AETERNIUM_INGOT = registerEndItem(
            EndItems.prepareItemPath("aeternium_ingot"),
            new ModelProviderItem(makeEndItemSettings().fireResistant())
    );
    public final static Item AETERNIUM_FORGED_PLATE = registerEndItem(EndItems.prepareItemPath("aeternium_forged_plate"),
            new ModelProviderItem(makeEndItemSettings().fireResistant())
    );
    public final static Item END_LILY_LEAF = registerEndItem("end_lily_leaf");
    public final static Item END_LILY_LEAF_DRIED = registerEndItem("end_lily_leaf_dried");
    public final static Item CRYSTAL_SHARDS = registerEndItem("crystal_shards");
    public final static Item RAW_AMBER = registerEndItem("raw_amber");
    public final static Item AMBER_GEM = registerEndItem("amber_gem");
    public final static Item GLOWING_BULB = registerEndItem("glowing_bulb");
    public final static Item CRYSTALLINE_SULPHUR = registerEndItem("crystalline_sulphur");
    public final static Item HYDRALUX_PETAL = registerEndItem("hydralux_petal");
    public final static Item GELATINE = registerEndItem("gelatine");
    public static final Item ETERNAL_CRYSTAL = registerEndItem(
            EndItems.prepareItemPath("eternal_crystal"),
            new EternalCrystalItem()
    );
    public final static Item ENCHANTED_PETAL = registerEndItem(EndItems.prepareItemPath("enchanted_petal"), new EnchantedItem(HYDRALUX_PETAL));
    public final static Item LEATHER_STRIPE = registerEndItem("leather_stripe");
    public final static Item LEATHER_WRAPPED_STICK = registerEndItem("leather_wrapped_stick");
    public final static Item SILK_FIBER = registerEndItem("silk_fiber");
    public final static Item LUMECORN_ROD = registerEndItem("lumecorn_rod");
    public final static Item SILK_MOTH_MATRIX = registerEndItem("silk_moth_matrix");
    public final static Item ENCHANTED_MEMBRANE = registerEndItem(
            EndItems.prepareItemPath("enchanted_membrane"),
            new EnchantedItem(Items.PHANTOM_MEMBRANE)
    );
    public static final Item GUIDE_BOOK = BetterEnd.ENABLE_GUIDEBOOK
            ? registerEndItem(EndItems.prepareItemPath("guidebook"), new GuideBookItem())
            : Items.AIR;

    // Music Discs
    public final static Item MUSIC_DISC_STRANGE_AND_ALIEN = registerEndDisc(
            "music_disc_strange_and_alien",
            EndSounds.RECORD_STRANGE_AND_ALIEN
    );
    public final static Item MUSIC_DISC_GRASPING_AT_STARS = registerEndDisc(
            "music_disc_grasping_at_stars",
            EndSounds.RECORD_GRASPING_AT_STARS
    );
    public final static Item MUSIC_DISC_ENDSEEKER = registerEndDisc(
            "music_disc_endseeker",
            EndSounds.RECORD_ENDSEEKER
    );
    public final static Item MUSIC_DISC_EO_DRACONA = registerEndDisc(
            "music_disc_eo_dracona",
            EndSounds.RECORD_EO_DRACONA
    );

    // Armor //
    public static final Item AETERNIUM_HELMET = registerEndItem(EndItems.prepareItemPath("aeternium_helmet"),
            new BaseArmorItem(
                    EndArmorMaterial.AETERNIUM,
                    ArmorType.HELMET,
                    EndArmorItem.createDefaultEndArmorSettings(ArmorSlot.HELMET_SLOT, EndArmorTier.AETERNIUM)
                                .fireResistant()
            )
    );
    public static final Item AETERNIUM_CHESTPLATE = registerEndItem(EndItems.prepareItemPath("aeternium_chestplate"),
            new BaseArmorItem(
                    EndArmorMaterial.AETERNIUM,
                    ArmorType.CHESTPLATE,
                    EndArmorItem.createDefaultEndArmorSettings(ArmorSlot.CHESTPLATE_SLOT, EndArmorTier.AETERNIUM)
                                .fireResistant()
            )
    );
    public static final Item AETERNIUM_LEGGINGS = registerEndItem(EndItems.prepareItemPath("aeternium_leggings"),
            new BaseArmorItem(
                    EndArmorMaterial.AETERNIUM,
                    ArmorType.LEGGINGS,
                    EndArmorItem.createDefaultEndArmorSettings(ArmorSlot.LEGGINGS_SLOT, EndArmorTier.AETERNIUM)
                                .fireResistant()
            )
    );
    public static final Item AETERNIUM_BOOTS = registerEndItem(EndItems.prepareItemPath("aeternium_boots"),
            new BaseArmorItem(
                    EndArmorMaterial.AETERNIUM,
                    ArmorType.BOOTS,
                    EndArmorItem.createDefaultEndArmorSettings(ArmorSlot.BOOTS_SLOT, EndArmorTier.AETERNIUM)
                                .fireResistant()
            )
    );
    public static final Item CRYSTALITE_HELMET = registerEndItem(EndItems.prepareItemPath("crystalite_helmet"), new CrystaliteHelmet());
    public static final Item CRYSTALITE_CHESTPLATE = registerEndItem(EndItems.prepareItemPath("crystalite_chestplate"),
            new CrystaliteChestplate()
    );
    public static final Item CRYSTALITE_LEGGINGS = registerEndItem(EndItems.prepareItemPath("crystalite_leggings"), new CrystaliteLeggings());
    public static final Item CRYSTALITE_BOOTS = registerEndItem(EndItems.prepareItemPath("crystalite_boots"), new CrystaliteBoots());
    public static final Item ARMORED_ELYTRA = registerEndItem(EndItems.prepareItemPath("elytra_armored"),
            new ArmoredElytra(
                    "elytra_armored",
                    EndArmorTier.AETERNIUM,
                    Items.PHANTOM_MEMBRANE,
                    900,
                    0.97D,
                    1.15f,
                    1.15f,
                    true
            )
    );
    public static final Item CRYSTALITE_ELYTRA = registerEndItem(EndItems.prepareItemPath("elytra_crystalite"), new CrystaliteElytra(650, 1.0D));

    // Tools //
    public static final Item AETERNIUM_SHOVEL = registerEndTool(EndItems.prepareItemPath("aeternium_shovel"), new BaseShovelItem(
            EndToolMaterial.AETERNIUM.toolMaterial(), 1.5F, -3.0F, makeEndItemSettings().fireResistant()));
    public static final Item AETERNIUM_SWORD = registerEndTool(EndItems.prepareItemPath("aeternium_sword"),
            new BaseSwordItem(
                    EndToolMaterial.AETERNIUM.toolMaterial(),
                    3,
                    -2.4F,
                    makeEndItemSettings().fireResistant()
            )
    );
    public static final Item AETERNIUM_PICKAXE = registerEndTool(EndItems.prepareItemPath("aeternium_pickaxe"),
            new EndPickaxe(
                    EndToolMaterial.AETERNIUM.toolMaterial(),
                    1,
                    -2.8F,
                    makeEndItemSettings().fireResistant()
            )
    );
    public static final Item AETERNIUM_AXE = registerEndTool(EndItems.prepareItemPath("aeternium_axe"),
            new BaseAxeItem(
                    EndToolMaterial.AETERNIUM.toolMaterial(),
                    5.0F,
                    -3.0F,
                    makeEndItemSettings().fireResistant()
            )
    );
    public static final Item AETERNIUM_HOE = registerEndTool(EndItems.prepareItemPath("aeternium_hoe"),
            new BaseHoeItem(
                    EndToolMaterial.AETERNIUM.toolMaterial(),
                    -3,
                    0.0F,
                    makeEndItemSettings().fireResistant()
            )
    );
    public static final Item AETERNIUM_HAMMER = registerEndTool(EndItems.prepareItemPath("aeternium_hammer"),
            new EndHammerItem(
                    EndToolMaterial.AETERNIUM.toolMaterial(),
                    6.0F,
                    -3.0F,
                    0.3f,
                    makeEndItemSettings().fireResistant()
            )
    );

    // Toolparts //
    public final static Item AETERNIUM_SHOVEL_HEAD = registerEndItem(EndItems.prepareItemPath("aeternium_shovel_head"),
            new ModelProviderItem(makeEndItemSettings().fireResistant())
    );
    public final static Item AETERNIUM_PICKAXE_HEAD = registerEndItem(EndItems.prepareItemPath("aeternium_pickaxe_head"),
            new ModelProviderItem(makeEndItemSettings().fireResistant())
    );
    public final static Item AETERNIUM_AXE_HEAD = registerEndItem(EndItems.prepareItemPath("aeternium_axe_head"),
            new ModelProviderItem(makeEndItemSettings().fireResistant())
    );
    public final static Item AETERNIUM_HOE_HEAD = registerEndItem(EndItems.prepareItemPath("aeternium_hoe_head"),
            new ModelProviderItem(makeEndItemSettings().fireResistant())
    );
    public final static Item AETERNIUM_HAMMER_HEAD = registerEndItem(EndItems.prepareItemPath("aeternium_hammer_head"),
            new ModelProviderItem(makeEndItemSettings().fireResistant())
    );
    public final static Item AETERNIUM_SWORD_BLADE = registerEndItem(EndItems.prepareItemPath("aeternium_sword_blade"),
            new ModelProviderItem(makeEndItemSettings().fireResistant())
    );
    public final static Item AETERNIUM_SWORD_HANDLE = registerEndItem(EndItems.prepareItemPath("aeternium_sword_handle"),
            new ModelProviderItem(makeEndItemSettings().fireResistant())
    );

    // ITEM_HAMMERS //
    public static final Item IRON_HAMMER = registerEndTool(EndItems.prepareItemPath("iron_hammer"),
            new EndHammerItem(
                    ToolMaterial.IRON,
                    5.0F,
                    -3.2F,
                    0.2f,
                    makeEndItemSettings()
            )
    );
    public static final Item GOLDEN_HAMMER = registerEndTool(EndItems.prepareItemPath("golden_hammer"),
            new EndHammerItem(
                    ToolMaterial.GOLD,
                    4.5F,
                    -3.4F,
                    0.3f,
                    makeEndItemSettings()
            )
    );
    public static final Item DIAMOND_HAMMER = registerEndTool(EndItems.prepareItemPath("diamond_hammer"),
            new EndHammerItem(
                    ToolMaterial.DIAMOND,
                    5.5F,
                    -3.1F,
                    0.2f,
                    makeEndItemSettings()
            )
    );
    public static final Item NETHERITE_HAMMER = registerEndTool(EndItems.prepareItemPath("netherite_hammer"),
            new EndHammerItem(
                    ToolMaterial.NETHERITE,
                    5.0F,
                    -3.0F,
                    0.2f,
                    makeEndItemSettings().fireResistant()
            )
    );

    // Food //
    public final static Item SHADOW_BERRY_RAW = registerEndFood("shadow_berry_raw", 4, 0.5F);
    public final static Item SHADOW_BERRY_COOKED = registerEndFood("shadow_berry_cooked", 6, 0.7F);
    public final static Item END_FISH_RAW = registerEndFood("end_fish_raw", Foods.SALMON);
    public final static Item END_FISH_COOKED = registerEndFood("end_fish_cooked", Foods.COOKED_SALMON);
    public final static Item BUCKET_END_FISH = registerEndItem(EndItems.prepareItemPath("bucket_end_fish"),
            new EndBucketItem(EndEntities.END_FISH.type())
    );
    public final static Item BUCKET_CUBOZOA = registerEndItem(EndItems.prepareItemPath("bucket_cubozoa"),
            new EndBucketItem(EndEntities.CUBOZOA.type())
    );
    public final static Item SWEET_BERRY_JELLY = registerEndFood("sweet_berry_jelly", 8, 0.7F);
    public final static Item SHADOW_BERRY_JELLY = registerEndFood(
            "shadow_berry_jelly",
            6,
            0.8F,
            new MobEffectInstance(MobEffects.NIGHT_VISION, 400)
    );
    public final static Item BLOSSOM_BERRY_JELLY = registerEndFood("blossom_berry_jelly", 8, 0.7F);
    public final static Item BLOSSOM_BERRY = registerEndFood("blossom_berry", Foods.APPLE);
    public final static Item AMBER_ROOT_RAW = registerEndFood("amber_root_raw", 2, 0.8F);
    public final static Item CHORUS_MUSHROOM_RAW = registerEndFood("chorus_mushroom_raw", 3, 0.5F);
    public final static Item CHORUS_MUSHROOM_COOKED = registerEndFood("chorus_mushroom_cooked", Foods.MUSHROOM_STEW);
    public final static Item BOLUX_MUSHROOM_COOKED = registerEndFood("bolux_mushroom_cooked", Foods.MUSHROOM_STEW);
    public final static Item CAVE_PUMPKIN_PIE = registerEndFood("cave_pumpkin_pie", Foods.PUMPKIN_PIE);

    // Drinks //
    public final static Item UMBRELLA_CLUSTER_JUICE = registerEndDrink("umbrella_cluster_juice", 5, 0.7F);

    public static List<Item> getModItems() {
        return getItemRegistry().allItems().toList();
    }

    public static Item registerEndDisc(String name, ResourceKey<JukeboxSong> sound) {
        final String path = prepareItemPath(name);
        try {
            Item item = BaseDiscItem.create(sound, BehaviourBuilders.createDisc());
            RecordItemModelProvider.add(item);
            getItemRegistry().register(path, item, CommonItemTags.MUSIC_DISCS);
            return item;
        } finally {
            getItemRegistry().finishConstructionPath();
        }
    }

    public static Item registerEndItem(String name) {
        return registerEndItem(prepareItemPath(name), new ModelProviderItem(makeEndItemSettings()));
    }

    public static Item registerEndItem(String name, Item item) {
        try {
            return getItemRegistry().register(name, item);
        } finally {
            getItemRegistry().finishConstructionPath();
        }
    }

    public static <T extends Item> T registerEndTool(String name, T item) {
        try {
            return getItemRegistry().registerAsTool(name, item);
        } finally {
            getItemRegistry().finishConstructionPath();
        }
    }

    public static Item registerEndEgg(String name, EntityType<? extends Mob> type, int background, int dots) {
        final String path = prepareItemPath(name);
        try {
            return getItemRegistry().registerEgg(path, new BaseSpawnEggItem(type, background, dots, makeEndItemSettings()));
        } finally {
            getItemRegistry().finishConstructionPath();
        }
    }

    public static Item registerEndFood(String name, int hunger, float saturation, MobEffectInstance... effects) {
        final String path = prepareItemPath(name);
        try {
            return getItemRegistry().registerFood(path, ModelProviderItem::new, hunger, saturation, effects);
        } finally {
            getItemRegistry().finishConstructionPath();
        }
    }

    public static Item registerEndFood(String name, FoodProperties foodComponent) {
        final String path = prepareItemPath(name);
        try {
            return getItemRegistry().register(path, new ModelProviderItem(getItemRegistry()
                    .createDefaultItemSettings()
                    .food(foodComponent)));
        } finally {
            getItemRegistry().finishConstructionPath();
        }
    }

    public static Item registerEndDrink(String name, int hunger, float saturation) {
        final String path = prepareItemPath(name);
        try {
            return getItemRegistry().registerDrink(path, ModelProviderItem::new, hunger, saturation);
        } finally {
            getItemRegistry().finishConstructionPath();
        }
    }

    public static Item.Properties makeEndItemSettings() {
        return new Item.Properties();
    }

    public static String prepareItemPath(String name) {
        return getItemRegistry().prepareConstructionPath(name);
    }

    @NotNull
    public static ItemRegistry getItemRegistry() {
        if (ITEMS_REGISTRY == null) {
            ITEMS_REGISTRY = ItemRegistry.forMod(BetterEnd.C);
        }
        return ITEMS_REGISTRY;
    }

    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        if (BCLib.isDevEnvironment()) {
            DebugHelpers.generateDebugItems();
        }
    }

    public static Item.Properties defaultSettings() {
        return new Item.Properties();
    }
}

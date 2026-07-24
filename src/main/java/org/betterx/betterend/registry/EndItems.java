package org.betterx.betterend.registry;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.api.v2.ComposterAPI;
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
import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.complex.api.equipment.ArmorSlot;
import org.betterx.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.resources.ResourceKey;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;

import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class EndItems {
    private static ItemRegistry ITEMS_REGISTRY;

    // Materials //
    public final static Item ENDER_DUST = registerEndItem("ender_dust");
    public final static Item ENDER_SHARD = registerEndItem("ender_shard");
    public final static Item AETERNIUM_INGOT = registerEndItem(
            "aeternium_ingot",
            new ModelProviderItem(makeEndItemSettings().fireResistant())
    );
    public final static Item AETERNIUM_FORGED_PLATE = registerEndItem(
            "aeternium_forged_plate",
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
    public static final Item ETERNAL_CRYSTAL = registerEndItem("eternal_crystal", new EternalCrystalItem());
    public final static Item ENCHANTED_PETAL = registerEndItem("enchanted_petal", new EnchantedItem(HYDRALUX_PETAL));
    public final static Item LEATHER_STRIPE = registerEndItem("leather_stripe");
    public final static Item LEATHER_WRAPPED_STICK = registerEndItem("leather_wrapped_stick");
    public final static Item SILK_FIBER = registerEndItem("silk_fiber");
    public final static Item LUMECORN_ROD = registerEndItem("lumecorn_rod");
    public final static Item SILK_MOTH_MATRIX = registerEndItem("silk_moth_matrix");
    public final static Item ENCHANTED_MEMBRANE = registerEndItem(
            "enchanted_membrane",
            new EnchantedItem(Items.PHANTOM_MEMBRANE)
    );

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
    public static final Item AETERNIUM_HELMET = registerEndItem(
            "aeternium_helmet",
            new BaseArmorItem(
                    EndArmorMaterial.AETERNIUM,
                    ArmorItem.Type.HELMET,
                    EndArmorItem.createDefaultEndArmorSettings(ArmorSlot.HELMET_SLOT, EndArmorTier.AETERNIUM).fireResistant()
            )
    );
    public static final Item AETERNIUM_CHESTPLATE = registerEndItem(
            "aeternium_chestplate",
            new BaseArmorItem(
                    EndArmorMaterial.AETERNIUM,
                    ArmorItem.Type.CHESTPLATE,
                    EndArmorItem.createDefaultEndArmorSettings(ArmorSlot.CHESTPLATE_SLOT, EndArmorTier.AETERNIUM).fireResistant()
            )
    );
    public static final Item AETERNIUM_LEGGINGS = registerEndItem(
            "aeternium_leggings",
            new BaseArmorItem(
                    EndArmorMaterial.AETERNIUM,
                    ArmorItem.Type.LEGGINGS,
                    EndArmorItem.createDefaultEndArmorSettings(ArmorSlot.LEGGINGS_SLOT, EndArmorTier.AETERNIUM).fireResistant()
            )
    );
    public static final Item AETERNIUM_BOOTS = registerEndItem(
            "aeternium_boots",
            new BaseArmorItem(
                    EndArmorMaterial.AETERNIUM,
                    ArmorItem.Type.BOOTS,
                    EndArmorItem.createDefaultEndArmorSettings(ArmorSlot.BOOTS_SLOT, EndArmorTier.AETERNIUM).fireResistant()
            )
    );
    public static final Item CRYSTALITE_HELMET = registerEndItem("crystalite_helmet", new CrystaliteHelmet());
    public static final Item CRYSTALITE_CHESTPLATE = registerEndItem(
            "crystalite_chestplate",
            new CrystaliteChestplate()
    );
    public static final Item CRYSTALITE_LEGGINGS = registerEndItem("crystalite_leggings", new CrystaliteLeggings());
    public static final Item CRYSTALITE_BOOTS = registerEndItem("crystalite_boots", new CrystaliteBoots());
    public static final Item ARMORED_ELYTRA = registerEndItem(
            "elytra_armored",
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
    public static final Item CRYSTALITE_ELYTRA = registerEndItem("elytra_crystalite", new CrystaliteElytra(650, 1.0D));

    // Tools //
    public static final TieredItem AETERNIUM_SHOVEL = registerEndTool("aeternium_shovel", new BaseShovelItem(
            EndToolMaterial.AETERNIUM, 1.5F, -3.0F, makeEndItemSettings().fireResistant()));
    public static final TieredItem AETERNIUM_SWORD = registerEndTool(
            "aeternium_sword",
            new BaseSwordItem(
                    EndToolMaterial.AETERNIUM,
                    3,
                    -2.4F,
                    makeEndItemSettings().fireResistant()
            )
    );
    public static final TieredItem AETERNIUM_PICKAXE = registerEndTool(
            "aeternium_pickaxe",
            new EndPickaxe(
                    EndToolMaterial.AETERNIUM,
                    1,
                    -2.8F,
                    makeEndItemSettings().fireResistant()
            )
    );
    public static final TieredItem AETERNIUM_AXE = registerEndTool(
            "aeternium_axe",
            new BaseAxeItem(
                    EndToolMaterial.AETERNIUM,
                    5.0F,
                    -3.0F,
                    makeEndItemSettings().fireResistant()
            )
    );
    public static final TieredItem AETERNIUM_HOE = registerEndTool(
            "aeternium_hoe",
            new BaseHoeItem(
                    EndToolMaterial.AETERNIUM,
                    -3,
                    0.0F,
                    makeEndItemSettings().fireResistant()
            )
    );
    public static final TieredItem AETERNIUM_HAMMER = registerEndTool(
            "aeternium_hammer",
            new EndHammerItem(
                    EndToolMaterial.AETERNIUM,
                    6.0F,
                    -3.0F,
                    0.3f,
                    makeEndItemSettings().fireResistant()
            )
    );

    // Toolparts //
    public final static Item AETERNIUM_SHOVEL_HEAD = registerEndItem(
            "aeternium_shovel_head",
            new ModelProviderItem(makeEndItemSettings().fireResistant())
    );
    public final static Item AETERNIUM_PICKAXE_HEAD = registerEndItem(
            "aeternium_pickaxe_head",
            new ModelProviderItem(makeEndItemSettings().fireResistant())
    );
    public final static Item AETERNIUM_AXE_HEAD = registerEndItem(
            "aeternium_axe_head",
            new ModelProviderItem(makeEndItemSettings().fireResistant())
    );
    public final static Item AETERNIUM_HOE_HEAD = registerEndItem(
            "aeternium_hoe_head",
            new ModelProviderItem(makeEndItemSettings().fireResistant())
    );
    public final static Item AETERNIUM_HAMMER_HEAD = registerEndItem(
            "aeternium_hammer_head",
            new ModelProviderItem(makeEndItemSettings().fireResistant())
    );
    public final static Item AETERNIUM_SWORD_BLADE = registerEndItem(
            "aeternium_sword_blade",
            new ModelProviderItem(makeEndItemSettings().fireResistant())
    );
    public final static Item AETERNIUM_SWORD_HANDLE = registerEndItem(
            "aeternium_sword_handle",
            new ModelProviderItem(makeEndItemSettings().fireResistant())
    );

    // ITEM_HAMMERS //
    public static final TieredItem IRON_HAMMER = registerEndTool(
            "iron_hammer",
            new EndHammerItem(
                    Tiers.IRON,
                    5.0F,
                    -3.2F,
                    0.2f,
                    makeEndItemSettings()
            )
    );
    public static final TieredItem GOLDEN_HAMMER = registerEndTool(
            "golden_hammer",
            new EndHammerItem(
                    Tiers.GOLD,
                    4.5F,
                    -3.4F,
                    0.3f,
                    makeEndItemSettings()
            )
    );
    public static final TieredItem DIAMOND_HAMMER = registerEndTool(
            "diamond_hammer",
            new EndHammerItem(
                    Tiers.DIAMOND,
                    5.5F,
                    -3.1F,
                    0.2f,
                    makeEndItemSettings()
            )
    );
    public static final TieredItem NETHERITE_HAMMER = registerEndTool(
            "netherite_hammer",
            new EndHammerItem(
                    Tiers.NETHERITE,
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
    public final static Item BUCKET_END_FISH = registerEndItem(
            "bucket_end_fish",
            new EndBucketItem(EndEntities.END_FISH.type())
    );
    public final static Item BUCKET_CUBOZOA = registerEndItem(
            "bucket_cubozoa",
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

    public static void registerCompostableFoods() {
        getModItems().forEach(item -> {
            FoodProperties food = item.components().get(DataComponents.FOOD);
            if (food != null) {
                ComposterAPI.allowCompost(food.nutrition() * food.saturation() * 0.18F, item);
            }
        });
    }

    public static Item registerEndDisc(String name, ResourceKey<JukeboxSong> sound) {
        Item item = BaseDiscItem.create(sound, BehaviourBuilders.createDisc());
        RecordItemModelProvider.add(item);
        getItemRegistry().register(name, item, CommonItemTags.MUSIC_DISCS);
        return item;
    }

    public static Item registerEndItem(String name) {
        return getItemRegistry().register(name, new ModelProviderItem(makeEndItemSettings()));
    }

    public static Item registerEndItem(String name, Item item) {
        return getItemRegistry().register(name, item);
    }

    public static TieredItem registerEndTool(String name, TieredItem item) {
        return getItemRegistry().registerAsTool(name, item);
    }

    public static Item registerEndEgg(String name, EntityType<? extends Mob> type, int background, int dots) {
        return getItemRegistry().registerEgg(name, new BaseSpawnEggItem(type, background, dots, makeEndItemSettings()));
    }

    public static Item registerEndFood(String name, int hunger, float saturation, MobEffectInstance... effects) {
        return getItemRegistry().registerFood(name, ModelProviderItem::new, hunger, saturation, effects);
    }

    public static Item registerEndFood(String name, FoodProperties foodComponent) {
        return getItemRegistry().register(name, new ModelProviderItem(getItemRegistry()
                .createDefaultItemSettings()
                .food(foodComponent)));
    }

    public static Item registerEndDrink(String name, int hunger, float saturation) {
        return getItemRegistry().registerDrink(name, ModelProviderItem::new, hunger, saturation);
    }

    public static Item.Properties makeEndItemSettings() {
        return new Item.Properties();
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
        GuideBookItem.ensureStaticallyLoaded();
        if (BCLib.isDevEnvironment()) {
            DebugHelpers.generateDebugItems();
        }
    }

    public static Item.Properties defaultSettings() {
        return new Item.Properties();
    }
}

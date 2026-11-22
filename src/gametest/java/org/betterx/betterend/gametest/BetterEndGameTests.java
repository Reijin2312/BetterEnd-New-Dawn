package org.betterx.betterend.gametest;

import org.betterx.betterend.blocks.EndPathBlock;
import org.betterx.betterend.blocks.SulphurCrystalBlock;
import org.betterx.betterend.blocks.UmbrellaTreeClusterBlock;
import org.betterx.betterend.blocks.UmbrellaTreeClusterEmptyBlock;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndPortals;
import org.betterx.betterend.registry.EndTags;
import org.betterx.betterend.rituals.EternalRitual;
import org.betterx.betterend.registry.EndBiomes;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BiomeTags;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.function.Consumer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;


@EventBusSubscriber(modid = "betterend")
public final class BetterEndGameTests {
    private static final String TEMPLATE = "biome/lantern_woods/stump_1";

    @SubscribeEvent
    public static void registerGameTests(RegisterGameTestsEvent event) {
        Holder<TestEnvironmentDefinition> environment = event.registerEnvironment(
                id("upstream_regressions"), new TestEnvironmentDefinition.AllOf());
        register(event, environment, "november_structures_and_worldgen_tuning_are_loaded", BetterEndGameTests::novemberStructuresAndWorldgenTuningAreLoaded);
        register(event, environment, "paths_restore_their_end_terrain", BetterEndGameTests::pathsRestoreTheirEndTerrain);
        register(event, environment, "late_pedestal_has_block_entity", BetterEndGameTests::latePedestalHasBlockEntity);
        register(event, environment, "lone_eternal_pedestal_does_not_create_broken_ritual", BetterEndGameTests::loneEternalPedestalDoesNotCreateBrokenRitual);
        register(event, environment, "structure_placed_waterloggable_blocks_start_dry", BetterEndGameTests::structurePlacedWaterloggableBlocksStartDry);
        register(event, environment, "dry_crystals_leave_no_water_and_natural_state_tracks_fluid", BetterEndGameTests::dryCrystalsLeaveNoWaterAndNaturalStateTracksFluid);
        register(event, environment, "sulphur_crystal_loot_works_at_every_age", BetterEndGameTests::sulphurCrystalLootWorksAtEveryAge);
        register(event, environment, "natural_umbrella_cluster_refills", BetterEndGameTests::naturalUmbrellaClusterRefills);
        register(event, environment, "invalid_portal_ids_use_safe_fallbacks", BetterEndGameTests::invalidPortalIdsUseSafeFallbacks);
        register(event, environment, "furnace_recipes_are_loaded", BetterEndGameTests::furnaceRecipesAreLoaded);
        register(event, environment, "lacugrove_sapling_survives_on_endstone_dust", BetterEndGameTests::lacugroveSaplingSurvivesOnEndstoneDust);
        register(event, environment, "menger_sponge_has_real_block_properties", BetterEndGameTests::mengerSpongeHasRealBlockProperties);
        register(event, environment, "armor_keeps_configured_durability", BetterEndGameTests::armorKeepsConfiguredDurability);
        register(event, environment, "compatibility_tags_contain_all_biomes_and_furnaces", BetterEndGameTests::compatibilityTagsContainAllBiomesAndFurnaces);
    }

    private static void register(
            RegisterGameTestsEvent event,
            Holder<TestEnvironmentDefinition> environment,
            String name,
            Consumer<GameTestHelper> test
    ) {
        TestData<Holder<TestEnvironmentDefinition>> data = new TestData<>(environment, id(TEMPLATE), 200, 0, true);
        event.registerTest(id(name), new DirectTestInstance(data, test));
    }

    private static final class DirectTestInstance extends GameTestInstance {
        private final Consumer<GameTestHelper> test;

        private DirectTestInstance(TestData<Holder<TestEnvironmentDefinition>> data, Consumer<GameTestHelper> test) {
            super(data);
            this.test = test;
        }

        @Override
        public void run(GameTestHelper helper) {
            test.accept(helper);
        }

        @Override
        public MapCodec<? extends GameTestInstance> codec() {
            return FunctionGameTestInstance.CODEC;
        }

        @Override
        protected MutableComponent typeDescription() {
            return Component.literal("betterend upstream regression");
        }
    }

    private BetterEndGameTests() {
    }

    public static void novemberStructuresAndWorldgenTuningAreLoaded(GameTestHelper helper) {
        assertNovemberStructuresAndWorldgen(helper);
    }

    private static void assertNovemberStructuresAndWorldgen(GameTestHelper helper) {
        var structures = helper.getLevel().registryAccess().lookupOrThrow(Registries.STRUCTURE);
        var structureSets = helper.getLevel().registryAccess().lookupOrThrow(Registries.STRUCTURE_SET);
        for (String path : List.of(
                "crashed_ship", "blossoming_spires_house", "blossoming_spires_ruins",
                "chorus_forest_fallen_tree", "chorus_forest_ruins", "chorus_forest_stump",
                "foggy_mushroomland_tree_house", "lantern_woods_cabin",
                "shadow_forest_small_mansion", "umbrella_jungle_house_1"
        )) {
            Identifier identifier = id(path);
            helper.assertTrue(structures.get(ResourceKey.create(Registries.STRUCTURE, identifier)).isPresent(),
                    "Missing runtime structure betterend:" + path);
            helper.assertTrue(structureSets.get(ResourceKey.create(Registries.STRUCTURE_SET, identifier)).isPresent(),
                    "Missing runtime structure set betterend:" + path);
        }
        try {
            var rarity = org.betterx.betterend.world.features.terrain.BigAuroraCrystalFeature.class
                    .getDeclaredField("RARITY_DIVISOR");
            rarity.setAccessible(true);
            helper.assertTrue(rarity.getInt(null) == 7, "Aurora crystal rarity is not the upstream 1-in-7 value");
        } catch (ReflectiveOperationException exception) {
            helper.fail("Could not verify Aurora crystal rarity: " + exception);
            return;
        }
        assertCrystalMountainTuning(helper);
        assertCrashedShipLoot(helper);
        helper.succeed();
    }

    public static void armorKeepsConfiguredDurability(GameTestHelper helper) {
        assertMaxDamage(helper, EndBlocks.THALLASIUM.helmet, 165);
        assertMaxDamage(helper, EndBlocks.THALLASIUM.chestplate, 240);
        assertMaxDamage(helper, EndBlocks.THALLASIUM.leggings, 225);
        assertMaxDamage(helper, EndBlocks.THALLASIUM.boots, 195);

        assertMaxDamage(helper, EndBlocks.TERMINITE.helmet, 363);
        assertMaxDamage(helper, EndBlocks.TERMINITE.chestplate, 528);
        assertMaxDamage(helper, EndBlocks.TERMINITE.leggings, 495);
        assertMaxDamage(helper, EndBlocks.TERMINITE.boots, 429);

        assertMaxDamage(helper, EndItems.CRYSTALITE_HELMET, 418);
        assertMaxDamage(helper, EndItems.CRYSTALITE_CHESTPLATE, 608);
        assertMaxDamage(helper, EndItems.CRYSTALITE_LEGGINGS, 570);
        assertMaxDamage(helper, EndItems.CRYSTALITE_BOOTS, 494);

        assertMaxDamage(helper, EndItems.AETERNIUM_HELMET, 440);
        assertMaxDamage(helper, EndItems.AETERNIUM_CHESTPLATE, 640);
        assertMaxDamage(helper, EndItems.AETERNIUM_LEGGINGS, 600);
        assertMaxDamage(helper, EndItems.AETERNIUM_BOOTS, 520);

        assertMaxDamage(helper, EndItems.ARMORED_ELYTRA, 900);
        assertMaxDamage(helper, EndItems.CRYSTALITE_ELYTRA, 650);
        helper.succeed();
    }

    public static void pathsRestoreTheirEndTerrain(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 2, 1);
        Block path = block("end_moss_path");
        Block source = block("end_moss");
        helper.setBlock(pos, path.defaultBlockState());
        helper.setBlock(pos.above(), Blocks.STONE);

        ((EndPathBlock) path).tick(
                path.defaultBlockState(),
                helper.getLevel(),
                helper.absolutePos(pos),
                RandomSource.create(1L)
        );

        helper.assertBlockPresent(source, pos);
        helper.succeed();
    }

    public static void latePedestalHasBlockEntity(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 2, 1);
        helper.setBlock(pos, block("smaragdant_crystal_pedestal"));
        helper.assertTrue(
                helper.getLevel().getBlockEntity(helper.absolutePos(pos)) != null,
                "Smaragdant Crystal Pedestal did not create its block entity"
        );
        helper.succeed();
    }

    public static void loneEternalPedestalDoesNotCreateBrokenRitual(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 2, 1);
        helper.setBlock(pos, EndBlocks.ETERNAL_PEDESTAL);

        EternalRitual ritual = new EternalRitual(helper.getLevel(), helper.absolutePos(pos));
        helper.assertTrue(ritual.getCenter() == null, "A lone pedestal unexpectedly found a ritual center");
        ritual.checkStructure(helper.makeMockPlayer(net.minecraft.world.level.GameType.CREATIVE));
        helper.succeed();
    }

    public static void structurePlacedWaterloggableBlocksStartDry(GameTestHelper helper) {
        for (String id : new String[]{"sulphur_crystal", "smaragdant_crystal_shard", "hydrothermal_vent"}) {
            BlockState state = block(id).defaultBlockState();
            helper.assertTrue(
                    !state.hasProperty(BlockStateProperties.WATERLOGGED)
                            || !state.getValue(BlockStateProperties.WATERLOGGED),
                    id + " defaults to waterlogged"
            );
        }
        helper.succeed();
    }

    public static void dryCrystalsLeaveNoWaterAndNaturalStateTracksFluid(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 2, 1);
        helper.setBlock(pos.below(), EndBlocks.BRIMSTONE);
        helper.setBlock(pos, EndBlocks.SULPHUR_CRYSTAL.defaultBlockState());
        helper.getLevel().destroyBlock(helper.absolutePos(pos), false);
        helper.assertTrue(helper.getBlockState(pos).isAir() && helper.getBlockState(pos).getFluidState().isEmpty(),
                "Breaking a dry sulphur crystal left a water source");

        BlockState crystal = EndBlocks.SULPHUR_CRYSTAL.defaultBlockState();
        BlockState wet = SulphurCrystalBlock.naturalState(
                crystal, Blocks.WATER.defaultBlockState(), Direction.UP, SulphurCrystalBlock.MAX_AGE);
        BlockState dry = SulphurCrystalBlock.naturalState(
                crystal, Blocks.AIR.defaultBlockState(), Direction.UP, SulphurCrystalBlock.MAX_AGE);
        helper.assertTrue(wet.getValue(SulphurCrystalBlock.WATERLOGGED), "Natural crystal ignored water");
        helper.assertTrue(!dry.getValue(SulphurCrystalBlock.WATERLOGGED), "Natural crystal became wet in air");
        helper.succeed();
    }

    public static void sulphurCrystalLootWorksAtEveryAge(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 2, 1);
        for (int age = 0; age <= SulphurCrystalBlock.MAX_AGE; age++) {
            BlockState state = EndBlocks.SULPHUR_CRYSTAL.defaultBlockState()
                    .setValue(SulphurCrystalBlock.FACING, Direction.UP)
                    .setValue(SulphurCrystalBlock.AGE, age);
            helper.setBlock(pos.below(), EndBlocks.BRIMSTONE);
            helper.setBlock(pos, state);
            List<ItemStack> drops = Block.getDrops(
                    state, helper.getLevel(), helper.absolutePos(pos), null, null, new ItemStack(Items.IRON_PICKAXE));
            if (age < SulphurCrystalBlock.MAX_AGE) {
                helper.assertTrue(drops.size() == 1
                                && drops.get(0).is(EndBlocks.SULPHUR_CRYSTAL.asItem())
                                && drops.get(0).getCount() == 1,
                        "Young sulphur crystal did not drop exactly itself at age " + age + ": " + drops);
            } else {
                helper.assertTrue(drops.stream().anyMatch(stack -> stack.is(EndItems.CRYSTALLINE_SULPHUR)),
                        "Fully grown sulphur crystal dropped no crystalline sulphur: " + drops);
            }
        }
        helper.succeed();
    }

    public static void naturalUmbrellaClusterRefills(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 2, 1);
        BlockState empty = EndBlocks.UMBRELLA_TREE_CLUSTER_EMPTY.defaultBlockState()
                .setValue(UmbrellaTreeClusterEmptyBlock.NATURAL, true);
        helper.setBlock(pos, empty);
        helper.assertTrue(empty.isRandomlyTicking(), "Natural empty umbrella cluster is not random ticking");
        for (int i = 0; i < 1000 && helper.getBlockState(pos).is(EndBlocks.UMBRELLA_TREE_CLUSTER_EMPTY); i++) {
            BlockState state = helper.getBlockState(pos);
            state.randomTick(helper.getLevel(), helper.absolutePos(pos), helper.getLevel().getRandom());
        }
        BlockState filled = helper.getBlockState(pos);
        helper.assertTrue(filled.is(EndBlocks.UMBRELLA_TREE_CLUSTER), "Natural umbrella cluster never refilled");
        helper.assertTrue(filled.getValue(UmbrellaTreeClusterBlock.NATURAL), "Refilled cluster lost its natural flag");
        helper.succeed();
    }

    public static void invalidPortalIdsUseSafeFallbacks(GameTestHelper helper) {
        int expected = EndPortals.getColor(0);
        helper.assertTrue(EndPortals.getColor(-1) == expected, "Negative portal ID has no safe color fallback");
        helper.assertTrue(EndPortals.getColor(Integer.MAX_VALUE) == expected,
                "Oversized portal ID has no safe color fallback");
        helper.assertTrue(EndPortals.getWorld(helper.getLevel().getServer(), Integer.MAX_VALUE)
                        == helper.getLevel().getServer().overworld(),
                "Oversized portal ID has no safe world fallback");
        helper.succeed();
    }

    public static void furnaceRecipesAreLoaded(GameTestHelper helper) {
        for (String id : new String[]{
                "end_fish_smelting",
                "end_berry_smelting",
                "chorus_mushroom_smelting",
                "bolux_mushroom_smelting"
        }) {
            helper.assertTrue(
                    helper.getLevel().getServer().getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, id(id))).isPresent(),
                    "Missing furnace recipe betterend:" + id
            );
        }
        helper.succeed();
    }

    public static void lacugroveSaplingSurvivesOnEndstoneDust(GameTestHelper helper) {
        assertSurvivesOn(helper, "lacugrove_sapling", "endstone_dust", new BlockPos(1, 2, 1));
        helper.succeed();
    }

    public static void mengerSpongeHasRealBlockProperties(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 2, 1);
        BlockState state = EndBlocks.MENGER_SPONGE.defaultBlockState();
        helper.setBlock(pos, state);
        helper.assertTrue(
                state.getDestroySpeed(helper.getLevel(), helper.absolutePos(pos)) > 0.0F,
                "Menger Sponge still breaks instantly"
        );
        helper.assertTrue(
                state.getSoundType() == Blocks.SPONGE.defaultBlockState().getSoundType(),
                "Menger Sponge does not use sponge sounds"
        );
        helper.succeed();
    }

    private static void assertMaxDamage(GameTestHelper helper, Item item, int expected) {
        int actual = new ItemStack(item).getMaxDamage();
        helper.assertTrue(
                actual == expected,
                BuiltInRegistries.ITEM.getKey(item) + " has max damage " + actual + ", expected " + expected
        );
    }

    public static void compatibilityTagsContainAllBiomesAndFurnaces(GameTestHelper helper) {
        var biomes = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME);
        assertAllEndBiomesTagged(helper, biomes);
        for (Block furnace : List.of(
                EndBlocks.END_STONE_FURNACE,
                EndBlocks.FLAVOLITE.furnace,
                EndBlocks.VIOLECITE.furnace,
                EndBlocks.SULPHURIC_ROCK.furnace,
                EndBlocks.VIRID_JADESTONE.furnace,
                EndBlocks.AZURE_JADESTONE.furnace,
                EndBlocks.SANDY_JADESTONE.furnace,
                EndBlocks.UMBRALITH.furnace
        )) {
            helper.assertTrue(
                    furnace.defaultBlockState().is(EndTags.FURNACES),
                    "Furnace is missing from common workstation tag: " + furnace
            );
        }
        helper.succeed();
    }

    private static void assertAllEndBiomesTagged(GameTestHelper helper, net.minecraft.core.HolderLookup.RegistryLookup<net.minecraft.world.level.biome.Biome> biomes) {
        try {
            for (var field : EndBiomes.class.getFields()) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())
                        && org.betterx.betterend.world.biome.EndBiomeKey.class.isAssignableFrom(field.getType())) {
                    var key = (org.betterx.betterend.world.biome.EndBiomeKey<?, ?>) field.get(null);
                    var biome = biomes.getOrThrow(key.key);
                    if (biome.is(EndTags.IS_END_CAVE)) {
                        helper.assertFalse(biome.is(BiomeTags.IS_END),
                                "BetterEnd cave biome leaked into the surface minecraft:is_end tag: " + key.key);
                    } else {
                        helper.assertTrue(biome.is(BiomeTags.IS_END),
                                "BetterEnd surface biome is missing from minecraft:is_end: " + key.key);
                    }
                }
            }
        } catch (IllegalAccessException exception) {
            helper.fail("Could not inspect BetterEnd biome keys: " + exception);
        }
    }

    private static void assertCrystalMountainTuning(GameTestHelper helper) {
        try {
            Class<?> type = org.betterx.betterend.world.structures.piece.CrystalMountainPiece.class;
            helper.assertTrue(intConstant(type, "CRYSTAL_BIG_MAX_COUNT") == 6,
                    "Crystal Mountain large-crystal budget differs from upstream");
            helper.assertTrue(intConstant(type, "CROWN_MIN_COUNT") == 3,
                    "Crystal Mountain crown minimum differs from upstream");
            helper.assertTrue(intConstant(type, "CROWN_MAX_COUNT") == 5,
                    "Crystal Mountain crown maximum differs from upstream");
            helper.assertTrue(Math.abs(floatConstant(type, "CRYSTAL_END_STONE_CRYSTAL_CHANCE") - 0.03F) < 0.0001F,
                    "Crystal Mountain end-stone crystal density differs from upstream");
            helper.assertTrue(Math.abs(floatConstant(type, "CRYSTAL_MOSS_CRYSTAL_CHANCE") - 0.02F) < 0.0001F,
                    "Crystal Mountain moss crystal density differs from upstream");
        } catch (ReflectiveOperationException exception) {
            helper.fail("Could not verify Crystal Mountain tuning: " + exception);
        }
    }

    private static int intConstant(Class<?> type, String name) throws ReflectiveOperationException {
        var field = type.getDeclaredField(name);
        field.setAccessible(true);
        return field.getInt(null);
    }

    private static float floatConstant(Class<?> type, String name) throws ReflectiveOperationException {
        var field = type.getDeclaredField(name);
        field.setAccessible(true);
        return field.getFloat(null);
    }

    private static void assertCrashedShipLoot(GameTestHelper helper) {
        String resource = "data/betterend/structure/crashed_ship.nbt";
        try (var input = BetterEndGameTests.class.getClassLoader().getResourceAsStream(resource)) {
            helper.assertTrue(input != null, "Missing crashed ship structure template");
            if (input == null) return;
            try (var gzip = new java.util.zip.GZIPInputStream(input)) {
                String nbt = new String(gzip.readAllBytes(), java.nio.charset.StandardCharsets.ISO_8859_1);
                String lootTable = "minecraft:chests/end_city_treasure";
                int first = nbt.indexOf(lootTable);
                int second = first < 0 ? -1 : nbt.indexOf(lootTable, first + lootTable.length());
                helper.assertTrue(first >= 0 && second >= 0,
                        "Both crashed ship containers must use End City treasure");
                helper.assertTrue(nbt.indexOf(lootTable, second + lootTable.length()) < 0,
                        "Crashed ship has an unexpected third loot container");
                helper.assertTrue(!nbt.contains("LootTableSeed"),
                        "Crashed ship loot must use an independent random seed when opened");
            }
        } catch (java.io.IOException exception) {
            helper.fail("Could not inspect crashed ship loot: " + exception);
        }
    }

    private static void assertSurvivesOn(GameTestHelper helper, String saplingId, String soilId, BlockPos pos) {
        Block sapling = block(saplingId);
        helper.setBlock(pos.below(), block(soilId));
        helper.setBlock(pos, sapling);
        helper.assertTrue(
                sapling.defaultBlockState().canSurvive(helper.getLevel(), helper.absolutePos(pos)),
                saplingId + " does not survive on " + soilId
        );
    }

    private static Block block(String path) {
        Block block = BuiltInRegistries.BLOCK.getValue(id(path));
        if (block == Blocks.AIR) {
            throw new IllegalStateException("Missing BetterEnd block: " + path);
        }
        return block;
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath("betterend", path);
    }
}

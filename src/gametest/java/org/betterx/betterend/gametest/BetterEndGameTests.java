package org.betterx.betterend.gametest;

import org.betterx.betterend.blocks.EndPathBlock;
import org.betterx.betterend.blocks.SulphurCrystalBlock;
import org.betterx.betterend.blocks.UmbrellaTreeClusterBlock;
import org.betterx.betterend.blocks.UmbrellaTreeClusterEmptyBlock;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndPortals;
import org.betterx.betterend.rituals.EternalRitual;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("betterend")
@PrefixGameTestTemplate(false)
public final class BetterEndGameTests {
    private static final String TEMPLATE = "biome/lantern_woods/stump_1";

    private BetterEndGameTests() {
    }

    @GameTest(template = TEMPLATE)
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

    @GameTest(template = TEMPLATE)
    public static void latePedestalHasBlockEntity(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 2, 1);
        helper.setBlock(pos, block("smaragdant_crystal_pedestal"));
        helper.assertTrue(
                helper.getLevel().getBlockEntity(helper.absolutePos(pos)) != null,
                "Smaragdant Crystal Pedestal did not create its block entity"
        );
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void loneEternalPedestalDoesNotCreateBrokenRitual(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 2, 1);
        helper.setBlock(pos, EndBlocks.ETERNAL_PEDESTAL);

        EternalRitual ritual = new EternalRitual(helper.getLevel(), helper.absolutePos(pos));
        helper.assertTrue(ritual.getCenter() == null, "A lone pedestal unexpectedly found a ritual center");
        ritual.checkStructure(helper.makeMockPlayer(net.minecraft.world.level.GameType.CREATIVE));
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
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

    @GameTest(template = TEMPLATE)
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

    @GameTest(template = TEMPLATE)
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

    @GameTest(template = TEMPLATE)
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

    @GameTest(template = TEMPLATE)
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

    @GameTest(template = TEMPLATE)
    public static void furnaceRecipesAreLoaded(GameTestHelper helper) {
        for (String id : new String[]{
                "end_fish_smelting",
                "end_berry_smelting",
                "chorus_mushroom_smelting",
                "bolux_mushroom_smelting"
        }) {
            helper.assertTrue(
                    helper.getLevel().getRecipeManager().byKey(BetterEndGameTests.id(id)).isPresent(),
                    "Missing furnace recipe betterend:" + id
            );
        }
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void lacugroveSaplingSurvivesOnEndstoneDust(GameTestHelper helper) {
        assertSurvivesOn(helper, "lacugrove_sapling", "endstone_dust", new BlockPos(1, 2, 1));
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
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
        Block block = BuiltInRegistries.BLOCK.get(id(path));
        if (block == Blocks.AIR) {
            throw new IllegalStateException("Missing BetterEnd block: " + path);
        }
        return block;
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("betterend", path);
    }
}

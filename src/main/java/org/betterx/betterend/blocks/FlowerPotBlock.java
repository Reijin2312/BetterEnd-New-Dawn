package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourStone;
import org.betterx.bclib.blocks.BaseBlockNotFull;
import org.betterx.bclib.client.render.BCLRenderLayer;
import org.betterx.bclib.interfaces.PostInitable;
import org.betterx.bclib.interfaces.RenderLayerProvider;
import org.betterx.bclib.util.JsonFactory;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.entities.FlowerPotBlockEntity;
import org.betterx.betterend.interfaces.PottablePlant;
import org.betterx.betterend.interfaces.PottableTerrain;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.pottable.api.PottablePlantRegistry;
import org.betterx.wover.pottable.api.PottableSoil;
import org.betterx.wover.pottable.api.PottableSoilRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.fml.loading.FMLPaths;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class FlowerPotBlock extends BaseBlockNotFull implements EntityBlock, RenderLayerProvider, PostInitable {
    public static final IntegerProperty POT_LIGHT = EndBlockProperties.POT_LIGHT;
    private static final VoxelShape SHAPE_EMPTY;
    private static final VoxelShape SHAPE_FULL;
    private static Block[] plants;
    private static Block[] soils;
    private static final String[] DEFAULT_PLANT_IDS = new String[]{
            "twisted_umbrella_moss",
            "dragon_tree_sapling",
            "bolux_mushroom",
            "small_amaranita_mushroom",
            "crystal_grass",
            "creeping_moss",
            "pythadendron_sapling",
            "salteago",
            "murkweed",
            "chorus_grass",
            "tenanea_sapling",
            "lucernia_leaves",
            "neon_cactus",
            "amber_grass",
            "umbrella_moss",
            "flammalix",
            "lucernia_sapling",
            "lutebus",
            "small_jellyshroom",
            "blossom_berry_seed",
            "jungle_grass",
            "tenanea_leaves",
            "shadow_plant",
            "cave_grass",
            "aeridium",
            "dragon_tree_leaves",
            "blooming_cooksonia",
            "helix_tree_sapling",
            "mossy_glowshroom_sapling",
            "shadow_berry",
            "fracturn",
            "inflexia",
            "lacugrove_sapling",
            "vaiolush_fern",
            "orango",
            "end_lotus_flower",
            "chorus_mushroom_seed",
            "needlegrass",
            "amber_root_seed",
            "clawfern",
            "globulagus",
            "pythadendron_leaves",
            "bushy_grass",
            "lamellarium",
            "umbrella_tree_sapling",
            "lacugrove_leaves"
    };
    private static final String[] DEFAULT_SOIL_IDS = new String[]{
            "end_mycelium",
            "chorus_nylium",
            "shadow_grass",
            "sangnum",
            "pink_moss",
            "amber_moss",
            "jungle_moss",
            "rutiscus",
            "pallidium_full",
            "crystal_moss",
            "cave_moss",
            "end_moss"
    };

    public FlowerPotBlock(Block source) {
        super(BlockBehaviour.Properties.ofLegacyCopy(source).lightLevel(state -> state.getValue(POT_LIGHT) * 5));
        this.registerDefaultState(this.defaultBlockState().setValue(POT_LIGHT, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POT_LIGHT);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new FlowerPotBlockEntity(blockPos, blockState);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, Builder builder) {
        List<ItemStack> drop = Lists.newArrayList(new ItemStack(this));
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof FlowerPotBlockEntity flowerPot) {
            flowerPot.getSoilBlock().ifPresent(block -> drop.add(new ItemStack(block)));
            flowerPot.getPlantBlock().ifPresent(block -> drop.add(new ItemStack(block)));
        }
        return drop;
    }

    @Override
    public void postInit() {
        ensureInit();
    }

    private static void ensureInit() {
        if (plants != null && soils != null) {
            return;
        }
        synchronized (FlowerPotBlock.class) {
            if (plants != null && soils != null) {
                return;
            }
            initPottableLists();
        }
    }

    private static void initPottableLists() {
        Block[] plants = new Block[128];
        Block[] soils = new Block[16];

        Map<String, Integer> reservedPlantsIDs = defaultPottableIds(DEFAULT_PLANT_IDS);
        Map<String, Integer> reservedSoilIDs = defaultPottableIds(DEFAULT_SOIL_IDS);

        JsonObject obj = JsonFactory.getJsonObject(new File(
                FMLPaths.CONFIGDIR.get().toFile(),
                BetterEnd.MOD_ID + "/blocks.json"
        ));
        if (obj.get("flower_pots") != null) {
            JsonElement plantsObj = obj.get("flower_pots").getAsJsonObject().get("plants");
            JsonElement soilsObj = obj.get("flower_pots").getAsJsonObject().get("soils");
            if (plantsObj != null) {
                plantsObj.getAsJsonObject().entrySet().forEach(entry -> {
                    String name = entry.getKey().substring(0, entry.getKey().indexOf(' '));
                    reservedPlantsIDs.put(name, entry.getValue().getAsInt());
                });
            }
            if (soilsObj != null) {
                soilsObj.getAsJsonObject().entrySet().forEach(entry -> {
                    String name = entry.getKey().substring(0, entry.getKey().indexOf(' '));
                    reservedSoilIDs.put(name, entry.getValue().getAsInt());
                });
            }
        }

        EndBlocks.getModBlocks()
                 .stream()
                 .sorted(Comparator.comparing(FlowerPotBlock::blockId))
                 .forEach(block -> {
                     if (block instanceof PottablePlant && ((PottablePlant) block).canBePotted()) {
                         processBlock(plants, block, "flower_pots.plants", reservedPlantsIDs);
                     } else if (block instanceof PottableTerrain && ((PottableTerrain) block).canBePotted()) {
                         processBlock(soils, block, "flower_pots.soils", reservedSoilIDs);
                     }
                 });

        FlowerPotBlock.plants = new Block[maxNotNull(plants) + 1];
        System.arraycopy(plants, 0, FlowerPotBlock.plants, 0, FlowerPotBlock.plants.length);

        FlowerPotBlock.soils = new Block[maxNotNull(soils) + 1];
        System.arraycopy(soils, 0, FlowerPotBlock.soils, 0, FlowerPotBlock.soils.length);

    }

    public static PottableEntries getPottableEntries() {
        ensureInit();
        return new PottableEntries(plants.clone(), soils.clone());
    }

    public record PottableEntries(Block[] plants, Block[] soils) {
    }

    private static int maxNotNull(Block[] array) {
        int max = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                max = i;
            }
        }
        return max;
    }

    private static String blockId(Block block) {
        Identifier location = BuiltInRegistries.BLOCK.getKey(block);
        return location == null ? "" : location.toString();
    }

    private static Map<String, Integer> defaultPottableIds(String[] blockIds) {
        Map<String, Integer> ids = Maps.newHashMap();
        for (int i = 0; i < blockIds.length; i++) {
            ids.put(blockIds[i], i);
        }
        return ids;
    }

    private static void processBlock(Block[] target, Block block, String path, Map<String, Integer> idMap) {
        Identifier location = BuiltInRegistries.BLOCK.getKey(block);
        if (location == null) {
            return;
        }
        if (idMap.containsKey(location.getPath())) {
            target[idMap.get(location.getPath())] = block;
        } else {
            for (int i = 0; i < target.length; i++) {
                if (!idMap.containsValue(i)) {
                    target[i] = block;
                    idMap.put(location.getPath(), i);
                    break;
                }
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult useItemOn(
            ItemStack itemStack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (level.isClientSide()) {
            return InteractionResult.CONSUME;
        }
        if (!(level.getBlockEntity(pos) instanceof FlowerPotBlockEntity flowerPot)) {
            return InteractionResult.PASS;
        }

        if (flowerPot.getSoil().isEmpty()) {
            if (!(itemStack.getItem() instanceof BlockItem item)) {
                return InteractionResult.PASS;
            }
            Registry<PottableSoil> soils = level.registryAccess()
                                                .lookup(PottableSoilRegistry.POTTABLE_SOIL_REGISTRY)
                                                .orElse(null);
            Block block = item.getBlock();
            ResourceKey<Block> blockKey = block.builtInRegistryHolder().key();
            if (soils == null || findByBlock(soils, blockKey, soil -> soil.block) == null) {
                level.playSound(
                        player,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        SoundEvents.DISPENSER_FAIL,
                        SoundSource.BLOCKS,
                        0.6F,
                        1
                );
                return InteractionResult.FAIL;
            }
            flowerPot.setSoil(Optional.of(blockKey));
            if (!player.isCreative()) {
                itemStack.shrink(1);
            }
            level.playSound(
                    player,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    SoundEvents.SOUL_SOIL_PLACE,
                    SoundSource.BLOCKS,
                    1,
                    1
            );
            return InteractionResult.SUCCESS;
        }

        if (itemStack.isEmpty()) {
            if (flowerPot.getPlant().isPresent()) {
                Optional<Block> plantBlock = flowerPot.getPlantBlock();
                flowerPot.setPlant(Optional.empty());
                plantBlock.ifPresent(block -> player.addItem(new ItemStack(block)));
                return InteractionResult.SUCCESS;
            }
            Optional<Block> soilBlock = flowerPot.getSoilBlock();
            if (soilBlock.isPresent()) {
                flowerPot.setSoil(Optional.empty());
                player.addItem(new ItemStack(soilBlock.get()));
            }
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (!(itemStack.getItem() instanceof BlockItem item)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        Registry<org.betterx.wover.pottable.api.PottablePlant> plants = level.registryAccess()
                .lookup(PottablePlantRegistry.POTTABLE_PLANT_REGISTRY)
                .orElse(null);
        Block block = item.getBlock();
        ResourceKey<Block> blockKey = block.builtInRegistryHolder().key();
        org.betterx.wover.pottable.api.PottablePlant plant =
                plants == null ? null : findByBlock(plants, blockKey, p -> p.block);
        if (plant == null) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        Block soilBlock = flowerPot.getSoilBlock().orElse(null);
        if (soilBlock == null || !plant.isValidSoil(soilBlock)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        flowerPot.setPlant(Optional.of(blockKey));
        level.playSound(
                player,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                SoundEvents.HOE_TILL,
                SoundSource.BLOCKS,
                1,
                1
        );
        if (!player.isCreative()) {
            itemStack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    private static <T> T findByBlock(
            Registry<T> registry,
            ResourceKey<Block> blockKey,
            Function<T, ResourceKey<Block>> accessor
    ) {
        for (T entry : registry) {
            if (accessor.apply(entry).equals(blockKey)) {
                return entry;
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        if (view.getBlockEntity(pos) instanceof FlowerPotBlockEntity flowerPot && flowerPot.getPlant().isPresent()) {
            return SHAPE_FULL;
        }
        return SHAPE_EMPTY;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE_EMPTY;
    }

    @Override
    public BCLRenderLayer getRenderLayer() {
        return BCLRenderLayer.CUTOUT;
    }

    public static class Stone extends FlowerPotBlock implements BehaviourStone {
        public Stone(Block source) {
            super(source);
        }
    }

    static {
        SHAPE_EMPTY = Shapes.or(Block.box(4, 1, 4, 12, 8, 12), Block.box(5, 0, 5, 11, 1, 11));
        SHAPE_FULL = Shapes.or(SHAPE_EMPTY, Block.box(3, 8, 3, 13, 16, 13));
    }
}

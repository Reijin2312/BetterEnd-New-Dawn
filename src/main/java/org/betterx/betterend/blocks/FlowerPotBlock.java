package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourStone;
import org.betterx.bclib.blocks.BaseBlockNotFull;
import org.betterx.bclib.client.models.BasePatterns;
import org.betterx.bclib.client.models.ModelsHelper;
import org.betterx.bclib.client.models.ModelsHelper.MultiPartBuilder;
import org.betterx.bclib.client.models.PatternsHelper;
import org.betterx.bclib.client.render.BCLRenderLayer;
import org.betterx.bclib.interfaces.PostInitable;
import org.betterx.bclib.interfaces.RenderLayerProvider;
import org.betterx.bclib.interfaces.RuntimeBlockModelProvider;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.JsonFactory;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.basis.PottableLeavesBlock;
import org.betterx.betterend.client.models.Patterns;
import org.betterx.betterend.interfaces.PottablePlant;
import org.betterx.betterend.interfaces.PottableTerrain;
import org.betterx.betterend.registry.EndBlocks;

import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.fml.loading.FMLPaths;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.joml.Vector3f;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FlowerPotBlock extends BaseBlockNotFull implements RenderLayerProvider, PostInitable, RuntimeBlockModelProvider {
    private static final IntegerProperty PLANT_ID = EndBlockProperties.PLANT_ID;
    private static final IntegerProperty SOIL_ID = EndBlockProperties.SOIL_ID;
    private static final IntegerProperty POT_LIGHT = EndBlockProperties.POT_LIGHT;
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
        super(BlockBehaviour.Properties.ofFullCopy(source).lightLevel(state -> state.getValue(POT_LIGHT) * 5));
        this.registerDefaultState(
                this.defaultBlockState()
                    .setValue(PLANT_ID, 0)
                    .setValue(SOIL_ID, 0)
                    .setValue(POT_LIGHT, 0)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PLANT_ID, SOIL_ID, POT_LIGHT);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, Builder builder) {
        ensureInit();
        List<ItemStack> drop = Lists.newArrayList(new ItemStack(this));
        int id = state.getValue(SOIL_ID) - 1;
        if (id >= 0 && id < soils.length && soils[id] != null) {
            drop.add(new ItemStack(soils[id]));
        }
        id = state.getValue(PLANT_ID) - 1;
        if (id >= 0 && id < plants.length && plants[id] != null) {
            drop.add(new ItemStack(plants[id]));
        }
        return drop;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(
            BlockState state,
            Direction facing,
            BlockState neighborState,
            LevelAccessor world,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        ensureInit();
        int plantID = state.getValue(PLANT_ID);
        if (plantID < 1 || plantID > plants.length || plants[plantID - 1] == null) {
            return state.getValue(POT_LIGHT) > 0 ? state.setValue(POT_LIGHT, 0) : state;
        }
        int light = plants[plantID - 1].defaultBlockState().getLightEmission() / 5;
        if (state.getValue(POT_LIGHT) != light) {
            state = state.setValue(POT_LIGHT, light);
        }
        return state;
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

        if (PLANT_ID.getValue(Integer.toString(FlowerPotBlock.plants.length)).isEmpty()) {
            throw new RuntimeException("There are too much plant ID values!");
        }
        if (SOIL_ID.getValue(Integer.toString(FlowerPotBlock.soils.length)).isEmpty()) {
            throw new RuntimeException("There are too much soil ID values!");
        }
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
        ResourceLocation location = BuiltInRegistries.BLOCK.getKey(block);
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
        ResourceLocation location = BuiltInRegistries.BLOCK.getKey(block);
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
    public ItemInteractionResult useItemOn(
            ItemStack itemStack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        ensureInit();
        if (level.isClientSide) {
            return ItemInteractionResult.CONSUME;
        }
        int soilID = state.getValue(SOIL_ID);
        if (soilID == 0 || soilID > soils.length || soils[soilID - 1] == null) {
            if (!(itemStack.getItem() instanceof BlockItem)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            Block block = ((BlockItem) itemStack.getItem()).getBlock();
            for (int i = 0; i < soils.length; i++) {
                if (block == soils[i]) {
                    BlocksHelper.setWithUpdate(level, pos, state.setValue(SOIL_ID, i + 1));
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
                    return ItemInteractionResult.SUCCESS;
                }
            }
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
            return ItemInteractionResult.FAIL;
        }

        int plantID = state.getValue(PLANT_ID);
        if (itemStack.isEmpty()) {
            if (plantID > 0 && plantID <= plants.length && plants[plantID - 1] != null) {
                BlocksHelper.setWithUpdate(level, pos, state.setValue(PLANT_ID, 0).setValue(POT_LIGHT, 0));
                player.addItem(new ItemStack(plants[plantID - 1]));
                return ItemInteractionResult.SUCCESS;
            }
            if (soilID > 0 && soilID <= soils.length && soils[soilID - 1] != null) {
                BlocksHelper.setWithUpdate(level, pos, state.setValue(SOIL_ID, 0));
                player.addItem(new ItemStack(soils[soilID - 1]));
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!(itemStack.getItem() instanceof BlockItem)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        BlockItem item = (BlockItem) itemStack.getItem();
        for (int i = 0; i < plants.length; i++) {
            if (item.getBlock() == plants[i]) {
                if (!((PottablePlant) plants[i]).canPlantOn(soils[soilID - 1])) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }
                int light = plants[i].defaultBlockState().getLightEmission() / 5;
                BlocksHelper.setWithUpdate(level, pos, state.setValue(PLANT_ID, i + 1).setValue(POT_LIGHT, light));
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
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockModel getItemModel(ResourceLocation blockId) {
        Optional<String> pattern = PatternsHelper.createJson(Patterns.BLOCK_FLOWER_POT, blockId);
        return ModelsHelper.fromPattern(pattern);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockModel getBlockModel(ResourceLocation blockId, BlockState blockState) {
        Optional<String> pattern = PatternsHelper.createJson(Patterns.BLOCK_FLOWER_POT, blockId);
        return ModelsHelper.fromPattern(pattern);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public UnbakedModel getModelVariant(
            ModelResourceLocation stateId,
            BlockState blockState,
            Map<ResourceLocation, UnbakedModel> modelCache
    ) {
        ensureInit();
        MultiPartBuilder model = MultiPartBuilder.create(stateDefinition);
        ModelResourceLocation baseId = RuntimeBlockModelProvider.remapModelResourceLocation(stateId, blockState);
        registerBlockModel(stateId, baseId, blockState, modelCache);
        model.part(baseId.id()).add();
        Transformation offset = new Transformation(new Vector3f(0, 7.5F / 16F, 0), null, null, null);

        for (int i = 0; i < plants.length; i++) {
            if (plants[i] == null) {
                continue;
            }

            final int compareID = i + 1;
            ResourceLocation modelPath = BuiltInRegistries.BLOCK.getKey(plants[i]);
            ResourceLocation objSource = ResourceLocation.fromNamespaceAndPath(
                    modelPath.getNamespace(),
                    "models/block/" + modelPath.getPath() + "_potted.json"
            );

            if (Minecraft.getInstance().getResourceManager().getResource(objSource).isPresent()) {
                objSource = ResourceLocation.fromNamespaceAndPath(modelPath.getNamespace(), "block/" + modelPath.getPath() + "_potted");
                model.part(objSource)
                     .setTransformation(offset)
                     .setCondition(state -> state.getValue(PLANT_ID) == compareID)
                     .add();
                continue;
            } else if (plants[i] instanceof SaplingBlock) {
                ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(plants[i]);
                modelPath = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), "block/" + loc.getPath() + "_potted");
                Map<String, String> textures = Maps.newHashMap();
                textures.put("%modid%", loc.getNamespace());
                textures.put("%texture%", loc.getPath());
                Optional<String> pattern = Patterns.createJson(BasePatterns.BLOCK_CROSS, textures);
                UnbakedModel unbakedModel = ModelsHelper.fromPattern(pattern);
                if (unbakedModel != null) {
                    modelCache.put(modelPath, unbakedModel);
                    model.part(modelPath)
                         .setTransformation(offset)
                         .setCondition(state -> state.getValue(PLANT_ID) == compareID)
                         .add();
                } else {
                    BetterEnd.LOGGER.warn("Missing pattern for pot plant " + modelPath);
                }
                continue;
            } else if (plants[i] instanceof PottableLeavesBlock) {
                ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(plants[i]);
                modelPath = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), "block/" + loc.getPath() + "_potted");
                Map<String, String> textures = Maps.newHashMap();
                textures.put("%leaves%", loc.getPath().contains("lucernia") ? loc.getPath() + "_1" : loc.getPath());
                textures.put("%stem%", loc.getPath().replace("_leaves", "_log_side"));
                Optional<String> pattern = Patterns.createJson(Patterns.BLOCK_POTTED_LEAVES, textures);
                UnbakedModel unbakedModel = ModelsHelper.fromPattern(pattern);
                if (unbakedModel != null) {
                    modelCache.put(modelPath, unbakedModel);
                    model.part(modelPath)
                         .setTransformation(offset)
                         .setCondition(state -> state.getValue(PLANT_ID) == compareID)
                         .add();
                } else {
                    BetterEnd.LOGGER.warn("Missing pattern for pot plant " + modelPath);
                }
                continue;
            }

            objSource = ResourceLocation.fromNamespaceAndPath(modelPath.getNamespace(), "blockstates/" + modelPath.getPath() + ".json");
            JsonObject obj = JsonFactory.getJsonObject(objSource);
            if (obj != null) {
                JsonElement variants = obj.get("variants");
                JsonElement list = null;
                String path = null;

                if (variants == null) {
                    continue;
                }

                if (variants.isJsonArray()) {
                    list = variants.getAsJsonArray().get(0);
                } else if (variants.isJsonObject()) {
                    list = variants.getAsJsonObject().get(((PottablePlant) plants[i]).getPottedState());
                }

                if (list == null) {
                    BetterEnd.LOGGER.warn("Incorrect json for pot plant " + objSource + ", no matching variants");
                    continue;
                }

                if (list.isJsonArray()) {
                    path = list.getAsJsonArray().get(0).getAsJsonObject().get("model").getAsString();
                } else {
                    path = list.getAsJsonObject().get("model").getAsString();
                }

                if (path == null) {
                    BetterEnd.LOGGER.warn("Incorrect json for pot plant " + objSource + ", no matching variants");
                    continue;
                }

                model.part(ResourceLocation.parse(path))
                     .setTransformation(offset)
                     .setCondition(state -> state.getValue(PLANT_ID) == compareID)
                     .add();
            } else {
                ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(plants[i]);
                modelPath = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), "block/" + loc.getPath() + "_potted");
                Map<String, String> textures = Maps.newHashMap();
                textures.put("%modid%", loc.getNamespace());
                textures.put("%texture%", loc.getPath());
                Optional<String> pattern = Patterns.createJson(BasePatterns.BLOCK_CROSS, textures);
                UnbakedModel unbakedModel = ModelsHelper.fromPattern(pattern);
                if (unbakedModel != null) {
                    modelCache.put(modelPath, unbakedModel);
                    model.part(modelPath)
                         .setTransformation(offset)
                         .setCondition(state -> state.getValue(PLANT_ID) == compareID)
                         .add();
                } else {
                    BetterEnd.LOGGER.warn("Missing pattern for pot plant " + modelPath);
                }
            }
        }

        for (int i = 0; i < soils.length; i++) {
            if (soils[i] == null) {
                continue;
            }

            ResourceLocation soilLoc = BetterEnd.C.mk("flower_pot_soil_" + i);
            if (!modelCache.containsKey(soilLoc)) {
                String texture = BuiltInRegistries.BLOCK.getKey(soils[i]).getPath() + "_top";
                if (texture.contains("rutiscus")) {
                    texture += "_1";
                }
                Optional<String> pattern = Patterns.createJson(Patterns.BLOCK_FLOWER_POT_SOIL, texture);
                UnbakedModel soil = ModelsHelper.fromPattern(pattern);
                if (soil != null) {
                    modelCache.put(soilLoc, soil);
                } else {
                    BetterEnd.LOGGER.warn("Missing pattern for flower pot soil " + soilLoc);
                }
            }
            final int compareID = i + 1;
            if (modelCache.containsKey(soilLoc)) {
                model.part(soilLoc).setCondition(state -> state.getValue(SOIL_ID) == compareID).add();
            }
        }

        UnbakedModel result = model.build();
        modelCache.put(stateId.id(), result);
        return result;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        ensureInit();
        int id = state.getValue(PLANT_ID);
        return id > 0 && id <= plants.length ? SHAPE_FULL : SHAPE_EMPTY;
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

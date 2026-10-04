package org.betterx.betterend.world.biome;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.config.Configs;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeGenerationDataContainer;
import org.betterx.wover.generator.api.biomesource.WoverBiomeData;
import org.betterx.wover.generator.api.biomesource.WoverBiomePicker;
import org.betterx.wover.surface.api.SurfaceRuleBuilder;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EndBiome extends WoverBiomeData implements SurfaceMaterialProvider {
    public static final MapCodec<EndBiome> CODEC = codec(
            Codec.BOOL.fieldOf("has_caves").orElse(true).forGetter(o -> o.hasCaves),
            SurfaceMaterialProvider.CODEC.fieldOf("surface")
                                         .orElse(Config.DEFAULT_MATERIAL)
                                         .forGetter(o -> o.surfMatProv),
            EndBiome::new
    );
    public EndBiome(
            float fogDensity,
            @NotNull ResourceKey<Biome> biome,
            @NotNull BiomeGenerationDataContainer generatorData,
            float terrainHeight,
            float genChance,
            int edgeSize,
            boolean vertical,
            @Nullable ResourceKey<Biome> edge,
            @Nullable ResourceKey<Biome> parent,
            boolean hasCaves,
            SurfaceMaterialProvider surface
    ) {
        super(
                fogDensity, biome, generatorData, terrainHeight,
                genChance, edgeSize, vertical, edge, parent
        );
        this.hasCaves = hasCaves;
        this.surfMatProv = surface;
    }

    public void datagenSetup(BootstrapContext<BiomeData> dataContext) {

    }

    @Override
    public MapCodec<? extends WoverBiomeData> codec() {
        return CODEC;
    }

    public boolean isEnabled() {
        return Configs.BIOMES_TOGGLE.isEnabled(biomeKey);
    }

    @Override
    public boolean isPickable() {
        return isEnabled() && super.isPickable();
    }

    private boolean hasCaves = true;

    void setHasCaves(boolean v) {
        this.hasCaves = v;
    }

    public boolean hasCaves() {
        return hasCaves;
    }

    public static class DefaultSurfaceMaterialProvider implements SurfaceMaterialProvider {
        public static final BlockState END_STONE = Blocks.END_STONE.defaultBlockState();

        @Override
        public BlockState getTopMaterial() {
            return getUnderMaterial();
        }

        @Override
        public BlockState getAltTopMaterial() {
            return getTopMaterial();
        }

        @Override
        public BlockState getUnderMaterial() {
            return END_STONE;
        }

        @Override
        public boolean generateFloorRule() {
            return true;
        }

        @Override
        public SurfaceRuleBuilder surface() {
            SurfaceRuleBuilder builder = SurfaceRuleBuilder.start();

            if (generateFloorRule() && getTopMaterial() != getUnderMaterial()) {
                if (getTopMaterial() != getAltTopMaterial()) {
                    builder.floor(getTopMaterial());
                } else {
                    builder.chancedFloor(getTopMaterial(), getAltTopMaterial());
                }
            }
            return builder.filler(getUnderMaterial());
        }
    }

    public abstract static class Config implements EndBiomeBuilder.BiomeFactory {
        public static final SurfaceMaterialProvider DEFAULT_MATERIAL = new DefaultSurfaceMaterialProvider();

        private static final java.util.function.Supplier<MaterialRule> END_STONE = () -> MaterialRules.state(
                DefaultSurfaceMaterialProvider.END_STONE);
        private static final java.util.function.Supplier<MaterialRule> END_MOSS = () -> MaterialRules.state(
                EndBlocks.END_MOSS.defaultBlockState());
        private static final java.util.function.Supplier<MaterialRule> ENDSTONE_DUST = () -> MaterialRules.state(
                EndBlocks.ENDSTONE_DUST.defaultBlockState());
        private static final java.util.function.Supplier<MaterialRule> END_MYCELIUM = () -> MaterialRules.state(
                EndBlocks.END_MYCELIUM.defaultBlockState());
        private static final java.util.function.Supplier<MaterialRule> FLAVOLITE = () -> MaterialRules.state(
                EndBlocks.FLAVOLITE.stone.defaultBlockState());
        private static final java.util.function.Supplier<MaterialRule> SULPHURIC_ROCK = () -> MaterialRules.state(
                EndBlocks.SULPHURIC_ROCK.stone.defaultBlockState());
        private static final java.util.function.Supplier<MaterialRule> BRIMSTONE = () -> MaterialRules.state(
                EndBlocks.BRIMSTONE.defaultBlockState());
        private static final java.util.function.Supplier<MaterialRule> PALLIDIUM_FULL = () -> MaterialRules.state(
                EndBlocks.PALLIDIUM_FULL.defaultBlockState());
        private static final java.util.function.Supplier<MaterialRule> PALLIDIUM_HEAVY = () -> MaterialRules.state(
                EndBlocks.PALLIDIUM_HEAVY.defaultBlockState());
        private static final java.util.function.Supplier<MaterialRule> PALLIDIUM_THIN = () -> MaterialRules.state(
                EndBlocks.PALLIDIUM_THIN.defaultBlockState());
        private static final java.util.function.Supplier<MaterialRule> PALLIDIUM_TINY = () -> MaterialRules.state(
                EndBlocks.PALLIDIUM_TINY.defaultBlockState());
        private static final java.util.function.Supplier<MaterialRule> UMBRALITH = () -> MaterialRules.state(
                EndBlocks.UMBRALITH.stone.defaultBlockState());

        protected Config() {
        }

        public abstract void addCustomBuildData(EndBiomeBuilder builder);

        public boolean hasCaves() {
            return true;
        }

        protected static MaterialRule endStone() {
            return END_STONE.get();
        }

        protected static MaterialRule endMoss() {
            return END_MOSS.get();
        }

        protected static MaterialRule endstoneDust() {
            return ENDSTONE_DUST.get();
        }

        protected static MaterialRule endMycelium() {
            return END_MYCELIUM.get();
        }

        protected static MaterialRule flavolite() {
            return FLAVOLITE.get();
        }

        protected static MaterialRule sulphuricRock() {
            return SULPHURIC_ROCK.get();
        }

        protected static MaterialRule brimstone() {
            return BRIMSTONE.get();
        }

        protected static MaterialRule pallidiumFull() {
            return PALLIDIUM_FULL.get();
        }

        protected static MaterialRule pallidiumHeavy() {
            return PALLIDIUM_HEAVY.get();
        }

        protected static MaterialRule pallidiumThin() {
            return PALLIDIUM_THIN.get();
        }

        protected static MaterialRule pallidiumTiny() {
            return PALLIDIUM_TINY.get();
        }

        protected static MaterialRule umbralith() {
            return UMBRALITH.get();
        }

        public boolean hasReturnGateway() {
            return true;
        }

        public SurfaceMaterialProvider surfaceMaterial() {
            return DEFAULT_MATERIAL;
        }

        public @NotNull EndBiome instantiateBiome(
                float fogDensity,
                BiomeKey<?> key,
                @NotNull BiomeGenerationDataContainer generatorData,
                float terrainHeight,
                float genChance,
                int edgeSize,
                boolean vertical,
                @Nullable ResourceKey<Biome> edge,
                @Nullable ResourceKey<Biome> parent,
                boolean hasCave,
                SurfaceMaterialProvider surface
        ) {
            return new EndBiome(
                    fogDensity, key.key, generatorData,
                    terrainHeight, genChance, edgeSize, vertical, edge, parent,
                    hasCave, surface
            );
        }
    }

    protected SurfaceMaterialProvider surfMatProv = Config.DEFAULT_MATERIAL;

    @Override
    public BlockState getTopMaterial() {
        return surfMatProv.getTopMaterial();
    }

    @Override
    public BlockState getUnderMaterial() {
        return surfMatProv.getUnderMaterial();
    }

    @Override
    public BlockState getAltTopMaterial() {
        return surfMatProv.getAltTopMaterial();
    }

    @Override
    public boolean generateFloorRule() {
        return surfMatProv.generateFloorRule();
    }

    @Override
    public SurfaceRuleBuilder surface() {
        return surfMatProv.surface();
    }

    public static BlockState findTopMaterial(Holder<Biome> biome) {
        return SurfaceMaterialProvider
                .findSurfaceMaterialProvider(biome)
                .map(SurfaceMaterialProvider::getTopMaterial)
                .orElse(EndBiome.Config.DEFAULT_MATERIAL.getTopMaterial());
    }

    public static BlockState findTopMaterial(WorldGenLevel world, BlockPos pos) {
        return SurfaceMaterialProvider
                .findSurfaceMaterialProvider(WoverBiomePicker.getBiomeAt(world, pos))
                .map(SurfaceMaterialProvider::getTopMaterial)
                .orElse(EndBiome.Config.DEFAULT_MATERIAL.getTopMaterial());
    }

    public static BlockState sampleTopMaterial(WorldGenLevel world, BlockPos topSolidPos) {
        final BlockState top = world.getBlockState(topSolidPos);
        if (top.is(CommonBlockTags.TERRAIN) && !top.is(Blocks.END_STONE)) {
            return top;
        }
        return findTopMaterial(world, topSolidPos);
    }

    public static BlockState findUnderMaterial(Holder<Biome> biome) {
        return SurfaceMaterialProvider
                .findSurfaceMaterialProvider(biome)
                .map(SurfaceMaterialProvider::getUnderMaterial)
                .orElse(EndBiome.Config.DEFAULT_MATERIAL.getUnderMaterial());
    }

    public static BlockState findUnderMaterial(WorldGenLevel world, BlockPos pos) {
        return SurfaceMaterialProvider
                .findSurfaceMaterialProvider(WoverBiomePicker.getBiomeAt(world, pos))
                .map(SurfaceMaterialProvider::getUnderMaterial)
                .orElse(EndBiome.Config.DEFAULT_MATERIAL.getUnderMaterial());
    }
}

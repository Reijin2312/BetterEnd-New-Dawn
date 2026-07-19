package org.betterx.betterend.world.structures.features;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;

public abstract class FeatureBaseStructure extends Structure {
    protected static final BlockState AIR = Blocks.AIR.defaultBlockState();

    public FeatureBaseStructure(Structure.StructureSettings structureSettings) {
        super(structureSettings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        BlockPos pos = getGenerationHeight(
                context.chunkPos(),
                context.chunkGenerator(),
                context.heightAccessor(),
                context.randomState()
        );
        if (pos.getY() >= 10) {
            return Optional.of(new Structure.GenerationStub(pos, (structurePiecesBuilder) -> {
                generatePieces(structurePiecesBuilder, context);
            }));
        }
        return Optional.empty();
    }

    protected Holder<Biome> getNoiseBiome(ChunkGenerator cg, RandomState rState, int i, int j, int k) {
        return cg.getBiomeSource().getNoiseBiome(i, j, k, rState.sampler());
    }

    protected abstract void generatePieces(
            StructurePiecesBuilder structurePiecesBuilder,
            Structure.GenerationContext context
    );

    private static BlockPos getGenerationHeight(
            ChunkPos chunkPos,
            ChunkGenerator chunkGenerator,
            LevelHeightAccessor levelHeightAccessor,
            RandomState rState
    ) {
        LegacyRandomSource random = new LegacyRandomSource(chunkPos.x() + chunkPos.z() * 10387313);
        Rotation blockRotation = Rotation.getRandom(random);

        int offsetX = 5;
        int offsetZ = 5;
        if (blockRotation == Rotation.CLOCKWISE_90) {
            offsetX = -5;
        } else if (blockRotation == Rotation.CLOCKWISE_180) {
            offsetX = -5;
            offsetZ = -5;
        } else if (blockRotation == Rotation.COUNTERCLOCKWISE_90) {
            offsetZ = -5;
        }

        int blockX = chunkPos.getBlockX(7);
        int blockZ = chunkPos.getBlockZ(7);
        int minZ = Integer.MAX_VALUE;
        BlockPos.MutableBlockPos result = new BlockPos.MutableBlockPos(blockX, Integer.MIN_VALUE, blockZ);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                int z = chunkGenerator.getFirstOccupiedHeight(
                        blockX + i * offsetX,
                        blockZ + j * offsetZ,
                        Heightmap.Types.WORLD_SURFACE_WG,
                        levelHeightAccessor,
                        rState
                );
                if (z < minZ) {
                    result.set(blockX + i * offsetX, z, blockZ + j * offsetZ);
                }
            }
        }

        return result;
    }
}

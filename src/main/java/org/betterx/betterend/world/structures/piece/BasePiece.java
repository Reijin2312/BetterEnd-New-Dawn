package org.betterx.betterend.world.structures.piece;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public abstract class BasePiece extends StructurePiece {
    protected BasePiece(StructurePieceType type, int i, BoundingBox boundingBox) {
        super(type, i, boundingBox);
    }

    protected BasePiece(StructurePieceType type, CompoundTag tag) {
        super(type, tag);
        fromNbt(tag);
    }

    protected abstract void fromNbt(CompoundTag tag);

    protected static int worldgenSurfaceHeight(WorldGenLevel world, int x, int z) {
        final ChunkAccess chunk = world.getChunk(
                SectionPos.blockToSectionCoord(x),
                SectionPos.blockToSectionCoord(z)
        );
        if (chunk.hasPrimedHeightmap(Heightmap.Types.WORLD_SURFACE_WG)) {
            return chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) + 1;
        }
        final ServerChunkCache source = world.getLevel().getChunkSource();
        return source.getGenerator()
                     .getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, world, source.randomState());
    }

    protected static void setChunkBlock(ChunkAccess chunk, BlockPos pos, BlockState state) {
        final BlockState old = chunk.setBlockState(pos, state, false);
        if (old != null && old.hasBlockEntity() && !old.is(state.getBlock())) {
            chunk.removeBlockEntity(new BlockPos(
                    chunk.getPos().getBlockX(pos.getX() & 15),
                    pos.getY(),
                    chunk.getPos().getBlockZ(pos.getZ() & 15)
            ));
        }
    }

    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(
            StructurePieceSerializationContext structurePieceSerializationContext,
            CompoundTag compoundTag
    ) {
        addAdditionalSaveData(compoundTag);
    }
}

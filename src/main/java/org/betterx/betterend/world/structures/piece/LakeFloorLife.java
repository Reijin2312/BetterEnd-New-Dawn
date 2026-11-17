package org.betterx.betterend.world.structures.piece;

import org.betterx.betterend.blocks.EndLilyBlock;
import org.betterx.betterend.blocks.EndLotusSeedBlock;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.block.api.BlockProperties.TripleShape;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import org.jetbrains.annotations.Nullable;

/** Deterministic underwater life for lake floors that fall inside a cave biome band. */
final class LakeFloorLife {
    private LakeFloorLife() {}

    private static final OpenSimplexNoise CLUSTER = new OpenSimplexNoise(0x1A4EF100L);
    private static final double LIFE_PER_COLUMN = 0.7;
    private static final double BIOME_TOPUP = 0.2;
    private static BlockState[] plants;
    private static final int[] WEIGHTS = {115, 110, 21, 12, 6, 6, 16, 11, 4, 2, 40};

    private static void init() {
        if (plants != null) return;
        plants = new BlockState[]{
                EndBlocks.CHARNIA_CYAN.defaultBlockState(),
                EndBlocks.CHARNIA_LIGHT_BLUE.defaultBlockState(),
                EndBlocks.CHARNIA_RED.defaultBlockState(),
                EndBlocks.CHARNIA_GREEN.defaultBlockState(),
                EndBlocks.CHARNIA_PURPLE.defaultBlockState(),
                EndBlocks.CHARNIA_ORANGE.defaultBlockState(),
                EndBlocks.MENGER_SPONGE_WET.defaultBlockState(),
                EndBlocks.BUBBLE_CORAL.defaultBlockState(),
                EndBlocks.POND_ANEMONE.defaultBlockState(),
                EndBlocks.END_LILY.defaultBlockState(),
                null
        };
    }

    private static boolean isLakeBiome(Holder<Biome> holder) {
        return holder.unwrapKey().map(k -> k.location().getPath().startsWith("megalake")).orElse(false);
    }

    static void place(
            WorldGenLevel world,
            ChunkAccess chunk,
            int sx,
            int sz,
            int surfaceY,
            @Nullable ResourceKey<Biome> lakeBiome,
            long seed
    ) {
        init();
        final MutableBlockPos mut = new MutableBlockPos();
        final BlockState lily = EndBlocks.END_LILY.defaultBlockState();
        int total = 0;
        for (int weight : WEIGHTS) total += weight;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                final int wx = sx + x;
                final int wz = sz + z;
                mut.set(x, surfaceY, z);
                if (!chunk.getBlockState(mut).is(Blocks.WATER) || !chunk.getFluidState(mut).isSource()) continue;
                mut.setY(surfaceY + 1);
                if (!chunk.getBlockState(mut).isAir()) continue;
                int y = surfaceY;
                while (y > chunk.getMinBuildHeight() + 1) {
                    mut.setY(y - 1);
                    if (!chunk.getBlockState(mut).is(Blocks.WATER)) break;
                    y--;
                }
                final Holder<Biome> floorBiome = chunk.getNoiseBiome(
                        QuartPos.fromBlock(wx), QuartPos.fromBlock(y), QuartPos.fromBlock(wz));
                final boolean biomeCovers = isLakeBiome(floorBiome)
                        && lakeBiome != null
                        && floorBiome.is(lakeBiome);
                final double cluster = Mth.clamp(CLUSTER.eval(wx * 0.05, wz * 0.05) * 0.8 + 1.0, 0.2, 1.8);
                final double rate = biomeCovers ? BIOME_TOPUP : LIFE_PER_COLUMN;
                if (hash01(wx, wz, seed, 0xA11CEL) >= rate * cluster) continue;
                double pick = hash01(wx, wz, seed, 0xF100L) * total;
                int choice = 0;
                while (choice < WEIGHTS.length - 1 && (pick -= WEIGHTS[choice]) >= 0) choice++;
                final BlockState plant = plants[choice];
                final BlockPos at = new BlockPos(wx, y, wz);
                final int depth = surfaceY - y + 1;
                mut.setY(y);
                if (plant == null) {
                    if (depth < 2 || x < 2 || x > 13 || z < 2 || z > 13) continue;
                    ((EndLotusSeedBlock) EndBlocks.END_LOTUS_SEED).grow(
                            world,
                            RandomSource.create(hash64(wx, wz, seed, 0x1075L)),
                            at
                    );
                } else if (plant == lily) {
                    if (depth < 2) continue;
                    final BlockState bottom = lily.setValue(EndLilyBlock.SHAPE, TripleShape.BOTTOM);
                    if (!bottom.canSurvive(world, at)) continue;
                    BasePiece.setChunkBlock(chunk, mut, bottom);
                    for (int yy = y + 1; yy <= surfaceY; yy++) {
                        BasePiece.setChunkBlock(chunk, mut.setY(yy), lily.setValue(EndLilyBlock.SHAPE, TripleShape.MIDDLE));
                    }
                    BasePiece.setChunkBlock(chunk, mut.setY(surfaceY + 1), lily.setValue(EndLilyBlock.SHAPE, TripleShape.TOP));
                } else if (plant.is(EndBlocks.MENGER_SPONGE_WET)) {
                    if (!world.getBlockState(at.below()).is(CommonBlockTags.END_STONES)) continue;
                    BasePiece.setChunkBlock(chunk, mut, plant);
                } else {
                    if (!plant.canSurvive(world, at)) continue;
                    BasePiece.setChunkBlock(chunk, mut, plant);
                }
            }
        }
    }

    private static long hash64(int x, int z, long seed, long salt) {
        long h = (long) x * 0x9E3779B97F4A7C15L ^ (long) z * 0xC2B2AE3D27D4EB4FL
                ^ (seed + salt) * 0x165667B19E3779F9L;
        h ^= h >>> 31;
        h *= 0xBF58476D1CE4E5B9L;
        h ^= h >>> 29;
        return h;
    }

    private static double hash01(int x, int z, long seed, long salt) {
        return (hash64(x, z, seed, salt) >>> 11) * 0x1.0p-53;
    }
}

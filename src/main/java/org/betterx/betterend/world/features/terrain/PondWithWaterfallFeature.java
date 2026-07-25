package org.betterx.betterend.world.features.terrain;


import org.betterx.betterend.registry.EndBlocks;
import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.noise.OpenSimplexNoise;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Carves a shallow rim pond into the top of a small void-ring island and spills one or two
 * thin waterfalls over the island edge into the void. Used only by
 * {@link org.betterx.betterend.world.biome.air.WaterfallPondsBiome}.
 * <p>
 * Chunk-safety: the feature is placed once per chunk with NO {@code squarePlacement} modifier,
 * so {@link FeaturePlaceContext#origin()} is the chunk's SW corner. All geometry is centred on
 * the chunk centre ({@code origin + (8, 0, 8)}) and the pond radius is capped at {@value #MAX_RADIUS}
 * (+ noise jitter {@value #JITTER}), keeping the whole bowl inside the origin chunk. The single
 * spill point is clamped to the chunk bounds, and the waterfall descends in a fixed vertical
 * column, so no block is written outside the origin chunk. Placed water/rim blocks are marked
 * via {@link ChunkAccess#markPosForPostprocessing(BlockPos)} (the {@code LakePiece.fixWater}
 * precedent) so fluids settle without the aggressive draining of {@code BlockFixer.fixBlocks}
 * (which would delete the intentionally wall-hugging waterfall column).
 */
public class PondWithWaterfallFeature extends DefaultFeature {
    private static final BlockState END_STONE = Blocks.END_STONE.defaultBlockState();
    private static final BlockState END_MOSS = EndBlocks.END_MOSS.defaultBlockState();
    private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(6114);

    private static final int MAX_RADIUS = 6;
    private static final int MIN_RADIUS = 4;
    private static final double JITTER = 1.0;
    private static final int MAX_FLATNESS_VARIANCE = 3;
    private static final int MIN_TERRAIN_ABOVE_FLOOR = 5;
    private static final int MAX_WATERFALL_DROP = 12;

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        final WorldGenLevel world = ctx.level();
        final RandomSource random = ctx.random();
        final BlockPos origin = ctx.origin();

        final int minX = origin.getX();
        final int minZ = origin.getZ();
        final int maxX = minX + 15;
        final int maxZ = minZ + 15;
        final int centerX = minX + 8;
        final int centerZ = minZ + 8;

        // Island top at the placement column (WORLD_SURFACE_WG heightmap, pre-vegetation).
        final int topY = world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, centerX, centerZ) - 1;
        if (topY <= world.getMinY() + MIN_TERRAIN_ABOVE_FLOOR) {
            return false;
        }

        // Require a reasonably flat area: sample 4 offsets +-4 blocks around the centre.
        int hMin = topY;
        int hMax = topY;
        final int[][] offsets = {{4, 0}, {-4, 0}, {0, 4}, {0, -4}};
        for (int[] off : offsets) {
            final int h = world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, centerX + off[0], centerZ + off[1]) - 1;
            hMin = Math.min(hMin, h);
            hMax = Math.max(hMax, h);
        }
        if (hMax - hMin > MAX_FLATNESS_VARIANCE) {
            return false;
        }

        final int radius = MHelper.randRange(MIN_RADIUS, MAX_RADIUS, random);
        final int depth = MHelper.randRange(2, 3, random);
        final int waterLevel = topY - 1;

        final ChunkAccess chunk = world.getChunk(centerX >> 4, centerZ >> 4);
        final MutableBlockPos pos = new MutableBlockPos();

        // Carve the bowl: deeper at the centre, tapering to the rim.
        for (int dx = -radius; dx <= radius; dx++) {
            final int x = centerX + dx;
            if (x < minX || x > maxX) continue;
            for (int dz = -radius; dz <= radius; dz++) {
                final int z = centerZ + dz;
                if (z < minZ || z > maxZ) continue;

                final double dist = Math.sqrt(dx * dx + dz * dz);
                final double jitteredR = radius + NOISE.eval(x * 0.2, z * 0.2) * JITTER;
                if (dist > jitteredR) continue;

                final int localDepth = (int) Math.round(depth * (jitteredR - dist) / jitteredR);
                if (localDepth < 1) continue; // leave the outermost ring as an intact END_MOSS bank

                final int floorY = topY - localDepth;

                // Line the bowl floor: END_MOSS lip near the rim, end stone underneath.
                pos.set(x, floorY, z);
                BlocksHelper.setWithoutUpdate(world, pos, localDepth == 1 ? END_MOSS : END_STONE);
                pos.set(x, floorY - 1, z);
                BlocksHelper.setWithoutUpdate(world, pos, END_STONE);

                // Fill with water up to rim-1.
                for (int y = floorY + 1; y <= waterLevel; y++) {
                    pos.set(x, y, z);
                    BlocksHelper.setWithoutUpdate(world, pos, WATER);
                    chunk.markPosForPostprocessing(pos.immutable());
                }
                // Clear the lip above the water so the pond is open to the sky.
                for (int y = waterLevel + 1; y <= topY; y++) {
                    pos.set(x, y, z);
                    BlocksHelper.setWithoutUpdate(world, pos, AIR);
                }
            }
        }

        // 1-2 spill-over waterfalls in distinct rim directions.
        final Direction[] dirs = shuffledHorizontals(random);
        final int waterfalls = 1 + random.nextInt(2);
        int made = 0;
        for (int i = 0; i < dirs.length && made < waterfalls; i++) {
            if (spillWaterfall(world, chunk, dirs[i], centerX, centerZ, radius, waterLevel, minX, minZ, maxX, maxZ)) {
                made++;
            }
        }

        return true;
    }

    /**
     * Carves a 1-wide rim notch in {@code dir} down to {@code waterLevel} so the pond spills, then
     * follows the island's outer wall straight down, placing a 1-wide column of water source blocks
     * for up to {@value #MAX_WATERFALL_DROP} blocks or until the island underside is passed (no solid
     * horizontal neighbour), whichever comes first. All writes are clamped to the origin chunk.
     */
    private boolean spillWaterfall(
            WorldGenLevel world, ChunkAccess chunk, Direction dir,
            int centerX, int centerZ, int radius, int waterLevel,
            int minX, int minZ, int maxX, int maxZ
    ) {
        final MutableBlockPos pos = new MutableBlockPos();

        // Rim column: one block past the pond radius along dir (the outer spill lip).
        final int rimX = centerX + dir.getStepX() * (radius + 1);
        final int rimZ = centerZ + dir.getStepZ() * (radius + 1);
        final int spillX = Math.max(minX, Math.min(maxX, rimX));
        final int spillZ = Math.max(minZ, Math.min(maxZ, rimZ));

        // Carve the notch: open the lip at the spill column from waterLevel+1 upward a couple blocks
        // so the pond water can crest the bank.
        for (int y = waterLevel + 1; y <= waterLevel + 2; y++) {
            pos.set(spillX, y, spillZ);
            BlocksHelper.setWithoutUpdate(world, pos, AIR);
        }
        // Seed the spill lip with a water source at rim level.
        pos.set(spillX, waterLevel, spillZ);
        BlocksHelper.setWithoutUpdate(world, pos, WATER);
        chunk.markPosForPostprocessing(pos.immutable());

        // Follow the outer wall down.
        boolean placedAny = false;
        for (int k = 1; k <= MAX_WATERFALL_DROP; k++) {
            final int y = waterLevel - k;
            if (y <= world.getMinY() + 1) break;

            pos.set(spillX, y, spillZ);
            if (!hasSolidHorizontalNeighbour(world, spillX, y, spillZ, minX, minZ, maxX, maxZ)) {
                break; // passed the island underside -> let it fall into the void as particles
            }
            BlocksHelper.setWithoutUpdate(world, pos, WATER);
            chunk.markPosForPostprocessing(pos.immutable());
            placedAny = true;
        }
        return placedAny;
    }

    private boolean hasSolidHorizontalNeighbour(
            WorldGenLevel world, int x, int y, int z, int minX, int minZ, int maxX, int maxZ
    ) {
        final MutableBlockPos p = new MutableBlockPos();
        for (Direction d : BlocksHelper.HORIZONTAL) {
            final int nx = x + d.getStepX();
            final int nz = z + d.getStepZ();
            if (nx < minX || nx > maxX || nz < minZ || nz > maxZ) continue;
            p.set(nx, y, nz);
            final BlockState state = world.getBlockState(p);
            if (!state.isAir() && state.getFluidState().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static Direction[] shuffledHorizontals(RandomSource random) {
        final Direction[] dirs = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        for (int i = dirs.length - 1; i > 0; i--) {
            final int j = random.nextInt(i + 1);
            final Direction tmp = dirs[i];
            dirs[i] = dirs[j];
            dirs[j] = tmp;
        }
        return dirs;
    }
}

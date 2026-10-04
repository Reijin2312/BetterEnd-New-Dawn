package org.betterx.betterend.world.features.terrain;


import com.mojang.serialization.MapCodec;


import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.sdf.operator.SDFDisplacement;
import org.betterx.bclib.sdf.operator.SDFScale3D;
import org.betterx.bclib.sdf.primitive.SDFSphere;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.feature.api.WriteZone;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;


public class ObsidianBoulderFeature extends DefaultFeature {
    public static final MapCodec<ObsidianBoulderFeature> CODEC = MapCodec.unit(ObsidianBoulderFeature::new);

    @Override
    public MapCodec<ObsidianBoulderFeature> codec() {
        return CODEC;
    }
    @Override
    public boolean place(WorldGenLevel featureWorld, ChunkGenerator featureGenerator, RandomSource featureRandom, BlockPos featureOrigin) {
        final RandomSource random = featureRandom;
        BlockPos pos = featureOrigin;
        final WorldGenLevel world = featureWorld;
        final WriteZone zone = WriteZone.of(world);
        int initialX = zone.clampX(pos.getX() + random.nextInt(16));
        int initialZ = zone.clampZ(pos.getZ() + random.nextInt(16));
        pos = getPosOnSurface(
                world,
                new BlockPos(initialX, pos.getY(), initialZ)
        );
        if (!world.getBlockState(pos.below()).is(CommonBlockTags.END_STONES)) {
            return false;
        }

        int count = MHelper.randRange(1, 5, random);
        for (int i = 0; i < count; i++) {
            int x = zone.clampX(pos.getX() + random.nextInt(16) - 8);
            int z = zone.clampZ(pos.getZ() + random.nextInt(16) - 8);
            BlockPos p = getPosOnSurface(
                    world,
                    new BlockPos(x, pos.getY(), z)
            );
            makeBoulder(world, p, random, zone);
        }

        return true;
    }

    private void makeBoulder(WorldGenLevel world, BlockPos pos, RandomSource random, WriteZone zone) {
        if (!world.getBlockState(pos.below()).is(CommonBlockTags.END_STONES)) {
            return;
        }

        float radius = MHelper.randRange(1F, 5F, random);
        SDF sphere = new SDFSphere().setRadius(radius).setBlock(Blocks.OBSIDIAN);
        float sx = MHelper.randRange(0.7F, 1.3F, random);
        float sy = MHelper.randRange(0.7F, 1.3F, random);
        float sz = MHelper.randRange(0.7F, 1.3F, random);
        sphere = new SDFScale3D().setScale(sx, sy, sz).setSource(sphere);
        OpenSimplexNoise noise = new OpenSimplexNoise(random.nextLong());
        sphere = new SDFDisplacement().setFunction((vec) -> {
            return (float) (noise.eval(vec.x() * 0.2, vec.y() * 0.2, vec.z() * 0.2) * 1.5F);
        }).setSource(sphere);

        BlockState mossy = EndBlocks.MOSSY_OBSIDIAN.defaultBlockState();
        sphere.addPostProcess((info) -> {
            if (info.getStateUp().isAir() && random.nextFloat() > 0.1F) {
                return mossy;
            }
            return info.getState();
        }).setReplaceFunction((state) -> {
            return state.is(CommonBlockTags.END_STONES) || BlocksHelper.replaceableOrPlant(state);
        // The noise displacement (+/-1.5) can push the flood-fill a little past the sphere's own radius;
        // clip it to the write zone so it can't wander into unloaded neighbour chunks. See WriteZone.
        }).fillRecursive(world, pos, zone.toBoundingBox());
    }
}

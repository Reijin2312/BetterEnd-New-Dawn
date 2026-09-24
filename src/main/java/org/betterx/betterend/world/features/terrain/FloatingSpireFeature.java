package org.betterx.betterend.world.features.terrain;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.sdf.operator.SDFDisplacement;
import org.betterx.bclib.sdf.primitive.SDFSphere;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndBiomes;
import org.betterx.betterend.registry.features.EndConfiguredVegetation;
import org.betterx.wover.feature.api.WriteZone;
import org.betterx.betterend.world.biome.EndBiome;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;

import com.google.common.collect.Lists;

import java.util.List;

public class FloatingSpireFeature extends SpireFeature {
    public static final MapCodec<FloatingSpireFeature> CODEC = MapCodec.unit(FloatingSpireFeature::new);

    @Override
    public MapCodec<FloatingSpireFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel world, ChunkGenerator chunkGenerator, RandomSource random, BlockPos pos) {
        int minY = getYOnSurface(world, pos.getX(), pos.getZ());
        int y = minY > 57 ? MHelper.floor(MHelper.randRange(minY, minY * 2, random) * 0.5F + 32) : MHelper.randRange(
                64,
                192,
                random
        );
        pos = new BlockPos(pos.getX(), y, pos.getZ());

        SDF sdf = new SDFSphere().setRadius(MHelper.randRange(2, 3, random)).setBlock(Blocks.END_STONE);
        int count = MHelper.randRange(3, 5, random);

        for (int i = 0; i < count; i++) {
            float rMin = (i * 1.3F) + 2.5F;
            sdf = addSegment(sdf, MHelper.randRange(rMin, rMin + 1.5F, random), random);
        }
        for (int i = count - 1; i > 0; i--) {
            float rMin = (i * 1.3F) + 2.5F;
            sdf = addSegment(sdf, MHelper.randRange(rMin, rMin + 1.5F, random), random);
        }

        OpenSimplexNoise noise = new OpenSimplexNoise(random.nextLong());
        sdf = new SDFDisplacement().setFunction((vec) -> {
            return (float) (Math.abs(noise.eval(
                    vec.x() * 0.1,
                    vec.y() * 0.1,
                    vec.z() * 0.1
            )) * 3F + Math.abs(noise.eval(
                    vec.x() * 0.3,
                    vec.y() * 0.3 + 100,
                    vec.z() * 0.3
            )) * 1.3F);
        }).setSource(sdf);
        final BlockPos center = pos;
        List<BlockPos> support = Lists.newArrayList();
        sdf.setReplaceFunction(REPLACE).addPostProcess((info) -> {
            if (info.getStateUp().isAir()) {
                if (random.nextInt(16) == 0) {
                    support.add(info.getPos().above());
                }
                return EndBiome.findTopMaterial(
                        world,
                        info.getPos()
                );//world.getBiome(info.getPos()).value().getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial();
            } else if (info.getState(Direction.UP, 3).isAir()) {
                return EndBiome.findUnderMaterial(world, info.getPos());
//				return world.getBiome(info.getPos())
//							.getGenerationSettings()
//							.getSurfaceBuilderConfig()
//							.getUnderMaterial();
            }
            return info.getState();
        });
        sdf.fillRecursive(world, center, WriteZone.of(world).toBoundingBox());

        for (BlockPos bpos : support) {
            final Holder<Biome> biome = world.getBiome(bpos);
            if (biome.is(EndBiomes.BLOSSOMING_SPIRES.key)) {
                EndConfiguredVegetation.TENANEA_BUSH.placeInWorld(world, bpos, random);
            }
        }

        return true;
    }
}

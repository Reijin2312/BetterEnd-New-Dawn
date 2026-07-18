package org.betterx.betterend.world.features.trees;

import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.sdf.PosInfo;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.sdf.operator.SDFDisplacement;
import org.betterx.bclib.sdf.operator.SDFSubtraction;
import org.betterx.bclib.sdf.operator.SDFTranslate;
import org.betterx.bclib.sdf.primitive.SDFSphere;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.bclib.util.SplineHelper;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import org.joml.Vector3f;

import java.util.List;
import java.util.function.Function;

public class LacugroveFeature extends DefaultFeature {
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        final BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        if (!world.getBlockState(pos.below()).is(BlockTags.NYLIUM)) return false;

        float size = MHelper.randRange(15, 25, random);
        List<Vector3f> spline = SplineHelper.makeSpline(0, 0, 0, 0, size, 0, 6);
        SplineHelper.offsetParts(spline, random, 1F, 0, 1F);

        if (!SplineHelper.canGenerate(spline, pos, world, replaceFunc())) {
            return false;
        }

        OpenSimplexNoise noise = new OpenSimplexNoise(random.nextLong());

        float radius = MHelper.randRange(6F, 8F, random);
        radius *= (size - 15F) / 20F + 1F;
        Vector3f center = spline.get(4);
        leavesBall(world, pos.offset((int) center.x(), (int) center.y(), (int) center.z()), radius, random, noise);

        radius = MHelper.randRange(1.2F, 1.8F, random);
        SDF function = SplineHelper.buildSDF(
                spline,
                radius,
                0.7F,
                (bpos) -> EndBlocks.LACUGROVE.getBark().defaultBlockState()
        );

        function.setReplaceFunction(replaceFunc());
        function.addPostProcess(postProcessFunc());
        function.fillRecursive(world, pos);

        spline = spline.subList(4, 6);
        SplineHelper.fillSpline(spline, world, EndBlocks.LACUGROVE.getBark().defaultBlockState(), pos, replaceFunc());

        MutableBlockPos mut = new MutableBlockPos();
        int offset = random.nextInt(2);
        for (int i = 0; i < 100; i++) {
            double px = pos.getX() + MHelper.randRange(-5, 5, random);
            double pz = pos.getZ() + MHelper.randRange(-5, 5, random);
            mut.setX(MHelper.floor(px + 0.5));
            mut.setZ(MHelper.floor(pz + 0.5));
            if (((mut.getX() + mut.getZ() + offset) & 1) == 0) {
                double distance = 3.5 - MHelper.length(px - pos.getX(), pz - pos.getZ()) * 0.5;
                if (distance > 0) {
                    int minY = MHelper.floor(pos.getY() - distance * 0.5);
                    int maxY = MHelper.floor(pos.getY() + distance + random.nextDouble());
                    boolean generate = false;
                    for (int y = minY; y < maxY; y++) {
                        mut.setY(y);
                        if (world.getBlockState(mut).is(CommonBlockTags.END_STONES)) {
                            generate = true;
                            break;
                        }
                    }
                    if (generate) {
                        int top = maxY - 1;
                        for (int y = top; y >= minY; y--) {
                            mut.setY(y);
                            BlockState state = world.getBlockState(mut);
                            if (BlocksHelper.replaceableOrPlant(state) || state.is(CommonBlockTags.END_STONES)) {
                                BlocksHelper.setWithoutUpdate(
                                        world,
                                        mut,
                                        y == top ? EndBlocks.LACUGROVE.getBark() : EndBlocks.LACUGROVE.getBark()
                                );
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    private void leavesBall(
            WorldGenLevel world,
            BlockPos pos,
            float radius,
            RandomSource random,
            OpenSimplexNoise noise
    ) {
        SDF sphere = new SDFSphere().setRadius(radius)
                                    .setBlock(EndBlocks.LACUGROVE_LEAVES.defaultBlockState()
                                                                        .setValue(LeavesBlock.DISTANCE, 6));
        sphere = new SDFDisplacement().setFunction((vec) -> (float) noise.eval(
                vec.x() * 0.2,
                vec.y() * 0.2,
                vec.z() * 0.2
        ) * 3).setSource(sphere);
        sphere = new SDFDisplacement().setFunction((vec) -> random.nextFloat() * 3F - 1.5F).setSource(sphere);
        sphere = new SDFSubtraction().setSourceA(sphere)
                                     .setSourceB(new SDFTranslate().setTranslate(0, -radius - 2, 0).setSource(sphere));
        MutableBlockPos mut = new MutableBlockPos();
        sphere.addPostProcess((info) -> {
            if (random.nextInt(5) == 0) {
                for (Direction dir : Direction.values()) {
                    BlockState state = info.getState(dir, 2);
                    if (state.isAir()) {
                        return info.getState();
                    }
                }
                info.setState(EndBlocks.LACUGROVE.getBark().defaultBlockState());
                for (int x = -6; x < 7; x++) {
                    int ax = Math.abs(x);
                    mut.setX(x + info.getPos().getX());
                    for (int z = -6; z < 7; z++) {
                        int az = Math.abs(z);
                        mut.setZ(z + info.getPos().getZ());
                        for (int y = -6; y < 7; y++) {
                            int ay = Math.abs(y);
                            int d = ax + ay + az;
                            if (d < 7) {
                                mut.setY(y + info.getPos().getY());
                                BlockState state = info.getState(mut);
                                if (state.getBlock() instanceof LeavesBlock) {
                                    int distance = state.getValue(LeavesBlock.DISTANCE);
                                    if (d < distance) {
                                        info.setState(mut, state.setValue(LeavesBlock.DISTANCE, d));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return info.getState();
        });
        sphere.fillRecursiveIgnore(world, pos, ignoreFunc());

        if (radius > 5) {
            int count = (int) (radius * 2.5F);
            for (int i = 0; i < count; i++) {
                BlockPos p = pos.offset(
                        (int) (random.nextGaussian() * 1),
                        (int) (random.nextGaussian() * 1),
                        (int) (random.nextGaussian() * 1)
                );
                boolean place = true;
                for (Direction d : Direction.values()) {
                    BlockState state = world.getBlockState(p.relative(d));
                    if (!EndBlocks.LACUGROVE.isTreeLog(state) && !state.is(EndBlocks.LACUGROVE_LEAVES)) {
                        place = false;
                        break;
                    }
                }
                if (place) {
                    BlocksHelper.setWithoutUpdate(world, p, EndBlocks.LACUGROVE.getBark());
                }
            }
        }

        BlocksHelper.setWithoutUpdate(world, pos, EndBlocks.LACUGROVE.getBark());
    }

    private Function<BlockState, Boolean> replaceFunc() {
        return (state) -> {
            if (EndBlocks.LACUGROVE.isTreeLog(state)) {
                return true;
            }
            if (state.getBlock() == EndBlocks.LACUGROVE_LEAVES) {
                return true;
            }
            return BlocksHelper.replaceableOrPlant(state);
        };
    }

    private Function<BlockState, Boolean> ignoreFunc() {
        return EndBlocks.LACUGROVE::isTreeLog;
    }

    private Function<PosInfo, BlockState> postProcessFunc() {
        return (info) -> {
            if (EndBlocks.LACUGROVE.isTreeLog(info.getStateUp()) && EndBlocks.LACUGROVE.isTreeLog(info.getStateDown())) {
                return EndBlocks.LACUGROVE.getBark().defaultBlockState();
            }
            return info.getState();
        };
    }
}

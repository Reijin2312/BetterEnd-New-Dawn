package org.betterx.betterend.world.features.terrain;

import com.mojang.serialization.MapCodec;
import org.betterx.bclib.blocks.StalactiteBlock;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

public class StalactiteFeature implements Feature {
    public static final MapCodec<StalactiteFeature> CODEC = StalactiteFeatureConfig.CODEC.xmap(StalactiteFeature::new, f -> f.config);
    public final StalactiteFeatureConfig config;

    public StalactiteFeature(StalactiteFeatureConfig config) {
        this.config = config;
    }

    @Override
    public MapCodec<StalactiteFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel world, ChunkGenerator generator, RandomSource random, BlockPos pos) {
        final StalactiteFeatureConfig cfg = config;
        if (!cfg.allowedGround.test(world, cfg.ceiling ? pos.above() : pos.below())) {
            return false;
        }

        MutableBlockPos mut = new MutableBlockPos().set(pos);
        int height = random.nextInt(16);
        int dir = cfg.ceiling ? -1 : 1;
        boolean stalagnate = false;

        for (int i = 1; i <= height; i++) {
            mut.setY(pos.getY() + i * dir);
            BlockState state = world.getBlockState(mut);
            if (!state.canBeReplaced()) {
                stalagnate = state.is(CommonBlockTags.END_STONES);
                height = i;
                break;
            }
        }

        if (!stalagnate && height > 7) {
            height = random.nextInt(8);
        }

        float center = height * 0.5F;
        for (int i = 0; i < height; i++) {
            mut.setY(pos.getY() + i * dir);
            int size = stalagnate ? Mth.clamp((int) (Mth.abs(i - center) + 1), 1, 7) : height - i - 1;
            boolean waterlogged = !world.getFluidState(mut).isEmpty();
            BlockState base = cfg.block.getState(world, random, mut)
                                       .setValue(StalactiteBlock.SIZE, size)
                                       .setValue(BlockStateProperties.WATERLOGGED, waterlogged);
            BlockState state = stalagnate ? base.setValue(
                    StalactiteBlock.IS_FLOOR,
                    dir > 0 ? i < center : i > center
            ) : base.setValue(StalactiteBlock.IS_FLOOR, dir > 0);
            BlocksHelper.setWithoutUpdate(world, mut, state);
        }

        return true;
    }
}

package org.betterx.betterend.world.surface;

import org.betterx.betterend.mixin.common.MaterialRuleContextAccessor;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import org.betterx.wover.surface.api.noise.NumericProvider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.synth.Noise;

public class VerticalBandNoiseCondition implements NumericProvider {
    public static final VerticalBandNoiseCondition DEFAULT = new VerticalBandNoiseCondition(
            Noises.CLAY_BANDS_OFFSET,
            1.5,
            5.0,
            6.0,
            1.3
    );
    public static final MapCodec<VerticalBandNoiseCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(o -> o.noise),
                    Codec.DOUBLE.fieldOf("offset_scale").orElse(4.0).forGetter(o -> o.offsetScale),
                    Codec.DOUBLE.fieldOf("band_scale").orElse(4.0).forGetter(o -> o.bandScale),
                    Codec.DOUBLE.fieldOf("xz_scale").orElse(6.0).forGetter(o -> o.xzScale),
                    Codec.DOUBLE.fieldOf("y_scale").orElse(1.3).forGetter(o -> o.yScale)
            )
            .apply(instance, VerticalBandNoiseCondition::new));

    private final ResourceKey<NormalNoise> noise;
    private final double offsetScale;
    private final double bandScale;
    private final double xzScale;
    private final double yScale;

    public VerticalBandNoiseCondition(
            ResourceKey<NormalNoise> noise,
            double offsetScale,
            double bandScale,
            double xzScale,
            double yScale
    ) {
        this.noise = noise;
        this.offsetScale = offsetScale;
        this.bandScale = bandScale;
        this.xzScale = xzScale;
        this.yScale = yScale;
    }

    @Override
    public int getNumber(MaterialRuleContext context) {
        final Noise normalNoise = ((MaterialRuleContextAccessor) (Object) context)
                .be_getRandomState()
                .getOrCreateNoise(this.noise);
        double offset = normalNoise.get(
                (double) context.blockX() * xzScale,
                context.blockY() * yScale * 10,
                (double) context.blockZ() * xzScale
        ) * offsetScale;


        return (int) (context.blockY() / bandScale + offset);
    }

    @Override
    public MapCodec<? extends NumericProvider> pcodec() {
        return CODEC;
    }
}

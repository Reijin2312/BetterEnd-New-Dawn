package org.betterx.betterend.mixin.common;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.block.api.BlockProperties.TripleShape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

@Mixin(value = ServerPlayer.class, priority = 200)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    private static Direction[] horizontal;
    private static Constructor<?> respawnPosAngleConstructor;

    @Inject(method = "findRespawnAndUseSpawnBlock", at = @At(value = "HEAD"), cancellable = true, remap = false, require = 0)
    private static void be_findRespawnAndUseSpawnBlock(
            ServerLevel world,
            ServerPlayer.RespawnConfig respawnConfig,
            boolean bl,
            CallbackInfoReturnable<Optional<?>> info
    ) {
        if (respawnConfig == null || respawnConfig.respawnData() == null) {
            return;
        }
        BlockPos pos = respawnConfig.respawnData().pos();
        float angle = respawnConfig.respawnData().yaw();
        BlockState blockState = world.getBlockState(pos);
        if (blockState.is(EndBlocks.RESPAWN_OBELISK)) {
            info.setReturnValue(be_obeliskRespawnPosition(world, pos, angle, blockState));
            info.cancel();
        }
    }

    private static Optional<?> be_obeliskRespawnPosition(
            ServerLevel world,
            BlockPos pos,
            float angle,
            BlockState state
    ) {
        if (state.getValue(BlockProperties.TRIPLE_SHAPE) == TripleShape.TOP) {
            pos = pos.below(2);
        } else if (state.getValue(BlockProperties.TRIPLE_SHAPE) == TripleShape.MIDDLE) {
            pos = pos.below();
        }
        if (horizontal == null) {
            horizontal = BlocksHelper.makeHorizontal();
        }
        MHelper.shuffle(horizontal, world.getRandom());
        for (Direction dir : horizontal) {
            BlockPos p = pos.relative(dir);
            BlockState state2 = world.getBlockState(p);
            if (!state2.blocksMotion() && state2.getCollisionShape(world, pos).isEmpty()) {
                Object respawnPosition = be_createRespawnPosition(
                        Vec3.atLowerCornerOf(p).add(0.5, 0, 0.5),
                        angle
                );
                return Optional.ofNullable(respawnPosition);
            }
        }
        return Optional.empty();
    }

    private static Object be_createRespawnPosition(Vec3 position, float yaw) {
        try {
            if (respawnPosAngleConstructor == null) {
                for (Class<?> nestedClass : ServerPlayer.class.getDeclaredClasses()) {
                    try {
                        Constructor<?> constructor = nestedClass.getDeclaredConstructor(
                                Vec3.class,
                                float.class,
                                float.class
                        );
                        constructor.setAccessible(true);
                        respawnPosAngleConstructor = constructor;
                        break;
                    } catch (NoSuchMethodException ignored) {
                    }
                }
            }
            return respawnPosAngleConstructor == null
                    ? null
                    : respawnPosAngleConstructor.newInstance(position, yaw, 0.0F);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | SecurityException ignored) {
            return null;
        }
    }
}

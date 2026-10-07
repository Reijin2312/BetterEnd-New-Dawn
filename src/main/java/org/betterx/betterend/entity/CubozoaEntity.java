package org.betterx.betterend.entity;

import org.betterx.betterend.registry.EndBiomes;
import org.betterx.betterend.registry.EndItems;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CubozoaEntity extends AbstractSchoolingFish {
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(
            CubozoaEntity.class,
            EntityDataSerializers.INT
    );
    private static final EntityDataAccessor<Integer> SCALE = SynchedEntityData.defineId(
            CubozoaEntity.class,
            EntityDataSerializers.INT
    );

    public CubozoaEntity(EntityType<CubozoaEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor world,
            DifficultyInstance difficulty,
            MobSpawnType spawnReason,
            @Nullable SpawnGroupData entityData
    ) {
        SpawnGroupData data = super.finalizeSpawn(world, difficulty, spawnReason, entityData);

        Holder<Biome> biome = world.getBiome(blockPosition());
        if (biome.is(EndBiomes.SULPHUR_SPRINGS.key)) {
            this.entityData.set(VARIANT, 1);
        }

        this.refreshDimensions();
        return data;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
        builder.define(SCALE, this.getRandom().nextInt(16));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", (byte) getVariant());
        tag.putInt("Scale", this.entityData.get(SCALE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Variant")) {
            this.entityData.set(VARIANT, tag.getInt("Variant"));
        }
        if (tag.contains("Scale")) {
            this.entityData.set(SCALE, tag.getInt("Scale"));
        }
    }

    @Override
    public void saveToBucketTag(ItemStack itemStack) {
        super.saveToBucketTag(itemStack);
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, itemStack, (tag) -> {
            tag.putInt("Variant", entityData.get(VARIANT));
            tag.putInt("Scale", entityData.get(SCALE));
        });
    }

    @Override
    public @NotNull ItemStack getBucketItemStack() {
        ItemStack bucket = EndItems.BUCKET_CUBOZOA.getDefaultInstance();
//        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, bucket, (tag) -> {
//            tag.putByte("Variant", entityData.get(VARIANT));
//            tag.putByte("Scale", entityData.get(SCALE));
//        });
        return bucket;
    }

    public static AttributeSupplier.@NotNull Builder createMobAttributes() {
        return LivingEntity
                .createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 2.0)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5);
    }

    public int getVariant() {
        return (int) this.entityData.get(VARIANT);
    }

    public float getScale() {
        return this.entityData.get(SCALE) / 32F + 0.75F;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height() * 0.5F;
    }

    @Override
    protected void dropFromLootTable(DamageSource source, boolean causedByPlayer) {
        super.dropFromLootTable(source, causedByPlayer);
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.SALMON_FLOP;
    }

    @Override
    public void playerTouch(Player player) {
        if (player instanceof ServerPlayer && player.hurt(player.damageSources().mobAttack(this), 0.5F)) {
            if (!this.isSilent()) {
                ((ServerPlayer) player).connection.send(new ClientboundGameEventPacket(
                        ClientboundGameEventPacket.PUFFER_FISH_STING,
                        0.0F
                ));
            }
            if (random.nextBoolean()) {
                player.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 0));
            }
        }
    }
}

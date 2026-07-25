package org.betterx.betterend.blocks.entities;

import org.betterx.betterend.blocks.FlowerPotBlock;
import org.betterx.betterend.registry.EndBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class FlowerPotBlockEntity extends BlockEntity {
    private Optional<ResourceKey<Block>> plant = Optional.empty();
    private Optional<ResourceKey<Block>> soil = Optional.empty();

    public FlowerPotBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(EndBlockEntities.FLOWER_POT, blockPos, blockState);
    }

    public Optional<ResourceKey<Block>> getPlant() {
        return plant;
    }

    public Optional<ResourceKey<Block>> getSoil() {
        return soil;
    }

    public Optional<Block> getPlantBlock() {
        return plant.map(BuiltInRegistries.BLOCK::get);
    }

    public Optional<Block> getSoilBlock() {
        return soil.map(BuiltInRegistries.BLOCK::get);
    }

    public void setPlant(Optional<ResourceKey<Block>> plant) {
        this.plant = plant;
        setChanged();
    }

    public void setSoil(Optional<ResourceKey<Block>> soil) {
        this.soil = soil;
        setChanged();
    }

    @Override
    public void setChanged() {
        if (level != null && !level.isClientSide()) {
            BlockState state = level.getBlockState(worldPosition);
            if (state.getBlock() instanceof FlowerPotBlock) {
                int light = getPlantBlock()
                        .map(block -> block.defaultBlockState().getLightEmission() / 5)
                        .orElse(0);
                state = state.setValue(FlowerPotBlock.POT_LIGHT, light);
                level.setBlockAndUpdate(worldPosition, state);
                level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
            }
            level.blockEntityChanged(worldPosition);
        }
        super.setChanged();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        plant = readBlockKey(tag, "plant");
        soil = readBlockKey(tag, "soil");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        plant.ifPresent(key -> tag.putString("plant", key.location().toString()));
        soil.ifPresent(key -> tag.putString("soil", key.location().toString()));
        super.saveAdditional(tag, provider);
    }

    private static Optional<ResourceKey<Block>> readBlockKey(CompoundTag tag, String name) {
        if (!tag.contains(name)) {
            return Optional.empty();
        }
        return Optional.of(ResourceKey.create(Registries.BLOCK, ResourceLocation.parse(tag.getString(name))));
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }
}

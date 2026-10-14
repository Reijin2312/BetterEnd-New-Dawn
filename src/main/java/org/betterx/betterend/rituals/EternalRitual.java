package org.betterx.betterend.rituals;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.advancements.BECriteria;
import org.betterx.betterend.blocks.EndPortalBlock;
import org.betterx.betterend.blocks.EternalPedestal;
import org.betterx.betterend.blocks.RunedFlavolite;
import org.betterx.betterend.blocks.basis.PedestalBlock;
import org.betterx.betterend.blocks.entities.EternalPedestalEntity;
import org.betterx.betterend.network.RitualUpdate;
import org.betterx.betterend.portal.PortalBuilder;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndPortals;
import org.betterx.wover.block.api.BlockProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.border.WorldBorder;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class EternalRitual {
    public static final int ACTIVATION_CHARGE_TICKS = 60;
    private final static List<Point> PEDESTAL_POSITIONS = List.of(
            new Point(-4, -5),
            new Point(-4, 5),
            new Point(-6, 0),
            new Point(4, -5),
            new Point(4, 5),
            new Point(6, 0)
    );

    public static List<Point> pedestalOffsets() {
        return PEDESTAL_POSITIONS;
    }

    private final static Block PEDESTAL = EndBlocks.ETERNAL_PEDESTAL;
    public final static BooleanProperty ACTIVE = BlockProperties.ACTIVE;

    private Level world;
    private Direction.Axis axis;
    private ResourceLocation targetWorldId;
    private BlockPos center;
    private BlockPos exit;
    private boolean active = false;
    private boolean willActivate = false;
    private ResourceLocation pendingKeyItemId;
    private BlockPos pendingStartPosition;

    public EternalRitual(Level world) {
        this.world = world;
    }

    public EternalRitual(Level world, BlockPos initial) {
        this(world);
        this.configure(initial);
    }

    public BlockPos getCenter() {
        return center;
    }

    public Direction.Axis getAxis() {
        return axis;
    }

    public void setWorld(Level world) {
        this.world = world;
    }

    public Level getWorld() {
        return world;
    }

    public int getPedestalItemMask() {
        if (world == null || center == null || axis == null) return 0;
        Direction moveX = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        Direction moveY = axis == Direction.Axis.X ? Direction.NORTH : Direction.EAST;
        int mask = 0;
        for (int i = 0; i < PEDESTAL_POSITIONS.size(); i++) {
            Point offset = PEDESTAL_POSITIONS.get(i);
            BlockPos pedestalPos = center.relative(moveX, offset.x).relative(moveY, offset.y);
            if (world.getBlockEntity(pedestalPos) instanceof EternalPedestalEntity pedestal && !pedestal.isEmpty()) {
                mask |= 1 << i;
            }
        }
        return mask;
    }

    @Nullable
    public ResourceLocation getTargetWorldId() {
        return targetWorldId;
    }

    private boolean isInvalid() {
        return world == null || world.isClientSide() || center == null || axis == null;
    }

    public void checkStructure(Player player) {
        if (isInvalid()) return;
        Direction moveX, moveY;
        if (Direction.Axis.X == axis) {
            moveX = Direction.EAST;
            moveY = Direction.NORTH;
        } else {
            moveX = Direction.SOUTH;
            moveY = Direction.EAST;
        }
        boolean valid = checkFrame(world, center.below());
        Item item = null;
        for (Point pos : PEDESTAL_POSITIONS) {
            BlockPos.MutableBlockPos checkPos = center.mutable();
            checkPos.move(moveX, pos.x).move(moveY, pos.y);
            valid &= isActive(checkPos);
            if (valid) {
                EternalPedestalEntity pedestal = (EternalPedestalEntity) world.getBlockEntity(checkPos);
                if (pedestal != null) {
                    Item pItem = pedestal.getItem(0).getItem();
                    if (item == null) {
                        item = pItem;
                    } else if (!item.equals(pItem)) {
                        valid = false;
                    }
                }
            }
        }
        if (valid && item != null) {
            activatePortal(player, item);
            if (player instanceof ServerPlayer sp) {
                BECriteria.PORTAL_ON.trigger(sp);
            }
        }
    }

    public void updateActiveStateOnPedestals() {
        if (world == null) return;
        updateActiveStateOnPedestals(center, axis, active, willActivate, world, this);
        if (world instanceof ServerLevel serverLevel) {
            new RitualUpdate(this).sendToClient(serverLevel);
        }

    }

    public static void updateActiveStateOnPedestals(
            BlockPos center,
            Direction.Axis axis,
            boolean active,
            boolean willActivate,
            Level world,
            EternalRitual fallback
    ) {
        updateActiveStateOnPedestals(center, axis, active, willActivate, world, fallback, null, -1);
    }

    public static void updateActiveStateOnPedestals(
            BlockPos center,
            Direction.Axis axis,
            boolean active,
            boolean willActivate,
            Level world,
            EternalRitual fallback,
            Item clientKeyItem,
            int pedestalItemMask
    ) {
        Direction moveX, moveY;
        if (Direction.Axis.X == axis) {
            moveX = Direction.EAST;
            moveY = Direction.NORTH;
        } else {
            moveX = Direction.SOUTH;
            moveY = Direction.EAST;
        }


        for (int pedestalIndex = 0; pedestalIndex < PEDESTAL_POSITIONS.size(); pedestalIndex++) {
            Point pos = PEDESTAL_POSITIONS.get(pedestalIndex);
            BlockPos.MutableBlockPos checkPos = center.mutable();
            checkPos.move(moveX, pos.x).move(moveY, pos.y);
            if (world.getBlockEntity(checkPos) instanceof EternalPedestalEntity pedestal) {
                if (world.isClientSide() && pedestalItemMask >= 0) {
                    boolean hasItem = (pedestalItemMask & (1 << pedestalIndex)) != 0;
                    pedestal.setClientItem(hasItem && clientKeyItem != null ? new ItemStack(clientKeyItem) : ItemStack.EMPTY);
                    BlockState state = world.getBlockState(checkPos);
                    if (state.is(PEDESTAL)) {
                        state = state.setValue(PedestalBlock.HAS_ITEM, hasItem);
                        if (!hasItem) {
                            state = state.setValue(PedestalBlock.HAS_LIGHT, false)
                                         .setValue(EternalPedestal.ACTIVATED, false);
                        } else if (active || willActivate) {
                            state = state.setValue(PedestalBlock.HAS_LIGHT, true)
                                         .setValue(EternalPedestal.ACTIVATED, true);
                        }
                        world.setBlockAndUpdate(checkPos, state);
                    }
                }
                if (pedestal.hasRitual()) {
                    if (fallback == null) fallback = pedestal.getRitual();
                    pedestal.getRitual().active = active;
                    pedestal.getRitual().willActivate = willActivate;
                } else {
                    if (fallback == null) {
                        fallback = new EternalRitual(world);
                        fallback.center = center;
                        fallback.axis = axis;
                        fallback.willActivate = willActivate;
                        fallback.active = active;
                    }
                    pedestal.linkRitual(fallback);
                }
            }
        }
    }

    @Nullable
    public Item getKeyItem() {
        if (world == null || center == null || axis == null) return null;
        Direction moveX = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        Direction moveY = axis == Direction.Axis.X ? Direction.NORTH : Direction.EAST;
        for (Point pos : PEDESTAL_POSITIONS) {
            BlockPos checkPos = center.relative(moveX, pos.x).relative(moveY, pos.y);
            if (world.getBlockEntity(checkPos) instanceof EternalPedestalEntity pedestal && !pedestal.isEmpty()) {
                return pedestal.getItem(0).getItem();
            }
        }
        return null;
    }

    private boolean checkFrame(Level world, BlockPos framePos) {
        Direction moveDir = Direction.Axis.X == axis ? Direction.NORTH : Direction.EAST;
        boolean valid = true;
        for (Point point : PortalBuilder.FRAME_POSITIONS) {
            BlockPos pos = framePos.mutable().move(moveDir, point.x).move(Direction.UP, point.y);
            BlockState state = world.getBlockState(pos);
            valid &= state.getBlock() instanceof RunedFlavolite;
            pos = framePos.mutable().move(moveDir, -point.x).move(Direction.UP, point.y);
            state = world.getBlockState(pos);
            valid &= state.getBlock() instanceof RunedFlavolite;
        }
        return valid;
    }

    public boolean isActive() {
        return active;
    }

    public boolean willActivate() {
        return willActivate;
    }

    private void activatePortal(Player player, Item keyItem) {
        if (active || willActivate) return;
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(keyItem);
        int portalId = EndPortals.getPortalIdByItem(itemId);
        Level targetWorld = getTargetWorld(portalId);
        pendingKeyItemId = itemId;
        pendingStartPosition = PortalBuilder.getStartingPos(
                world,
                targetWorld,
                player,
                targetWorld.getWorldBorder()
        );
        willActivate = true;
        updateActiveStateOnPedestals();
        world.scheduleTick(getActivationPedestalPos(), PEDESTAL, ACTIVATION_CHARGE_TICKS);
    }

    public void finishActivation() {
        if (!willActivate || active || isInvalid() || pendingKeyItemId == null || pendingStartPosition == null) return;
        Item keyItem = getKeyItem();
        if (keyItem == null
                || !pendingKeyItemId.equals(BuiltInRegistries.ITEM.getKey(keyItem))
                || !hasValidStructureFor(keyItem)) {
            cancelActivation();
            return;
        }

        int portalId = EndPortals.getPortalIdByItem(pendingKeyItemId);
        Level targetWorld = getTargetWorld(portalId);
        ResourceLocation worldId = targetWorld.dimension().location();
        try {
            if (exit == null) {
                initPortal(pendingStartPosition, worldId, portalId);
            } else {
                if (!worldId.equals(targetWorldId)) {
                    initPortal(pendingStartPosition, worldId, portalId);
                } else if (!checkFrame(targetWorld, exit.below())) {
                    Direction.Axis portalAxis = (Direction.Axis.X == axis) ? Direction.Axis.Z : Direction.Axis.X;
                    PortalBuilder.generatePortal(targetWorld, exit, portalAxis, portalId);
                }
                activatePortal(targetWorld, exit, portalId);
            }
            activatePortal(world, center, portalId);
            if (world instanceof ServerLevel serverLevel) {
                doEffects(serverLevel, center);
            }
            willActivate = false;
            active = true;
            clearPendingActivation();
            updateActiveStateOnPedestals();
        } catch (Exception ex) {
            BetterEnd.LOGGER.error("Create End portals error.", ex);
            removePortal(targetWorld, exit);
            removePortal(world, center);
            willActivate = false;
            active = false;
            clearPendingActivation();
            updateActiveStateOnPedestals();
        }
    }

    public void cancelActivation() {
        if (!willActivate) return;
        willActivate = false;
        clearPendingActivation();
        updateActiveStateOnPedestals();
    }

    private void clearPendingActivation() {
        pendingKeyItemId = null;
        pendingStartPosition = null;
    }

    private BlockPos getActivationPedestalPos() {
        Direction moveX = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        Direction moveY = axis == Direction.Axis.X ? Direction.NORTH : Direction.EAST;
        Point offset = PEDESTAL_POSITIONS.get(0);
        return center.relative(moveX, offset.x).relative(moveY, offset.y);
    }

    private boolean hasValidStructureFor(Item keyItem) {
        if (!checkFrame(world, center.below())) return false;
        Direction moveX = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        Direction moveY = axis == Direction.Axis.X ? Direction.NORTH : Direction.EAST;
        for (Point offset : PEDESTAL_POSITIONS) {
            BlockPos pedestalPos = center.relative(moveX, offset.x).relative(moveY, offset.y);
            if (!isActive(pedestalPos)
                    || !(world.getBlockEntity(pedestalPos) instanceof EternalPedestalEntity pedestal)
                    || pedestal.isEmpty()
                    || pedestal.getItem(0).getItem() != keyItem) {
                return false;
            }
        }
        return true;
    }

    private void initPortal(BlockPos startPosition, ResourceLocation worldId, int portalId) {
        targetWorldId = worldId;
        if (world instanceof ServerLevel sourceWorld) {
            ServerLevel targetLevel = (ServerLevel) getTargetWorld(portalId);
            PortalBuilder builder = new PortalBuilder(world, targetLevel);
            final WorldBorder worldBorder = targetLevel.getWorldBorder();
            final Optional<PortalBuilder.FoundPortalRect> foundRectangle = builder.findPortalAround(
                    this.center,
                    worldBorder
            );

            if (foundRectangle.isEmpty()) {
                Optional<BlockPos> centerPos;
                centerPos = builder.createPortal(
                        startPosition,
                        axis,
                        portalId
                );
                centerPos.ifPresent(blockPos -> this.exit = blockPos);
            }
        }
    }

    private void doEffects(ServerLevel serverWorld, BlockPos center) {
        Direction moveX, moveY;
        if (Direction.Axis.X == axis) {
            moveX = Direction.EAST;
            moveY = Direction.NORTH;
        } else {
            moveX = Direction.SOUTH;
            moveY = Direction.EAST;
        }
        for (Point pos : PEDESTAL_POSITIONS) {
            BlockPos.MutableBlockPos p = center.mutable();
            p.move(moveX, pos.x).move(moveY, pos.y);
            serverWorld.sendParticles(
                    ParticleTypes.PORTAL,
                    p.getX() + 0.5, p.getY() + 1.5, p.getZ() + 0.5,
                    20,
                    0, 0, 0, 1
            );
            serverWorld.sendParticles(
                    ParticleTypes.REVERSE_PORTAL,
                    p.getX() + 0.5, p.getY() + 1.5, p.getZ() + 0.5,
                    20,
                    0, 0, 0, 0.3
            );
        }
        serverWorld.playSound(null, center, SoundEvents.END_PORTAL_SPAWN, SoundSource.NEUTRAL, 16, 1);
    }

    private void activatePortal(Level world, BlockPos center, int portalId) {
        BlockPos framePos = center.below();
        Direction moveDir = Direction.Axis.X == axis ? Direction.NORTH : Direction.EAST;
        BlockState frame = PortalBuilder.FRAME.defaultBlockState().setValue(ACTIVE, true);
        PortalBuilder.FRAME_POSITIONS.forEach(point -> {
            BlockPos pos = framePos.mutable().move(moveDir, point.x).move(Direction.UP, point.y);
            BlockState state = world.getBlockState(pos);
            if (state.hasProperty(ACTIVE) && !state.getValue(ACTIVE)) {
                world.setBlockAndUpdate(pos, frame);
            }
            pos = framePos.mutable().move(moveDir, -point.x).move(Direction.UP, point.y);
            state = world.getBlockState(pos);
            if (state.hasProperty(ACTIVE) && !state.getValue(ACTIVE)) {
                world.setBlockAndUpdate(pos, frame);
            }
        });
        Direction.Axis portalAxis = Direction.Axis.X == axis ? Direction.Axis.Z : Direction.Axis.X;
        BlockState portal = PortalBuilder.PORTAL.defaultBlockState()
                                                .setValue(EndPortalBlock.AXIS, portalAxis)
                                                .setValue(EndPortalBlock.PORTAL, portalId);
        ParticleOptions effect = new BlockParticleOption(ParticleTypes.BLOCK, portal);
        if (world instanceof ServerLevel serverWorld) {
            PortalBuilder.PORTAL_POSITIONS.forEach(point -> {
                BlockPos pos = center.mutable().move(moveDir, point.x).move(Direction.UP, point.y);
                if (!world.getBlockState(pos).is(PortalBuilder.PORTAL)) {
                    world.setBlockAndUpdate(pos, portal);
                    serverWorld.sendParticles(
                            effect,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            10,
                            0.5, 0.5, 0.5, 0.1
                    );
                    serverWorld.sendParticles(
                            ParticleTypes.REVERSE_PORTAL,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            10,
                            0.5, 0.5, 0.5, 0.3
                    );
                }
                pos = center.mutable().move(moveDir, -point.x).move(Direction.UP, point.y);
                if (!world.getBlockState(pos).is(PortalBuilder.PORTAL)) {
                    world.setBlockAndUpdate(pos, portal);
                    serverWorld.sendParticles(
                            effect,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            10,
                            0.5, 0.5, 0.5, 0.1
                    );
                    serverWorld.sendParticles(
                            ParticleTypes.REVERSE_PORTAL,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            10,
                            0.5, 0.5, 0.5, 0.3
                    );
                }

            });
        }
    }

    public void disablePortal(int state) {
        if (!active || isInvalid()) return;
        if (!world.isClientSide())
            removePortal(getTargetWorld(state), exit);
        removePortal(world, center);
    }

    private void removePortal(Level world, BlockPos center) {
        if (world == null || center == null) return;
        BlockPos framePos = center.below();
        Direction moveDir = Direction.Axis.X == axis ? Direction.NORTH : Direction.EAST;
        PortalBuilder.FRAME_POSITIONS.forEach(point -> {
            BlockPos pos = framePos.mutable().move(moveDir, point.x).move(Direction.UP, point.y);
            BlockState state = world.getBlockState(pos);
            if (state.is(PortalBuilder.FRAME) && state.getValue(ACTIVE)) {
                world.setBlockAndUpdate(pos, state.setValue(ACTIVE, false));
            }
            pos = framePos.mutable().move(moveDir, -point.x).move(Direction.UP, point.y);
            state = world.getBlockState(pos);
            if (state.is(PortalBuilder.FRAME) && state.getValue(ACTIVE)) {
                world.setBlockAndUpdate(pos, state.setValue(ACTIVE, false));
            }
        });
        PortalBuilder.PORTAL_POSITIONS.forEach(point -> {
            BlockPos pos = center.mutable().move(moveDir, point.x).move(Direction.UP, point.y);
            if (world.getBlockState(pos).is(PortalBuilder.PORTAL)) {
                world.removeBlock(pos, false);
            }
            pos = center.mutable().move(moveDir, -point.x).move(Direction.UP, point.y);
            if (world.getBlockState(pos).is(PortalBuilder.PORTAL)) {
                world.removeBlock(pos, false);
            }
        });
        this.active = false;
        updateActiveStateOnPedestals();
    }

    private Level getTargetWorld(int state) {
        if (world.dimension() == Level.END) {
            return EndPortals.getWorld(world.getServer(), state);
        }
        return Objects.requireNonNull(world.getServer()).getLevel(Level.END);
    }

    public void configure(BlockPos initial) {
        BlockPos checkPos = initial.east(12);
        if (this.hasPedestal(checkPos)) {
            this.axis = Direction.Axis.X;
            this.center = initial.east(6);
            return;
        }
        checkPos = initial.west(12);
        if (this.hasPedestal(checkPos)) {
            this.axis = Direction.Axis.X;
            this.center = initial.west(6);
            return;
        }
        checkPos = initial.south(12);
        if (this.hasPedestal(checkPos)) {
            this.axis = Direction.Axis.Z;
            this.center = initial.south(6);
            return;
        }
        checkPos = initial.north(12);
        if (this.hasPedestal(checkPos)) {
            this.axis = Direction.Axis.Z;
            this.center = initial.north(6);
            return;
        }
        checkPos = initial.north(10);
        if (this.hasPedestal(checkPos)) {
            this.axis = Direction.Axis.X;
            checkPos = checkPos.east(8);
            if (this.hasPedestal(checkPos)) {
                this.center = initial.north(5).east(4);
            } else {
                this.center = initial.north(5).west(4);
            }
            return;
        }
        checkPos = initial.south(10);
        if (this.hasPedestal(checkPos)) {
            this.axis = Direction.Axis.X;
            checkPos = checkPos.east(8);
            if (this.hasPedestal(checkPos)) {
                this.center = initial.south(5).east(4);
            } else {
                this.center = initial.south(5).west(4);
            }
            return;
        }
        checkPos = initial.east(10);
        if (this.hasPedestal(checkPos)) {
            this.axis = Direction.Axis.Z;
            checkPos = checkPos.south(8);
            if (this.hasPedestal(checkPos)) {
                this.center = initial.east(5).south(4);
            } else {
                this.center = initial.east(5).north(4);
            }
            return;
        }
        checkPos = initial.west(10);
        if (this.hasPedestal(checkPos)) {
            this.axis = Direction.Axis.Z;
            checkPos = checkPos.south(8);
            if (this.hasPedestal(checkPos)) {
                this.center = initial.west(5).south(4);
            } else {
                this.center = initial.west(5).north(4);
            }
        }
    }

    private boolean hasPedestal(BlockPos pos) {
        return world.getBlockState(pos).is(PEDESTAL);
    }

    private boolean isActive(BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.is(PEDESTAL)) {
            EternalPedestalEntity pedestal = (EternalPedestalEntity) world.getBlockEntity(pos);
            if (pedestal != null) {
                if (!pedestal.hasRitual()) {
                    pedestal.linkRitual(this);
                } else {
                    EternalRitual ritual = pedestal.getRitual();
                    if (!ritual.equals(this)) {
                        pedestal.linkRitual(this);
                    }
                }
            }
            return state.getValue(ACTIVE);
        }
        return false;
    }

    public CompoundTag toTag(CompoundTag tag) {
        if (center != null && axis != null) {
            tag.put("center", NbtUtils.writeBlockPos(center));
            tag.putString("axis", axis.getName());
        }
        tag.putBoolean("active", active);
        tag.putBoolean("will_activate", willActivate);
        if (pendingKeyItemId != null) {
            tag.putString("pending_key_item", pendingKeyItemId.toString());
        }
        if (pendingStartPosition != null) {
            tag.put("pending_start", NbtUtils.writeBlockPos(pendingStartPosition));
        }
        if (targetWorldId != null) {
            tag.putString("key_item", targetWorldId.toString());
        }
        if (exit != null) {
            tag.put("exit", NbtUtils.writeBlockPos(exit));
        }
        return tag;
    }

    public void fromTag(CompoundTag tag) {
        axis = Direction.Axis.byName(tag.getString("axis"));
        center = NbtUtils.readBlockPos(tag, "center").orElse(null);
        active = tag.getBoolean("active");
        willActivate = tag.getBoolean("will_activate");
        if (tag.contains("pending_key_item")) {
            pendingKeyItemId = ResourceLocation.parse(tag.getString("pending_key_item"));
        }
        if (tag.contains("pending_start")) {
            pendingStartPosition = NbtUtils.readBlockPos(tag, "pending_start").orElse(null);
        }
        if (tag.contains("exit")) {
            exit = NbtUtils.readBlockPos(tag, "exit").orElse(BlockPos.ZERO);
        }
        if (tag.contains("key_item")) {
            targetWorldId = ResourceLocation.parse(tag.getString("key_item"));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EternalRitual ritual = (EternalRitual) o;
        return world.equals(ritual.world) && Objects.equals(center, ritual.center) && Objects.equals(exit, ritual.exit);
    }

    public static EternalRitual findRitualForActivePortal(Level world, BlockPos startPos) {
        BlockState state = world.getBlockState(startPos);

        if (isActivePortalBlock(state)) {
            final var direction = state.getValue(NetherPortalBlock.AXIS) == Direction.Axis.X
                    ? Direction.EAST
                    : Direction.NORTH;
            while (world.getBlockState(startPos.below()).is(EndBlocks.END_PORTAL_BLOCK)) {
                startPos = startPos.below();
            }

            // X O O O O O X
            // X O O O O O X
            // X a c O c b X
            //   X a O b X
            //     X X X


            if (world.getBlockState(startPos.relative(direction, -1)).is(EndBlocks.FLAVOLITE_RUNED_ETERNAL))
                startPos = startPos.relative(direction, 1); //pos (a)
            else if (world.getBlockState(startPos.relative(direction, 1)).is(EndBlocks.FLAVOLITE_RUNED_ETERNAL))
                startPos = startPos.relative(direction, -1); //pos (b)

            if (!world.getBlockState(startPos.below()).is(EndBlocks.FLAVOLITE_RUNED_ETERNAL))
                startPos = startPos.below(); //pos (c)

            if (world.getBlockState(startPos.relative(direction, -1)).is(EndBlocks.FLAVOLITE_RUNED_ETERNAL))
                startPos = startPos.relative(direction, 1); //pos (a)
            else if (world.getBlockState(startPos.relative(direction, 1)).is(EndBlocks.FLAVOLITE_RUNED_ETERNAL))
                startPos = startPos.relative(direction, -1); //pos (b)

            startPos = startPos.relative(direction.getClockWise(), 6);
            state = world.getBlockState(startPos);
            if (state.is(EndBlocks.ETERNAL_PEDESTAL) && world.getBlockEntity(startPos) instanceof EternalPedestalEntity pedestal) {
                return pedestal.getRitual();
            }
            return null;
        }

        return null;
    }

    private static boolean isActivePortalBlock(BlockState state) {
        return state.is(EndBlocks.END_PORTAL_BLOCK)
                && state.hasProperty(NetherPortalBlock.AXIS);
    }
}

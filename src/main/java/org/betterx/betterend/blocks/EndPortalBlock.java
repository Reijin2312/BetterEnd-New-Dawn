package org.betterx.betterend.blocks;

import org.betterx.bclib.client.render.BCLRenderLayer;
import org.betterx.bclib.interfaces.BlockColorProvider;
import org.betterx.bclib.interfaces.CustomColorProvider;
import org.betterx.bclib.interfaces.ItemColorProvider;
import org.betterx.bclib.interfaces.RenderLayerProvider;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.advancements.BECriteria;
import org.betterx.betterend.portal.PortalBuilder;
import org.betterx.betterend.registry.EndParticles;
import org.betterx.betterend.registry.EndPortals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.BlockUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;


import java.util.Optional;

public class EndPortalBlock extends NetherPortalBlock implements RenderLayerProvider, CustomColorProvider, Portal {
    public static final IntegerProperty PORTAL = EndBlockProperties.PORTAL;

    public EndPortalBlock() {
        super(BlockBehaviour.Properties.ofLegacyCopy(Blocks.NETHER_PORTAL)
                                       .destroyTime(Blocks.BEDROCK.getExplosionResistance())
                                       .lightLevel((bs) -> 15));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PORTAL);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (random.nextInt(100) == 0) {
            world.playLocalSound(
                    pos.getX() + 0.5D,
                    pos.getY() + 0.5D,
                    pos.getZ() + 0.5D,
                    SoundEvents.PORTAL_AMBIENT,
                    SoundSource.BLOCKS,
                    0.5F,
                    random.nextFloat() * 0.4F + 0.8F,
                    false
            );
        }

        double x = pos.getX() + random.nextDouble();
        double y = pos.getY() + random.nextDouble();
        double z = pos.getZ() + random.nextDouble();
        int k = random.nextInt(2) * 2 - 1;
        if (!world.getBlockState(pos.west()).is(this) && !world.getBlockState(pos.east()).is(this)) {
            x = pos.getX() + 0.5D + 0.25D * k;
        } else {
            z = pos.getZ() + 0.5D + 0.25D * k;
        }

        world.addParticle(EndParticles.PORTAL_SPHERE, x, y, z, 0, 0, 0);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos posFrom,
            BlockState newState,
            RandomSource random
    ) {
        return state;
    }


    @Override
    protected void entityInside(
            BlockState state,
            Level world,
            BlockPos pos,
            Entity entity,
            InsideBlockEffectApplier insideBlockEffectApplier,
            boolean isMoving
    ) {
        //if (entity instanceof TravelingEntity te && te.be_getTravelerState() != null) {
        if (validate(entity)) {
            //te.be_getTravelerState().handleInsidePortal(pos);
            entity.setAsInsidePortal(this, pos);
        }
        //}
    }

    private boolean validate(Entity entity) {
        return !entity.isPassenger() && !entity.isVehicle() && entity.canUsePortal(false);
    }

    @Override
    public BCLRenderLayer getRenderLayer() {
        return BCLRenderLayer.TRANSLUCENT;
    }

    @Override
    public BlockColorProvider getProvider() {
        return (state, world, pos, tintIndex) -> EndPortals.getColor(state.getValue(PORTAL));
    }

    @Override
    public ItemColorProvider getItemProvider() {
        return (stack, tintIndex) -> EndPortals.getColor(0);
    }

    @Override
    public TeleportTransition getPortalDestination(ServerLevel serverLevel, Entity entity, BlockPos blockPos) {
        final ResourceKey<Level> destination = serverLevel.dimension() == Level.END ? Level.OVERWORLD : Level.END;
        final ServerLevel destinationLevel = serverLevel.getServer().getLevel(destination);
        if (destinationLevel == null) {
            return null;
        } else {
            WorldBorder worldBorder = destinationLevel.getWorldBorder();
            final double d = DimensionType.getTeleportationScale(serverLevel.dimensionType(), destinationLevel.dimensionType());
            final BlockPos startPos = worldBorder.clampToBounds(entity.getX() * d, entity.getY(), entity.getZ() * d);
            return this.getExitPortal(serverLevel, destinationLevel, entity, blockPos, startPos, worldBorder);
        }
    }

    private TeleportTransition getExitPortal(
            ServerLevel sourceLevel,
            ServerLevel destinationLevel,
            Entity entity,
            BlockPos pos,
            BlockPos startPos,
            WorldBorder worldBorder
    ) {
        final PortalBuilder builder = new PortalBuilder(sourceLevel, destinationLevel);
        final Optional<PortalBuilder.FoundPortalRect> portalRectangle = builder.findPortalAround(
                startPos,
                worldBorder
        );
        if (portalRectangle.isPresent()) {
            BlockUtil.FoundRectangle foundRectangle = portalRectangle.get().rect();
            TeleportTransition.PostTeleportTransition postDimensionTransition = TeleportTransition.PLAY_PORTAL_SOUND.then((x) -> {
                x.placePortalTicket(portalRectangle.get().pos());
                if (entity instanceof ServerPlayer sp) {
                    BECriteria.PORTAL_TRAVEL.trigger(sp);
                }
            });
            return getDimensionTransitionFromExit(entity, destinationLevel, pos, foundRectangle, postDimensionTransition);
        } else {
            BetterEnd.C.LOG.error("Unable to find portal exit.");
            return null;
        }
    }

    private static TeleportTransition getDimensionTransitionFromExit(
            Entity entity,
            ServerLevel destinationLevel,
            BlockPos portalEntrancePos,
            BlockUtil.FoundRectangle foundRectangle,
            TeleportTransition.PostTeleportTransition postDimensionTransition
    ) {
        BlockState blockState = entity.level().getBlockState(portalEntrancePos);
        Direction.Axis portalAxis;
        Vec3 exitPos;
        if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
            portalAxis = blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS);
            BlockUtil.FoundRectangle exitZone = BlockUtil.getLargestRectangleAround(portalEntrancePos, portalAxis, 21, Direction.Axis.Y, 21,
                    (p) -> entity
                            .level()
                            .getBlockState(p) == blockState
            );
            exitPos = entity.getRelativePortalPosition(portalAxis, exitZone);
        } else {
            portalAxis = Direction.Axis.X;
            exitPos = new Vec3(0.5, 0.0, 0.0);
        }

        return createDimensionTransition(destinationLevel, entity, entity.getDeltaMovement(), foundRectangle, portalAxis, exitPos, entity.getYRot(), entity.getXRot(), postDimensionTransition);
    }

    private static TeleportTransition createDimensionTransition(
            ServerLevel destinationLevel,
            Entity entity, Vec3 entityMovementDirection, BlockUtil.FoundRectangle foundRectangle,
            Direction.Axis portalAxis,
            Vec3 exitPos,
            float yRot,
            float xRot,
            TeleportTransition.PostTeleportTransition postDimensionTransition
    ) {
        BlockPos portalBasePos = foundRectangle.minCorner;
        BlockState exitPortalState = destinationLevel.getBlockState(portalBasePos);
        Direction.Axis destinationAxis = exitPortalState
                .getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS)
                .orElse(Direction.Axis.X);

        EntityDimensions entityDimensions = entity.getDimensions(entity.getPose());

        final int rotationOffset = portalAxis == destinationAxis ? 0 : 90;
        Vec3 exitMovementDirection = portalAxis == destinationAxis
                ? entityMovementDirection
                : new Vec3(entityMovementDirection.z, entityMovementDirection.y, -entityMovementDirection.x);

        double relX = entityDimensions.width() / 2.0 + (foundRectangle.axis1Size - entityDimensions.width()) * exitPos.x();
        double relZ = 0.5 + exitPos.z();
        final boolean exitInX = destinationAxis == Direction.Axis.X;

        Vec3 teleportPosition = PortalShape.findCollisionFreePosition(new Vec3(
                portalBasePos.getX() + (exitInX ? relX : relZ),
                portalBasePos.getY() + (foundRectangle.axis2Size - entityDimensions.height()) * exitPos.y(),
                portalBasePos.getZ() + (exitInX ? relZ : relX)
        ), destinationLevel, entity, entityDimensions);
        return new TeleportTransition(destinationLevel, teleportPosition, exitMovementDirection, yRot + rotationOffset, xRot, postDimensionTransition);
    }
}

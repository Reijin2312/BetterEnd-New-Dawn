package org.betterx.betterend.blocks;

import org.betterx.bclib.client.render.BCLRenderLayer;
import org.betterx.bclib.interfaces.CustomColorProvider;
import org.betterx.bclib.interfaces.RenderLayerProvider;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.advancements.BECriteria;
import org.betterx.betterend.portal.PortalBuilder;
import org.betterx.betterend.registry.EndParticles;
import org.betterx.betterend.registry.EndPortals;

import net.minecraft.BlockUtil;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.Vec3;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EndPortalBlock extends NetherPortalBlock implements RenderLayerProvider, CustomColorProvider, Portal {
    public static final IntegerProperty PORTAL = EndBlockProperties.PORTAL;

    public EndPortalBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_PORTAL)
                                       .destroyTime(Blocks.BEDROCK.getExplosionResistance())
                                       .lightLevel((bs) -> 15));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PORTAL);
    }

    @Override
    @Environment(EnvType.CLIENT)
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
            Direction direction,
            BlockState newState,
            LevelAccessor world,
            BlockPos pos,
            BlockPos posFrom
    ) {
        return state;
    }


    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
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
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public BlockColor getProvider() {
        return (state, world, pos, tintIndex) -> EndPortals.getColor(state.getValue(PORTAL));
    }

    @Override
    public ItemColor getItemProvider() {
        return (stack, tintIndex) -> EndPortals.getColor(0);
    }

    @Override
    public DimensionTransition getPortalDestination(ServerLevel serverLevel, Entity entity, BlockPos blockPos) {
        final BlockState entrance = serverLevel.getBlockState(blockPos);
        final int portalId = entrance.hasProperty(PORTAL) ? entrance.getValue(PORTAL) : 0;
        final List<ServerLevel> destinations = getDestinationLevels(serverLevel, portalId);
        for (ServerLevel destinationLevel : destinations) {
            final PortalBuilder builder = new PortalBuilder(serverLevel, destinationLevel);
            final Optional<PortalBuilder.FoundPortalRect> portal = builder.findPortalAround(
                    getStartPos(serverLevel, destinationLevel, entity), destinationLevel.getWorldBorder());
            if (portal.isPresent()) return transition(entity, destinationLevel, blockPos, portal.get());
        }
        if (destinations.isEmpty()) return null;
        final ServerLevel destinationLevel = destinations.get(0);
        final PortalBuilder builder = new PortalBuilder(serverLevel, destinationLevel);
        final Direction.Axis entranceAxis = entrance.getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS)
                                                    .orElse(Direction.Axis.X);
        final Optional<PortalBuilder.FoundPortalRect> created = builder.createPortal(
                getStartPos(serverLevel, destinationLevel, entity),
                entranceAxis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X,
                portalId
        ).map(builder::getPortalAt);
        if (created.isPresent()) return transition(entity, destinationLevel, blockPos, created.get());
        BetterEnd.C.LOG.error("Unable to find or build a portal exit in " + destinationLevel.dimension().location());
        return null;
    }

    private static List<ServerLevel> getDestinationLevels(ServerLevel source, int portalId) {
        final MinecraftServer server = source.getServer();
        final List<ServerLevel> levels = new ArrayList<>();
        if (source.dimension() != Level.END) {
            final ServerLevel end = server.getLevel(Level.END);
            if (end != null) levels.add(end);
            return levels;
        }
        final ServerLevel preferred = EndPortals.getWorld(server, portalId);
        if (preferred != null) levels.add(preferred);
        for (ServerLevel level : server.getAllLevels()) {
            if (level.dimension() != Level.END && !levels.contains(level)) levels.add(level);
        }
        return levels;
    }

    private static BlockPos getStartPos(ServerLevel source, ServerLevel target, Entity entity) {
        final double scale = DimensionType.getTeleportationScale(source.dimensionType(), target.dimensionType());
        return target.getWorldBorder().clampToBounds(entity.getX() * scale, entity.getY(), entity.getZ() * scale);
    }

    private static DimensionTransition transition(Entity entity, ServerLevel target, BlockPos entrance,
                                                  PortalBuilder.FoundPortalRect portal) {
        DimensionTransition.PostDimensionTransition post = DimensionTransition.PLAY_PORTAL_SOUND.then(x -> {
            x.placePortalTicket(portal.pos());
            if (entity instanceof ServerPlayer sp) BECriteria.PORTAL_TRAVEL.trigger(sp);
        });
        return getDimensionTransitionFromExit(entity, target, entrance, portal.rect(), post);
    }

    private static DimensionTransition getDimensionTransitionFromExit(
            Entity entity,
            ServerLevel destinationLevel,
            BlockPos portalEntrancePos,
            BlockUtil.FoundRectangle foundRectangle,
            DimensionTransition.PostDimensionTransition postDimensionTransition
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

    private static DimensionTransition createDimensionTransition(
            ServerLevel destinationLevel,
            Entity entity, Vec3 entityMovementDirection, BlockUtil.FoundRectangle foundRectangle,
            Direction.Axis portalAxis,
            Vec3 exitPos,
            float yRot,
            float xRot,
            DimensionTransition.PostDimensionTransition postDimensionTransition
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
        return new DimensionTransition(destinationLevel, teleportPosition, exitMovementDirection, yRot + rotationOffset, xRot, postDimensionTransition);
    }
}

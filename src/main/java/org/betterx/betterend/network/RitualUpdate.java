package org.betterx.betterend.network;

import de.ambertation.wunderlib.network.ClientBoundNetworkPayload;
import de.ambertation.wunderlib.network.ClientBoundPacketHandler;
import org.betterx.bclib.api.v2.dataexchange.BaseDataHandler;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.rituals.EternalRitual;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import de.ambertation.wunderlib.network.PacketSender;

public class RitualUpdate extends ClientBoundPacketHandler<RitualUpdate.Payload> {
    public static final Identifier CHANNEL = BetterEnd.C.mk("ritual_update");
    public static final RitualUpdate INSTANCE = new RitualUpdate();
    private static ClientProcessor clientProcessor =
            (center, axis, active, willActivate, clientKeyItem, pedestalItemMask, client) -> {};

    @FunctionalInterface
    public interface ClientProcessor {
        void process(
                BlockPos center,
                Direction.Axis axis,
                boolean active,
                boolean willActivate,
                Item clientKeyItem,
                int pedestalItemMask,
                Object client
        );
    }

    public static void registerClientProcessor(ClientProcessor processor) {
        clientProcessor = processor;
    }

    public RitualUpdate() {
        super(
                CHANNEL,
                Payload::new
        );
    }

    public static class Payload extends ClientBoundNetworkPayload<Payload> {
        byte flags;
        BlockPos center;
        Direction.Axis axis;
        Identifier keyItemId;
        int pedestalItemMask;

        public Payload(EternalRitual ritual) {
            super(INSTANCE);
            this.center = ritual.getCenter();
            this.axis = ritual.getAxis();

            if (ritual.isActive()) {
                this.flags |= ACTIVE_FLAG;
            }
            if (ritual.willActivate()) {
                this.flags |= WILL_ACTIVATE_FLAG;
            }
            Item keyItem = ritual.getKeyItem();
            if (keyItem != null) {
                this.keyItemId = BuiltInRegistries.ITEM.getKey(keyItem);
            }
            this.pedestalItemMask = ritual.getPedestalItemMask();
        }

        public Payload(RegistryFriendlyByteBuf buf) {
            super(INSTANCE);
            center = buf.readBlockPos();
            axis = Direction.Axis.byName(BaseDataHandler.readString(buf));
            flags = buf.readByte();
            keyItemId = buf.readBoolean() ? buf.readIdentifier() : null;
            pedestalItemMask = buf.readUnsignedByte();
        }


        @Override
        protected void write(RegistryFriendlyByteBuf buf) {
            buf.writeBlockPos(center);
            BaseDataHandler.writeString(buf, axis.getName());
            buf.writeByte(flags);
            buf.writeBoolean(keyItemId != null);
            if (keyItemId != null) {
                buf.writeIdentifier(keyItemId);
            }
            buf.writeByte(pedestalItemMask);
        }

        @Override
        protected void prepareOnServer(ServerPlayer player) {

        }

        @Override
        protected void processOnClient(PacketSender responseSender) {

        }

        @Override
        protected void processOnGameThread(Object client) {
            clientProcessor.process(
                    center,
                    axis,
                    (flags & ACTIVE_FLAG) != 0,
                    (flags & WILL_ACTIVATE_FLAG) != 0,
                    keyItemId == null ? null : BuiltInRegistries.ITEM.getValue(keyItemId),
                    pedestalItemMask,
                    client
            );
        }

    }

    Payload payload;
    private static final byte ACTIVE_FLAG = 1;
    private static final byte WILL_ACTIVATE_FLAG = 2;

    public RitualUpdate(EternalRitual ritual) {
        this();
        this.payload = new Payload(ritual);
    }

    public void sendToClient(ServerLevel level) {
        super.sendToClient(level, payload);
    }
}

package org.betterx.betterend.network;

import de.ambertation.wunderlib.network.ClientBoundNetworkPayload;
import de.ambertation.wunderlib.network.ClientBoundPacketHandler;
import org.betterx.bclib.api.v2.dataexchange.BaseDataHandler;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.rituals.EternalRitual;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import de.ambertation.wunderlib.network.PacketSender;

public class RitualUpdate extends ClientBoundPacketHandler<RitualUpdate.Payload> {
    public static final Identifier CHANNEL = BetterEnd.C.mk("ritual_update");
    public static final RitualUpdate INSTANCE = new RitualUpdate();

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
        }

        public Payload(RegistryFriendlyByteBuf buf) {
            super(INSTANCE);
            center = buf.readBlockPos();
            axis = Direction.Axis.byName(BaseDataHandler.readString(buf));
            flags = buf.readByte();
        }


        @Override
        protected void write(RegistryFriendlyByteBuf buf) {
            buf.writeBlockPos(center);
            BaseDataHandler.writeString(buf, axis.getName());
            buf.writeByte(flags);
        }

        @Override
        protected void prepareOnServer(ServerPlayer player) {

        }

        @Override
        protected void processOnClient(PacketSender responseSender) {

        }

        @Override
        protected void processOnGameThread(Minecraft client) {
            Level level = getClientLevel(client);
            if (level == null) {
                return;
            }

            EternalRitual.updateActiveStateOnPedestals(
                    center,
                    axis,
                    (flags & ACTIVE_FLAG) != 0,
                    (flags & WILL_ACTIVATE_FLAG) != 0,
                    level,
                    null
            );
        }

        private static Level getClientLevel(Minecraft client) {
            try {
                Object value = Minecraft.class.getField("level").get(client);
                if (value instanceof Level level) {
                    return level;
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
            return null;
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

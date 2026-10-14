package org.betterx.betterend.network;

import de.ambertation.wunderlib.network.ClientBoundNetworkPayload;
import de.ambertation.wunderlib.network.ClientBoundPacketHandler;
import org.betterx.bclib.api.v2.dataexchange.BaseDataHandler;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.rituals.EternalRitual;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import de.ambertation.wunderlib.network.PacketSender;

public class RitualUpdate extends ClientBoundPacketHandler<RitualUpdate.Payload> {
    public static final ResourceLocation CHANNEL = BetterEnd.C.mk("ritual_update");
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
        ResourceLocation keyItemId;
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
            keyItemId = buf.readBoolean() ? buf.readResourceLocation() : null;
            pedestalItemMask = buf.readUnsignedByte();
        }


        @Override
        protected void write(RegistryFriendlyByteBuf buf) {
            buf.writeBlockPos(center);
            BaseDataHandler.writeString(buf, axis.getName());
            buf.writeByte(flags);
            buf.writeBoolean(keyItemId != null);
            if (keyItemId != null) {
                buf.writeResourceLocation(keyItemId);
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
        @OnlyIn(Dist.CLIENT)
        protected void processOnGameThread(Minecraft client) {
            EternalRitual.updateActiveStateOnPedestals(
                    center,
                    axis,
                    (flags & ACTIVE_FLAG) != 0,
                    (flags & WILL_ACTIVATE_FLAG) != 0,
                    client.level,
                    null,
                    keyItemId == null ? null : BuiltInRegistries.ITEM.get(keyItemId),
                    pedestalItemMask
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

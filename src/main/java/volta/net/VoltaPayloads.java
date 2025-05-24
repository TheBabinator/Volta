package volta.net;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class VoltaPayloads {
    public static void register(IEventBus eventBus) {
        eventBus.addListener(VoltaPayloads::onRegisterPayloadHandlers);
    }

    private static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent registerPayloadHandlersEvent) {
        PayloadRegistrar payloadRegistrar = registerPayloadHandlersEvent.registrar("1");
        payloadRegistrar.playToClient(TerminalUpdate.TYPE, TerminalUpdate.STREAM_CODEC, TerminalUpdate::handle);
        payloadRegistrar.playToClient(WireConnectionUpdate.TYPE, WireConnectionUpdate.STREAM_CODEC, WireConnectionUpdate::handle);
        payloadRegistrar.playToServer(UseSpoolItem.TYPE, UseSpoolItem.STREAM_CODEC, UseSpoolItem::handle);
        payloadRegistrar.playToServer(RequestTerminalData.TYPE, RequestTerminalData.STREAM_CODEC, RequestTerminalData::handle);
    }

    public static void sendToPlayersTrackingBlockEntity(BlockEntity blockEntity, CustomPacketPayload payload) {
        Level level = blockEntity.getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }
        ChunkPos chunkPos = new ChunkPos(blockEntity.getBlockPos());
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, chunkPos, payload);
    }

    public static void sendToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }
}

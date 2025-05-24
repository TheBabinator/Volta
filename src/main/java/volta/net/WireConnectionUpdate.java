package volta.net;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import volta.Volta;
import volta.electricity.Connection;
import volta.electricity.LevelSimulationManager;
import volta.electricity.Simulation;
import volta.electricity.Terminal;
import volta.electricity.connections.WireConnection;
import volta.item.FilledSpoolItem;

public record WireConnectionUpdate(TerminalReference positive, TerminalReference negative, Item item) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<WireConnectionUpdate> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Volta.ID, "wire_connection_update"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WireConnectionUpdate> STREAM_CODEC = StreamCodec.composite(
            TerminalReference.STREAM_CODEC,
            WireConnectionUpdate::positive,
            TerminalReference.STREAM_CODEC,
            WireConnectionUpdate::negative,
            ByteBufCodecs.registry(Registries.ITEM),
            WireConnectionUpdate::item,
            WireConnectionUpdate::new
    );

    public static WireConnectionUpdate of(Terminal positive, Terminal negative, Item item) {
        return new WireConnectionUpdate(TerminalReference.of(positive), TerminalReference.of(negative), item);
    }

    public void handle(IPayloadContext payloadContext) {
        payloadContext.enqueueWork(() -> {
            try {
                Level level = payloadContext.player().level();
                Simulation simulation = LevelSimulationManager.getSimulation(level);
                Terminal positiveTerminal = positive.getTerminal(level);
                Terminal negativeTerminal = negative.getTerminal(level);
                for (Connection connection : simulation.getConnections(positiveTerminal, negativeTerminal)) {
                    if (connection instanceof WireConnection wireConnection) {
                        simulation.removeConnection(positiveTerminal, negativeTerminal, wireConnection);
                    }
                }
                if (item instanceof FilledSpoolItem filledSpoolItem) {
                    simulation.addConnection(positiveTerminal, negativeTerminal, new WireConnection(filledSpoolItem));
                }
            } catch (Exception ignored) {

            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

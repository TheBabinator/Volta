package volta.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import volta.Volta;
import volta.electricity.*;

public record RequestTerminalData(TerminalReference terminalReference) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RequestTerminalData> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Volta.ID, "request_terminal_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestTerminalData> STREAM_CODEC = StreamCodec.composite(
            TerminalReference.STREAM_CODEC,
            RequestTerminalData::terminalReference,
            RequestTerminalData::new
    );

    public static RequestTerminalData of(Terminal terminal) {
        return new RequestTerminalData(TerminalReference.of(terminal));
    }

    public void handle(IPayloadContext payloadContext) {
        payloadContext.enqueueWork(() -> {
            try {
                Level level = payloadContext.player().level();
                Simulation simulation = LevelSimulationManager.getSimulation(level);
                Terminal terminal = terminalReference.getTerminal(level);
                for (Terminal connectedTerminal : simulation.getConnectedTerminals(terminal)) {
                    for (Connection connection : simulation.getConnections(terminal, connectedTerminal)) {
                        connection.onConnected(simulation, terminal, connectedTerminal);
                    }
                }
                for (Terminal connectedTerminal : simulation.getReverseConnectedTerminals(terminal)) {
                    for (Connection connection : simulation.getConnections(connectedTerminal, terminal)) {
                        connection.onConnected(simulation, connectedTerminal, terminal);
                    }
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

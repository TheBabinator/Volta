package volta.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import volta.Volta;
import volta.electricity.Terminal;

public record TerminalUpdate(TerminalReference terminalReference, double stored) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TerminalUpdate> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Volta.ID, "terminal_update"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TerminalUpdate> STREAM_CODEC = StreamCodec.composite(
            TerminalReference.STREAM_CODEC,
            TerminalUpdate::terminalReference,
            ByteBufCodecs.DOUBLE,
            TerminalUpdate::stored,
            TerminalUpdate::new
    );

    public static TerminalUpdate of(Terminal terminal) {
        return new TerminalUpdate(TerminalReference.of(terminal), terminal.getSignedEnergyStored());
    }

    public void handle(IPayloadContext payloadContext) {
        payloadContext.enqueueWork(() -> {
            try {
                terminalReference.getTerminal(payloadContext.player().level()).setSignedEnergyStored(stored);
            } catch (Exception ignored) {

            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

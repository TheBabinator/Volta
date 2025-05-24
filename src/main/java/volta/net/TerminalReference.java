package volta.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import volta.electricity.Terminal;
import volta.electricity.TerminalProvider;

public record TerminalReference(BlockPos blockPos, int terminalIndex) {
    public static final StreamCodec<ByteBuf, TerminalReference> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            TerminalReference::blockPos,
            ByteBufCodecs.VAR_INT,
            TerminalReference::terminalIndex,
            TerminalReference::new
    );

    public static TerminalReference of(Terminal terminal) {
        TerminalProvider terminalProvider = terminal.getTerminalProvider();
        if (terminalProvider == null) {
            throw new IllegalArgumentException();
        }
        BlockPos blockPos = terminalProvider.getBlockEntity().getBlockPos();
        int terminalIndex = terminalProvider.getTerminalIndex(terminal);
        return new TerminalReference(blockPos, terminalIndex);
    }

    public Terminal getTerminal(Level level) {
        if (!level.hasChunk(SectionPos.blockToSectionCoord(blockPos.getX()), SectionPos.blockToSectionCoord(blockPos.getY()))) {
            throw new IllegalArgumentException();
        }
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof TerminalProvider terminalProvider) {
            return terminalProvider.getTerminal(terminalIndex);
        }
        throw new IllegalArgumentException();
    }
}

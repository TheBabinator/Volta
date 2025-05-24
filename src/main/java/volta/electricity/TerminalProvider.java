package volta.electricity;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface TerminalProvider {
    BlockEntity getBlockEntity();
    Simulation getSimulation();
    Terminal getTerminal(int i);
    int getTerminalIndex(Terminal terminal);
    int getTerminalCount();
}

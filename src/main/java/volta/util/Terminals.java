package volta.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import volta.electricity.Connection;
import volta.electricity.Simulation;
import volta.electricity.Terminal;
import volta.electricity.TerminalProvider;
import volta.electricity.connections.WireConnection;
import volta.item.FilledSpoolItem;

import java.util.List;

public class Terminals {
    /**
     * Calculates the capacitance between two terminals.
     * @param positive the positive terminal
     * @param negative the negative terminal
     * @return the capacitance
     */
    public static double getCapacitance(Terminal positive, Terminal negative) {
        double positiveCapacitance = positive.getCapacitance();
        double negativeCapacitance = negative.getCapacitance();
        return positiveCapacitance * negativeCapacitance / (positiveCapacitance + negativeCapacitance);
    }

    /**
     * Calculates the difference in potential between two terminals.
     * @param positive the positive terminal
     * @param negative the negative terminal
     * @return the potential difference
     */
    public static double getVoltage(Terminal positive, Terminal negative) {
        return positive.getPotential() - negative.getPotential();
    }

    /**
     * Calculates the flow of charge required to add to the difference in potential between two terminals.
     * @param positive the positive terminal
     * @param negative the negative terminal
     * @param voltage the voltage to add
     * @return the charge flow required
     */
    public static double flowAddVoltage(Terminal positive, Terminal negative, double voltage) {
        return voltage * getCapacitance(positive, negative);
    }

    /**
     * Calculates the flow of charge required to set to the difference in potential between two terminals.
     * @param positive the positive terminal
     * @param negative the negative terminal
     * @param voltage the voltage to reach
     * @return the charge flow required
     */
    public static double flowSetVoltage(Terminal positive, Terminal negative, double voltage) {
        return flowAddVoltage(positive, negative, getVoltage(positive, negative) - voltage);
    }

    /**
     * Calculates the flow of charge in a diode.
     * @param positive the positive terminal
     * @param negative the negative terminal
     * @param drop the voltage drop across the diode
     * @return the charge flow required
     */
    public static double flowDiode(Terminal positive, Terminal negative, double drop) {
        double voltage = Terminals.getVoltage(positive, negative);
        if (voltage < drop) {
            return 0.0;
        }
        return Terminals.flowSetVoltage(positive, negative, drop);
    }

    /**
     * Restores the state of a list of terminals from NBT.
     * @param terminals the list of terminals
     * @param tag the NBT tag containing the terminal data
     */
    public static void loadListFromTag(List<Terminal> terminals, CompoundTag tag) {
        ListTag listTag = tag.getList("terminals", 10);
        int i = 0;
        for (Terminal terminal : terminals) {
            CompoundTag terminalTag = (CompoundTag) listTag.get(i++);
            loadFromTag(terminal, terminalTag);
        }
    }

    /**
     * Stores the state of a list of terminals into NBT.
     * @param terminals the list of terminals
     * @param tag the NBT tag to contain the terminal data
     */
    public static void saveListIntoTag(List<Terminal> terminals, CompoundTag tag) {
        ListTag listTag = new ListTag();
        for (Terminal terminal : terminals) {
            CompoundTag terminalTag = new CompoundTag();
            saveIntoTag(terminal, terminalTag);
            listTag.add(terminalTag);
        }
        tag.put("terminals", listTag);
    }

    /**
     * Restores the state of a terminal from NBT.
     * @param terminal the terminal
     * @param tag the NBT tag containing the terminal data
     */
    public static void loadFromTag(Terminal terminal, CompoundTag tag) {
        terminal.setSignedEnergyStored(tag.getDouble("stored"));
        TerminalProvider terminalProvider = terminal.getTerminalProvider();
        if (terminalProvider == null) {
            return;
        }
        loadConnectionListFromTag(terminal, tag, false);
        loadConnectionListFromTag(terminal, tag, true);
    }

    /**
     * Stores the state of a terminal into NBT.
     * @param terminal the terminal
     * @param tag the NBT tag to contain the terminal data
     */
    public static void saveIntoTag(Terminal terminal, CompoundTag tag) {
        tag.putDouble("stored", terminal.getSignedEnergyStored());
        TerminalProvider terminalProvider = terminal.getTerminalProvider();
        if (terminalProvider == null) {
            return;
        }
        Simulation simulation = terminalProvider.getSimulation();
        for (Terminal connectedTerminal : simulation.getConnectedTerminals(terminal)) {
            saveConnectionListIntoTag(terminal, connectedTerminal, tag, false);
        }
        for (Terminal connectedTerminal : simulation.getReverseConnectedTerminals(terminal)) {
            saveConnectionListIntoTag(terminal, connectedTerminal, tag, true);
        }
    }

    private static void loadConnectionListFromTag(Terminal terminal, CompoundTag tag, boolean reversed) {
        TerminalProvider terminalProvider = terminal.getTerminalProvider();
        if (terminalProvider == null) {
            return;
        }
        Simulation simulation = terminalProvider.getSimulation();
        Level level = terminalProvider.getBlockEntity().getLevel();
        if (level == null) {
            return;
        }
        ListTag listTag = tag.getList(reversed ? "reversed" : "forward", 10);
        int n = listTag.size();
        for (int i = 0; i < n; i++) {
            CompoundTag connectionTag = listTag.getCompound(i);
            Item item = BuiltInRegistries.ITEM.byNameCodec().parse(NbtOps.INSTANCE, connectionTag.get("item")).getOrThrow();
            BlockPos blockPos = BlockPos.CODEC.parse(NbtOps.INSTANCE, connectionTag.get("target")).getOrThrow();
            int terminalIndex = connectionTag.getInt("index");
            if (!level.hasChunk(SectionPos.blockToSectionCoord(blockPos.getX()), SectionPos.blockToSectionCoord(blockPos.getZ()))) {
                continue;
            }
            if (level.getBlockEntity(blockPos) instanceof TerminalProvider connectedTerminalProvider) {
                if (item instanceof FilledSpoolItem filledSpoolItem) {
                    Terminal connectedTerminal = connectedTerminalProvider.getTerminal(terminalIndex);
                    if (reversed) {
                        if (!simulation.hasConnection(connectedTerminal, terminal, WireConnection.class)) {
                            simulation.addConnection(connectedTerminal, terminal, new WireConnection(filledSpoolItem));
                        }
                    } else {
                        if (!simulation.hasConnection(terminal, connectedTerminal, WireConnection.class)) {
                            simulation.addConnection(terminal, connectedTerminal, new WireConnection(filledSpoolItem));
                        }
                    }
                }
            }
        }
    }

    private static void saveConnectionListIntoTag(Terminal terminal, Terminal connectedTerminal, CompoundTag tag, boolean reversed) {
        TerminalProvider connectedTerminalProvider = connectedTerminal.getTerminalProvider();
        if (connectedTerminalProvider == null) {
            return;
        }
        Simulation simulation = connectedTerminalProvider.getSimulation();
        List<Connection> connections;
        if (reversed) {
            connections = simulation.getConnections(connectedTerminal, terminal);
        } else {
            connections = simulation.getConnections(terminal, connectedTerminal);
        }
        ListTag listTag = tag.getList(reversed ? "reversed" : "forward", 10);
        for (Connection connection : connections) {
            if (connection instanceof WireConnection wireConnection) {
                CompoundTag connectionTag = new CompoundTag();
                connectionTag.put("item", BuiltInRegistries.ITEM.byNameCodec().encodeStart(NbtOps.INSTANCE, wireConnection.getItem()).getOrThrow());
                connectionTag.put("target", BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, connectedTerminalProvider.getBlockEntity().getBlockPos()).getOrThrow());
                connectionTag.putInt("index", connectedTerminalProvider.getTerminalIndex(connectedTerminal));
                listTag.add(connectionTag);
            }
        }
        if (listTag.isEmpty()) {
            return;
        }
        tag.put(reversed ? "reversed" : "forward", listTag);
    }
}

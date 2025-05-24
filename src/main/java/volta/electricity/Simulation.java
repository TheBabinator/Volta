package volta.electricity;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import volta.net.RequestTerminalData;
import volta.net.TerminalUpdate;
import volta.net.VoltaPayloads;

import java.util.*;

public class Simulation {
    private final Set<Terminal> terminals = new HashSet<>();
    private final Map<Terminal, Map<Terminal, Set<Connection>>> terminalConnections = new HashMap<>();
    private final Map<Terminal, Map<Terminal, Set<Connection>>> reversedTerminalConnections = new HashMap<>();

    public void addTerminal(Terminal terminal) {
        terminals.add(terminal);
        terminalConnections.put(terminal, new HashMap<>());
        reversedTerminalConnections.put(terminal, new HashMap<>());
        Level level = LevelSimulationManager.findLevel(this);
        if (level != null && level.isClientSide()) {
            VoltaPayloads.sendToServer(RequestTerminalData.of(terminal));
        }
    }

    public void removeTerminal(Terminal terminal) {
        fullyRemoveConnections(terminal);
        terminalConnections.remove(terminal);
        reversedTerminalConnections.remove(terminal);
        terminals.remove(terminal);
    }

    public boolean hasTerminal(Terminal terminal) {
        return terminals.contains(terminal);
    }

    public List<Terminal> getTerminals() {
        return terminals.stream().toList();
    }

    public void forEachTerminal(Terminal.Handler terminalHandler) {
        for (Terminal terminal : terminals) {
            terminalHandler.accept(this, terminal);
        }
    }

    public void addConnection(Terminal positive, Terminal negative, Connection connection) {
        if (!hasTerminal(positive) || !hasTerminal(negative)) {
            return;
        }
        terminalConnections.get(positive).computeIfAbsent(negative, ignored -> new HashSet<>()).add(connection);
        reversedTerminalConnections.get(negative).computeIfAbsent(positive, ignored -> new HashSet<>()).add(connection);
        connection.onConnected(this, positive, negative);
    }

    public void removeConnection(Terminal positive, Terminal negative, Connection connection) {
        if (!hasTerminal(positive) || !hasTerminal(negative)) {
            return;
        }
        terminalConnections.get(positive).getOrDefault(negative, new HashSet<>()).remove(connection);
        reversedTerminalConnections.get(negative).getOrDefault(positive, new HashSet<>()).remove(connection);
        connection.onDisconnected(this, positive, negative);
    }

    public void removeConnections(Terminal positive, Terminal negative) {
        if (!hasTerminal(positive) || !hasTerminal(negative)) {
            return;
        }
        terminalConnections.get(positive).remove(negative);
        reversedTerminalConnections.get(negative).remove(positive);
    }

    public void fullyRemoveConnections(Terminal terminal) {
        for (Terminal connectedTerminal : getConnectedTerminals(terminal)) {
            removeConnections(terminal, connectedTerminal);
        }
        for (Terminal connectedTerminal : getConnectedTerminals(terminal)) {
            removeConnections(connectedTerminal, terminal);
        }
    }

    public List<Terminal> getConnectedTerminals(Terminal positive) {
        if (!hasTerminal(positive)) {
            return List.of();
        }
        return terminalConnections.get(positive).keySet().stream().toList();
    }

    public List<Terminal> getReverseConnectedTerminals(Terminal negative) {
        if (!hasTerminal(negative)) {
            return List.of();
        }
        return reversedTerminalConnections.get(negative).keySet().stream().toList();
    }

    public List<Connection> getConnections(Terminal positive, Terminal negative) {
        if (!hasTerminal(positive) || !hasTerminal(negative)) {
            return List.of();
        }
        return terminalConnections.get(positive).getOrDefault(negative, new HashSet<>()).stream().toList();
    }

    public <T extends Connection> T findConnection(Terminal positive, Terminal negative, Class<? extends T> clazz) {
        for (Connection connection : getConnections(positive, negative)) {
            if (clazz.isInstance(connection)) {
                return clazz.cast(connection);
            }
        }
        throw new NoSuchElementException();
    }

    public boolean hasConnection(Terminal positive, Terminal negative, Class<? extends Connection> clazz) {
        for (Connection connection : getConnections(positive, negative)) {
            if (clazz.isInstance(connection)) {
                return true;
            }
        }
        return false;
    }

    public void forEachConnection(Connection.Handler connectionHandler) {
        for (Map.Entry<Terminal, Map<Terminal, Set<Connection>>> terminals : terminalConnections.entrySet()) {
            Terminal positive = terminals.getKey();
            Map<Terminal, Set<Connection>> negativeConnections = terminals.getValue();
            for (Map.Entry<Terminal, Set<Connection>> negativeConnection : negativeConnections.entrySet()) {
                Terminal negative = negativeConnection.getKey();
                Set<Connection> connections = negativeConnection.getValue();
                for (Connection connection : connections) {
                    connectionHandler.accept(this, positive, negative, connection);
                }
            }
        }
    }

    public void simulate(int steps, double deltaTime) {
        double stepDeltaTime = deltaTime / steps;
        doPreSimulation();
        for (int step = 0; step < steps; step++) {
            doStep(stepDeltaTime);
        }
        doPostSimulation();
    }

    public void doPreSimulation() {
        forEachConnection((simulation, positive, negative, connection) -> {
            connection.preSimulation(simulation, positive, negative);
        });
    }

    public void doPostSimulation() {
        forEachConnection((simulation, positive, negative, connection) -> {
            connection.postSimulation(simulation, positive, negative);
        });
        forEachTerminal((simulation, terminal) -> {
            TerminalProvider terminalProvider = terminal.getTerminalProvider();
            if (terminalProvider == null) {
                return;
            }
            BlockEntity blockEntity = terminalProvider.getBlockEntity();
            blockEntity.setChanged();
            VoltaPayloads.sendToPlayersTrackingBlockEntity(blockEntity, TerminalUpdate.of(terminal));
        });
    }

    public void doStep(double deltaTime) {
        forEachConnection((simulation, positive, negative, connection) -> {
            double chargeFlow = connection.getChargeFlow(simulation, positive, negative, deltaTime);
            positive.setOnlyCharge(positive.getCharge() - chargeFlow);
            negative.setOnlyCharge(negative.getCharge() + chargeFlow);
        });
        forEachTerminal((simulation, terminal) -> {
            terminal.setOnlyPotential(terminal.getCharge() / terminal.getCapacitance());
        });
    }
}

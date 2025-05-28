package volta.electricity.connections;

import volta.electricity.Simulation;
import volta.electricity.Terminal;
import volta.util.Terminals;

public abstract class DiodeConnection extends BaseConnection {
    @Override
    public double getChargeFlow(Simulation simulation, Terminal positive, Terminal negative, double deltaTime) {
        return Terminals.flowDiode(positive, negative, getVoltageDrop());
    }

    public abstract double getVoltageDrop();
}

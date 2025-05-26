package volta.electricity.connections;

import volta.electricity.Simulation;
import volta.electricity.Terminal;
import volta.util.Terminals;

public abstract class TransistorConnection extends DiodeConnection {
    private final Terminal base;

    public TransistorConnection(Terminal base) {
        this.base = base;
    }

    @Override
    public double getChargeFlow(Simulation simulation, Terminal positive, Terminal negative, double deltaTime) {
        double diodeFlow = super.getChargeFlow(simulation, positive, negative, deltaTime);
        double baseVoltage = Terminals.getVoltage(base, positive);
        return diodeFlow;
    }

    public abstract double getVoltageDrop();
}

package volta.electricity.connections;

import volta.electricity.Simulation;
import volta.electricity.Terminal;
import volta.util.Terminals;

public abstract class PNPTransistorConnection extends BaseConnection {
    private final Terminal base;

    public PNPTransistorConnection(Terminal base) {
        this.base = base;
    }

    @Override
    public double getChargeFlow(Simulation simulation, Terminal positive, Terminal negative, double deltaTime) {
        double baseChargeFlow = Terminals.flowDiode(positive, base, getBaseVoltageDrop());
        positive.setOnlyCharge(positive.getCharge() - baseChargeFlow);
        base.setOnlyCharge(base.getCharge() + baseChargeFlow);
        double collectorChargeFlow = baseChargeFlow * getCurrentGain();
        double collectorMaxChargeFlow = Terminals.flowDiode(positive, negative, 0.0);
        return Math.min(collectorChargeFlow, collectorMaxChargeFlow);
    }

    public abstract double getBaseVoltageDrop();
    public abstract double getCurrentGain();
}

package volta.electricity.connections;

import volta.electricity.Simulation;
import volta.electricity.Terminal;
import volta.util.Terminals;

public abstract class DiodeConnection extends BaseConnection {
    @Override
    public double getChargeFlow(Simulation simulation, Terminal positive, Terminal negative, double deltaTime) {
        double voltageDrop = getVoltageDrop();
        if (Terminals.getVoltage(positive, negative) >= -voltageDrop) {
            return 0.0;
        }
        return Terminals.flowSetVoltage(positive, negative, -voltageDrop);
    }

    public abstract double getVoltageDrop();
}

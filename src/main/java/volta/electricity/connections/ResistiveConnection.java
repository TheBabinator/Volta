package volta.electricity.connections;

import volta.electricity.Simulation;
import volta.electricity.Terminal;
import volta.util.Terminals;

public abstract class ResistiveConnection extends BaseConnection {
    @Override
    public double getChargeFlow(Simulation simulation, Terminal positive, Terminal negative, double deltaTime) {
        double maxFlow = Terminals.flowSetVoltage(positive, negative, 0.0);
        double resistance = getResistance();
        double current;
        if (resistance > 0.0) {
            current = Terminals.getVoltage(positive, negative) / getResistance();
        } else {
            current = Double.POSITIVE_INFINITY;
        }
        if (maxFlow > 0.0) {
            return Math.min(maxFlow * 0.5, current * deltaTime);
        } else if (maxFlow < 0.0) {
            return Math.max(maxFlow * 0.5, current * deltaTime);
        } else {
            return 0.0;
        }
    }

    public abstract double getResistance();
}

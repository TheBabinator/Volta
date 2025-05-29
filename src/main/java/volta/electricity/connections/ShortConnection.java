package volta.electricity.connections;

import volta.electricity.Simulation;
import volta.electricity.Terminal;
import volta.util.Terminals;

public class ShortConnection extends BaseConnection {
    @Override
    public double getChargeFlow(Simulation simulation, Terminal positive, Terminal negative, double deltaTime) {
        return Terminals.flowSetVoltage(positive, negative, 0.0);
    }
}

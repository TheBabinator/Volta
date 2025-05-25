package volta.electricity.connections;

import volta.electricity.Simulation;
import volta.electricity.Terminal;
import volta.util.Terminals;

import java.util.function.DoubleSupplier;

public abstract class ElectromotiveConnection extends BaseConnection {
    @Override
    public double getChargeFlow(Simulation simulation, Terminal positive, Terminal negative, double deltaTime) {
        return Terminals.flowSetVoltage(positive, negative, getSupplyVoltage());
    }

    public abstract double getSupplyVoltage();
}

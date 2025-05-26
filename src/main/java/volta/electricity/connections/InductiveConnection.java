package volta.electricity.connections;

import volta.electricity.Simulation;
import volta.electricity.Terminal;
import volta.util.Terminals;

public abstract class InductiveConnection extends BaseConnection {
    private double current;

    public double getSignedEnergyStored() {
        return 0.5 * Math.signum(current) * current * current * getInductance();
    }

    public void setSignedEnergyStored(double energy) {
        current = Math.signum(energy) * Math.sqrt(2.0 * Math.abs(energy) / getInductance());
    }

    @Override
    public double getChargeFlow(Simulation simulation, Terminal positive, Terminal negative, double deltaTime) {
        double currentDerivative = Terminals.getVoltage(positive, negative) / getInductance();
        current += currentDerivative * deltaTime;
        return current * deltaTime;
    }

    public abstract double getInductance();
}

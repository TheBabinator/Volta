package volta.electricity.connections;

import volta.electricity.Simulation;
import volta.electricity.Terminal;

public abstract class CapacitiveConnection extends BaseConnection {
    private double charge;

    public double getSignedEnergyStored() {
        return 0.5 * Math.signum(charge) * charge * charge / getCapacitance();
    }

    public void setSignedEnergyStored(double energy) {
        charge = Math.signum(energy) * Math.sqrt(2.0 * Math.abs(energy) * getCapacitance());
    }

    @Override
    public double getChargeFlow(Simulation simulation, Terminal positive, Terminal negative, double deltaTime) {
        return 0.0;
    }

    public abstract double getCapacitance();
}

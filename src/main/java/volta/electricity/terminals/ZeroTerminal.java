package volta.electricity.terminals;

import volta.electricity.Terminal;

public class ZeroTerminal implements Terminal {
    @Override
    public double getCapacitance() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getCharge() {
        return 0;
    }

    @Override
    public double getPotential() {
        return 0;
    }

    @Override
    public void setOnlyCharge(double charge) {

    }

    @Override
    public void setOnlyPotential(double potential) {

    }

    @Override
    public boolean isInvalid() {
        return false;
    }
}

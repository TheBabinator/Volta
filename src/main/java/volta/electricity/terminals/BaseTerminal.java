package volta.electricity.terminals;

import volta.electricity.Terminal;

public abstract class BaseTerminal implements Terminal {
    private double charge;
    private double potential;

    @Override
    public double getCharge() {
        return charge;
    }

    @Override
    public double getPotential() {
        return potential;
    }

    @Override
    public void setOnlyCharge(double charge) {
        this.charge = charge;
    }

    @Override
    public void setOnlyPotential(double potential) {
        this.potential = potential;
    }
}

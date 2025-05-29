package volta.electricity.connections;

import volta.electricity.Simulation;
import volta.electricity.Terminal;

public class SwitchConnection extends ShortConnection {
    private boolean closed = false;

    @Override
    public double getChargeFlow(Simulation simulation, Terminal positive, Terminal negative, double deltaTime) {
        if (closed) {
            return super.getChargeFlow(simulation, positive, negative, deltaTime);
        }
        return 0.0;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}

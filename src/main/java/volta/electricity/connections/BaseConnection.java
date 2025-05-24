package volta.electricity.connections;

import volta.electricity.Connection;
import volta.electricity.Simulation;
import volta.electricity.Terminal;

public abstract class BaseConnection implements Connection {
    @Override
    public void onConnected(Simulation simulation, Terminal positive, Terminal negative) {

    }

    @Override
    public void onDisconnected(Simulation simulation, Terminal positive, Terminal negative) {

    }

    @Override
    public void preSimulation(Simulation simulation, Terminal positive, Terminal negative) {

    }

    @Override
    public void postSimulation(Simulation simulation, Terminal positive, Terminal negative) {

    }
}

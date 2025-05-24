package volta.electricity;

public interface Connection {
    void onConnected(Simulation simulation, Terminal positive, Terminal negative);
    void onDisconnected(Simulation simulation, Terminal positive, Terminal negative);
    void preSimulation(Simulation simulation, Terminal positive, Terminal negative);
    void postSimulation(Simulation simulation, Terminal positive, Terminal negative);
    double getChargeFlow(Simulation simulation, Terminal positive, Terminal negative, double deltaTime);

    interface Handler {
        void accept(Simulation simulation, Terminal positive, Terminal negative, Connection connection);
    }
}

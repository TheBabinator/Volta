package volta.electricity;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public interface Terminal {
    double getCapacitance();
    double getCharge();
    double getPotential();
    void setOnlyCharge(double charge);
    void setOnlyPotential(double potential);
    boolean isInvalid();

    default void setChargeAndPotential(double charge) {
        setOnlyCharge(charge);
        setOnlyPotential(charge / getCapacitance());
    }

    default void setPotentialAndCharge(double potential) {
        setOnlyPotential(potential);
        setOnlyCharge(potential * getCapacitance());
    }

    default double getSignedEnergyStored() {
        return 0.5 * getCharge() * Math.abs(getPotential());
    }

    default void setSignedEnergyStored(double energy) {
        setChargeAndPotential(Math.signum(energy) * Math.sqrt(2.0 * Math.abs(energy) * getCapacitance()));
    }

    default void preSimulation(Terminal positive, Terminal negative, Connection connection) {

    }

    default void postSimulation(Terminal positive, Terminal negative, Connection connection) {

    }

    default Vec3 getWorldPosition() {
        return Vec3.ZERO;
    }

    default VoxelShape getShape() {
        return Shapes.empty();
    }

    default @Nullable TerminalProvider getTerminalProvider() {
        return null;
    }

    interface Handler {
        void accept(Simulation simulation, Terminal terminal);
    }
}

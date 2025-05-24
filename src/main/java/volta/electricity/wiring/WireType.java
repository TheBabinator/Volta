package volta.electricity.wiring;

import volta.VoltaConfig;

import java.util.function.DoubleSupplier;

public enum WireType {
    COPPER(WireStyle.COPPER, VoltaConfig.COPPER_WIRE_RESISTANCE, VoltaConfig.COPPER_WIRE_CURRENT_RATING, VoltaConfig.MINIMAL_VOLTAGE),
    WAXED_COPPER(WireStyle.COPPER, VoltaConfig.COPPER_WIRE_RESISTANCE, VoltaConfig.COPPER_WIRE_CURRENT_RATING, VoltaConfig.LOW_VOLTAGE),
    INSULATED_COPPER(WireStyle.INSULATED_COPPER, VoltaConfig.COPPER_WIRE_RESISTANCE, VoltaConfig.COPPER_WIRE_CURRENT_RATING, VoltaConfig.MEDIUM_VOLTAGE);

    private final WireStyle wireStyle;
    private final DoubleSupplier unitResistanceSupplier;
    private final DoubleSupplier currentRatingSupplier;
    private final DoubleSupplier insulationRatingSupplier;

    WireType(WireStyle wireStyle, DoubleSupplier unitResistanceSupplier, DoubleSupplier currentRatingSupplier, DoubleSupplier insulationRatingSupplier) {
        this.wireStyle = wireStyle;
        this.unitResistanceSupplier = unitResistanceSupplier;
        this.currentRatingSupplier = currentRatingSupplier;
        this.insulationRatingSupplier = insulationRatingSupplier;
    }

    public WireStyle getWireStyle() {
        return wireStyle;
    }

    public double getUnitResistance() {
        return unitResistanceSupplier.getAsDouble();
    }

    public double getCurrentRating() {
        return currentRatingSupplier.getAsDouble();
    }

    public double getInsulationRating() {
        return insulationRatingSupplier.getAsDouble();
    }
}

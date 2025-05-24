package volta;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

public class VoltaConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final IntSupplier SIMULATION_QUALITY = BUILDER.comment("The number of simulation steps to run each tick.")
            .defineInRange("simulationQuality", 50, 1, 1000);

    public static final DoubleSupplier TERMINAL_CAPACITANCE = BUILDER.comment("The capacitance of every machine's terminals, measured in farads.")
            .defineInRange("terminalCapacitance", 0.05, 0.001, 1.0);

    public static final DoubleSupplier MINIMAL_VOLTAGE = BUILDER.comment("The limit for typical minimal voltage, measured in volts.")
            .defineInRange("minimalVoltage", 10.0, 1.0, 1000000.0);

    public static final DoubleSupplier LOW_VOLTAGE = BUILDER.comment("The limit for typical low voltage, measured in volts.")
            .defineInRange("lowVoltage", 50.0, 1.0, 1000000.0);

    public static final DoubleSupplier MEDIUM_VOLTAGE = BUILDER.comment("The limit for typical medium voltage, measured in volts.")
            .defineInRange("mediumVoltage", 250.0, 1.0, 1000000.0);

    public static final DoubleSupplier HIGH_VOLTAGE = BUILDER.comment("The limit for typical high voltage, measured in volts.")
            .defineInRange("highVoltage", 1000.0, 1.0, 1000000.0);

    public static final DoubleSupplier EXTREME_VOLTAGE = BUILDER.comment("The limit for typical extreme voltage, measured in volts.")
            .defineInRange("extremeVoltage", 10000.0, 1.0, 1000000.0);

    public static final DoubleSupplier CREATIVE_CELL_SUPPLY_VOLTAGE = BUILDER.comment("The supply voltage of the creative cells.")
            .defineInRange("creativeCellSupplyVoltage", 9.0, 1.0, 1000000.0);

    public static final DoubleSupplier CAPACITOR_CAPACITANCE = BUILDER.comment("The capacitance of the capacitors.")
            .defineInRange("capacitorCapacitance", 10.0, 1.0, 1000.0);

    public static final DoubleSupplier COPPER_WIRE_RESISTANCE = BUILDER.comment("The resistance of copper wire over its length, measured in ohms per metre.")
            .defineInRange("copperWireResistance", 0.25, 0.001, 1000.0);

    public static final DoubleSupplier COPPER_WIRE_CURRENT_RATING = BUILDER.comment("The maximum safe current that can pass through copper wire, measured in amps.")
            .defineInRange("copperWireCurrentRating", 5.0, 0.001, 1000.0);

    public static void register(IEventBus eventBus, ModContainer modContainer) {
        eventBus.addListener(VoltaConfig::onModConfig);
        modContainer.registerConfig(ModConfig.Type.SERVER, BUILDER.build());
    }

    private static void onModConfig(ModConfigEvent modConfigEvent) {

    }
}

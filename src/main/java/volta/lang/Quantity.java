package volta.lang;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public enum Quantity {
    VOLTAGE_READING("voltage_reading", Unit.VOLT, true),
    TERMINAL_CAPACITANCE("terminal_capacitance", Unit.FARAD, false),
    INTERNAL_CAPACITANCE("internal_capacitance", Unit.FARAD, false),
    SUPPLY_VOLTAGE("supply_voltage", Unit.VOLT, false),
    UNIT_RESISTANCE("unit_resistance", Unit.OHM, false),
    CURRENT_RATING("current_rating", Unit.AMP, false),
    INSULATION_RATING("insulation_rating", Unit.VOLT, false);

    private final String key;
    private final Unit unit;
    private final boolean signAlways;

    Quantity(String name, Unit unit, boolean signAlways) {
        key = "quantity." + name;
        this.unit = unit;
        this.signAlways = signAlways;
    }

    public Component format(double value) {
        return Component.translatable(key)
                .withStyle(ChatFormatting.DARK_GRAY)
                .append(unit.format(value, signAlways));
    }
}

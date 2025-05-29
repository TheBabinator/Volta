package volta.lang;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public enum Unit {
    RATIO("ratio"),
    COULOMB("coulomb"),
    VOLT("volt"),
    AMP("amp"),
    OHM("ohm"),
    FARAD("farad"),
    HENRY("henry"),
    METRE("metre"),
    JOULE("joule"),
    WATT("watt"),
    OHMS_PER_METRE("ohms_per_metre");

    private final String key;

    Unit(String name) {
        key = "unit." + name;
    }

    public Component format(double value, boolean signAlways) {
        double magnitude = Math.abs(value);
        double scaledValue;
        String prefixKey;
        if (magnitude >= 1e9) {
            scaledValue = value / 1e9;
            prefixKey = "prefix.giga";
        } else if (magnitude >= 1e6) {
            scaledValue = value / 1e6;
            prefixKey = "prefix.mega";
        } else if (magnitude >= 1e3) {
            scaledValue = value / 1e3;
            prefixKey = "prefix.kilo";
        } else if (magnitude >= 1e0) {
            scaledValue = value;
            prefixKey = "prefix.unit";
        } else if (magnitude >= 1e-3) {
            scaledValue = value / 1e-3;
            prefixKey = "prefix.milli";
        } else if (magnitude >= 1e-6) {
            scaledValue = value / 1e-6;
            prefixKey = "prefix.micro";
        } else if (magnitude >= 1e-9) {
            scaledValue = value / 1e-9;
            prefixKey = "prefix.nano";
        } else {
            scaledValue = 0.0;
            prefixKey = "prefix.unit";
        }
        return Component.literal(String.format(signAlways ? "%+.1f" : "%.1f", scaledValue))
                .withStyle(ChatFormatting.GRAY)
                .append(Component.translatable(prefixKey)
                        .append(Component.translatable(key))
                        .withStyle(ChatFormatting.DARK_GRAY));
    }
}

package volta.electricity.wiring;

public enum WireStyle {
    COPPER(0xC15A36, 0xE77C56, 0.03125f),
    INSULATED_COPPER(0x111111, 0xE77C56, 0.03125f),
    MULTIMETER(0x959595, 0xABABAB, 0.03125f),
    UNSPOOLING(0x951111, 0xAB1111, 0.03125f);

    private final int color;
    private final int accentColor;
    private final float radius;

    WireStyle(int color, int accentColor, float radius) {
        this.color = color;
        this.accentColor = accentColor;
        this.radius = radius;
    }

    public int getColor() {
        return color;
    }

    public int getAccentColor() {
        return accentColor;
    }

    public float getRadius() {
        return radius;
    }
}

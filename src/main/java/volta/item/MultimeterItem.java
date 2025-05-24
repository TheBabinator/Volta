package volta.item;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import volta.electricity.Terminal;
import volta.lang.Quantity;
import volta.util.Terminals;
import volta.electricity.wiring.WireStyle;

public class MultimeterItem extends ClientConnectorItem {
    public MultimeterItem(Properties properties) {
        super(properties);
    }

    @Override
    public WireStyle getWireStyle() {
        return WireStyle.MULTIMETER;
    }

    @Override
    public void connect(Terminal first, Terminal last) {
        double voltage = Terminals.getVoltage(last, first);
        Component message = Quantity.VOLTAGE_READING.format(voltage);
        Minecraft.getInstance().gui.getChat().addMessage(message);
    }

    @Override
    public boolean shouldReset() {
        return false;
    }
}

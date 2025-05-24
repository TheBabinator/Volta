package volta.item;

import volta.electricity.Terminal;
import volta.net.UseSpoolItem;
import volta.net.VoltaPayloads;
import volta.electricity.wiring.WireStyle;

public class EmptySpoolItem extends ClientConnectorItem {
    public EmptySpoolItem(Properties properties) {
        super(properties);
    }

    @Override
    public WireStyle getWireStyle() {
        return WireStyle.UNSPOOLING;
    }

    @Override
    public void connect(Terminal first, Terminal last) {
        VoltaPayloads.sendToServer(UseSpoolItem.of(first, last, this));
    }

    @Override
    public boolean shouldReset() {
        return true;
    }
}

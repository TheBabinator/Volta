package volta.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import volta.electricity.Terminal;
import volta.lang.Quantity;
import volta.net.UseSpoolItem;
import volta.net.VoltaPayloads;
import volta.electricity.wiring.WireStyle;
import volta.electricity.wiring.WireType;

import java.util.List;

public class FilledSpoolItem extends ClientConnectorItem {
    private final WireType wireType;

    public FilledSpoolItem(WireType wireType, Properties properties) {
        super(properties);
        this.wireType = wireType;
    }

    public WireType getWireType() {
        return wireType;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Quantity.UNIT_RESISTANCE.format(wireType.getUnitResistance()));
        tooltipComponents.add(Quantity.CURRENT_RATING.format(wireType.getCurrentRating()));
        tooltipComponents.add(Quantity.INSULATION_RATING.format(wireType.getInsulationRating()));
    }

    @Override
    public WireStyle getWireStyle() {
        return wireType.getWireStyle();
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

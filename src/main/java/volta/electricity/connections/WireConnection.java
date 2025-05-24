package volta.electricity.connections;

import net.minecraft.world.level.block.entity.BlockEntity;
import volta.electricity.Simulation;
import volta.electricity.Terminal;
import volta.electricity.TerminalProvider;
import volta.item.FilledSpoolItem;
import volta.item.VoltaItems;
import volta.net.VoltaPayloads;
import volta.net.WireConnectionUpdate;

public class WireConnection extends ResistiveConnection {
    private final FilledSpoolItem item;
    private double resistance;

    public WireConnection(FilledSpoolItem item) {
        this.item = item;
    }

    @Override
    public void onConnected(Simulation simulation, Terminal positive, Terminal negative) {
        TerminalProvider positiveTerminalProvider = positive.getTerminalProvider();
        if (positiveTerminalProvider == null) {
            return;
        }
        TerminalProvider negativeTerminalProvider = negative.getTerminalProvider();
        if (negativeTerminalProvider == null) {
            return;
        }
        BlockEntity positiveBlockEntity = positiveTerminalProvider.getBlockEntity();
        BlockEntity negativeBlockEntity = negativeTerminalProvider.getBlockEntity();
        WireConnectionUpdate wireConnectionUpdate = WireConnectionUpdate.of(positive, negative, item);
        VoltaPayloads.sendToPlayersTrackingBlockEntity(positiveBlockEntity, wireConnectionUpdate);
        VoltaPayloads.sendToPlayersTrackingBlockEntity(negativeBlockEntity, wireConnectionUpdate);
    }

    @Override
    public void onDisconnected(Simulation simulation, Terminal positive, Terminal negative) {
        TerminalProvider positiveTerminalProvider = positive.getTerminalProvider();
        if (positiveTerminalProvider == null) {
            return;
        }
        TerminalProvider negativeTerminalProvider = negative.getTerminalProvider();
        if (negativeTerminalProvider == null) {
            return;
        }
        BlockEntity positiveBlockEntity = positiveTerminalProvider.getBlockEntity();
        BlockEntity negativeBlockEntity = negativeTerminalProvider.getBlockEntity();
        WireConnectionUpdate wireConnectionUpdate = WireConnectionUpdate.of(positive, negative, VoltaItems.EMPTY_WIRE_SPOOL.get());
        VoltaPayloads.sendToPlayersTrackingBlockEntity(positiveBlockEntity, wireConnectionUpdate);
        VoltaPayloads.sendToPlayersTrackingBlockEntity(negativeBlockEntity, wireConnectionUpdate);
    }

    @Override
    public void preSimulation(Simulation simulation, Terminal positive, Terminal negative) {
        double length = positive.getWorldPosition().distanceTo(negative.getWorldPosition());
        resistance = item.getWireType().getUnitResistance() * length;
    }

    public FilledSpoolItem getItem() {
        return item;
    }

    @Override
    public double getResistance() {
        return resistance;
    }
}

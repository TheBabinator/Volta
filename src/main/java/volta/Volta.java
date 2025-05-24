package volta;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import volta.block.VoltaBlocks;
import volta.block.entity.VoltaBlockEntityTypes;
import volta.data.VoltaDataProviders;
import volta.net.VoltaPayloads;
import volta.electricity.wiring.ClientConnector;
import volta.electricity.LevelSimulationManager;
import volta.item.VoltaItems;
import volta.item.VoltaTabs;

@Mod(Volta.ID)
public class Volta {
    public static final String ID = "volta";

    public Volta(IEventBus modEventBus, ModContainer modContainer) {
        VoltaConfig.register(modEventBus, modContainer);
        VoltaDataProviders.register(modEventBus);
        VoltaPayloads.register(modEventBus);
        VoltaTabs.register(modEventBus);
        VoltaItems.register(modEventBus);
        VoltaBlocks.register(modEventBus);
        VoltaBlockEntityTypes.register(modEventBus);
        LevelSimulationManager.register();
    }

    @Mod(value = Volta.ID, dist = Dist.CLIENT)
    public static class Client {
        public Client(IEventBus modEventBus, ModContainer modContainer) {
            ClientConnector.register();
        }
    }
}

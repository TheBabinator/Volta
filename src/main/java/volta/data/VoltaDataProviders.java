package volta.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class VoltaDataProviders {
    public static void register(IEventBus eventBus) {
        eventBus.addListener(VoltaDataProviders::onGatherData);
    }

    private static void onGatherData(GatherDataEvent gatherDataEvent) {
        CompletableFuture<HolderLookup.Provider> registries = gatherDataEvent.getLookupProvider();
        DataGenerator dataGenerator = gatherDataEvent.getGenerator();
        PackOutput packOutput = dataGenerator.getPackOutput();
        ExistingFileHelper existingFileHelper = gatherDataEvent.getExistingFileHelper();
        dataGenerator.addProvider(gatherDataEvent.includeClient(), new VoltaItemModelProvider(packOutput, existingFileHelper));
        dataGenerator.addProvider(gatherDataEvent.includeClient(), new VoltaBlockStateProvider(packOutput, existingFileHelper));
        dataGenerator.addProvider(gatherDataEvent.includeServer(), new VoltaRecipeProvider(packOutput, registries));
    }
}

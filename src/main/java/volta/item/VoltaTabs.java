package volta.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import volta.Volta;

import java.util.List;
import java.util.function.Supplier;

public class VoltaTabs {
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Volta.ID);

    public static final Supplier<CreativeModeTab> ITEMS = group("items", VoltaItems.GROUP_ITEMS);
    public static final Supplier<CreativeModeTab> BLOCKS = group("blocks", "items", VoltaItems.GROUP_BLOCKS);
    public static final Supplier<CreativeModeTab> ELECTRONICS = group("electronics", "blocks", VoltaItems.GROUP_ELECTRONICS);
    public static final Supplier<CreativeModeTab> MACHINES = group("machines", "electronics", VoltaItems.GROUP_MACHINES);

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

    private static Supplier<CreativeModeTab> group(String name, List<Supplier<Item>> itemGroup) {
        return CREATIVE_MODE_TABS.register(name,
                () -> CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.volta." + name))
                        .icon(() -> itemGroup.getFirst().get().getDefaultInstance())
                        .displayItems(((parameters, output) -> itemGroup.stream()
                                .map(Supplier::get)
                                .map(Item::getDefaultInstance)
                                .forEach(output::accept)))
                        .build());
    }

    private static Supplier<CreativeModeTab> group(String name, String previous, List<Supplier<Item>> itemGroup) {
        return CREATIVE_MODE_TABS.register(name,
                () -> CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.volta." + name))
                        .icon(() -> itemGroup.getFirst().get().getDefaultInstance())
                        .displayItems(((parameters, output) -> itemGroup.stream()
                                .map(Supplier::get)
                                .map(Item::getDefaultInstance)
                                .forEach(output::accept)))
                        .withTabsBefore(ResourceLocation.fromNamespaceAndPath(Volta.ID, previous))
                        .build());
    }
}

package volta.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import volta.Volta;
import volta.block.VoltaBlocks;
import volta.electricity.wiring.WireType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class VoltaItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Volta.ID);

    public static final List<Supplier<Item>> GROUP_ITEMS = new ArrayList<>();
    public static final List<Supplier<Item>> GROUP_BLOCKS = new ArrayList<>();
    public static final List<Supplier<Item>> GROUP_ELECTRONICS = new ArrayList<>();
    public static final List<Supplier<Item>> GROUP_MACHINES = new ArrayList<>();

    public static final Supplier<Item> MULTIMETER =
            item("multimeter", 1, MultimeterItem::new);
    public static final Supplier<Item> EMPTY_WIRE_SPOOL =
            item("empty_wire_spool", 16, EmptySpoolItem::new);
    public static final Supplier<Item> COPPER_WIRE_SPOOL =
            item("copper_wire_spool", 16, FilledSpoolItem::new, WireType.COPPER);
    public static final Supplier<Item> WAXED_COPPER_WIRE_SPOOL =
            item("waxed_copper_wire_spool", 16, FilledSpoolItem::new, WireType.WAXED_COPPER);
    public static final Supplier<Item> INSULATED_COPPER_WIRE_SPOOL =
            item("insulated_copper_wire_spool", 16, FilledSpoolItem::new, WireType.INSULATED_COPPER);

    public static final Supplier<Item> COPPER_WIRE = item("copper_wire");
    public static final Supplier<Item> WAXED_COPPER_WIRE = item("waxed_copper_wire");
    public static final Supplier<Item> INSULATED_COPPER_WIRE = item("insulated_copper_wire");

    public static final Supplier<Item> CREATIVE_CELL =
            electronic("creative_cell", VoltaBlocks.CREATIVE_CELL);
    public static final Supplier<Item> CAPACITOR_BANK =
            electronic("capacitor_bank", VoltaBlocks.CAPACITOR_BANK);
    public static final Supplier<Item> WALL_BRACKET =
            electronic("wall_bracket", VoltaBlocks.WALL_BRACKET);
    public static final Supplier<Item> DOUBLE_WALL_BRACKET =
            electronic("double_wall_bracket", VoltaBlocks.DOUBLE_WALL_BRACKET);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    private static Supplier<Item> item(String name) {
        Supplier<Item> itemSupplier = ITEMS.register(name,
                () -> new Item(new Item.Properties()));
        GROUP_ITEMS.add(itemSupplier);
        return itemSupplier;
    }

    private static Supplier<Item> item(String name, int stackSize) {
        Supplier<Item> itemSupplier = ITEMS.register(name,
                () -> new Item(new Item.Properties().stacksTo(stackSize)));
        GROUP_ITEMS.add(itemSupplier);
        return itemSupplier;
    }

    private static Supplier<Item> item(String name, int stackSize, Function<Item.Properties, Item> itemFactory) {
        Supplier<Item> itemSupplier = ITEMS.register(name,
                () -> itemFactory.apply(new Item.Properties().stacksTo(stackSize)));
        GROUP_ITEMS.add(itemSupplier);
        return itemSupplier;
    }

    private static <T> Supplier<Item> item(String name, int stackSize, BiFunction<T, Item.Properties, Item> itemFactory, T parameter) {
        Supplier<Item> itemSupplier = ITEMS.register(name,
                () -> itemFactory.apply(parameter, new Item.Properties().stacksTo(stackSize)));
        GROUP_ITEMS.add(itemSupplier);
        return itemSupplier;
    }

    private static Supplier<Item> block(String name, Supplier<Block> blockSupplier) {
        Supplier<Item> itemSupplier = ITEMS.register(name,
                () -> new BlockItem(blockSupplier.get(), new Item.Properties()));
        GROUP_BLOCKS.add(itemSupplier);
        return itemSupplier;
    }

    private static Supplier<Item> electronic(String name, Supplier<Block> blockSupplier) {
        Supplier<Item> itemSupplier = ITEMS.register(name,
                () -> new BlockItem(blockSupplier.get(), new Item.Properties()));
        GROUP_ELECTRONICS.add(itemSupplier);
        return itemSupplier;
    }
}

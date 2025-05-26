package volta.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import volta.Volta;
import volta.block.entity.VoltaBlockEntityType;
import volta.block.entity.VoltaBlockEntityTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class VoltaBlocks {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Volta.ID);

    public static final List<Supplier<Block>> GROUP_HORIZONTAL = new ArrayList<>();

    public static final Supplier<Block> CREATIVE_CELL = machine("creative_cell", VoltaBlockEntityTypes.CREATIVE_CELL);
    public static final Supplier<Block> CAPACITOR_BANK = machine("capacitor_bank", VoltaBlockEntityTypes.CAPACITOR_BANK);
    public static final Supplier<Block> INDUCTOR_BANK = machine("inductor_bank", VoltaBlockEntityTypes.INDUCTOR_BANK);
    public static final Supplier<Block> DIODE = machine("diode", VoltaBlockEntityTypes.DIODE);
    public static final Supplier<Block> NPN_TRANSISTOR = machine("npn_transistor", VoltaBlockEntityTypes.NPN_TRANSISTOR);
    public static final Supplier<Block> PNP_TRANSISTOR = machine("pnp_transistor", VoltaBlockEntityTypes.PNP_TRANSISTOR);
    public static final Supplier<Block> WALL_BRACKET = bracket("wall_bracket", VoltaBlockEntityTypes.WALL_BRACKET);
    public static final Supplier<Block> DOUBLE_WALL_BRACKET = bracket("double_wall_bracket", VoltaBlockEntityTypes.DOUBLE_WALL_BRACKET);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    private static Supplier<Block> bracket(String name, Supplier<VoltaBlockEntityType> typeSupplier) {
        Supplier<Block> blockSupplier = BLOCKS.register(name,
                () -> new BracketedVoltaBlock(typeSupplier, BlockBehaviour.Properties.of()));
        GROUP_HORIZONTAL.add(blockSupplier);
        return blockSupplier;
    }

    private static Supplier<Block> machine(String name, Supplier<VoltaBlockEntityType> typeSupplier) {
        Supplier<Block> blockSupplier = BLOCKS.register(name,
                () -> new VoltaBlock(typeSupplier, BlockBehaviour.Properties.of()));
        GROUP_HORIZONTAL.add(blockSupplier);
        return blockSupplier;
    }
}

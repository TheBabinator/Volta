package volta.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import volta.Volta;
import volta.VoltaConfig;
import volta.block.VoltaBlocks;
import volta.electricity.Simulation;
import volta.electricity.Terminal;
import volta.electricity.connections.*;
import volta.lang.Quantity;
import volta.util.SuperSupplier;

import java.util.List;
import java.util.function.Supplier;

public class VoltaBlockEntityTypes {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Volta.ID);

    private static final List<Vec3> DOUBLE_TERMINAL = List.of(
            new Vec3(0.0, 0.4375, -0.3125), new Vec3(0.0, 0.4375, 0.3125)
    );

    private static final List<Vec3> TRI_TERMINAL = List.of(
            new Vec3(0.3125, 0.4375, -0.3125), new Vec3(-0.3125, 0.4375, -0.3125),
            new Vec3(0.0, 0.4375, 0.3125)
    );

    private static final List<Vec3> QUAD_TERMINAL = List.of(
            new Vec3(0.3125, 0.4375, -0.3125), new Vec3(-0.3125, 0.4375, -0.3125),
            new Vec3(-0.3125, 0.4375, 0.3125), new Vec3(0.3125, 0.4375, 0.3125)
    );

    public static final Supplier<VoltaBlockEntityType> WALL_BRACKET =
            simple("wall_bracket", () -> VoltaBlocks.WALL_BRACKET,
                    List.of(new Vec3(0.0, 0.0, -0.0625)));

    public static final Supplier<VoltaBlockEntityType> DOUBLE_WALL_BRACKET =
            simple("double_wall_bracket", () -> VoltaBlocks.DOUBLE_WALL_BRACKET,
                    List.of(new Vec3(0.0, -0.125, -0.0625), new Vec3(0.0, 0.125, -0.0625)));

    public static final Supplier<VoltaBlockEntityType> CREATIVE_CELL =
            initialized("creative_cell", () -> VoltaBlocks.CREATIVE_CELL, DOUBLE_TERMINAL, entity -> {
                entity.getSimulation().addConnection(entity.getTerminal(0), entity.getTerminal(1),
                        new ElectromotiveConnection() {
                            @Override
                            public double getSupplyVoltage() {
                                return VoltaConfig.CREATIVE_CELL_SUPPLY_VOLTAGE.getAsDouble();
                            }
                        });
            }, () -> List.of(
                    Quantity.SUPPLY_VOLTAGE.format(VoltaConfig.CREATIVE_CELL_SUPPLY_VOLTAGE.getAsDouble())
            ));

    public static final Supplier<VoltaBlockEntityType> CAPACITOR_BANK =
            persistent("capacitor_bank", () -> VoltaBlocks.CAPACITOR_BANK, DOUBLE_TERMINAL, entity -> {
                entity.getSimulation().addConnection(entity.getTerminal(0), entity.getTerminal(1),
                        new CapacitiveConnection() {
                            @Override
                            public double getCapacitance() {
                                return VoltaConfig.CAPACITOR_BANK_CAPACITANCE.getAsDouble();
                            }
                        });
            }, (entity, tag) -> {
                Simulation simulation = entity.getSimulation();
                Terminal positive = entity.getTerminal(0);
                Terminal negative = entity.getTerminal(1);
                CapacitiveConnection capacitiveConnection = simulation.findConnection(positive, negative, CapacitiveConnection.class);
                capacitiveConnection.setSignedEnergyStored(tag.getDouble("stored"));
            }, (entity, tag) -> {
                Simulation simulation = entity.getSimulation();
                Terminal positive = entity.getTerminal(0);
                Terminal negative = entity.getTerminal(1);
                CapacitiveConnection capacitiveConnection = simulation.findConnection(positive, negative, CapacitiveConnection.class);
                tag.putDouble("stored", capacitiveConnection.getSignedEnergyStored());
            }, () -> List.of(
                    Quantity.INTERNAL_CAPACITANCE.format(VoltaConfig.CAPACITOR_BANK_CAPACITANCE.getAsDouble())
            ));

    public static final Supplier<VoltaBlockEntityType> INDUCTOR_BANK =
            persistent("inductor_bank", () -> VoltaBlocks.INDUCTOR_BANK, DOUBLE_TERMINAL, entity -> {
                entity.getSimulation().addConnection(entity.getTerminal(0), entity.getTerminal(1),
                        new InductiveConnection() {
                            @Override
                            public double getInductance() {
                                return VoltaConfig.INDUCTOR_BANK_INDUCTANCE.getAsDouble();
                            }
                        });
            }, (entity, tag) -> {
                Simulation simulation = entity.getSimulation();
                Terminal positive = entity.getTerminal(0);
                Terminal negative = entity.getTerminal(1);
                InductiveConnection inductiveConnection = simulation.findConnection(positive, negative, InductiveConnection.class);
                inductiveConnection.setSignedEnergyStored(tag.getDouble("stored"));
            }, (entity, tag) -> {
                Simulation simulation = entity.getSimulation();
                Terminal positive = entity.getTerminal(0);
                Terminal negative = entity.getTerminal(1);
                InductiveConnection inductiveConnection = simulation.findConnection(positive, negative, InductiveConnection.class);
                tag.putDouble("stored", inductiveConnection.getSignedEnergyStored());
            }, () -> List.of(
                    Quantity.INDUCTANCE.format(VoltaConfig.INDUCTOR_BANK_INDUCTANCE.getAsDouble())
            ));

    public static final Supplier<VoltaBlockEntityType> DIODE =
            initialized("diode", () -> VoltaBlocks.DIODE, DOUBLE_TERMINAL, entity -> {
                entity.getSimulation().addConnection(entity.getTerminal(1), entity.getTerminal(0),
                        new DiodeConnection() {
                            @Override
                            public double getVoltageDrop() {
                                return VoltaConfig.DIODE_VOLTAGE_DROP.getAsDouble();
                            }
                        });
            }, () -> List.of(
                    Quantity.VOLTAGE_DROP.format(VoltaConfig.DIODE_VOLTAGE_DROP.getAsDouble())
            ));

    public static final Supplier<VoltaBlockEntityType> NPN_TRANSISTOR =
            initialized("npn_transistor", () -> VoltaBlocks.NPN_TRANSISTOR, TRI_TERMINAL, entity -> {
                entity.getSimulation().addConnection(entity.getTerminal(0), entity.getTerminal(1),
                        new NPNTransistorConnection(entity.getTerminal(2)) {
                            @Override
                            public double getBaseVoltageDrop() {
                                return VoltaConfig.TRANSISTOR_BASE_VOLTAGE_DROP.getAsDouble();
                            }

                            @Override
                            public double getCurrentGain() {
                                return VoltaConfig.TRANSISTOR_CURRENT_GAIN.getAsDouble();
                            }
                        });
            }, () -> List.of(
                    Quantity.BASE_VOLTAGE_DROP.format(VoltaConfig.TRANSISTOR_BASE_VOLTAGE_DROP.getAsDouble()),
                    Quantity.CURRENT_GAIN.format(VoltaConfig.TRANSISTOR_CURRENT_GAIN.getAsDouble())
            ));

    public static final Supplier<VoltaBlockEntityType> PNP_TRANSISTOR =
            initialized("pnp_transistor", () -> VoltaBlocks.PNP_TRANSISTOR, TRI_TERMINAL, entity -> {
                entity.getSimulation().addConnection(entity.getTerminal(0), entity.getTerminal(1),
                        new PNPTransistorConnection(entity.getTerminal(2)) {
                            @Override
                            public double getBaseVoltageDrop() {
                                return VoltaConfig.TRANSISTOR_BASE_VOLTAGE_DROP.getAsDouble();
                            }

                            @Override
                            public double getCurrentGain() {
                                return VoltaConfig.TRANSISTOR_CURRENT_GAIN.getAsDouble();
                            }
                        });
            }, () -> List.of(
                    Quantity.BASE_VOLTAGE_DROP.format(VoltaConfig.TRANSISTOR_BASE_VOLTAGE_DROP.getAsDouble()),
                    Quantity.CURRENT_GAIN.format(VoltaConfig.TRANSISTOR_CURRENT_GAIN.getAsDouble())
            ));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

    private static Supplier<VoltaBlockEntityType> simple(String name, SuperSupplier<Block> blockSupplier, List<Vec3> terminals) {
        return BLOCK_ENTITY_TYPES.register(name, () -> VoltaBlockEntityType.of(blockSupplier.getGet(), terminals));
    }

    private static Supplier<VoltaBlockEntityType> initialized(String name, SuperSupplier<Block> blockSupplier, List<Vec3> terminals, VoltaBlockEntityType.Handler initializer, Supplier<List<Component>> tooltipSource) {
        return BLOCK_ENTITY_TYPES.register(name, () -> VoltaBlockEntityType.of(blockSupplier.getGet(), terminals).withInitializer(initializer).withTooltipSource(tooltipSource));
    }

    private static Supplier<VoltaBlockEntityType> persistent(String name, SuperSupplier<Block> blockSupplier, List<Vec3> terminals, VoltaBlockEntityType.Handler initializer, VoltaBlockEntityType.TagHandler loader, VoltaBlockEntityType.TagHandler saver, Supplier<List<Component>> tooltipSource) {
        return BLOCK_ENTITY_TYPES.register(name, () -> VoltaBlockEntityType.of(blockSupplier.getGet(), terminals).withInitializer(initializer).withLoader(loader).withSaver(saver).withTooltipSource(tooltipSource));
    }
}

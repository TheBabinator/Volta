package volta.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import volta.VoltaConfig;
import volta.lang.Quantity;
import volta.util.Holder;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class VoltaBlockEntityType extends BlockEntityType<VoltaBlockEntity> {
    private final List<Vec3> terminalPositions;
    private Handler initializer = Handler.EMPTY;
    private Handler ticker = Handler.EMPTY;
    private TagHandler loader = TagHandler.EMPTY;
    private TagHandler saver = TagHandler.EMPTY;
    private Supplier<List<Component>> tooltipSource = List::of;

    @SuppressWarnings("all")
    private VoltaBlockEntityType(BlockEntitySupplier<VoltaBlockEntity> factory, Block block, List<Vec3> terminalPositions) {
        super(factory, Set.of(block), null);
        this.terminalPositions = terminalPositions;
    }

    public static VoltaBlockEntityType of(Block block, List<Vec3> terminalPositions) {
        Holder<VoltaBlockEntityType> reference = new Holder<>();
        VoltaBlockEntityType type = new VoltaBlockEntityType(
                (blockPos, blockState) -> new VoltaBlockEntity(reference.get(), blockPos, blockState),
                block, terminalPositions
        );
        reference.accept(type);
        return type;
    }

    public VoltaBlockEntityType withInitializer(Handler initializer) {
        this.initializer = initializer;
        return this;
    }

    public VoltaBlockEntityType withTicker(Handler ticker) {
        this.ticker = ticker;
        return this;
    }

    public VoltaBlockEntityType withLoader(TagHandler loader) {
        this.loader = loader;
        return this;
    }

    public VoltaBlockEntityType withSaver(TagHandler saver) {
        this.saver = saver;
        return this;
    }

    public VoltaBlockEntityType withTooltipSource(Supplier<List<Component>> tooltipSource) {
        this.tooltipSource = tooltipSource;
        return this;
    }

    public <T extends BlockEntity> void genericServerTick(Level level, BlockPos blockPos, BlockState blockState, T blockEntity) {
        if (blockEntity instanceof VoltaBlockEntity voltaBlockEntity) {
            voltaBlockEntity.tick();
        }
    }

    public List<Vec3> getTerminalPositions() {
        return terminalPositions;
    }

    public Handler getInitializer() {
        return initializer;
    }

    public Handler getTicker() {
        return ticker;
    }

    public TagHandler getLoader() {
        return loader;
    }

    public TagHandler getSaver() {
        return saver;
    }

    public void appendHoverText(List<Component> tooltipComponents) {
        tooltipComponents.add(Quantity.TERMINAL_CAPACITANCE.format(VoltaConfig.TERMINAL_CAPACITANCE.getAsDouble()));
        tooltipComponents.addAll(tooltipSource.get());
    }

    public interface Handler {
        Handler EMPTY = ignored -> {};

        void accept(VoltaBlockEntity entity);
    }

    public interface TagHandler {
        TagHandler EMPTY = (ignored1, ignored2) -> {};

        void accept(VoltaBlockEntity entity, CompoundTag tag);
    }
}

package volta.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import volta.block.VoltaBlock;
import volta.electricity.LevelSimulationManager;
import volta.electricity.Simulation;
import volta.electricity.Terminal;
import volta.electricity.TerminalProvider;
import volta.electricity.terminals.VoltaTerminal;
import volta.util.Terminals;

import java.util.ArrayList;
import java.util.List;

public class VoltaBlockEntity extends BlockEntity implements TerminalProvider {
    private final VoltaBlockEntityType type;
    private final List<Terminal> terminals = new ArrayList<>();
    private @Nullable Simulation simulation;
    private @Nullable CompoundTag unappliedData;

    public VoltaBlockEntity(VoltaBlockEntityType type, BlockPos blockPos, BlockState blockState) {
        super(type, blockPos, blockState);
        this.type = type;
        for (Vec3 position : type.getTerminalPositions()) {
            terminals.add(new VoltaTerminal(this, position));
        }
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        simulation = LevelSimulationManager.getSimulation(level);
        for (Terminal terminal : terminals) {
            simulation.addTerminal(terminal);
        }
        type.getInitializer().accept(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (simulation == null) {
            return;
        }
        for (Terminal terminal : terminals) {
            simulation.removeTerminal(terminal);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        unappliedData = tag;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        Terminals.saveListIntoTag(terminals, tag, registries);
        type.getSaver().accept(this, tag);
    }

    @Override
    public BlockEntity getBlockEntity() {
        return this;
    }

    @Override
    public Simulation getSimulation() {
        if (simulation == null) {
            throw new IllegalStateException();
        }
        return simulation;
    }

    @Override
    public Terminal getTerminal(int i) {
        return terminals.get(i);
    }

    @Override
    public int getTerminalIndex(Terminal terminal) {
        return terminals.indexOf(terminal);
    }

    @Override
    public int getTerminalCount() {
        return terminals.size();
    }

    public Vec3 toWorldPosition(Vec3 offset) {
        return offset.yRot(-Mth.DEG_TO_RAD * getBlockState().getValue(VoltaBlock.FACING).toYRot()).add(getBlockPos().getCenter());
    }

    public void tick() {
        if (level == null || simulation == null) {
            return;
        }
        if (unappliedData != null) {
            for (Terminal terminal : terminals) {
                simulation.removeTerminal(terminal);
                simulation.addTerminal(terminal);
            }
            type.getInitializer().accept(this);
            Terminals.loadListFromTag(terminals, unappliedData, level.registryAccess());
            type.getLoader().accept(this, unappliedData);
        }
        type.getTicker().accept(this);
    }
}

package volta.electricity.terminals;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import volta.VoltaConfig;
import volta.block.entity.VoltaBlockEntity;
import volta.electricity.TerminalProvider;

public class VoltaTerminal extends BaseTerminal {
    private static final VoxelShape SHAPE = Shapes.box(-0.0625, -0.0625, -0.0625, 0.0625, 0.0625, 0.0625);
    private final VoltaBlockEntity entity;
    private final Vec3 offset;

    public VoltaTerminal(VoltaBlockEntity entity, Vec3 offset) {
        this.entity = entity;
        this.offset = offset;
    }

    @Override
    public double getCapacitance() {
        return VoltaConfig.TERMINAL_CAPACITANCE.getAsDouble();
    }

    @Override
    public boolean isInvalid() {
        return entity.isRemoved();
    }

    @Override
    public Vec3 getWorldPosition() {
        return entity.toWorldPosition(offset);
    }

    @Override
    public VoxelShape getShape() {
        return SHAPE;
    }

    @Override
    public @Nullable TerminalProvider getTerminalProvider() {
        return entity;
    }
}

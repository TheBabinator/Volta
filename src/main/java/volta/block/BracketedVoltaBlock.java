package volta.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import volta.block.entity.VoltaBlockEntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BracketedVoltaBlock extends VoltaBlock {
    private static final Map<Direction, VoxelShape> SHAPES = new HashMap<>();
    private static final Map<Direction, VoxelShape> SUPPORT_SHAPES = new HashMap<>();

    public BracketedVoltaBlock(Supplier<VoltaBlockEntityType> typeSupplier, Properties properties) {
        super(typeSupplier, properties);
    }

    @Override
    protected VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPES.get(blockState.getValue(FACING));
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState blockState, BlockGetter level, BlockPos blockPos) {
        return SUPPORT_SHAPES.get(blockState.getValue(FACING));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        BlockState blockState;
        Direction normalDirection = blockPlaceContext.getClickedFace();
        Direction facingDirection = blockPlaceContext.getHorizontalDirection();
        if (normalDirection.getAxis() == Direction.Axis.Y) {
            blockState = defaultBlockState().setValue(FACING, facingDirection.getOpposite());
        } else {
            blockState = defaultBlockState().setValue(FACING, normalDirection);
        }
        if (blockState.canSurvive(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos())) {
            return blockState;
        }
        return null;
    }

    static {
        SHAPES.put(Direction.NORTH, Shapes.box(0.25, 0.0, 0.75, 0.75, 1.0, 1.0));
        SHAPES.put(Direction.EAST, Shapes.box(0.0, 0.0, 0.25, 0.25, 1.0, 0.75));
        SHAPES.put(Direction.SOUTH, Shapes.box(0.25, 0.0, 0.0, 0.75, 1.0, 0.25));
        SHAPES.put(Direction.WEST, Shapes.box(0.75, 0.0, 0.25, 1.0, 1.0, 0.75));
        SUPPORT_SHAPES.put(Direction.NORTH, Shapes.box(0.0, 0.0, 0.75, 1.0, 1.0, 1.0));
        SUPPORT_SHAPES.put(Direction.EAST, Shapes.box(0.0, 0.0, 0.0, 0.25, 1.0, 1.0));
        SUPPORT_SHAPES.put(Direction.SOUTH, Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 0.25));
        SUPPORT_SHAPES.put(Direction.WEST, Shapes.box(0.75, 0.0, 0.0, 1.0, 1.0, 1.0));
    }
}

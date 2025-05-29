package volta.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import volta.block.entity.VoltaBlockEntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class LeverVoltaBlock extends BracketedVoltaBlock {
    private static final Map<Direction, VoxelShape> SHAPES = new HashMap<>();
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public LeverVoltaBlock(Supplier<VoltaBlockEntityType> typeSupplier, Properties properties) {
        super(typeSupplier, properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPES.get(blockState.getValue(FACING));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            pull(blockState, level, blockPos);
            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected void onExplosionHit(BlockState blockState, Level level, BlockPos blockPos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer) {
        if (explosion.canTriggerBlocks()) {
            pull(blockState, level, blockPos);
        }
        super.onExplosionHit(blockState, level, blockPos, explosion, dropConsumer);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    private void pull(BlockState blockState, Level level, BlockPos blockPos) {
        BlockState newState = blockState.cycle(POWERED);
        level.setBlock(blockPos, newState, 3);
        level.playSound(null, blockPos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3f, newState.getValue(POWERED) ? 0.6f : 0.5f);
        level.gameEvent(null, newState.getValue(POWERED) ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, blockPos);
    }

    static {
        SHAPES.put(Direction.NORTH, Shapes.box(0.25, 0.25, 0.625, 0.75, 0.75, 1.0));
        SHAPES.put(Direction.EAST, Shapes.box(0.0, 0.25, 0.25, 0.375, 0.75, 0.75));
        SHAPES.put(Direction.SOUTH, Shapes.box(0.25, 0.25, 0.0, 0.75, 0.75, 0.375));
        SHAPES.put(Direction.WEST, Shapes.box(0.625, 0.25, 0.25, 1.0, 0.75, 0.75));
    }
}

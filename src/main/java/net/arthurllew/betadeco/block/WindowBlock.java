package net.arthurllew.betadeco.block;

import net.arthurllew.betadeco.registry.BetaDecoSounds;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Window block from Deko-Mod (for beta 1.7.3).
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WindowBlock extends Block {
    /**
     * Stores direction state.
     */
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    /**
     * Stores window opened/closed state.
     */
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    /**
     * Stores hinge state.
     */
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
    /**
     * Stores redstone power state.
     */
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    // Block shapes
    protected static final VoxelShape NORTH_SHAPE = Block.box(0.0, 0.0, 14.5, 16.0, 16.0, 16.0);
    protected static final VoxelShape WEST_SHAPE = Block.box(14.5, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.5);
    protected static final VoxelShape EAST_SHAPE = Block.box(0.0, 0.0, 0.0, 1.5, 16.0, 16.0);

    /**
     * Constructor matching super (also configures default block state).
     * @param properties block settings.
     */
    public WindowBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(OPEN, false)
                        .setValue(HINGE, DoorHingeSide.LEFT)
                        .setValue(POWERED, false));
    }

    /**
     * Connects properties to a block state.
     * @param builder block state builder.
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, HINGE, POWERED);
    }

    /**
     * Returns block state with properties reflecting placement context.
     * @param context placement context.
     * @return block state.
     */
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Get redstone power
        boolean isPowered = context.getLevel().hasNeighborSignal(context.getClickedPos());

        // Placement state
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(HINGE, this.getHinge(context))
                .setValue(POWERED, isPowered)
                .setValue(OPEN, isPowered);
    }

    /**
     * Determines door hinge from context.
     * @param context placement context.
     * @return door hinge.
     */
    private DoorHingeSide getHinge(BlockPlaceContext context) {
        BlockGetter blockView = context.getLevel();

        // Direction player is facing
        BlockPos blockPos = context.getClickedPos();
        Direction direction = context.getHorizontalDirection();

        // Block to the left
        Direction directionLeft = direction.getCounterClockWise();
        BlockPos blockPosLeft = blockPos.relative(directionLeft);
        BlockState blockStateLeft = blockView.getBlockState(blockPosLeft);
        boolean hasWindowLeft = blockStateLeft.is(this);

        // Block to the right
        Direction directionRight = direction.getClockWise();
        BlockPos blockPosRight = blockPos.relative(directionRight);
        BlockState blockStateRight = blockView.getBlockState(blockPosRight);
        boolean hasWindowRight = blockStateRight.is(this);

        // Check left and right blocks
        if (!hasWindowLeft || hasWindowRight && blockStateLeft.isCollisionShapeFullBlock(blockView, blockPosLeft)) {
            if (!hasWindowRight || hasWindowLeft && blockStateRight.isCollisionShapeFullBlock(blockView, blockPosRight)) {
                // Check hit position
                int offsetX = direction.getStepX();
                int offsetZ = direction.getStepZ();
                Vec3 hitPosition = context.getClickLocation();
                double hitX = hitPosition.x - (double)blockPos.getX();
                double hitZ = hitPosition.z - (double)blockPos.getZ();
                return (offsetX >= 0 || !(hitZ < 0.5))
                        && (offsetX <= 0 || !(hitZ > 0.5))
                        && (offsetZ >= 0 || !(hitX > 0.5))
                        && (offsetZ <= 0 || !(hitX < 0.5)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
            } else {
                return DoorHingeSide.LEFT;
            }
        } else {
            return DoorHingeSide.RIGHT;
        }
    }

    /**
     * Calculates block outline shape.
     * @param state block state.
     * @param level world.
     * @param pos block position.
     * @param context shape context.
     * @return outline shape.
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean isOpened = state.getValue(OPEN);
        boolean isHingeRight = state.getValue(HINGE) == DoorHingeSide.RIGHT;

        switch (state.getValue(FACING)) {
            case NORTH:
                return isOpened ? (isHingeRight ? EAST_SHAPE : WEST_SHAPE) : NORTH_SHAPE;
            case WEST:
                return isOpened ? (isHingeRight ? NORTH_SHAPE : SOUTH_SHAPE) : WEST_SHAPE;
            case SOUTH:
                return isOpened ? (isHingeRight ? WEST_SHAPE : EAST_SHAPE) : SOUTH_SHAPE;
            case EAST:
            default:
                return isOpened ? (isHingeRight ? SOUTH_SHAPE : NORTH_SHAPE) : EAST_SHAPE;
        }
    }

    /**
     * Fires when block is used (right mouse button) by a player.
     * @param state block state.
     * @param level world.
     * @param pos block position.
     * @param player player who used block.
     * @return interaction result.
     */
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                            BlockHitResult hitResult) {
        // Cycle opened state
        state = state.cycle(OPEN);

        // Update block state
        level.setBlock(pos, state, Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE);

        // Play sound
        this.playOpenCloseSound(player, level, pos, state.getValue(OPEN));
        // Emit event
        level.gameEvent(player, state.getValue(OPEN) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);

        // Action succeeded (depends on world side)
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /**
     * Determines a new block state after a neighboring block was changed.
     * @param state block.
     * @param direction block direction.
     * @param neighborState block neighbor.
     * @param level world.
     * @param pos block position.
     * @param neighborPos block neighbor position.
     * @return block state for neighbor.
     */
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                                LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        // If neighbor is window and direction is along Y axis
        if (level.getBlockState(neighborPos).is(this) && direction.getAxis() == Direction.Axis.Y) {
            // Update state to match the neighbor
            return state.setValue(OPEN, neighborState.getValue(OPEN));
        }

        // Default behaviour
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    /**
     * Called after a neighboring block was changed.
     * @param state block.
     * @param level world.
     * @param pos block position.
     * @param sourceBlock update source block.
     * @param sourcePos update source position.
     * @param notify block neighbor position.
     */
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos,
                                boolean notify) {
        // Whether this position is redstone powered
        boolean isPowered = level.hasNeighborSignal(pos);
        // Determine whether window is now closed/opened (state itself + redstone power change)
        boolean isOpened = (state.getValue(POWERED) == isPowered) ? this.isOpened(state, level, pos) : isPowered;

        // If window state will be changed
        if (isOpened != state.getValue(OPEN)) {
            // Play sound
            this.playOpenCloseSound(null, level, pos, isOpened);
            // Emit event
            level.gameEvent(null, isOpened ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);

            // Update state
            level.setBlock(pos, state.setValue(OPEN, isOpened).setValue(POWERED, isPowered), Block.UPDATE_ALL);
        }
        // Update power state
        else if (state.getValue(POWERED) != isPowered) {
            // Update state
            level.setBlock(pos, state.setValue(POWERED, isPowered), Block.UPDATE_CLIENTS);
        }
    }

    /**
     * @return whether an NPC can pathfind through this block.
     */
    @Override
    public boolean isPathfindable(BlockState state, PathComputationType type) {
        switch (type) {
            case LAND:
            case AIR:
                return state.getValue(OPEN);
            default:
                return false;
        }
    }

    /**
     * @return whether window is opened given current block state and up/down neighbors.
     */
    protected boolean isOpened(BlockState state, Level level, BlockPos pos) {
        // Whether window was opened/closed by itself
        boolean isOpened = state.getValue(OPEN);
        // Check if block below or above is a window and is redstone powered
        BlockState block = level.getBlockState(pos.below());
        if (block.is(this)) {
            isOpened = isOpened || block.getValue(OPEN);
        }
        block = level.getBlockState(pos.above());
        if (block.is(this)) {
            isOpened = isOpened || block.getValue(OPEN);
        }
        return isOpened;
    }

    /**
     * Plays window open/close sound.
     * @param entity entity.
     * @param level level.
     * @param pos block position.
     * @param isOpened whether window is opened.
     */
    private void playOpenCloseSound(@Nullable Entity entity, Level level, BlockPos pos, boolean isOpened) {
        level.playSound(entity, pos,
                isOpened ? BetaDecoSounds.DEKO_WINDOW_SOUND_OPEN.get() : BetaDecoSounds.DEKO_WINDOW_SOUND_CLOSE.get(),
                SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    /**
     * Rotates block.
     * @param state block state.
     * @param rotation rotation context.
     * @return block state corresponding to rotation context.
     */
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    /**
     * Mirrors block.
     * @param state block state.
     * @param mirror mirror context.
     * @return block state corresponding to mirror context.
     */
    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return mirror == Mirror.NONE ? state : state.rotate(mirror.getRotation(state.getValue(FACING))).cycle(HINGE);
    }
}

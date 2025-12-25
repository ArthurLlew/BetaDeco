package net.arthurllew.betadeco.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RopeBlock extends ChainBlock {
    // Block sizes
    protected static final VoxelShape Y_AXIS_AABB = Block.box(6.7D, 0.0D, 6.7D, 9.3D, 16.0D, 9.3D);
    protected static final VoxelShape Z_AXIS_AABB = Block.box(6.7D, 6.7D, 0.0D, 9.3D, 9.3D, 16.0D);
    protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 6.7D, 6.7D, 16.0D, 9.3D, 9.3D);

    /**
     * Constructor
     */
    public RopeBlock(Properties properties) {
        super(properties);
    }

    /**
     * @return block voxel shape (collision, outline, etc.)
     */
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        switch (state.getValue(AXIS)) {
            case Z:
                return Z_AXIS_AABB;
            case Y:
                return Y_AXIS_AABB;
            case X:
            default:
                return X_AXIS_AABB;
        }
    }

    /**
     * @return whether this block can be replaced with item, provided by use-context.
     */
    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return useContext.getItemInHand().is(this.asItem());
    }

    /**
     * Is called on block update.
     * @return parent method result if block can survive, air otherwise.
     */
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        // If block can't survive
        if (!state.canSurvive(level, pos)) {
            // Update water
            if (state.getValue(WATERLOGGED)) {
                level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            }

            // Replace with air
            return Blocks.AIR.defaultBlockState();
        }

        // Run parent method
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    /**
     * @return wherether this block can stay at its position
     */
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        // If rope is vertical
        if (state.getValue(AXIS) == Direction.Axis.Y) {
            // Check support condition above this block
            Direction direction = Direction.UP;
            return Block.canSupportCenter(level, pos.relative(direction), direction.getOpposite());
        }

        // Horizontal directions are not checked
        return true;
    }

    /**
     * @return {@code false} to make AI treat this block as solid (or something like it).
     */
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }
}

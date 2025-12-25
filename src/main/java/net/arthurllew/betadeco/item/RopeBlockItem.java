package net.arthurllew.betadeco.item;

import net.arthurllew.betadeco.block.RopeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class RopeBlockItem extends BlockItem {
    public RopeBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    /**
     * Updates placement context if rope block-item was clicked on rope, rope faces downwards and rope column
     * below can be extended by one block. Adopted from {@link net.minecraft.world.item.ScaffoldingBlockItem}.
     * @return unchanged or new placement context.
     */
    @Nullable
    public BlockPlaceContext updatePlacementContext(BlockPlaceContext context) {
        // Placement context
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        BlockState blockState = level.getBlockState(pos);
        Block rope = this.getBlock();

        // If target is rope
        if (blockState.is(rope)) {
            // If rope is vertical and clicked face is not directed up
            if ((blockState.getValue(RopeBlock.AXIS) == Direction.Axis.Y)
                    && (context.getClickedFace() != Direction.UP)) {
                // Get mutable block position below clicked block
                BlockPos.MutableBlockPos movablePos = pos.mutable().move(Direction.DOWN);

                // Iterate over all rope blocks below
                while (level.getBlockState(movablePos).is(rope)) {
                    movablePos.move(Direction.DOWN);
                }

                BlockState block = level.getBlockState(movablePos);
                // If block below rope column is air and position is in world height bounds
                if (block.isAir() && !level.isOutsideBuildHeight(movablePos) || block.is(Blocks.WATER)) {
                    // Return new placement context (will extend rope column by one block)
                    return BlockPlaceContext.at(context, movablePos, Direction.DOWN);
                }
            }

            // Normal block placement context direction and position
            Direction direction = context.isInside() ? context.getClickedFace().getOpposite() : context.getClickedFace();
            BlockPos.MutableBlockPos movablePos = pos.mutable().move(direction);
            // Check world bounds
            if (level.isInWorldBounds(movablePos)) {
                // Check air
                if (level.getBlockState(movablePos).isAir()) {
                    // Imitate normal block placement
                    return BlockPlaceContext.at(context, pos.relative(direction), direction);
                }
            }

            // We can't place block anywhere
            return null;
        }

        // Return unchanged context
        return context;
    }
}

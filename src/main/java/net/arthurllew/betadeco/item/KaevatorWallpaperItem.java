package net.arthurllew.betadeco.item;

import net.arthurllew.betadeco.entity.KaevatorWallpaperEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Wallpaper item.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class KaevatorWallpaperItem extends Item {
    /**
     * Constructor matching super.
     * @param settings item settings.
     */
    public KaevatorWallpaperItem(Item.Properties settings) {
        super(settings);
    }

    /**
     * Called when item is used on a block (code adopted from {@link HangingEntityItem}).
     * @param context usage context.
     * @return action result.
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        // Get context data
        BlockPos blockPos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        BlockPos blockPos2 = blockPos.relative(facing);
        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();

        // Check player
        if (player != null && !this.mayPlace(player, facing, itemStack, blockPos2)) {
            return InteractionResult.FAIL;
        } else {
            // Create wallpaper
            Level level = context.getLevel();
            HangingEntity wallpaper = new KaevatorWallpaperEntity(level, blockPos2, facing);

            // Check custom data
            CustomData data = itemStack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
            EntityType.updateCustomEntityTag(level, player, wallpaper, data);

            // Check if entity can stay attached
            if (wallpaper.survives()) {

                // Spawn entity
                if (!level.isClientSide) {
                    wallpaper.playPlacementSound();
                    level.gameEvent(player, GameEvent.ENTITY_PLACE, wallpaper.getPos());
                    level.addFreshEntity(wallpaper);
                }

                // Consume item
                itemStack.shrink(1);
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                // Consume item
                return InteractionResult.CONSUME;
            }
        }
    }

    /**
     * @return whether item can be placed (code adopted from {@link HangingEntityItem})
     */
    protected boolean mayPlace(Player player, Direction direction, ItemStack hangingEntityStack, BlockPos pos) {
        return !direction.getAxis().isVertical() && player.mayUseItemAt(pos, direction, hangingEntityStack);
    }
}

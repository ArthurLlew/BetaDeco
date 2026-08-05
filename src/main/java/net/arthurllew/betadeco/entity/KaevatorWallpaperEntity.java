package net.arthurllew.betadeco.entity;

import net.arthurllew.betadeco.core.component.WallpaperVariant;
import net.arthurllew.betadeco.registry.BetaDecoDataComponents;
import net.arthurllew.betadeco.registry.BetaDecoEntityTypes;
import net.arthurllew.betadeco.registry.BetaDecoItems;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Wallpaper entity from Kaevator's Wallpaper mod (for beta 1.7.3). Originally it was also an entity.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class KaevatorWallpaperEntity extends HangingEntity {
    /**
     * Wallpaper variant.
     */
    private static final EntityDataAccessor<Byte> VARIANT =
            SynchedEntityData.defineId(KaevatorWallpaperEntity.class, EntityDataSerializers.BYTE);
    /**
     * Wallpaper color.
     */
    private static final EntityDataAccessor<Integer> COLOR =
            SynchedEntityData.defineId(KaevatorWallpaperEntity.class, EntityDataSerializers.INT);

    /**
     * This constructor is used to register entity type.
     */
    public KaevatorWallpaperEntity(EntityType<? extends KaevatorWallpaperEntity> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * This constructor is used to spawn entity.
     */
    public KaevatorWallpaperEntity(Level level, BlockPos pos, Direction facing, byte variant) {
        super(BetaDecoEntityTypes.KAEVATOR_WALLPAPER_ENTITY_TYPE.get(), level, pos);
        // Set facing
        this.setDirection(facing);
        // Set wallpaper variant
        this.entityData.set(VARIANT, variant);
    }

    /**
     * Initializes data tracker. Data tracker tracks various value related to entity instance. The renderer needs it
     * in order to get actual entity data, like wallpaper variant.
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(VARIANT, (byte)0);
        // Firework color matches color values used in beta 1.7.3
        builder.define(COLOR, DyeColor.WHITE.getFireworkColor());
    }

    /**
     * Retrieves wallpaper variant.
     * @return wallpaper variant.
     */
    public byte getVariant() {
        return this.entityData.get(VARIANT);
    }

    /**
     * Retrieves wallpaper color.
     * @return wallpaper color.
     */
    public int getColor() {
        return this.entityData.get(COLOR);
    }

    /**
     * Calculates entity bounding box.
     */
    @Override
    protected AABB calculateBoundingBox(BlockPos pos, Direction direction) {
        Vec3 vec3 = Vec3.atCenterOf(pos).relative(direction, -0.46875F);
        Direction.Axis directionAxis = direction.getAxis();
        double xSize = directionAxis == Direction.Axis.X ? 0.46875D : 1.0D;
        double ySize = directionAxis == Direction.Axis.Y ? 0.46875D : 1.0D;
        double zSize = directionAxis == Direction.Axis.Z ? 0.46875D : 1.0D;
        return AABB.ofSize(vec3, xSize, ySize, zSize);
    }

    /**
     * Determines whether the entity can stay attached to a block.
     * @return whether the entity can stay attached.
     */
    @Override
    public boolean survives() {
        // Block in wallpaper location
        BlockState blockState1 = this.level().getBlockState(this.pos);
        // Opposite block (to witch wallpaper is attached)
        BlockState blockState2 = this.level().getBlockState(this.pos.relative(this.direction.getOpposite()));
        // Block in wallpaper location must not be a full cube (the same check was performed in Beta 1.7.3)
        // and opposite block should be solid or be a redstone gate
        return !blockState1.isCollisionShapeFullBlock(this.level(), this.pos)
                && blockState2.isSolid() || DiodeBlock.isDiode(blockState2);
    }

    /**
     * Called, when decoration entity is placed.
     */
    @Override
    public void playPlacementSound() {}

    /**
     * Called when a player interacts with this entity.
     * @return action result.
     */
    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        // Hand is not empty and entity is not removed
        if (!itemStack.isEmpty() && !this.isRemoved()) {
            // If wallpaper is in hand
            if (itemStack.is(BetaDecoItems.KAEVATOR_WALLPAPER.get())) {
                // Cycle wallpaper variant
                byte variant = this.getVariant();
                variant++; // Avoids conversion from int to byte
                variant = variant < 24 ? variant : 0;

                // Server-side actions
                if (!this.level().isClientSide) {
                    // Set wallpaper variant
                    this.entityData.set(VARIANT, variant);

                    // Emit event to tell that block was changed
                    this.gameEvent(GameEvent.BLOCK_CHANGE, player);
                }

                // Remember last used variant inside item
                itemStack.set(BetaDecoDataComponents.WALLPAPER_VARIANT_COMPONENT.get(), new WallpaperVariant(variant));

                // Action succeeded
                return InteractionResult.SUCCESS;
            }
            // If dye in hand
            else if (itemStack.getItem() instanceof DyeItem dye) {
                // Color should differ
                int color = dye.getDyeColor().getFireworkColor();
                if (color != getColor()) {
                    // Server-side actions
                    if (!this.level().isClientSide) {
                        // Change color
                        this.entityData.set(COLOR, dye.getDyeColor().getFireworkColor());
                        // Decrement item stack if player is not in creative
                        if (!player.isCreative()) {
                            itemStack.shrink(1);
                        }

                        // Emit event to tell that block was changed
                        this.gameEvent(GameEvent.BLOCK_CHANGE, player);
                    }

                    // Action succeeded
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }

    /**
     * Called when entity is broken by something.
     * @param entity who broke this entity.
     */
    @Override
    public void dropItem(@Nullable Entity entity) {
        // Check game rules
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            // Drop item if wallpaper was broken by a player and that player is not in creative mode
            if (entity instanceof Player playerEntity && playerEntity.getAbilities().instabuild) {
                return;
            }
            this.spawnAtLocation(BetaDecoItems.KAEVATOR_WALLPAPER.get().getDefaultInstance());
        }
    }

    /**
     * Determines, whether the entity should render at given render distance.
     * @param distance render distance.
     * @return whether the entity should render.
     */
    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d = 16.0;
        d *= 64.0 * getViewScale();
        return distance < d * d;
    }

    /**
     * Retrieves item stack for creative "pick block" player action.
     * @return wallpaper item.
     */
    @Override
    public ItemStack getPickResult() {
        return BetaDecoItems.KAEVATOR_WALLPAPER.get().getDefaultInstance();
    }

    /**
     * Writes custom NBT data into save file.
     */
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        // Write facing
        tag.putByte("facing", (byte)this.direction.get3DDataValue());
        // Write variant
        tag.putByte("variant", getVariant());
        // Write color
        tag.putInt("color", getColor());

        // Write parent data
        super.addAdditionalSaveData(tag);
    }

    /**
     * Reads custom NBT data from save file.
     */
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        // Read parent data
        super.readAdditionalSaveData(tag);

        // Read color
        this.entityData.set(COLOR, tag.getInt("color"));
        // Read variant
        this.entityData.set(VARIANT, tag.getByte("variant"));
        // Read facing
        this.direction = Direction.from3DDataValue(tag.getByte("facing"));

        // Set facing
        this.setDirection(this.direction);
    }

    /**
     * Creates entity spawn packet.
     * @return paket.
     */
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        return new ClientboundAddEntityPacket(this, this.direction.get3DDataValue(), this.getPos());
    }

    /**
     * Actions performed upon receiving spawn packet.
     */
    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.setDirection(Direction.from3DDataValue(packet.getData()));
    }
}

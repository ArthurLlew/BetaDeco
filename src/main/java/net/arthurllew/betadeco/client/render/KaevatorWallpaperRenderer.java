package net.arthurllew.betadeco.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.arthurllew.betadeco.BetaDeco;
import net.arthurllew.betadeco.entity.KaevatorWallpaperEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Renderer for Kaevator's Wallpaper entity.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class KaevatorWallpaperRenderer extends EntityRenderer<KaevatorWallpaperEntity> {
    /**
     * Wallpaper texture.
     */
    private final ResourceLocation wallpaperTexture = ResourceLocation
            .fromNamespaceAndPath(BetaDeco.MODID, "kaevator_wallpaper");

    /**
     * Constructor.
     */
    public KaevatorWallpaperRenderer(EntityRendererProvider.Context context) {
        super(context);

        // Register wallpaper texture
        Minecraft.getInstance().getTextureManager().register(wallpaperTexture,
                new SimpleTexture(ResourceLocation
                        .fromNamespaceAndPath(BetaDeco.MODID, "textures/entity/kaevator_wallpaper.png")));
    }

    /**
     * @return texture identifier.
     */
    @Override
    public ResourceLocation getTextureLocation(KaevatorWallpaperEntity entity) {
        return wallpaperTexture;
    }

    /**
     * Renders entity (code adopted from {@link net.minecraft.client.renderer.entity.PaintingRenderer#render}).
     */
    @Override
    public void render(KaevatorWallpaperEntity wallpaperEntity, float yaw, float tickDelta, PoseStack poseStack,
                       MultiBufferSource vertexConsumerProvider, int lightArg) {
        // Occupy pose stack
        poseStack.pushPose();

        // Rotation and scale transforms
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.scale(0.0625F, 0.0625F, 0.0625F);

        // Set sprite
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderType.entitySolid(wallpaperTexture));

        // Get matrices
        PoseStack.Pose pose = poseStack.last();

        // Vertex local coordinates
        float west = 8F;
        float east = -8F;
        float up = 8F;
        float down = -8F;

        // UV coordinates
        int variantU = wallpaperEntity.getVariant() % 5;
        int variantV = wallpaperEntity.getVariant() / 5;
        float minU = 0 + variantU * 0.2F;
        float minV = 0 + variantV * 0.2F;
        float maxU = 0.2F + variantU * 0.2F;
        float maxV = 0.2F + variantV * 0.2F;
        float minSideU = 0.00625F + variantU * 0.2F;
        float minSideV = 0.00625F + variantV * 0.2F;
        float maxSideU = 0.19375F + variantU * 0.2F;
        float maxSideV = 0.19375F + variantV * 0.2F;

        // Wallpaper block position
        int xPos = wallpaperEntity.getBlockX();
        int yPos = Mth.floor(wallpaperEntity.getY() + (double)((up + down) / 2.0F / 16.0F));
        int zPos = wallpaperEntity.getBlockZ();
        Direction direction = wallpaperEntity.getDirection();
        if (direction == Direction.NORTH) {
            xPos = Mth.floor(wallpaperEntity.getX() + (double)((west + east) / 2.0F / 16.0F));
        }
        if (direction == Direction.WEST) {
            zPos = Mth.floor(wallpaperEntity.getZ() - (double)((west + east) / 2.0F / 16.0F));
        }
        if (direction == Direction.SOUTH) {
            xPos = Mth.floor(wallpaperEntity.getX() - (double)((west + east) / 2.0F / 16.0F));
        }
        if (direction == Direction.EAST) {
            zPos = Mth.floor(wallpaperEntity.getZ() + (double)((west + east) / 2.0F / 16.0F));
        }
        BlockPos wallpaperPos = new BlockPos(xPos, yPos, zPos);

        // Get wallpaper color
        int color = wallpaperEntity.getColor();

        // Ambient occlusion light in wallpaper middle
        int light = LevelRenderer.getLightColor(wallpaperEntity.level(), wallpaperPos);

        // Smooth ambient occlusion for front face
        QuadAO frontAO = new QuadAO(wallpaperEntity.level(), wallpaperPos, direction);

        // Face NORTH
        this.vertex(pose, vertexConsumer,
                west, down, minU, maxV, -0.5F, color, 0, 0, -1, frontAO.lightmap[1]);
        this.vertex(pose, vertexConsumer,
                east, down, maxU, maxV, -0.5F, color, 0, 0, -1, frontAO.lightmap[2]);
        this.vertex(pose, vertexConsumer,
                east, up, maxU, minV, -0.5F, color, 0, 0, -1, frontAO.lightmap[3]);
        this.vertex(pose, vertexConsumer,
                west, up, minU, minV, -0.5F, color, 0, 0, -1, frontAO.lightmap[0]);
        // Face SOUTH
        this.vertex(pose, vertexConsumer,
                west, up, minU, minV, 0.5F, color, 0, 0, 1, light);
        this.vertex(pose, vertexConsumer,
                east, up, maxU, minV, 0.5F, color, 0, 0, 1, light);
        this.vertex(pose, vertexConsumer,
                east, down, maxU, maxV, 0.5F, color, 0, 0, 1, light);
        this.vertex(pose, vertexConsumer,
                west, down, minU, maxV, 0.5F, color, 0, 0, 1, light);
        // Face UP
        this.vertex(pose, vertexConsumer,
                west, up, minU, minV, -0.5F, color, 0, 1, 0, light);
        this.vertex(pose, vertexConsumer,
                east, up, maxU, minV, -0.5F, color, 0, 1, 0, light);
        this.vertex(pose, vertexConsumer,
                east, up, maxU, minSideV, 0.5F, color, 0, 1, 0, light);
        this.vertex(pose, vertexConsumer,
                west, up, minU, minSideV, 0.5F, color, 0, 1, 0, light);
        // Face DOWN
        this.vertex(pose, vertexConsumer,
                west, down, minU, maxSideV, 0.5F, color, 0, -1, 0, light);
        this.vertex(pose, vertexConsumer,
                east, down, maxU, maxSideV, 0.5F, color, 0, -1, 0, light);
        this.vertex(pose, vertexConsumer,
                east, down, maxU, maxV, -0.5F, color, 0, -1, 0, light);
        this.vertex(pose, vertexConsumer,
                west, down, minU, maxV, -0.5F, color, 0, -1, 0, light);
        // Face EAST
        this.vertex(pose, vertexConsumer,
                east, up, maxU, minV, -0.5F, color, 1, 0, 0, light);
        this.vertex(pose, vertexConsumer,
                east, down, maxU, maxV, -0.5F, color, 1, 0, 0, light);
        this.vertex(pose, vertexConsumer,
                east, down, maxSideU, maxV, 0.5F, color, 1, 0, 0, light);
        this.vertex(pose, vertexConsumer,
                east, up, maxSideU, minV, 0.5F, color, 1, 0, 0, light);
        // Face WEST
        this.vertex(pose, vertexConsumer,
                west, up, minSideU, minV, 0.5F, color, -1, 0, 0, light);
        this.vertex(pose, vertexConsumer,
                west, down, minSideU, maxV, 0.5F, color, -1, 0, 0, light);
        this.vertex(pose, vertexConsumer,
                west, down, minU, maxV, -0.5F, color, -1, 0, 0, light);
        this.vertex(pose, vertexConsumer,
                west, up, minU, minV, -0.5F, color, -1, 0, 0, light);

        // Free pose stack
        poseStack.popPose();
    }

    /**
     * Utility function to consume vertex.
     * @param pose matrix
     * @param vertexConsumer vertex consumer
     * @param x x position
     * @param y y position
     * @param u U coordinate (from 0 to 1, pointing right)
     * @param v V coordinate (from 0 to 1, pointing down)
     * @param z z position
     * @param color int packed color
     * @param normalX X normal
     * @param normalY Y normal
     * @param normalZ Z normal
     * @param light light
     */
    private void vertex(
            PoseStack.Pose pose,
            VertexConsumer vertexConsumer,
            float x,
            float y,
            float u,
            float v,
            float z,
            int color,
            int normalX,
            int normalY,
            int normalZ,
            int light
    ) {
        vertexConsumer
                // Set positions
                .addVertex(pose, x, y, z)
                // Set color
                .setColor((color & 0xFF0000) >> 16, (color & 0xFF00) >> 8, color & 0xFF, 255)
                // Set UVs
                .setUv(u, v)
                // Overlay
                .setOverlay(OverlayTexture.NO_OVERLAY)
                // Set light
                .setLight(light)
                // Set normals
                .setNormal(pose, (float)normalX, (float)normalY, (float)normalZ);
    }

    /**
     * Calculates quad AO. Adopted from {@link net.minecraft.client.renderer.block.ModelBlockRenderer}.
     */
    @OnlyIn(Dist.CLIENT)
    static class QuadAO {
        /**
         * Quad vertices lighting.
         */
        final int[] lightmap = new int[4];

        /**
         * Calculates vertices lighting.
         */
        public QuadAO(Level level, BlockPos pos, Direction direction) {
            lightmap[0] = lightmap[1] = lightmap[2] = lightmap[3] =
                    LevelRenderer.getLightColor(level, level.getBlockState(pos), pos.relative(direction));

            // Changeable position
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

            // Block state at wallpaper position
            BlockState block = level.getBlockState(pos);

            // Direction info
            DirectionsMap directionsMap = DirectionsMap.fromFacing(direction);

            // Get light at wallpaper position
            int light = LevelRenderer.getLightColor(level, block, pos);

            mutableBlockPos.setWithOffset(pos, directionsMap.corners[0]);
            BlockState block0 = level.getBlockState(mutableBlockPos);
            int i = LevelRenderer.getLightColor(level, block0, mutableBlockPos);

            mutableBlockPos.setWithOffset(pos, directionsMap.corners[1]);
            BlockState block1 = level.getBlockState(mutableBlockPos);
            int j = LevelRenderer.getLightColor(level, block1, mutableBlockPos);

            mutableBlockPos.setWithOffset(pos, directionsMap.corners[2]);
            BlockState block2 = level.getBlockState(mutableBlockPos);
            int k = LevelRenderer.getLightColor(level, block2, mutableBlockPos);

            mutableBlockPos.setWithOffset(pos, directionsMap.corners[3]);
            BlockState block3 = level.getBlockState(mutableBlockPos);
            int l = LevelRenderer.getLightColor(level, block3, mutableBlockPos);

            BlockState block4 = level.getBlockState(mutableBlockPos
                    .setWithOffset(pos, directionsMap.corners[0]).move(direction));
            boolean isInLight0 = !block4.isViewBlocking(level, mutableBlockPos)
                    || block4.getLightBlock(level, mutableBlockPos) == 0;

            BlockState block5 = level.getBlockState(mutableBlockPos
                    .setWithOffset(pos, directionsMap.corners[1]).move(direction));
            boolean isInLight1 = !block5.isViewBlocking(level, mutableBlockPos)
                    || block5.getLightBlock(level, mutableBlockPos) == 0;

            BlockState block6 = level.getBlockState(mutableBlockPos
                    .setWithOffset(pos, directionsMap.corners[2]).move(direction));
            boolean isInLight2 = !block6.isViewBlocking(level, mutableBlockPos)
                    || block6.getLightBlock(level, mutableBlockPos) == 0;

            BlockState block7 = level.getBlockState(mutableBlockPos
                    .setWithOffset(pos, directionsMap.corners[3]).move(direction));
            boolean isInLight3 = !block7.isViewBlocking(level, mutableBlockPos)
                    || block7.getLightBlock(level, mutableBlockPos) == 0;

            int light4;
            if (!isInLight2 && !isInLight0) {
                light4 = i;
            } else {
                mutableBlockPos.setWithOffset(pos, directionsMap.corners[0])
                        .move(directionsMap.corners[2]);
                BlockState block8 = level.getBlockState(mutableBlockPos);
                light4 = LevelRenderer.getLightColor(level, block8, mutableBlockPos);
            }

            int light5;
            if (!isInLight3 && !isInLight0) {
                light5 = i;
            } else {
                mutableBlockPos.setWithOffset(pos, directionsMap.corners[0])
                        .move(directionsMap.corners[3]);
                BlockState block10 = level.getBlockState(mutableBlockPos);
                light5 = LevelRenderer.getLightColor(level, block10, mutableBlockPos);
            }

            int light6;
            if (!isInLight2 && !isInLight1) {
                light6 = i;
            } else {
                mutableBlockPos.setWithOffset(pos, directionsMap.corners[1])
                        .move(directionsMap.corners[2]);
                BlockState block11 = level.getBlockState(mutableBlockPos);
                light6 = LevelRenderer.getLightColor(level, block11, mutableBlockPos);
            }

            int light7;
            if (!isInLight3 && !isInLight1) {
                light7 = i;
            } else {
                mutableBlockPos.setWithOffset(pos, directionsMap.corners[1])
                        .move(directionsMap.corners[3]);
                BlockState block12 = level.getBlockState(mutableBlockPos);
                light7 = LevelRenderer.getLightColor(level, block12, mutableBlockPos);
            }

            // Blend AO
            VertexLightMap lightMap = VertexLightMap.fromFacing(direction);
            this.lightmap[lightMap.vert0] = this.blend(l, i, light5, light);
            this.lightmap[lightMap.vert1] = this.blend(k, i, light4, light);
            this.lightmap[lightMap.vert2] = this.blend(k, j, light6, light);
            this.lightmap[lightMap.vert3] = this.blend(l, j, light7, light);
        }

        /**
         * @return blended ambient occlusion light color.
         */
        private int blend(int pLightColor0, int pLightColor1, int pLightColor2, int pLightColor3) {
            if (pLightColor0 == 0) {
                pLightColor0 = pLightColor3;
            }

            if (pLightColor1 == 0) {
                pLightColor1 = pLightColor3;
            }

            if (pLightColor2 == 0) {
                pLightColor2 = pLightColor3;
            }

            return pLightColor0 + pLightColor1 + pLightColor2 + pLightColor3 >> 2 & 16711935;
        }
    }

    /**
     * Direction mapper. Adopted from {@link net.minecraft.client.renderer.block.ModelBlockRenderer}.
     */
    @OnlyIn(Dist.CLIENT)
    enum DirectionsMap {
        DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}),
        UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}),
        NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}),
        SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}),
        WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}),
        EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH});

        final Direction[] corners;
        private static final DirectionsMap[] BY_FACING = Util.make(new DirectionsMap[6], (aiArray) -> {
            aiArray[Direction.DOWN.get3DDataValue()] = DOWN;
            aiArray[Direction.UP.get3DDataValue()] = UP;
            aiArray[Direction.NORTH.get3DDataValue()] = NORTH;
            aiArray[Direction.SOUTH.get3DDataValue()] = SOUTH;
            aiArray[Direction.WEST.get3DDataValue()] = WEST;
            aiArray[Direction.EAST.get3DDataValue()] = EAST;
        });

        DirectionsMap(Direction[] pCorners) {
            this.corners = pCorners;
        }

        public static DirectionsMap fromFacing(Direction pFacing) {
            return BY_FACING[pFacing.get3DDataValue()];
        }
    }

    /**
     * Vertex mapper. Adopted from {@link net.minecraft.client.renderer.block.ModelBlockRenderer}.
     */
    @OnlyIn(Dist.CLIENT)
    enum VertexLightMap {
        DOWN(0, 1, 2, 3),
        UP(2, 3, 0, 1),
        NORTH(3, 0, 1, 2),
        SOUTH(0, 1, 2, 3),
        WEST(3, 0, 1, 2),
        EAST(1, 2, 3, 0);

        final int vert0;
        final int vert1;
        final int vert2;
        final int vert3;
        private static final VertexLightMap[] BY_FACING = Util.make(new VertexLightMap[6], (array) -> {
            array[Direction.DOWN.get3DDataValue()] = DOWN;
            array[Direction.UP.get3DDataValue()] = UP;
            array[Direction.NORTH.get3DDataValue()] = NORTH;
            array[Direction.SOUTH.get3DDataValue()] = SOUTH;
            array[Direction.WEST.get3DDataValue()] = WEST;
            array[Direction.EAST.get3DDataValue()] = EAST;
        });

        VertexLightMap(int vertex0, int vertex1, int vertex2, int vertex3) {
            this.vert0 = vertex0;
            this.vert1 = vertex1;
            this.vert2 = vertex2;
            this.vert3 = vertex3;
        }

        public static VertexLightMap fromFacing(Direction direction) {
            return BY_FACING[direction.get3DDataValue()];
        }
    }
}

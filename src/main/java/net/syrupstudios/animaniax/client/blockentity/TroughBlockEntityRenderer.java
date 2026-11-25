package net.syrupstudios.animaniax.client.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.syrupstudios.animaniax.block.TroughBlock;
import net.syrupstudios.animaniax.block.TroughBlockEntity;
import net.syrupstudios.animaniax.client.model.PlaceholderModel;
import net.syrupstudios.animaniax.client.model.PlaceholderModelRegistry;
import org.joml.Matrix4f;

/**
 * Renders the double-wide trough and its water, slop, or grain feed contents.
 */
public final class TroughBlockEntityRenderer implements BlockEntityRenderer<TroughBlockEntity> {
    private static final ResourceLocation TROUGH_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/tileentities/block_trough.png");
    private static final ResourceLocation WHEAT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/tileentities/wheat.png");
    private static final ResourceLocation WATER_STILL =
            ResourceLocation.withDefaultNamespace("block/water_still");
    private static final ResourceLocation SLOP_STILL =
            ResourceLocation.fromNamespaceAndPath("animaniax", "block/fluid/slop_still");

    private static final String[] FEED_PARTS = {
            "FeedA", "FeedB", "FeedC", "FeedD", "FeedE", "FeedF", "FeedG", "FeedH"
    };
    private static final String[] FEED_PARTS_2 = {
            "FeedA1", "FeedB1", "FeedC1", "FeedD1", "FeedE1", "FeedF1", "FeedG1", "FeedH1"
    };

    public TroughBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(TroughBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int packedLight, int packedOverlay) {
        BlockState state = be.getBlockState();
        if (state.hasProperty(TroughBlock.PART) && state.getValue(TroughBlock.PART) != TroughBlock.Part.MAIN) {
            return;
        }
        PlaceholderModel model = PlaceholderModelRegistry.INSTANCE.blockEntity("trough");
        if (model == null) return;

        Direction facing = state.hasProperty(TroughBlock.FACING) ? state.getValue(TroughBlock.FACING) : Direction.NORTH;

        poseStack.pushPose();
        switch (facing) {
            case NORTH -> poseStack.translate(1.5D, 1.5D, 0.5D);
            case SOUTH -> {
                poseStack.translate(-0.5D, 1.5D, 0.5D);
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            }
            case WEST -> {
                poseStack.translate(0.5D, 1.5D, -0.5D);
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            }
            case EAST -> {
                poseStack.translate(0.5D, 1.5D, 1.5D);
                poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
            }
            default -> poseStack.translate(1.5D, 1.5D, 0.5D);
        }
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        VertexConsumer baseConsumer = buffers.getBuffer(RenderType.entityCutoutNoCull(TROUGH_TEXTURE));
        model.render(poseStack, baseConsumer, packedLight, packedOverlay, 0xFFFFFFFF, 0xFFFFFFFF, null, null, null);

        int content = state.hasProperty(TroughBlock.CONTENT) ? state.getValue(TroughBlock.CONTENT) : 0;
        int level = state.hasProperty(TroughBlock.LEVEL) ? state.getValue(TroughBlock.LEVEL) : 0;

        if (content == 1) {
            float fraction = Math.max(0.15F, Math.min(1.0F, be.getWaterAmount() / (float) TroughBlockEntity.WATER_CAPACITY));
            renderFluidSurface(poseStack, buffers, packedLight, packedOverlay, WATER_STILL, 0xCC3F76E4, fraction);
        } else if (content == 3) {
            float fraction = Math.max(0.15F, Math.min(1.0F, be.getSlopAmount() / (float) TroughBlockEntity.WATER_CAPACITY));
            renderFluidSurface(poseStack, buffers, packedLight, packedOverlay, SLOP_STILL, 0xFFFFFFFF, fraction);
        } else if (content == 2 && level > 0) {
            ItemStack feed = be.getFeedStack();
            int foodColor = feedColor(feed);
            int clamped = Math.max(1, Math.min(3, level));

            poseStack.pushPose();
            poseStack.translate(0.0F, 0.17F * (3 - clamped), 0.0F);
            VertexConsumer feedBase = buffers.getBuffer(RenderType.entityCutoutNoCull(TROUGH_TEXTURE));
            model.renderPart("Feed", poseStack, feedBase, packedLight, packedOverlay, foodColor, foodColor, null);
            poseStack.popPose();

            if (feed.isEmpty() || feed.is(Items.WHEAT)) {
                VertexConsumer wheatConsumer = buffers.getBuffer(RenderType.entityCutoutNoCull(WHEAT_TEXTURE));
                poseStack.pushPose();
                poseStack.translate(0.0F, 0.2F * (3 - clamped), 0.0F);
                poseStack.mulPose(Axis.YP.rotationDegrees(-10.0F));
                poseStack.scale(0.8F, 0.8F, 0.8F);
                poseStack.translate(0.0F, 0.25F, -0.1F);
                for (String part : FEED_PARTS) {
                    model.renderPart(part, poseStack, wheatConsumer, packedLight, packedOverlay, 0xFFFFFFFF, 0xFFFFFFFF, null);
                }
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                poseStack.translate(-1.4F, -0.1F, 0.0F);
                for (String part : FEED_PARTS_2) {
                    model.renderPart(part, poseStack, wheatConsumer, packedLight, packedOverlay, 0xFFFFFFFF, 0xFFFFFFFF, null);
                }
                poseStack.popPose();
            }
        }

        poseStack.popPose();
    }

    private static int feedColor(ItemStack stack) {
        if (stack.isEmpty() || stack.is(Items.WHEAT)) return 0xFFA07C59;
        if (stack.is(Items.CARROT)) return 0xFFE37829;
        if (stack.is(Items.APPLE)) return 0xFFC7392E;
        if (stack.is(Items.BEETROOT)) return 0xFFA22940;
        if (stack.is(Items.POTATO) || stack.is(Items.POISONOUS_POTATO)) return 0xFFC6A063;
        return 0xFF9F8852;
    }

    private static void renderFluidSurface(
            PoseStack poseStack,
            MultiBufferSource buffers,
            int light,
            int overlay,
            ResourceLocation spriteId,
            int argb,
            float fraction) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(spriteId);
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS));
        float y = (20.2F - 4.7F * fraction) / 16.0F;
        float x0 = -6.0F / 16.0F;
        float x1 = 22.0F / 16.0F;
        float z0 = -4.0F / 16.0F;
        float z1 = 4.0F / 16.0F;
        Matrix4f mat = poseStack.last().pose();
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        consumer.addVertex(mat, x0, y, z0).setColor(argb).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(0.0F, -1.0F, 0.0F);
        consumer.addVertex(mat, x1, y, z0).setColor(argb).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(0.0F, -1.0F, 0.0F);
        consumer.addVertex(mat, x1, y, z1).setColor(argb).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(0.0F, -1.0F, 0.0F);
        consumer.addVertex(mat, x0, y, z1).setColor(argb).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(0.0F, -1.0F, 0.0F);
    }

    @Override
    public boolean shouldRenderOffScreen(TroughBlockEntity be) {
        return true;
    }
}

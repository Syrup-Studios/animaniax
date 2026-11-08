package net.syrupstudios.animaniax.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.syrupstudios.animaniax.entity.PullCartEntity;

public final class PullCartRenderer extends EntityRenderer<PullCartEntity> {
    private final BlockRenderDispatcher blocks;

    public PullCartRenderer(EntityRendererProvider.Context context) { super(context); blocks = context.getBlockRenderDispatcher(); }

    @Override
    public void render(PullCartEntity cart, float yaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int light) {
        poseStack.pushPose();
        poseStack.translate(-0.5, 0.15, -0.5);
        renderBlock(Blocks.OAK_PLANKS.defaultBlockState(), poseStack, buffers, light, 1.0F, 0.18F, 0.9F);
        renderBlock(Blocks.OAK_FENCE.defaultBlockState(), poseStack, buffers, light, 0.12F, 0.45F, 0.12F, 0.08F, 0.1F, 0.08F);
        renderBlock(Blocks.OAK_FENCE.defaultBlockState(), poseStack, buffers, light, 0.12F, 0.45F, 0.12F, 0.8F, 0.1F, 0.08F);
        renderBlock(Blocks.OAK_FENCE.defaultBlockState(), poseStack, buffers, light, 0.12F, 0.45F, 0.12F, 0.08F, 0.1F, 0.7F);
        renderBlock(Blocks.OAK_FENCE.defaultBlockState(), poseStack, buffers, light, 0.12F, 0.45F, 0.12F, 0.8F, 0.1F, 0.7F);
        if (cart.hasChest()) renderBlock(Blocks.CHEST.defaultBlockState(), poseStack, buffers, light, 0.62F, 0.55F, 0.62F, 0.19F, 0.16F, 0.16F);
        poseStack.popPose();
        super.render(cart, yaw, partialTick, poseStack, buffers, light);
    }

    private void renderBlock(net.minecraft.world.level.block.state.BlockState state, PoseStack poseStack,
                             MultiBufferSource buffers, int light, float x, float y, float z) {
        renderBlock(state, poseStack, buffers, light, x, y, z, 0.5F - x / 2, 0, 0.5F - z / 2);
    }

    private void renderBlock(net.minecraft.world.level.block.state.BlockState state, PoseStack poseStack,
                             MultiBufferSource buffers, int light, float x, float y, float z,
                             float tx, float ty, float tz) {
        poseStack.pushPose();
        poseStack.translate(tx, ty, tz);
        poseStack.scale(x, y, z);
        blocks.renderSingleBlock(
                state, poseStack, buffers, light, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(PullCartEntity cart) {
        return ResourceLocation.withDefaultNamespace("textures/block/oak_planks.png");
    }
}

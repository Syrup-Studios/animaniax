package net.syrupstudios.animaniax.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.syrupstudios.animaniax.entity.TillerEntity;

public final class TillerRenderer extends EntityRenderer<TillerEntity> {
    private final BlockRenderDispatcher blocks;
    public TillerRenderer(EntityRendererProvider.Context context) { super(context); blocks = context.getBlockRenderDispatcher(); }
    @Override public void render(TillerEntity entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        pose.pushPose(); pose.translate(-0.5, 0.15, -0.5);
        render(Blocks.OAK_PLANKS.defaultBlockState(), pose, buffers, light, 1.0F, 0.18F, 0.9F, 0, 0, 0);
        render(Blocks.OAK_FENCE.defaultBlockState(), pose, buffers, light, 0.12F, 0.45F, 0.12F, 0.08F, 0.1F, 0.08F);
        render(Blocks.OAK_FENCE.defaultBlockState(), pose, buffers, light, 0.12F, 0.45F, 0.12F, 0.8F, 0.1F, 0.08F);
        render(Blocks.IRON_BARS.defaultBlockState(), pose, buffers, light, 0.8F, 0.12F, 0.12F, 0.1F, 0.02F, 0.78F);
        pose.popPose(); super.render(entity, yaw, partialTick, pose, buffers, light);
    }
    private void render(net.minecraft.world.level.block.state.BlockState state, PoseStack pose, MultiBufferSource buffers, int light,
                        float x, float y, float z, float tx, float ty, float tz) {
        pose.pushPose(); pose.translate(tx, ty, tz); pose.scale(x, y, z);
        blocks.renderSingleBlock(state, pose, buffers, light, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY); pose.popPose();
    }
    @Override public ResourceLocation getTextureLocation(TillerEntity entity) { return ResourceLocation.withDefaultNamespace("textures/block/oak_planks.png"); }
}

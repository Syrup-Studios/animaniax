package net.syrupstudios.animaniax.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.syrupstudios.animaniax.client.model.CraftStudioModel;
import net.syrupstudios.animaniax.client.model.PlaceholderModelRegistry;
import net.syrupstudios.animaniax.entity.TillerEntity;

/**
 * Renders the horse-drawn tiller using its Animania CraftStudio model.
 */
public final class TillerRenderer extends EntityRenderer<TillerEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/props/tiller.png");

    public TillerRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.0F;
    }

    @Override
    public void render(TillerEntity entity, float yaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int light) {
        CraftStudioModel model = PlaceholderModelRegistry.INSTANCE.craftStudio("model_tiller");
        if (model != null) {
            VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
            poseStack.pushPose();
            poseStack.translate(0.0F, 1.5F, 0.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            model.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF, 128.0F, 64.0F, null);
            poseStack.popPose();
        }
        super.render(entity, yaw, partialTick, poseStack, buffers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(TillerEntity entity) {
        return TEXTURE;
    }
}

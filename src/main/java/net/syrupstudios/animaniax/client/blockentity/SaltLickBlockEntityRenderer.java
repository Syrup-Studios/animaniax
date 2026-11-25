package net.syrupstudios.animaniax.client.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.syrupstudios.animaniax.block.SaltLickBlockEntity;
import net.syrupstudios.animaniax.client.model.PlaceholderModel;
import net.syrupstudios.animaniax.client.model.PlaceholderModelRegistry;
import net.syrupstudios.animaniax.config.AnimalCareConfig;

/**
 * Renders the salt lick block shrinking vertically as remaining uses decrease.
 */
public final class SaltLickBlockEntityRenderer implements BlockEntityRenderer<SaltLickBlockEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/tileentities/salt_lick.png");

    public SaltLickBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(SaltLickBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int packedLight, int packedOverlay) {
        PlaceholderModel model = PlaceholderModelRegistry.INSTANCE.blockEntity("salt_lick");
        if (model == null) return;

        float amountLeft = Math.max(0.08F, Math.min(1.0F,
                (float) be.getUses() / Math.max(1, AnimalCareConfig.saltLickMaxUses)));

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.scale(-1.0F, -amountLeft, 1.0F);
        poseStack.translate(0.0D, -1.5D, 0.0D);

        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.render(poseStack, consumer, packedLight, packedOverlay, 0xFFFFFFFF, 0xFFFFFFFF, null, null, null);
        poseStack.popPose();
    }
}

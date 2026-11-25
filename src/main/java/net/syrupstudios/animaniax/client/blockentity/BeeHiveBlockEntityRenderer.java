package net.syrupstudios.animaniax.client.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.syrupstudios.animaniax.block.BeeHiveBlock;
import net.syrupstudios.animaniax.block.BeeHiveBlockEntity;
import net.syrupstudios.animaniax.client.model.CraftStudioModel;
import net.syrupstudios.animaniax.client.model.PlaceholderModelRegistry;

/**
 * Renders player-crafted and wild bee hives using their CraftStudio models.
 */
public final class BeeHiveBlockEntityRenderer implements BlockEntityRenderer<BeeHiveBlockEntity> {
    private static final ResourceLocation HIVE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/props/bee_hive.png");

    public BeeHiveBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(BeeHiveBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int packedLight, int packedOverlay) {
        boolean wild = be.getBlockState().getBlock() instanceof BeeHiveBlock hive && hive.isWild();
        CraftStudioModel model = PlaceholderModelRegistry.INSTANCE.craftStudio(
                wild ? "model_wild_hive" : "model_bee_hive");
        if (model == null) return;

        poseStack.pushPose();
        poseStack.translate(0.5D, wild ? 1.0D : 1.5D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(HIVE_TEXTURE));
        model.render(poseStack, consumer, packedLight, packedOverlay, 0xFFFFFFFF, 128.0F, 64.0F, null);
        poseStack.popPose();
    }
}

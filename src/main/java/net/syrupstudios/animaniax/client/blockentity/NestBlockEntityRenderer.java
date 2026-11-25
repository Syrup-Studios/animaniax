package net.syrupstudios.animaniax.client.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.syrupstudios.animaniax.block.NestBlock;
import net.syrupstudios.animaniax.block.NestBlockEntity;
import net.syrupstudios.animaniax.client.model.PlaceholderModel;
import net.syrupstudios.animaniax.client.model.PlaceholderModelRegistry;
import net.syrupstudios.animaniax.item.BrownEggContent;

/**
 * Renders the straw nest and up to three chicken or peafowl eggs inside it.
 */
public final class NestBlockEntityRenderer implements BlockEntityRenderer<NestBlockEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/tileentities/block_nest_white.png");

    public NestBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(NestBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int packedLight, int packedOverlay) {
        PlaceholderModel model = PlaceholderModelRegistry.INSTANCE.blockEntity("nest");
        if (model == null) return;

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.render(poseStack, consumer, packedLight, packedOverlay, 0xFFFFFFFF, 0xFFFFFFFF, null, null, null);

        BlockState state = be.getBlockState();
        int eggs = state.hasProperty(NestBlock.EGGS) ? state.getValue(NestBlock.EGGS) : 0;
        if (eggs > 0) {
            String prefix = eggPrefix(be.getEggBreed(0));
            for (int i = 1; i <= Math.min(3, eggs); i++) {
                String slot = prefix + i;
                model.renderPart(slot, poseStack, consumer, packedLight, packedOverlay, 0xFFFFFFFF, 0xFFFFFFFF, null);
                model.renderPart(slot + "a", poseStack, consumer, packedLight, packedOverlay, 0xFFFFFFFF, 0xFFFFFFFF, null);
                model.renderPart(slot + "b", poseStack, consumer, packedLight, packedOverlay, 0xFFFFFFFF, 0xFFFFFFFF, null);
                model.renderPart(slot + "c", poseStack, consumer, packedLight, packedOverlay, 0xFFFFFFFF, 0xFFFFFFFF, null);
            }
        }
        poseStack.popPose();
    }

    private static String eggPrefix(String breed) {
        if (BrownEggContent.isBrownBreed(breed)) return "BEgg";
        if (breed != null && breed.startsWith("peafowl:")) {
            return "peafowl:White".equalsIgnoreCase(breed) ? "WEgg" : "BlEgg";
        }
        return "Egg";
    }
}

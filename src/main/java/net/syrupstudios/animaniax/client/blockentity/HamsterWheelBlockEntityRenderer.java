package net.syrupstudios.animaniax.client.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.syrupstudios.animaniax.block.HamsterWheelBlockEntity;
import net.syrupstudios.animaniax.client.HamsterRenderer;
import net.syrupstudios.animaniax.client.model.CraftStudioModel;
import net.syrupstudios.animaniax.client.model.PlaceholderModelRegistry;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

/**
 * Renders the hamster wheel and an animated hamster inside while occupied.
 */
public final class HamsterWheelBlockEntityRenderer implements BlockEntityRenderer<HamsterWheelBlockEntity> {
    private static final ResourceLocation WHEEL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/tileentities/hamster_wheel.png");

    private final Map<String, Vector3f> hamsterRotDeg = new HashMap<>();

    public HamsterWheelBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(HamsterWheelBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int packedLight, int packedOverlay) {
        CraftStudioModel wheel = PlaceholderModelRegistry.INSTANCE.craftStudio("model_hamster_wheel");
        if (wheel == null) return;

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        VertexConsumer wheelConsumer = buffers.getBuffer(RenderType.entityCutoutNoCull(WHEEL_TEXTURE));
        wheel.render(poseStack, wheelConsumer, packedLight, packedOverlay, 0xFFFFFFFF, 64.0F, 32.0F, null);

        if (be.occupied()) {
            CraftStudioModel hamster = PlaceholderModelRegistry.INSTANCE.craftStudio("hamster");
            if (hamster != null) {
                float time = be.getLevel() != null ? be.getLevel().getGameTime() + partialTick : partialTick;
                float swing = Mth.sin(time * 0.65F) * 40.0F;
                hamsterRotDeg.clear();
                hamsterRotDeg.put("hamsterleg1", new Vector3f(swing, 0.0F, 0.0F));
                hamsterRotDeg.put("hamsterleg4", new Vector3f(swing, 0.0F, 0.0F));
                hamsterRotDeg.put("hamsterleg2", new Vector3f(-swing, 0.0F, 0.0F));
                hamsterRotDeg.put("hamsterleg3", new Vector3f(-swing, 0.0F, 0.0F));

                poseStack.pushPose();
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.translate(0.0D, 0.9D, 0.0D);
                poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
                VertexConsumer hamsterConsumer = buffers.getBuffer(
                        RenderType.entityCutoutNoCull(HamsterRenderer.textureForColor(be.getHamsterColor())));
                hamster.render(poseStack, hamsterConsumer, packedLight, packedOverlay, 0xFFFFFFFF,
                        64.0F, 32.0F, hamsterRotDeg);
                poseStack.popPose();
            }
        }

        poseStack.popPose();
    }
}

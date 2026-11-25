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
import net.minecraft.util.Brightness;
import net.syrupstudios.animaniax.client.model.CraftStudioModel;
import net.syrupstudios.animaniax.client.model.PlaceholderModelRegistry;
import net.syrupstudios.animaniax.entity.PullCartEntity;
import net.syrupstudios.animaniax.entity.WagonEntity;

/**
 * Renders pull carts and covered wagons using Animania CraftStudio models.
 */
public final class PullCartRenderer extends EntityRenderer<PullCartEntity> {
    private static final ResourceLocation CART_TEX =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/props/cart.png");
    private static final ResourceLocation CART_CHEST_TEX =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/props/cart_chest.png");
    private static final ResourceLocation WAGON_DAY_TEX =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/props/wagon.png");
    private static final ResourceLocation WAGON_FLICKER_2 =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/props/wagon2.png");
    private static final ResourceLocation WAGON_FLICKER_3 =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/props/wagon3.png");

    public PullCartRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.0F;
    }

    @Override
    public void render(PullCartEntity cart, float yaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int light) {
        light = wagonLight(cart, light);
        boolean isWagon = cart instanceof WagonEntity;
        String modelKey = isWagon ? "model_wagon" : (cart.hasChest() ? "model_cart_chest" : "model_cart");
        CraftStudioModel model = PlaceholderModelRegistry.INSTANCE.craftStudio(modelKey);
        if (model != null) {
            ResourceLocation tex = getTextureLocation(cart);
            VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(tex));
            float texW = isWagon ? 256.0F : 128.0F;
            float texH = 128.0F;

            poseStack.pushPose();
            poseStack.translate(0.0F, isWagon ? 1.6F : 1.5F, 0.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            model.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF, texW, texH, null);
            poseStack.popPose();
        }
        super.render(cart, yaw, partialTick, poseStack, buffers, light);
    }

    private int wagonLight(PullCartEntity cart, int packedLight) {
        if (!(cart instanceof WagonEntity wagon)) return packedLight;
        Brightness brightness = Brightness.unpack(packedLight);
        if (wagon.getSleepLightTicks() > 0) return new Brightness(1, 0).pack();
        if (cart.level().getDayTime() % 24000 < 13000) return packedLight;
        int flicker = (int) ((cart.level().getDayTime() + cart.getId()) % 3);
        return new Brightness(Math.min(15, brightness.block() + flicker), brightness.sky()).pack();
    }

    @Override
    public ResourceLocation getTextureLocation(PullCartEntity cart) {
        if (cart instanceof WagonEntity) {
            long dayTime = cart.level().getDayTime() % 24000;
            if (dayTime < 13000) return WAGON_DAY_TEX;
            int step = (int) ((cart.level().getDayTime() + cart.getId()) % 3);
            return switch (step) {
                case 1 -> WAGON_FLICKER_2;
                case 2 -> WAGON_FLICKER_3;
                default -> WAGON_DAY_TEX;
            };
        }
        return cart.hasChest() ? CART_CHEST_TEX : CART_TEX;
    }
}

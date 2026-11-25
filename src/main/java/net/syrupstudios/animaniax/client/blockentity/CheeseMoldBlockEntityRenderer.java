package net.syrupstudios.animaniax.client.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.syrupstudios.animaniax.block.CheeseMoldBlockEntity;
import org.joml.Matrix4f;

/**
 * Renders the inner milk, water, cheese curd, or salt surface inside a filled cheese mold.
 */
public final class CheeseMoldBlockEntityRenderer implements BlockEntityRenderer<CheeseMoldBlockEntity> {
    private static final ResourceLocation WATER_STILL =
            ResourceLocation.withDefaultNamespace("block/water_still");
    private static final ResourceLocation SALT_TEX =
            ResourceLocation.fromNamespaceAndPath("animaniax", "block/salt");

    public CheeseMoldBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(CheeseMoldBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int packedLight, int packedOverlay) {
        if (!be.isFilled()) return;

        String cheeseType = be.getCheeseType();
        boolean ready = be.isReady();
        boolean isSaltWater = "salt".equals(cheeseType);
        ResourceLocation spriteId;
        int tint = 0xFFFFFFFF;
        boolean translucent;

        if (isSaltWater) {
            if (ready) {
                spriteId = SALT_TEX;
                translucent = false;
            } else {
                spriteId = WATER_STILL;
                tint = 0xCC3F76E4;
                translucent = true;
            }
        } else if (ready) {
            String topName = switch (cheeseType) {
                case "friesian_cheese" -> "friesian_cheese_top";
                case "holstein_cheese" -> "holstein_cheese_top";
                case "goat_cheese" -> "goat_cheese_top";
                case "sheep_cheese" -> "sheep_cheese_top";
                default -> "cheese_top";
            };
            spriteId = ResourceLocation.fromNamespaceAndPath("animaniax", "block/" + topName);
            translucent = false;
        } else {
            String milkSlug = switch (cheeseType) {
                case "friesian_cheese" -> "friesian";
                case "jersey_cheese" -> "jersey";
                case "goat_cheese" -> "goat";
                case "sheep_cheese" -> "sheep";
                default -> "holstein";
            };
            spriteId = ResourceLocation.fromNamespaceAndPath("animaniax", "block/fluid/milk_" + milkSlug + "_still");
            translucent = true;
        }

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(spriteId);
        VertexConsumer consumer = buffers.getBuffer(
                translucent ? RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS)
                        : RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS));

        float y = (isSaltWater && ready) ? 5.0F / 16.0F : 9.0F / 16.0F;
        float min = 1.0F / 16.0F;
        float max = 15.0F / 16.0F;
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        Matrix4f mat = poseStack.last().pose();

        consumer.addVertex(mat, min, y, min).setColor(tint).setUv(u0, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(mat, min, y, max).setColor(tint).setUv(u0, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(mat, max, y, max).setColor(tint).setUv(u1, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(mat, max, y, min).setColor(tint).setUv(u1, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(0.0F, 1.0F, 0.0F);
    }
}

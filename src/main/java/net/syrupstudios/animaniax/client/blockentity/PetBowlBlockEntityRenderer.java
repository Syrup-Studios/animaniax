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
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.syrupstudios.animaniax.block.PetBowlBlockEntity;
import net.syrupstudios.animaniax.client.model.PlaceholderModel;
import net.syrupstudios.animaniax.client.model.PlaceholderModelRegistry;
import org.joml.Matrix4f;

/**
 * Renders the pet bowl along with kibble piles or water fill.
 */
public final class PetBowlBlockEntityRenderer implements BlockEntityRenderer<PetBowlBlockEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/tileentities/pet_bowl.png");
    private static final ResourceLocation WATER_STILL =
            ResourceLocation.withDefaultNamespace("block/water_still");

    public PetBowlBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(PetBowlBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int packedLight, int packedOverlay) {
        PlaceholderModel model = PlaceholderModelRegistry.INSTANCE.blockEntity("pet_bowl");
        if (model == null) return;

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.render(poseStack, consumer, packedLight, packedOverlay, 0xFFFFFFFF, 0xFFFFFFFF, null, null, null);

        ItemStack food = be.getFood();
        if (!food.isEmpty()) {
            int kibbleTint = kibbleColor(food);
            int count = Math.max(1, Math.min(3, food.getCount()));
            poseStack.pushPose();
            poseStack.translate(0.0F, 0.04F * (3 - count), 0.0F);
            for (String partName : model.getParts().keySet()) {
                if (partName.startsWith("Kibble")) {
                    model.renderPart(partName, poseStack, consumer, packedLight, packedOverlay, kibbleTint, kibbleTint, null);
                }
            }
            poseStack.popPose();
        } else if (be.getWaterAmount() > 0) {
            float fraction = Math.max(0.15F, Math.min(1.0F, be.getWaterAmount() / (float) PetBowlBlockEntity.WATER_CAPACITY));
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(WATER_STILL);
            VertexConsumer waterConsumer = buffers.getBuffer(RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS));
            float y = (22.6F - 2.1F * fraction) / 16.0F;
            float r = 3.2F / 16.0F;
            Matrix4f mat = poseStack.last().pose();
            int tint = 0xCC3F76E4;
            waterConsumer.addVertex(mat, -r, y, -r).setColor(tint).setUv(sprite.getU0(), sprite.getV0()).setOverlay(packedOverlay).setLight(packedLight).setNormal(0.0F, -1.0F, 0.0F);
            waterConsumer.addVertex(mat, r, y, -r).setColor(tint).setUv(sprite.getU1(), sprite.getV0()).setOverlay(packedOverlay).setLight(packedLight).setNormal(0.0F, -1.0F, 0.0F);
            waterConsumer.addVertex(mat, r, y, r).setColor(tint).setUv(sprite.getU1(), sprite.getV1()).setOverlay(packedOverlay).setLight(packedLight).setNormal(0.0F, -1.0F, 0.0F);
            waterConsumer.addVertex(mat, -r, y, r).setColor(tint).setUv(sprite.getU0(), sprite.getV1()).setOverlay(packedOverlay).setLight(packedLight).setNormal(0.0F, -1.0F, 0.0F);
        }

        poseStack.popPose();
    }

    private static int kibbleColor(ItemStack stack) {
        if (stack.is(ItemTags.FISHES)) return 0xFF7DA4A6;
        if (stack.is(Items.BEEF) || stack.is(Items.MUTTON) || stack.is(Items.PORKCHOP)) return 0xFFB85B4C;
        if (stack.is(Items.CHICKEN) || stack.is(Items.RABBIT)) return 0xFFD9B18A;
        if (stack.is(Items.WHEAT_SEEDS) || stack.is(Items.PUMPKIN_SEEDS) || stack.is(Items.MELON_SEEDS)) return 0xFFD0B25A;
        return 0xFF9C663F;
    }
}

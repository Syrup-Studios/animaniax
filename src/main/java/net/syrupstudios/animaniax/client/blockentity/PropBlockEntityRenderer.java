package net.syrupstudios.animaniax.client.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.syrupstudios.animaniax.block.DecorativePropBlock;
import net.syrupstudios.animaniax.block.DecorativePropContent;
import net.syrupstudios.animaniax.block.PetBedContent;
import net.syrupstudios.animaniax.block.PropBlockEntity;
import net.syrupstudios.animaniax.client.model.CraftStudioModel;
import net.syrupstudios.animaniax.client.model.PlaceholderModelRegistry;

/**
 * Renders decorative pet beds and furniture props from CraftStudio models.
 */
public final class PropBlockEntityRenderer implements BlockEntityRenderer<PropBlockEntity> {
    private static final ResourceLocation CAT_BED_1_TEX = tex("cat_bed_1");
    private static final ResourceLocation CAT_BED_2_TEX = tex("cat_bed_2");
    private static final ResourceLocation DOG_PILLOW_TEX = tex("dog_pillow");
    private static final ResourceLocation CAT_TOWER_TEX = tex("cat_tower");
    private static final ResourceLocation DOG_HOUSE_TEX = tex("dog_house");
    private static final ResourceLocation LITTER_BOX_TEX = tex("litter_box");

    public PropBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    private static ResourceLocation tex(String name) {
        return ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/tileentities/" + name + ".png");
    }

    @Override
    public void render(PropBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int packedLight, int packedOverlay) {
        BlockState state = be.getBlockState();
        Block block = state.getBlock();
        String modelName;
        ResourceLocation texture;
        float texW;
        float texH;

        if (block == PetBedContent.CAT_BED_1) {
            modelName = "model_cat_bed_1";
            texture = CAT_BED_1_TEX;
            texW = 64.0F;
            texH = 32.0F;
        } else if (block == PetBedContent.CAT_BED_2) {
            modelName = "model_cat_bed_2";
            texture = CAT_BED_2_TEX;
            texW = 64.0F;
            texH = 32.0F;
        } else if (block == PetBedContent.DOG_PILLOW) {
            modelName = "model_dog_pillow";
            texture = DOG_PILLOW_TEX;
            texW = 128.0F;
            texH = 128.0F;
        } else if (block == DecorativePropContent.CAT_TOWER) {
            modelName = "model_cat_tower";
            texture = CAT_TOWER_TEX;
            texW = 128.0F;
            texH = 128.0F;
        } else if (block == DecorativePropContent.DOG_HOUSE) {
            modelName = "model_dog_house";
            texture = DOG_HOUSE_TEX;
            texW = 64.0F;
            texH = 64.0F;
        } else {
            modelName = "model_litter_box";
            texture = LITTER_BOX_TEX;
            texW = 64.0F;
            texH = 64.0F;
        }

        CraftStudioModel model = PlaceholderModelRegistry.INSTANCE.craftStudio(modelName);
        if (model == null) return;

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        if (state.hasProperty(DecorativePropBlock.FACING)) {
            poseStack.mulPose(Axis.YP.rotationDegrees(state.getValue(DecorativePropBlock.FACING).toYRot()));
        }
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(texture));
        model.render(poseStack, consumer, packedLight, packedOverlay, 0xFFFFFFFF, texW, texH, null);
        poseStack.popPose();
    }
}

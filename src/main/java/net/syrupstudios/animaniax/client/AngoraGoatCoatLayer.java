package net.syrupstudios.animaniax.client;

import net.minecraft.client.model.GoatModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.goat.Goat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public final class AngoraGoatCoatLayer extends RenderLayer<Goat, GoatModel<Goat>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            "minecraft", "textures/block/white_wool.png");
    private final AngoraCoatModel model;

    public AngoraGoatCoatLayer(RenderLayerParent<Goat, GoatModel<Goat>> parent, EntityModelSet models) {
        super(parent);
        model = new AngoraCoatModel(models.bakeLayer(ModelLayers.GOAT));
    }

    @Override
    public void render(PoseStack poseStack, net.minecraft.client.renderer.MultiBufferSource buffers,
            int packedLight, Goat goat, float limbSwing, float limbSwingAmount, float partialTick,
            float ageInTicks, float netHeadYaw, float headPitch) {
        if (goat.isInvisible()
                || !"Angora".equals(((net.syrupstudios.animaniax.entity.GoatBreedState) goat).animaniax$getGoatBreed())
                || ((net.syrupstudios.animaniax.entity.GoatBreedState) goat).animaniax$isSheared()) return;

        getParentModel().copyPropertiesTo(model);
        model.setupAnim(goat, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        model.prepareCoat();
        VertexConsumer vertices = buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, vertices, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
    }

    private static final class AngoraCoatModel extends GoatModel<Goat> {
        private AngoraCoatModel(net.minecraft.client.model.geom.ModelPart root) {
            super(root);
        }

        private void prepareCoat() {
            head.visible = false;
            rightFrontLeg.visible = false;
            leftFrontLeg.visible = false;
            rightHindLeg.visible = false;
            leftHindLeg.visible = false;
            body.xScale = 1.08F;
            body.yScale = 1.08F;
            body.zScale = 1.08F;
        }
    }
}

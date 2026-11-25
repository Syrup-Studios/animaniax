package net.syrupstudios.animaniax.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.syrupstudios.animaniax.client.model.CraftStudioModel;
import net.syrupstudios.animaniax.client.model.PlaceholderModel;
import net.syrupstudios.animaniax.client.model.PlaceholderModelRegistry;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.HamsterEntity;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

/**
 * Renders hamsters with their CraftStudio model, coat variants, and optional dyed hamster ball.
 */
public final class HamsterRenderer extends MobRenderer<HamsterEntity, EntityModel<HamsterEntity>> {
    private static final ResourceLocation BALL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/rodents/hamster_ball.png");
    private static final String[] COAT_NAMES = {
            "black", "brown", "darkbrown", "darkgray", "gray", "plum", "tarou", "white", "gold"
    };
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[COAT_NAMES.length];

    static {
        for (int i = 0; i < COAT_NAMES.length; i++) {
            TEXTURES[i] = ResourceLocation.fromNamespaceAndPath("animaniax",
                    "textures/entity/rodents/hamster_" + COAT_NAMES[i] + ".png");
        }
    }

    public HamsterRenderer(EntityRendererProvider.Context context) {
        super(context, new HamsterCraftModel(), 0.15F);
        addLayer(new BallLayer(this));
    }

    public static ResourceLocation textureForColor(int colorNumber) {
        return TEXTURES[Math.floorMod(colorNumber, TEXTURES.length)];
    }

    @Override
    protected void scale(HamsterEntity hamster, PoseStack poseStack, float partialTick) {
        poseStack.scale(0.4F, 0.4F, 0.4F);
        if (hamster.isSleeping() || ((AnimalSleepState) (Object) hamster).animaniax$isSleeping()) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(20.0F));
            poseStack.translate(0.0F, -0.15F, 0.0F);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(HamsterEntity hamster) {
        return textureForColor(hamster.getColorNumber());
    }

    private static final class HamsterCraftModel extends EntityModel<HamsterEntity> {
        private final Map<String, Vector3f> rotDeg = new HashMap<>();

        @Override
        public void setupAnim(HamsterEntity hamster, float limbSwing, float limbSwingAmount,
                              float ageInTicks, float netHeadYaw, float headPitch) {
            rotDeg.clear();
            boolean sleeping = hamster.isSleeping() || ((AnimalSleepState) (Object) hamster).animaniax$isSleeping();
            if (!sleeping) {
                rotDeg.put("hamsterHead", new Vector3f(
                        Mth.clamp(headPitch, -45.0F, 45.0F),
                        -Mth.clamp(netHeadYaw, -60.0F, 60.0F),
                        0.0F));
                float swing = Mth.cos(limbSwing * 1.2F) * 35.0F * limbSwingAmount;
                rotDeg.put("hamsterleg1", new Vector3f(swing, 0.0F, 0.0F));
                rotDeg.put("hamsterleg4", new Vector3f(swing, 0.0F, 0.0F));
                rotDeg.put("hamsterleg2", new Vector3f(-swing, 0.0F, 0.0F));
                rotDeg.put("hamsterleg3", new Vector3f(-swing, 0.0F, 0.0F));
                rotDeg.put("tail", new Vector3f(0.0F, Mth.sin(ageInTicks * 0.25F) * 10.0F, 0.0F));
            }
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int packedLight,
                                   int packedOverlay, int color) {
            CraftStudioModel model = PlaceholderModelRegistry.INSTANCE.craftStudio("hamster");
            if (model != null) {
                model.render(poseStack, consumer, packedLight, packedOverlay, color, 64.0F, 32.0F, rotDeg);
            }
        }
    }

    private static final class BallLayer extends RenderLayer<HamsterEntity, EntityModel<HamsterEntity>> {
        BallLayer(RenderLayerParent<HamsterEntity, EntityModel<HamsterEntity>> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight,
                           HamsterEntity hamster, float limbSwing, float limbSwingAmount,
                           float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            if (hamster.isInvisible() || !hamster.isInBall()) return;
            PlaceholderModel ball = PlaceholderModelRegistry.INSTANCE.entity("hamster_ball");
            if (ball == null) return;

            int colorIdx = hamster.getBallColor();
            int rgb = colorIdx >= 0 ? DyeColor.byId(Math.floorMod(colorIdx, 16)).getTextureDiffuseColor() : 0xFFFFFF;
            int argb = 0xAA000000 | (rgb & 0x00FFFFFF);

            poseStack.pushPose();
            // Rotate around ball center at y = 18/16 (1.125) blocks in entity space
            poseStack.translate(0.0F, 1.125F, 0.0F);
            poseStack.mulPose(Axis.XP.rotation(limbSwing * 1.4F));
            poseStack.translate(0.0F, -1.125F, 0.0F);
            VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucent(BALL_TEXTURE));
            ball.render(poseStack, consumer, packedLight,
                    LivingEntityRenderer.getOverlayCoords(hamster, 0.0F), argb, argb, null, null, null);
            poseStack.popPose();
        }
    }
}

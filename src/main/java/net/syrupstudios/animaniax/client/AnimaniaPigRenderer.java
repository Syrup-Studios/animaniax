package net.syrupstudios.animaniax.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Pig;
import net.syrupstudios.animaniax.client.model.PlaceholderEntityModel;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.AnimalVisualSyncState;
import net.syrupstudios.animaniax.entity.PigBreedState;

/**
 * Renders pigs with Animania breed models and optional muddy overlay layer.
 */
public final class AnimaniaPigRenderer extends MobRenderer<Pig, PlaceholderEntityModel<Pig>> {
    public AnimaniaPigRenderer(EntityRendererProvider.Context context) {
        super(context, new PlaceholderEntityModel<>(
                AnimaniaPigRenderer::modelKey,
                (pig, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, overrides) ->
                        AnimaniaModelAnimations.animateQuadruped(model, overrides, limbSwing, limbSwingAmount,
                                ageInTicks, netHeadYaw, headPitch, isSleeping(pig),
                                "Head", "Tail1", "Leg1", "Leg2", "Leg4", "Leg3")), 0.6F);
        addLayer(new MudLayer(this));
    }

    private static boolean isSleeping(Pig pig) {
        return pig.isSleeping() || ((AnimalSleepState) (Object) pig).animaniax$isSleeping();
    }

    private static boolean isFemale(Pig pig) {
        return ((AnimalVisualSyncState) (Object) pig).animaniax$isSyncedFemale();
    }

    private static String breedSlug(Pig pig) {
        return switch (((PigBreedState) (Object) pig).animaniax$getPigBreed()) {
            case "Duroc" -> "duroc";
            case "Hampshire" -> "hampshire";
            case "Large Black" -> "large_black";
            case "Large White" -> "large_white";
            case "Old Spot" -> "old_spot";
            default -> "yorkshire";
        };
    }

    private static String modelKey(Pig pig) {
        String slug = breedSlug(pig);
        if (pig.isBaby()) {
            return "hampshire".equals(slug) ? "piglet_hampshire" : "piglet";
        }
        String stage = isFemale(pig) ? "sow" : "hog";
        return switch (slug) {
            case "hampshire" -> stage + "_hampshire";
            case "large_black" -> stage + "_large_black";
            default -> stage;
        };
    }

    @Override
    protected void scale(Pig pig, PoseStack poseStack, float partialTick) {
        String slug = breedSlug(pig);
        float s;
        if (pig.isBaby()) {
            s = switch (slug) {
                case "large_black" -> 1.20F;
                case "hampshire", "large_white" -> 1.12F;
                case "old_spot" -> 1.10F;
                default -> 1.0F;
            };
        } else if (isFemale(pig)) {
            s = switch (slug) {
                case "duroc" -> 1.06F;
                case "hampshire" -> 1.02F;
                case "large_black" -> 1.14F;
                case "large_white" -> 1.08F;
                case "old_spot" -> 1.12F;
                default -> 1.0F;
            };
        } else {
            s = switch (slug) {
                case "duroc" -> 1.18F;
                case "hampshire" -> 1.12F;
                case "large_black" -> 1.20F;
                case "large_white" -> 1.16F;
                case "old_spot" -> 1.19F;
                default -> 1.10F;
            };
        }
        poseStack.scale(s, s, s);
        if (isSleeping(pig)) {
            poseStack.translate(0.0F, pig.isBaby() ? -0.15F : -0.25F, 0.0F);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Pig pig) {
        String slug = breedSlug(pig);
        String stage = pig.isBaby() ? "piglet" : (isFemale(pig) ? "sow" : "hog");
        return ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/pigs/" + stage + "_" + slug + ".png");
    }

    private static final class MudLayer extends RenderLayer<Pig, PlaceholderEntityModel<Pig>> {
        private static final ResourceLocation PIGLET_MUD =
                ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/pigs/piglet_muddy.png");
        private static final ResourceLocation HAMPSHIRE_MUD =
                ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/pigs/pig_muddy_hampshire.png");
        private static final ResourceLocation ADULT_MUD =
                ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/pigs/pig_muddy.png");

        MudLayer(RenderLayerParent<Pig, PlaceholderEntityModel<Pig>> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight, Pig pig,
                float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                float netHeadYaw, float headPitch) {
            if (pig.isInvisible() || !((AnimalVisualSyncState) (Object) pig).animaniax$isSyncedMuddy()) return;
            ResourceLocation mudTex = pig.isBaby() ? PIGLET_MUD
                    : ("hampshire".equals(breedSlug(pig)) ? HAMPSHIRE_MUD : ADULT_MUD);
            poseStack.pushPose();
            poseStack.scale(1.01F, 1.01F, 1.01F);
            VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucent(mudTex));
            getParentModel().renderToBuffer(poseStack, consumer, packedLight,
                    LivingEntityRenderer.getOverlayCoords(pig, 0.0F), 0xFFFFFFFF);
            poseStack.popPose();
        }
    }
}

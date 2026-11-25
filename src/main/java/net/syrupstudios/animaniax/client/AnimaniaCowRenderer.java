package net.syrupstudios.animaniax.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.syrupstudios.animaniax.client.model.PlaceholderEntityModel;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.AnimalVisualSyncState;
import net.syrupstudios.animaniax.entity.CowBreedState;

/**
 * Renders vanilla cows and mooshrooms using Animania bull, cow, and calf placeholder models.
 */
public final class AnimaniaCowRenderer extends MobRenderer<Cow, PlaceholderEntityModel<Cow>> {
    public AnimaniaCowRenderer(EntityRendererProvider.Context context) {
        super(context, new PlaceholderEntityModel<>(
                AnimaniaCowRenderer::modelKey,
                (cow, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, overrides) -> {
                    boolean sleeping = isSleeping(cow);
                    if (cow.isBaby() || !isFemale(cow)) {
                        AnimaniaModelAnimations.animateQuadruped(model, overrides, limbSwing, limbSwingAmount,
                                ageInTicks, netHeadYaw, headPitch, sleeping,
                                "Head", "Tail", "Leg1", "Leg0", "Leg3", "Leg2");
                    } else {
                        AnimaniaModelAnimations.animateQuadruped(model, overrides, limbSwing, limbSwingAmount,
                                ageInTicks, netHeadYaw, headPitch, sleeping,
                                "head", "Tail", "leg1", "leg2", "leg4", "leg3");
                    }
                }), 0.7F);
    }

    private static boolean isSleeping(Cow cow) {
        return cow.isSleeping() || ((AnimalSleepState) (Object) cow).animaniax$isSleeping();
    }

    private static boolean isFemale(Cow cow) {
        return ((AnimalVisualSyncState) (Object) cow).animaniax$isSyncedFemale();
    }

    private static String breedSlug(Cow cow) {
        if (cow.getType() == EntityType.MOOSHROOM) return "mooshroom";
        return switch (((CowBreedState) (Object) cow).animaniax$getBreed()) {
            case "Angus" -> "angus";
            case "Hereford" -> "hereford";
            case "Texas Longhorn" -> "longhorn";
            case "Holstein" -> "holstein";
            case "Jersey" -> "jersey";
            case "Highland" -> "highland";
            default -> "friesian";
        };
    }

    private static String modelKey(Cow cow) {
        String slug = breedSlug(cow);
        if (cow.isBaby()) {
            return switch (slug) {
                case "angus" -> "calf_angus";
                case "longhorn", "highland" -> "calf_longhorn";
                default -> "calf";
            };
        }
        String stage = isFemale(cow) ? "cow" : "bull";
        return switch (slug) {
            case "angus" -> stage + "_angus";
            case "hereford", "jersey" -> stage + "_hereford";
            case "longhorn", "highland" -> stage + "_longhorn";
            default -> stage;
        };
    }

    @Override
    protected void scale(Cow cow, PoseStack poseStack, float partialTick) {
        String slug = breedSlug(cow);
        float s;
        if (cow.isBaby()) {
            s = 1.0F;
        } else if (isFemale(cow)) {
            s = switch (slug) {
                case "friesian", "holstein" -> 1.24F;
                case "longhorn", "highland" -> 1.44F;
                default -> 1.34F;
            };
        } else {
            s = switch (slug) {
                case "friesian", "holstein", "mooshroom" -> 1.3F;
                case "longhorn", "highland" -> 1.5F;
                default -> 1.4F;
            };
        }
        poseStack.scale(s, s, s);
        if (isSleeping(cow)) {
            poseStack.translate(0.0F, cow.isBaby() ? -0.25F : -0.4F, 0.0F);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Cow cow) {
        String slug = breedSlug(cow);
        String stage = cow.isBaby() ? "calf" : (isFemale(cow) ? "cow" : "bull");
        if (("friesian".equals(slug) || "holstein".equals(slug))
                && cow.hasCustomName() && "Purplicious_Cow".equals(cow.getCustomName().getString())) {
            slug = "purplicious";
        }
        return ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/cows/" + stage + "_" + slug + ".png");
    }
}

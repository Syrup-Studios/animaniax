package net.syrupstudios.animaniax.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.syrupstudios.animaniax.client.model.PlaceholderEntityModel;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.AnimalVisualSyncState;
import net.syrupstudios.animaniax.entity.DogBreed;
import net.syrupstudios.animaniax.entity.DogEntity;

/**
 * Renders dogs using Animania breed models, coat variants, and sleeping or sitting poses.
 */
public final class AnimaniaDogRenderer extends MobRenderer<DogEntity, PlaceholderEntityModel<DogEntity>> {
    public AnimaniaDogRenderer(EntityRendererProvider.Context context) {
        super(context, new PlaceholderEntityModel<>(
                net.minecraft.client.renderer.RenderType::entityCutoutNoCull,
                dog -> dog.getBreed().id(),
                (dog, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, overrides) ->
                        AnimaniaModelAnimations.animateCatOrDog(model, overrides, limbSwing, limbSwingAmount,
                                ageInTicks, netHeadYaw, headPitch,
                                isSleeping(dog), dog.isInSittingPose(), dog.isTame()),
                dog -> isSleeping(dog) ? "sleeping" : (dog.isInSittingPose() ? "sitting" : null),
                null,
                null), 0.45F);
    }

    private static boolean isSleeping(DogEntity dog) {
        return dog.isSleeping() || ((AnimalSleepState) (Object) dog).animaniax$isSleeping();
    }

    @Override
    protected void scale(DogEntity dog, PoseStack poseStack, float partialTick) {
        DogBreed breed = dog.getBreed();
        boolean female = ((AnimalVisualSyncState) (Object) dog).animaniax$isSyncedFemale();
        float s;
        if (dog.isBaby()) {
            s = switch (breed) {
                case CHIHUAHUA, POMERANIAN -> 0.30F;
                case DACHSHUND -> 0.60F;
                case GREYHOUND, PUG -> 0.40F;
                default -> 0.50F;
            };
        } else {
            s = switch (breed) {
                case CHIHUAHUA, POMERANIAN -> female ? 0.50F : 0.60F;
                case DACHSHUND -> female ? 1.10F : 1.20F;
                case FOX -> 0.90F;
                case GREYHOUND, PUG -> female ? 0.70F : 0.80F;
                default -> female ? 0.90F : 1.00F;
            };
        }
        poseStack.scale(s, s, s);
        if (breed == DogBreed.CHIHUAHUA || breed == DogBreed.POMERANIAN || breed == DogBreed.PUG) {
            poseStack.translate(0.0F, 0.0F, -0.5F);
        } else if (breed == DogBreed.BLOOD_HOUND || breed == DogBreed.GREAT_DANE) {
            poseStack.translate(0.0F, 0.05F, 0.0F);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(DogEntity dog) {
        DogBreed breed = dog.getBreed();
        int color = dog.getColorNumber();
        String file = switch (breed) {
            case CHIHUAHUA -> "chihuahua" + (color % 2);
            case COLLIE -> "collie" + (color % 2);
            case LABRADOR -> (dog.hasCustomName() && "Gloria".equals(dog.getCustomName().getString()))
                    ? "gloria" : "labrador" + (color % 3);
            case POODLE -> "poodle" + (color % 3);
            case WOLF -> "wolf" + (color % 8);
            case FOX -> (dog.hasCustomName() && "Razz".equals(dog.getCustomName().getString()))
                    ? "razz_fox" : "fox";
            default -> breed.id();
        };
        return ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/dogs/" + file + ".png");
    }
}

package net.syrupstudios.animaniax.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.goat.Goat;
import net.syrupstudios.animaniax.client.model.PlaceholderEntityModel;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.AnimalVisualSyncState;
import net.syrupstudios.animaniax.entity.GoatBreedState;

/**
 * Renders goats using Animania buck, doe, and kid placeholder models across all seven breeds.
 */
public final class AnimaniaGoatRenderer extends MobRenderer<Goat, PlaceholderEntityModel<Goat>> {
    public AnimaniaGoatRenderer(EntityRendererProvider.Context context) {
        super(context, new PlaceholderEntityModel<>(
                AnimaniaGoatRenderer::modelKey,
                (goat, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, overrides) -> {
                    boolean sleeping = isSleeping(goat);
                    AnimaniaModelAnimations.animateQuadruped(model, overrides, limbSwing, limbSwingAmount,
                            ageInTicks, netHeadYaw, headPitch, sleeping,
                            "HeadNode", "Tail",
                            "FrontLeg_L", "FrontLeg_R", "BackLeg_L", "BackLeg_R");
                    AnimaniaModelAnimations.animateQuadrupedExtra(model, overrides, limbSwing, limbSwingAmount, sleeping,
                            "FrontLegWool_L", "FrontLegWool_R", "BackLegWool_L", "BackLegWool_R");
                }), 0.45F);
    }

    private static boolean isSleeping(Goat goat) {
        return goat.isSleeping() || ((AnimalSleepState) (Object) goat).animaniax$isSleeping();
    }

    private static boolean isFemale(Goat goat) {
        return ((AnimalVisualSyncState) (Object) goat).animaniax$isSyncedFemale();
    }

    private static String modelBreedSlug(Goat goat) {
        return switch (((GoatBreedState) (Object) goat).animaniax$getGoatBreed()) {
            case "Angora" -> "angora";
            case "Fainting" -> "fainting";
            case "Kiko" -> "kiko";
            case "Kinder" -> "kinder";
            case "Nigerian Dwarf" -> "nigerian_dwarf";
            case "Pygmy" -> "pygmy";
            default -> "alpine";
        };
    }

    private static String textureBreedSlug(Goat goat) {
        return switch (((GoatBreedState) (Object) goat).animaniax$getGoatBreed()) {
            case "Angora" -> "angora";
            case "Fainting" -> "fainting";
            case "Kiko" -> "kiko";
            case "Kinder" -> "kinder";
            case "Nigerian Dwarf" -> "nigerian";
            case "Pygmy" -> "pygmy";
            default -> "alpine";
        };
    }

    private static String modelKey(Goat goat) {
        String stage = goat.isBaby() ? "kid" : (isFemale(goat) ? "doe" : "buck");
        return stage + "_" + modelBreedSlug(goat);
    }

    @Override
    protected void scale(Goat goat, PoseStack poseStack, float partialTick) {
        String breed = ((GoatBreedState) (Object) goat).animaniax$getGoatBreed();
        float s;
        if (goat.isBaby()) {
            s = switch (breed) {
                case "Angora" -> 0.30F;
                case "Fainting" -> 0.22F;
                case "Kiko" -> 0.25F;
                case "Kinder" -> 0.27F;
                case "Nigerian Dwarf", "Pygmy" -> 0.24F;
                default -> 0.33F;
            };
        } else if (isFemale(goat)) {
            s = switch (breed) {
                case "Angora" -> 0.58F;
                case "Fainting" -> 0.40F;
                case "Kinder" -> 0.48F;
                case "Kiko", "Nigerian Dwarf", "Pygmy" -> 0.42F;
                default -> 0.60F;
            };
        } else {
            s = switch (breed) {
                case "Angora" -> 0.64F;
                case "Fainting" -> 0.42F;
                case "Kinder" -> 0.52F;
                case "Kiko", "Nigerian Dwarf", "Pygmy" -> 0.45F;
                default -> 0.67F;
            };
        }
        poseStack.scale(s, s, s);
        if (isSleeping(goat)) {
            poseStack.translate(0.0F, -0.35F, 0.0F);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Goat goat) {
        GoatBreedState state = (GoatBreedState) (Object) goat;
        String texBreed = textureBreedSlug(goat);
        if ("angora".equals(texBreed)) {
            if (goat.isBaby()) {
                return ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/goats/kid_angora.png");
            }
            String file = state.animaniax$isSheared() ? "buck_angora_sheared.png" : "buck_angora.png";
            return ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/goats/" + file);
        }
        String stage = goat.isBaby() ? "kid" : (isFemale(goat) ? "doe" : "buck");
        return ResourceLocation.fromNamespaceAndPath("animaniax",
                "textures/entity/goats/" + stage + "_" + texBreed + ".png");
    }
}

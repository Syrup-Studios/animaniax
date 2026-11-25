package net.syrupstudios.animaniax.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import net.syrupstudios.animaniax.client.model.PlaceholderEntityModel;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.AnimalVisualSyncState;
import net.syrupstudios.animaniax.entity.SheepBreedState;

/**
 * Renders sheep with Animania breed models, dyed wool coloring, and sheared states.
 */
public final class AnimaniaSheepRenderer extends MobRenderer<Sheep, PlaceholderEntityModel<Sheep>> {
    public AnimaniaSheepRenderer(EntityRendererProvider.Context context) {
        super(context, new PlaceholderEntityModel<>(
                net.minecraft.client.renderer.RenderType::entityCutoutNoCull,
                AnimaniaSheepRenderer::modelKey,
                (sheep, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, overrides) -> {
                    boolean sleeping = isSleeping(sheep);
                    AnimaniaModelAnimations.animateQuadruped(model, overrides, limbSwing, limbSwingAmount,
                            ageInTicks, netHeadYaw, headPitch, sleeping,
                            "HeadNode", "Tail",
                            "LeftFrontLeg", "RightFrontLeg", "LeftBackLeg", "RightBackLeg");
                    AnimaniaModelAnimations.animateQuadrupedExtra(model, overrides, limbSwing, limbSwingAmount, sleeping,
                            "LeftFrontLegWool", "RightFrontLegWool", "LeftBackLegWool", "RightBackLegWool");
                },
                null,
                null,
                sheep -> sheep.isSheared() ? 0xFFFFFFFF : (0xFF000000 | sheep.getColor().getTextureDiffuseColor())),
                0.55F);
    }

    private static boolean isSleeping(Sheep sheep) {
        return sheep.isSleeping() || ((AnimalSleepState) (Object) sheep).animaniax$isSleeping();
    }

    private static boolean isRam(Sheep sheep) {
        return !sheep.isBaby() && !((AnimalVisualSyncState) (Object) sheep).animaniax$isSyncedFemale();
    }

    private static String modelKey(Sheep sheep) {
        String breed = ((SheepBreedState) (Object) sheep).animaniax$getSheepBreed();
        boolean ram = isRam(sheep);
        return switch (breed) {
            case "Dorper" -> "dorper_sheep";
            case "Friesian" -> "friesian_sheep";
            case "Jacob" -> "jacob_sheep";
            case "Dorset" -> ram ? "dorset_ram" : "dorset_ewe";
            case "Merino" -> ram ? "merino_ram" : "merino_ewe";
            default -> ram ? "suffolk_ram" : "suffolk_ewe";
        };
    }

    @Override
    protected void scale(Sheep sheep, PoseStack poseStack, float partialTick) {
        String breed = ((SheepBreedState) (Object) sheep).animaniax$getSheepBreed();
        float s;
        if (sheep.isBaby()) {
            s = switch (breed) {
                case "Friesian" -> 0.36F;
                case "Jacob" -> 0.25F;
                case "Merino" -> 0.28F;
                default -> 0.33F;
            };
        } else if (isRam(sheep)) {
            s = switch (breed) {
                case "Dorper", "Suffolk" -> 0.68F;
                case "Dorset" -> 0.62F;
                case "Jacob" -> 0.52F;
                case "Merino" -> 0.56F;
                default -> 0.65F;
            };
        } else {
            s = switch (breed) {
                case "Dorper" -> 0.60F;
                case "Dorset" -> 0.58F;
                case "Jacob" -> 0.48F;
                case "Merino" -> 0.53F;
                case "Suffolk" -> 0.64F;
                default -> 0.61F;
            };
        }
        poseStack.scale(s, s, s);
        if (isSleeping(sheep)) {
            poseStack.translate(0.0F, -0.30F, 0.0F);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Sheep sheep) {
        SheepBreedState state = (SheepBreedState) (Object) sheep;
        String breed = state.animaniax$getSheepBreed();
        int color = state.animaniax$getSheepColorNumber();
        boolean sheared = sheep.isSheared();
        boolean ram = isRam(sheep);
        String shearedSuffix = sheared ? "_sheared" : "";

        String file = switch (breed) {
            case "Dorper" -> "sheep_dorper" + shearedSuffix;
            case "Jacob" -> (sheep.isBaby() && !sheared) ? "sheep_jacob_lamb" : "sheep_jacob" + shearedSuffix;
            case "Friesian" -> {
                String coat = switch (Math.floorMod(color, 3)) {
                    case 0 -> "black";
                    case 2 -> "brown";
                    default -> "white";
                };
                yield "sheep_friesian_" + coat + (ram ? "_ram" : "") + shearedSuffix;
            }
            case "Dorset", "Merino", "Suffolk" -> {
                String coat = color == 1 ? "brown" : "white";
                String sex = ram ? "ram" : "ewe";
                yield "sheep_" + breed.toLowerCase(java.util.Locale.ROOT) + "_" + coat + "_" + sex + shearedSuffix;
            }
            default -> "sheep_friesian_white" + shearedSuffix;
        };
        return ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/sheep/" + file + ".png");
    }
}

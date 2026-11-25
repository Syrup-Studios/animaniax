package net.syrupstudios.animaniax.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Rabbit;
import net.syrupstudios.animaniax.client.model.PlaceholderEntityModel;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.AnimalVisualSyncState;
import net.syrupstudios.animaniax.entity.RabbitBreedState;

/**
 * Renders rabbits using Animania breed models and Lop coat variants.
 */
public final class AnimaniaRabbitRenderer extends MobRenderer<Rabbit, PlaceholderEntityModel<Rabbit>> {
    private static final String[] LOP_VARIANTS = {
            "rabbit_lop_black", "rabbit_lop_brown", "rabbit_lop_golden", "rabbit_lop_olive",
            "rabbit_lop_patch_black", "rabbit_lop_patch_brown", "rabbit_lop_patch_grey"
    };

    public AnimaniaRabbitRenderer(EntityRendererProvider.Context context) {
        super(context, new PlaceholderEntityModel<>(
                AnimaniaRabbitRenderer::modelKey,
                (rabbit, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, overrides) ->
                        AnimaniaModelAnimations.animateRabbit(model, overrides, limbSwing, limbSwingAmount,
                                netHeadYaw, headPitch, isSleeping(rabbit))),
                0.25F);
    }

    private static boolean isSleeping(Rabbit rabbit) {
        return rabbit.isSleeping() || ((AnimalSleepState) (Object) rabbit).animaniax$isSleeping();
    }

    private static String modelKey(Rabbit rabbit) {
        return switch (((RabbitBreedState) (Object) rabbit).animaniax$getRabbitBreed()) {
            case "Chinchilla" -> "chinchilla";
            case "Dutch" -> "dutch";
            case "Havana" -> "havana";
            case "Jack" -> "jack";
            case "Lop" -> "lop";
            case "New Zealand" -> "new_zealand";
            case "Rex" -> "rex";
            default -> "cottontail";
        };
    }

    @Override
    protected void scale(Rabbit rabbit, PoseStack poseStack, float partialTick) {
        String key = modelKey(rabbit);
        float s;
        if (rabbit.isBaby()) {
            s = switch (key) {
                case "chinchilla" -> 0.35F;
                case "jack", "new_zealand" -> 0.33F;
                case "rex" -> 0.30F;
                case "lop" -> 0.25F;
                default -> 0.28F;
            };
        } else {
            boolean female = ((AnimalVisualSyncState) (Object) rabbit).animaniax$isSyncedFemale();
            s = switch (key) {
                case "chinchilla" -> female ? 0.59F : 0.57F;
                case "dutch" -> female ? 0.53F : 0.52F;
                case "havana", "cottontail" -> female ? 0.56F : 0.52F;
                case "jack" -> female ? 0.57F : 0.56F;
                case "lop" -> female ? 0.51F : 0.47F;
                case "new_zealand" -> female ? 0.58F : 0.57F;
                case "rex" -> female ? 0.58F : 0.54F;
                default -> 0.55F;
            };
        }
        poseStack.scale(s, s, s);
    }

    @Override
    public ResourceLocation getTextureLocation(Rabbit rabbit) {
        if (rabbit.getVariant() == Rabbit.Variant.EVIL) {
            return ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/rabbits/rabbit_killer.png");
        }
        String key = modelKey(rabbit);
        if ("lop".equals(key)) {
            int idx = Math.floorMod(((AnimalVisualSyncState) (Object) rabbit).animaniax$getSyncedVariant(), LOP_VARIANTS.length);
            return ResourceLocation.fromNamespaceAndPath("animaniax",
                    "textures/entity/rabbits/" + LOP_VARIANTS[idx] + ".png");
        }
        return ResourceLocation.fromNamespaceAndPath("animaniax",
                "textures/entity/rabbits/rabbit_" + key + ".png");
    }
}

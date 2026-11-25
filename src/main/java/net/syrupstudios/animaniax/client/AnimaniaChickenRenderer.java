package net.syrupstudios.animaniax.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Chicken;
import net.syrupstudios.animaniax.client.model.PlaceholderEntityModel;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.ChickenBreedState;
import net.syrupstudios.animaniax.entity.ChickenSexState;

/**
 * Renders chickens using Animania chick, hen, and rooster placeholder models.
 */
public final class AnimaniaChickenRenderer extends MobRenderer<Chicken, PlaceholderEntityModel<Chicken>> {
    public AnimaniaChickenRenderer(EntityRendererProvider.Context context) {
        super(context, new PlaceholderEntityModel<>(
                AnimaniaChickenRenderer::modelKey,
                (chicken, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, overrides) ->
                        AnimaniaModelAnimations.animateBird(model, overrides, limbSwing, limbSwingAmount,
                                ageInTicks, netHeadYaw, headPitch, isSleeping(chicken), false)),
                0.3F);
    }

    private static boolean isSleeping(Chicken chicken) {
        return chicken.isSleeping() || ((AnimalSleepState) (Object) chicken).animaniax$isSleeping();
    }

    private static boolean isHen(Chicken chicken) {
        return ((ChickenSexState) (Object) chicken).animaniax$isHen();
    }

    private static String modelKey(Chicken chicken) {
        if (chicken.isBaby()) return "chick";
        return isHen(chicken) ? "hen" : "rooster";
    }

    private static String colorSlug(Chicken chicken) {
        return switch (((ChickenBreedState) (Object) chicken).animaniax$getChickenBreed()) {
            case "Orpington" -> "golden";
            case "Plymouth Rock" -> "specked";
            case "Rhode Island Red" -> "red";
            case "Wyandotte" -> "brown";
            default -> "white";
        };
    }

    @Override
    protected void scale(Chicken chicken, PoseStack poseStack, float partialTick) {
        float s;
        if (chicken.isBaby()) {
            s = 1.0F;
        } else if (isHen(chicken)) {
            s = 0.9F;
        } else {
            s = (chicken.hasCustomName() && "Ducktonio".equals(chicken.getCustomName().getString())) ? 1.5F : 1.0F;
        }
        poseStack.scale(s, s, s);
        if (isSleeping(chicken)) {
            poseStack.translate(0.0F, -0.20F, 0.0F);
        }
    }

    @Override
    protected float getBob(Chicken chicken, float partialTick) {
        float flap = Mth.lerp(partialTick, chicken.oFlap, chicken.flap);
        float flapSpeed = Mth.lerp(partialTick, chicken.oFlapSpeed, chicken.flapSpeed);
        return (Mth.sin(flap) + 1.0F) * flapSpeed;
    }

    @Override
    public ResourceLocation getTextureLocation(Chicken chicken) {
        if (!chicken.isBaby() && !isHen(chicken)
                && chicken.hasCustomName() && "Ducktonio".equals(chicken.getCustomName().getString())) {
            return ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/chickens/rooster_antonio.png");
        }
        String stage = chicken.isBaby() ? "chick" : (isHen(chicken) ? "hen" : "rooster");
        return ResourceLocation.fromNamespaceAndPath("animaniax",
                "textures/entity/chickens/" + stage + "_" + colorSlug(chicken) + ".png");
    }
}

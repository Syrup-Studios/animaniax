package net.syrupstudios.animaniax.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.syrupstudios.animaniax.client.model.PlaceholderEntityModel;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.AnimalVisualSyncState;
import net.syrupstudios.animaniax.entity.CatBreed;
import net.syrupstudios.animaniax.entity.CompanionCatEntity;

/**
 * Renders companion cats using Animania breed models and sleeping or sitting poses.
 */
public final class AnimaniaCatRenderer extends MobRenderer<CompanionCatEntity, PlaceholderEntityModel<CompanionCatEntity>> {
    public AnimaniaCatRenderer(EntityRendererProvider.Context context) {
        super(context, new PlaceholderEntityModel<>(
                net.minecraft.client.renderer.RenderType::entityCutoutNoCull,
                AnimaniaCatRenderer::modelKey,
                (cat, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, overrides) ->
                        AnimaniaModelAnimations.animateCatOrDog(model, overrides, limbSwing, limbSwingAmount,
                                ageInTicks, netHeadYaw, headPitch,
                                isSleeping(cat), cat.isInSittingPose(), false),
                cat -> isSleeping(cat) ? "sleeping" : (cat.isInSittingPose() ? "sitting" : null),
                null,
                null), 0.25F);
    }

    private static boolean isSleeping(CompanionCatEntity cat) {
        return cat.isLying() || cat.isSleeping() || ((AnimalSleepState) (Object) cat).animaniax$isSleeping();
    }

    private static String modelKey(CompanionCatEntity cat) {
        CatBreed breed = cat.getBreed();
        return breed == CatBreed.NORWEGIAN ? "cat_ragdoll" : "cat_" + breed.id();
    }

    @Override
    protected void scale(CompanionCatEntity cat, PoseStack poseStack, float partialTick) {
        CatBreed breed = cat.getBreed();
        float s;
        if (cat.isBaby()) {
            s = breed == CatBreed.ASIATIC ? 0.35F : 0.30F;
        } else {
            boolean female = ((AnimalVisualSyncState) (Object) cat).animaniax$isSyncedFemale();
            s = switch (breed) {
                case RAGDOLL -> female ? 0.655F : 0.67F;
                case AMERICAN_SHORTHAIR -> female ? 0.635F : 0.65F;
                case ASIATIC -> female ? 0.765F : 0.78F;
                case EXOTIC -> female ? 0.585F : 0.60F;
                case NORWEGIAN -> female ? 0.575F : 0.59F;
                case OCELOT -> female ? 0.82F : 0.85F;
                case SIAMESE -> female ? 0.555F : 0.57F;
                case TABBY -> female ? 0.68F : 0.70F;
            };
        }
        poseStack.scale(s, s, s);
    }

    @Override
    public ResourceLocation getTextureLocation(CompanionCatEntity cat) {
        return ResourceLocation.fromNamespaceAndPath("animaniax",
                "textures/entity/cats/" + cat.getBreed().id() + ".png");
    }
}

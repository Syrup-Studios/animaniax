package net.syrupstudios.animaniax.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.entity.animal.horse.Variant;
import net.syrupstudios.animaniax.client.model.PlaceholderEntityModel;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.AnimalVisualSyncState;
import net.syrupstudios.animaniax.entity.DraftHorseEntity;

import java.util.Set;

/**
 * Renders draft horses using Animania stallion, mare, and foal placeholder models with optional saddle parts.
 */
public final class DraftHorseRenderer extends MobRenderer<DraftHorseEntity, PlaceholderEntityModel<DraftHorseEntity>> {
    private static final Set<String> SADDLE_PARTS = Set.of(
            "SaddleBase", "SaddleBase2", "SaddleBase3",
            "Saddle", "Saddle2", "Saddle3", "Saddle4", "Saddle5", "Saddle6", "Saddle7",
            "SaddleHump", "SaddleHump2",
            "Footstrap", "foot1", "foot2", "foot3", "foot4",
            "Footstrap2", "foot1a", "foot2a", "foot3a", "foot4a",
            "Strap1", "Strap2", "Strap3",
            "Block1", "Block2", "Reins1");

    public DraftHorseRenderer(EntityRendererProvider.Context context) {
        super(context, new PlaceholderEntityModel<>(
                net.minecraft.client.renderer.RenderType::entityCutoutNoCull,
                DraftHorseRenderer::modelKey,
                (horse, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, overrides) ->
                        AnimaniaModelAnimations.animateQuadruped(model, overrides, limbSwing, limbSwingAmount,
                                ageInTicks, netHeadYaw, headPitch, isSleeping(horse),
                                "HeadNode", "TailNode",
                                "FrontLeftMuscle", "FrontRightMuscle", "BackLeftMuscle", "BackRightMuscle"),
                null,
                horse -> (!horse.isBaby() && horse.isSaddled()) ? SADDLE_PARTS : null,
                null), 0.85F);
    }

    private static boolean isSleeping(DraftHorseEntity horse) {
        return horse.isSleeping() || ((AnimalSleepState) (Object) horse).animaniax$isSleeping();
    }

    private static boolean isMare(DraftHorseEntity horse) {
        return ((AnimalVisualSyncState) (Object) horse).animaniax$isSyncedFemale();
    }

    private static String modelKey(DraftHorseEntity horse) {
        if (horse.isBaby()) return "draft_horse_foal";
        return isMare(horse) ? "draft_horse_mare" : "draft_horse_stallion";
    }

    @Override
    protected void scale(DraftHorseEntity horse, PoseStack poseStack, float partialTick) {
        float s = horse.isBaby() ? 0.45F : (isMare(horse) ? 0.72F : 0.85F);
        poseStack.scale(s, s, s);
        if (isSleeping(horse)) {
            poseStack.translate(0.0F, -0.35F, 0.0F);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(DraftHorseEntity horse) {
        Variant variant = horse.getVariant();
        Markings markings = horse.getMarkings();
        String color;
        if (markings == Markings.BLACK_DOTS) {
            color = "bw2";
        } else if ((markings == Markings.WHITE_FIELD || markings == Markings.WHITE_DOTS)
                && (variant == Variant.BLACK || variant == Variant.DARK_BROWN || variant == Variant.BROWN)) {
            color = "bw1";
        } else {
            color = switch (variant) {
                case BLACK -> "black";
                case GRAY -> "grey";
                case CHESTNUT, BROWN, DARK_BROWN -> "red";
                case WHITE, CREAMY -> "white";
            };
        }
        return ResourceLocation.fromNamespaceAndPath("animaniax",
                "textures/entity/horses/draft_horse_" + color + ".png");
    }
}

package net.syrupstudios.animaniax.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.syrupstudios.animaniax.client.model.PlaceholderEntityModel;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.PeafowlEntity;

import java.util.Locale;

/**
 * Renders peafowl using Animania peachick, peahen, and peacock placeholder models.
 */
public final class PeafowlRenderer extends MobRenderer<PeafowlEntity, PlaceholderEntityModel<PeafowlEntity>> {
    public PeafowlRenderer(EntityRendererProvider.Context context) {
        super(context, new PlaceholderEntityModel<>(
                PeafowlRenderer::modelKey,
                (peafowl, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, overrides) ->
                        AnimaniaModelAnimations.animateBird(model, overrides, limbSwing, limbSwingAmount,
                                ageInTicks, netHeadYaw, headPitch, isSleeping(peafowl),
                                !peafowl.isBaby() && !peafowl.animaniax$isSyncedHen())),
                0.3F);
    }

    private static boolean isSleeping(PeafowlEntity peafowl) {
        return peafowl.isSleeping() || ((AnimalSleepState) (Object) peafowl).animaniax$isSleeping();
    }

    private static String modelKey(PeafowlEntity peafowl) {
        if (peafowl.isBaby()) return "peachick";
        return peafowl.animaniax$isSyncedHen() ? "peafowl" : "peacock";
    }

    @Override
    protected void scale(PeafowlEntity peafowl, PoseStack poseStack, float partialTick) {
        float s = peafowl.isBaby() ? 0.35F : (peafowl.animaniax$isSyncedHen() ? 0.9F : 1.0F);
        poseStack.scale(s, s, s);
        if (isSleeping(peafowl)) {
            poseStack.translate(0.0F, -0.25F, 0.0F);
        }
    }

    @Override
    public float getBob(PeafowlEntity peafowl, float partialTick) {
        return (Mth.sin(Mth.lerp(partialTick, peafowl.oFlap, peafowl.flap)) + 1.0F)
                * Mth.lerp(partialTick, peafowl.oFlapSpeed, peafowl.flapSpeed);
    }

    @Override
    public ResourceLocation getTextureLocation(PeafowlEntity peafowl) {
        String morph = peafowl.animaniax$getMorph().toLowerCase(Locale.ROOT);
        String stage = peafowl.isBaby() ? "peachick" : (peafowl.animaniax$isSyncedHen() ? "peafowl" : "peacock");
        return ResourceLocation.fromNamespaceAndPath("animaniax",
                "textures/entity/peacocks/" + stage + "_" + morph + ".png");
    }
}

package net.syrupstudios.animaniax.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.syrupstudios.animaniax.client.model.PlaceholderEntityModel;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.FerretContent;
import net.syrupstudios.animaniax.entity.FerretEntity;

/**
 * Renders grey and white ferrets using the Animania ferret placeholder model.
 */
public final class FerretRenderer extends MobRenderer<FerretEntity, PlaceholderEntityModel<FerretEntity>> {
    private static final ResourceLocation GREY =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/rodents/ferret_grey.png");
    private static final ResourceLocation WHITE =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/rodents/ferret_white.png");

    public FerretRenderer(EntityRendererProvider.Context context) {
        super(context, new PlaceholderEntityModel<>(
                ferret -> "ferret",
                (ferret, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, overrides) ->
                        AnimaniaModelAnimations.animateFerret(model, overrides, limbSwing, limbSwingAmount,
                                ageInTicks, netHeadYaw, headPitch,
                                ferret.isSleeping() || ((AnimalSleepState) (Object) ferret).animaniax$isSleeping())),
                0.2F);
    }

    @Override
    protected void scale(FerretEntity ferret, PoseStack poseStack, float partialTick) {
        poseStack.scale(0.5F, 0.5F, 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(FerretEntity ferret) {
        return ferret.getType() == FerretContent.WHITE ? WHITE : GREY;
    }
}

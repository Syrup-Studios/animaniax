package net.syrupstudios.animaniax.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.frog.Frog;
import net.syrupstudios.animaniax.client.model.PlaceholderEntityModel;
import net.syrupstudios.animaniax.entity.CommonFrogEntity;
import net.syrupstudios.animaniax.entity.DartFrogEntity;
import net.syrupstudios.animaniax.entity.ToadEntity;

/**
 * Renders frogs, dart frogs, and toads using Animania amphibian placeholder models.
 */
public final class AnimaniaAmphibianRenderer extends MobRenderer<Frog, PlaceholderEntityModel<Frog>> {
    private static final ResourceLocation TOAD_TEX =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/amphibians/toads/toad.png");
    private static final ResourceLocation FROG_DEFAULT =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/amphibians/frogs/default_frog.png");
    private static final ResourceLocation FROG_GREEN =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/amphibians/frogs/green_frog.png");
    private static final ResourceLocation FROG_PEPE =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/amphibians/frogs/pepe_frog.png");
    private static final ResourceLocation DART_YELLOW =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/amphibians/dartfrogs/yellow_dart_frog.png");
    private static final ResourceLocation DART_RED =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/amphibians/dartfrogs/red_dart_frog.png");
    private static final ResourceLocation DART_BLUE =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/amphibians/dartfrogs/blue_dart_frog.png");

    public AnimaniaAmphibianRenderer(EntityRendererProvider.Context context) {
        super(context, new PlaceholderEntityModel<>(
                frog -> frog instanceof ToadEntity ? "toad" : "frog",
                (frog, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, overrides) ->
                        AnimaniaModelAnimations.animateAmphibian(model, overrides, limbSwing, limbSwingAmount,
                                netHeadYaw, headPitch)),
                0.15F);
    }

    @Override
    protected void scale(Frog frog, PoseStack poseStack, float partialTick) {
        float s;
        if (frog instanceof ToadEntity) {
            // Compensate for ToadEntity's 1.3x SCALE attribute multiplier
            s = 0.25F;
        } else if (frog instanceof DartFrogEntity) {
            s = 0.22F;
        } else if (frog instanceof CommonFrogEntity common && common.isPepe()) {
            s = 0.50F;
        } else {
            s = 0.30F;
        }
        poseStack.scale(s, s, s);
    }

    @Override
    public ResourceLocation getTextureLocation(Frog frog) {
        if (frog instanceof ToadEntity) {
            return TOAD_TEX;
        }
        if (frog instanceof DartFrogEntity) {
            var key = frog.getVariant().unwrapKey().orElse(FrogVariant.TEMPERATE);
            if (key == FrogVariant.WARM) return DART_RED;
            if (key == FrogVariant.COLD) return DART_BLUE;
            return DART_YELLOW;
        }
        if (frog instanceof CommonFrogEntity common && common.isPepe()) {
            return FROG_PEPE;
        }
        var key = frog.getVariant().unwrapKey().orElse(FrogVariant.TEMPERATE);
        return key == FrogVariant.WARM ? FROG_GREEN : FROG_DEFAULT;
    }
}

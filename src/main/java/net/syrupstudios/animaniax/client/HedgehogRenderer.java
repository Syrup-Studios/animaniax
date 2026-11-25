package net.syrupstudios.animaniax.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.syrupstudios.animaniax.client.model.PlaceholderEntityModel;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.HedgehogContent;
import net.syrupstudios.animaniax.entity.HedgehogEntity;

/**
 * Renders standard and albino hedgehogs with Sonic and Sanic name variants.
 */
public final class HedgehogRenderer extends MobRenderer<HedgehogEntity, PlaceholderEntityModel<HedgehogEntity>> {
    private static final ResourceLocation BROWN =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/rodents/hedgehog.png");
    private static final ResourceLocation ALBINO =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/rodents/hedgehog_white.png");
    private static final ResourceLocation SONIC =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/rodents/hedgehog_sonic.png");
    private static final ResourceLocation SANIC =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/entity/rodents/hedgehog_sanic.png");

    public HedgehogRenderer(EntityRendererProvider.Context context) {
        super(context, new PlaceholderEntityModel<>(
                hedgehog -> "hedgehog",
                (hedgehog, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, overrides) ->
                        AnimaniaModelAnimations.animateHedgehog(model, overrides, limbSwing, limbSwingAmount,
                                netHeadYaw, headPitch,
                                hedgehog.isSleeping() || ((AnimalSleepState) (Object) hedgehog).animaniax$isSleeping())),
                0.2F);
    }

    @Override
    protected void scale(HedgehogEntity hedgehog, PoseStack poseStack, float partialTick) {
        poseStack.scale(0.6F, 0.6F, 0.6F);
        if (hedgehog.hasCustomName() && "Sanic".equals(hedgehog.getCustomName().getString())) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-20.0F));
            poseStack.scale(1.2F, 1.7F, 1.6F);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(HedgehogEntity hedgehog) {
        if (hedgehog.hasCustomName()) {
            String name = hedgehog.getCustomName().getString();
            if ("Sonic".equals(name)) return SONIC;
            if ("Sanic".equals(name)) return SANIC;
        }
        return hedgehog.getType() == HedgehogContent.HEDGEHOG_ALBINO ? ALBINO : BROWN;
    }
}

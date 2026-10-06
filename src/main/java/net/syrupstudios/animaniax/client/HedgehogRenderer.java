package net.syrupstudios.animaniax.client;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.syrupstudios.animaniax.entity.HedgehogContent;
import net.syrupstudios.animaniax.entity.HedgehogEntity;

public final class HedgehogRenderer extends MobRenderer<HedgehogEntity, HedgehogModel> {
    private static final ResourceLocation BROWN = ResourceLocation.withDefaultNamespace("textures/entity/rabbit/brown.png");
    private static final ResourceLocation ALBINO = ResourceLocation.withDefaultNamespace("textures/entity/rabbit/white.png");

    public HedgehogRenderer(EntityRendererProvider.Context context) {
        super(context, new HedgehogModel(context.bakeLayer(ModelLayers.RABBIT)), 0.2F);
    }

    @Override public ResourceLocation getTextureLocation(HedgehogEntity hedgehog) {
        return hedgehog.getType() == HedgehogContent.HEDGEHOG_ALBINO ? ALBINO : BROWN;
    }
}

package net.syrupstudios.animaniax.client;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.syrupstudios.animaniax.entity.HamsterEntity;

public final class HamsterRenderer extends MobRenderer<HamsterEntity, HamsterModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/rabbit/brown.png");

    public HamsterRenderer(EntityRendererProvider.Context context) {
        super(context, new HamsterModel(context.bakeLayer(ModelLayers.RABBIT)), 0.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(HamsterEntity hamster) { return TEXTURE; }
}

package net.syrupstudios.animaniax.client;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.syrupstudios.animaniax.entity.FerretContent;
import net.syrupstudios.animaniax.entity.FerretEntity;

public final class FerretRenderer extends MobRenderer<FerretEntity, FerretModel> {
    private static final ResourceLocation GREY = ResourceLocation.withDefaultNamespace("textures/entity/rabbit/brown.png");
    private static final ResourceLocation WHITE = ResourceLocation.withDefaultNamespace("textures/entity/rabbit/white.png");

    public FerretRenderer(EntityRendererProvider.Context context) {
        super(context, new FerretModel(context.bakeLayer(ModelLayers.RABBIT)), 0.2F);
    }

    @Override public ResourceLocation getTextureLocation(FerretEntity ferret) {
        return ferret.getType() == FerretContent.WHITE ? WHITE : GREY;
    }
}

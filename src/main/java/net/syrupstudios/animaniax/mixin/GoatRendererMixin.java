package net.syrupstudios.animaniax.mixin;

import net.minecraft.client.model.GoatModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.GoatRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.animal.goat.Goat;
import net.syrupstudios.animaniax.client.AngoraGoatCoatLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

@Mixin(GoatRenderer.class)
abstract class GoatRendererMixin extends MobRenderer<Goat, GoatModel<Goat>> {
    protected GoatRendererMixin(EntityRendererProvider.Context context, GoatModel<Goat> model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void animaniax$addAngoraCoat(EntityRendererProvider.Context context, CallbackInfo ci) {
        addLayer(new AngoraGoatCoatLayer(this, context.getModelSet()));
    }
}

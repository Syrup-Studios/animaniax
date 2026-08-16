package net.syrupstudios.animaniax.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.syrupstudios.animaniax.entity.GoatFaintingState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
abstract class FaintingGoatPoseMixin {
    @Inject(method = "setupRotations", at = @At("TAIL"))
    private void animaniax$rotateFaintingGoat(LivingEntity entity, PoseStack poseStack, float ageInTicks,
            float bodyRot, float scale, float yRot, CallbackInfo ci) {
        if (entity.getType() != EntityType.GOAT || !(entity instanceof GoatFaintingState goat)
                || !goat.animaniax$isFainting()) return;

        float pivotHeight = entity.getBbHeight() * 0.5F;
        poseStack.translate(0.0F, pivotHeight, 0.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(86.0F));
        poseStack.translate(0.0F, -pivotHeight, 0.0F);
    }
}

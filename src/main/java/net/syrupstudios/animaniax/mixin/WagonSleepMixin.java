package net.syrupstudios.animaniax.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.syrupstudios.animaniax.entity.WagonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
abstract class WagonSleepMixin {
    @Inject(method = "checkBedExists", at = @At("HEAD"), cancellable = true)
    private void animaniax$keepWagonSleep(CallbackInfoReturnable<Boolean> callback) {
        if ((Object) this instanceof ServerPlayer player && WagonEntity.supportsSleepAt(player)) {
            callback.setReturnValue(true);
        }
    }
}

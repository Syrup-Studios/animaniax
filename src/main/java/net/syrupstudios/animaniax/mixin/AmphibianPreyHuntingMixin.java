package net.syrupstudios.animaniax.mixin;

import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.FrogAi;
import net.syrupstudios.animaniax.entity.AmphibianBrainPolicy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FrogAi.class)
abstract class AmphibianPreyHuntingMixin {
    @Inject(method = "canAttack(Lnet/minecraft/world/entity/animal/frog/Frog;)Z",
            at = @At("HEAD"), cancellable = true)
    private static void animaniax$disableAmphibianBrainHunting(Frog frog, CallbackInfoReturnable<Boolean> cir) {
        if (AmphibianBrainPolicy.applies(frog)) cir.setReturnValue(false);
    }
}

package net.syrupstudios.animaniax.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.FrogAi;
import net.minecraft.world.entity.ai.Brain;
import net.syrupstudios.animaniax.entity.CommonFrogEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Frog.class)
abstract class PepeFrogBrainMixin {
    @Redirect(method = "customServerAiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/Brain;tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void animaniax$skipPepeBrainTick(Brain brain, ServerLevel level, LivingEntity entity) {
        if (!(entity instanceof CommonFrogEntity frog) || !frog.isPepe()) brain.tick(level, entity);
    }

    @Redirect(method = "customServerAiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/frog/FrogAi;updateActivity(Lnet/minecraft/world/entity/animal/frog/Frog;)V"))
    private void animaniax$skipPepeBrainActivity(Frog frog) {
        if (!(frog instanceof CommonFrogEntity commonFrog) || !commonFrog.isPepe())
            FrogAi.updateActivity(frog);
    }
}

package net.syrupstudios.animaniax.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.TemptingSensor;
import net.syrupstudios.animaniax.entity.AmphibianBrainPolicy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TemptingSensor.class)
abstract class AmphibianTemptationSensorMixin {
    @Inject(method = "doTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/PathfinderMob;)V",
            at = @At("HEAD"), cancellable = true)
    private void animaniax$clearAmphibianTemptation(ServerLevel level, PathfinderMob mob, CallbackInfo ci) {
        if (!AmphibianBrainPolicy.applies(mob)) return;
        mob.getBrain().eraseMemory(MemoryModuleType.TEMPTING_PLAYER);
        ci.cancel();
    }
}

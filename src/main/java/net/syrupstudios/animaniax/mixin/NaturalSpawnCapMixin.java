package net.syrupstudios.animaniax.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.ServerLevelAccessor;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.entity.NaturalSpawnCaps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpawnPlacements.class)
abstract class NaturalSpawnCapMixin {
    @Inject(method = "checkSpawnRules", at = @At("HEAD"), cancellable = true)
    private static <T extends net.minecraft.world.entity.Entity> void animaniax$limitNaturalPopulation(
            EntityType<T> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos,
            RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        if (reason != MobSpawnType.NATURAL && reason != MobSpawnType.CHUNK_GENERATION) {
            return;
        }
        if ((type == EntityType.WOLF && AnimalCareConfig.replaceVanillaWolves)
                || (type == EntityType.OCELOT && AnimalCareConfig.replaceVanillaOcelots)) {
            cir.setReturnValue(false);
            return;
        }
        if (NaturalSpawnCaps.isOverLimit(type, level, pos)) {
            cir.setReturnValue(false);
        }
    }
}

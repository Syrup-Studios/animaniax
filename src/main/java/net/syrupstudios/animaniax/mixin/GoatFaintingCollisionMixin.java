package net.syrupstudios.animaniax.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.syrupstudios.animaniax.entity.GoatBreedState;
import net.syrupstudios.animaniax.entity.GoatFaintingControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
abstract class GoatFaintingCollisionMixin {
    @Inject(method = "doPush", at = @At("HEAD"))
    private void animaniax$checkFaintingGoatCollision(Entity other, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Mob mob && other instanceof Player player)
            animaniax$tryStartFainting(player, mob);
        else if (entity instanceof Player player && other instanceof Mob mob)
            animaniax$tryStartFainting(player, mob);
    }

    @Unique private static void animaniax$tryStartFainting(Player player, Mob mob) {
        if (player.isSprinting() && mob.getType() == EntityType.GOAT
                && mob instanceof GoatBreedState breed && "Fainting".equals(breed.animaniax$getGoatBreed()))
            ((GoatFaintingControl) mob).animaniax$startFainting();
    }
}

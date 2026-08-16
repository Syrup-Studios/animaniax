package net.syrupstudios.animaniax.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.syrupstudios.animaniax.entity.RabbitBreedState;
import net.syrupstudios.animaniax.entity.RabbitKillerState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
abstract class RabbitBreedMixin implements RabbitBreedState, RabbitKillerState {
    @Unique private static final String[] ANIMANIAX$BREEDS = {
            "Lop", "Rex", "Dutch", "Havana", "New Zealand", "Jack", "Cottontail", "Chinchilla"
    };
    @Unique private String animaniax$rabbitBreed = "Cottontail";
    @Unique private boolean animaniax$killerStatsBoosted;
    @Unique private double animaniax$preKillerMaxHealth;
    @Unique private double animaniax$preKillerAttackDamage;

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$saveRabbitBreed(CompoundTag tag, CallbackInfo ci) {
        if (((Mob) (Object) this).getType() != EntityType.RABBIT) return;
        tag.putString("AnimaniaxRabbitBreed", animaniax$rabbitBreed);
        tag.putBoolean("AnimaniaxKillerStatsBoosted", animaniax$killerStatsBoosted);
        if (animaniax$killerStatsBoosted) {
            tag.putDouble("AnimaniaxPreKillerMaxHealth", animaniax$preKillerMaxHealth);
            tag.putDouble("AnimaniaxPreKillerAttackDamage", animaniax$preKillerAttackDamage);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$loadRabbitBreed(CompoundTag tag, CallbackInfo ci) {
        if (((Mob) (Object) this).getType() == EntityType.RABBIT && tag.contains("AnimaniaxRabbitBreed")) {
            animaniax$setRabbitBreed(tag.getString("AnimaniaxRabbitBreed"));
        }
        if (((Mob) (Object) this).getType() == EntityType.RABBIT) {
            animaniax$killerStatsBoosted = tag.getBoolean("AnimaniaxKillerStatsBoosted")
                    && tag.contains("AnimaniaxPreKillerMaxHealth") && tag.contains("AnimaniaxPreKillerAttackDamage");
            animaniax$preKillerMaxHealth = animaniax$killerStatsBoosted
                    ? tag.getDouble("AnimaniaxPreKillerMaxHealth") : 0.0;
            animaniax$preKillerAttackDamage = animaniax$killerStatsBoosted
                    ? tag.getDouble("AnimaniaxPreKillerAttackDamage") : 0.0;
        }
    }

    @Override @Unique public String animaniax$getRabbitBreed() { return animaniax$rabbitBreed; }

    @Override @Unique public void animaniax$setRabbitBreed(String breed) {
        for (String validBreed : ANIMANIAX$BREEDS) {
            if (validBreed.equals(breed)) {
                animaniax$rabbitBreed = validBreed;
                return;
            }
        }
        animaniax$rabbitBreed = "Cottontail";
    }

    @Override @Unique public boolean animaniax$areKillerStatsBoosted() { return animaniax$killerStatsBoosted; }
    @Override @Unique public void animaniax$setKillerStatsBoosted(boolean boosted) { animaniax$killerStatsBoosted = boosted; }
    @Override @Unique public double animaniax$getPreKillerMaxHealth() { return animaniax$preKillerMaxHealth; }
    @Override @Unique public void animaniax$setPreKillerMaxHealth(double value) { animaniax$preKillerMaxHealth = value; }
    @Override @Unique public double animaniax$getPreKillerAttackDamage() { return animaniax$preKillerAttackDamage; }
    @Override @Unique public void animaniax$setPreKillerAttackDamage(double value) { animaniax$preKillerAttackDamage = value; }
}

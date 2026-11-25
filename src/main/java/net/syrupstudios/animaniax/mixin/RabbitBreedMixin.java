package net.syrupstudios.animaniax.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.syrupstudios.animaniax.entity.AnimalVisualSyncState;
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
    @Unique private int animaniax$lopVariant = -1;
    @Unique private boolean animaniax$killerStatsBoosted;
    @Unique private double animaniax$preKillerMaxHealth;
    @Unique private double animaniax$preKillerAttackDamage;

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$saveRabbitBreed(CompoundTag tag, CallbackInfo ci) {
        if (((Mob) (Object) this).getType() != EntityType.RABBIT) return;
        tag.putString("AnimaniaxRabbitBreed", animaniax$rabbitBreed);
        if (animaniax$lopVariant >= 0) tag.putInt("AnimaniaxRabbitVariant", animaniax$lopVariant);
        tag.putBoolean("AnimaniaxKillerStatsBoosted", animaniax$killerStatsBoosted);
        if (animaniax$killerStatsBoosted) {
            tag.putDouble("AnimaniaxPreKillerMaxHealth", animaniax$preKillerMaxHealth);
            tag.putDouble("AnimaniaxPreKillerAttackDamage", animaniax$preKillerAttackDamage);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$loadRabbitBreed(CompoundTag tag, CallbackInfo ci) {
        Mob mob = (Mob) (Object) this;
        if (mob.getType() == EntityType.RABBIT && tag.contains("AnimaniaxRabbitBreed")) {
            animaniax$setRabbitBreed(tag.getString("AnimaniaxRabbitBreed"));
        }
        if (mob.getType() == EntityType.RABBIT) {
            if (tag.contains("AnimaniaxRabbitVariant")) {
                animaniax$lopVariant = Math.floorMod(tag.getInt("AnimaniaxRabbitVariant"), 7);
                if (!mob.level().isClientSide) ((AnimalVisualSyncState) mob).animaniax$setSyncedVariant(animaniax$lopVariant);
            }
            animaniax$killerStatsBoosted = tag.getBoolean("AnimaniaxKillerStatsBoosted")
                    && tag.contains("AnimaniaxPreKillerMaxHealth") && tag.contains("AnimaniaxPreKillerAttackDamage");
            animaniax$preKillerMaxHealth = animaniax$killerStatsBoosted
                    ? tag.getDouble("AnimaniaxPreKillerMaxHealth") : 0.0;
            animaniax$preKillerAttackDamage = animaniax$killerStatsBoosted
                    ? tag.getDouble("AnimaniaxPreKillerAttackDamage") : 0.0;
        }
    }

    @Override @Unique public String animaniax$getRabbitBreed() {
        Mob mob = (Mob) (Object) this;
        if (mob.level().isClientSide) {
            String synced = ((AnimalVisualSyncState) mob).animaniax$getSyncedBreed();
            if (!synced.isEmpty()) return synced;
        }
        return animaniax$rabbitBreed;
    }

    @Override @Unique public void animaniax$setRabbitBreed(String breed) {
        for (String validBreed : ANIMANIAX$BREEDS) {
            if (validBreed.equals(breed)) {
                animaniax$rabbitBreed = validBreed;
                Mob mob = (Mob) (Object) this;
                if (!mob.level().isClientSide && mob.getType() == EntityType.RABBIT) {
                    if (animaniax$lopVariant < 0) animaniax$lopVariant = mob.getRandom().nextInt(7);
                    ((AnimalVisualSyncState) mob).animaniax$setSyncedBreed(validBreed);
                    ((AnimalVisualSyncState) mob).animaniax$setSyncedVariant(animaniax$lopVariant);
                }
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

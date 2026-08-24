package net.syrupstudios.animaniax.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.entity.GoatBreedSyncedState;
import net.syrupstudios.animaniax.entity.GoatBreedState;
import net.syrupstudios.animaniax.entity.GoatFaintingControl;
import net.syrupstudios.animaniax.entity.GoatFaintingState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
abstract class GoatBreedMixin implements GoatBreedState, GoatFaintingControl {
    @Unique private static final String[] ANIMANIAX$GOAT_BREEDS = {
            "Alpine", "Angora", "Fainting", "Kiko", "Kinder", "Nigerian Dwarf", "Pygmy"
    };
    @Unique private String animaniax$goatBreed = "Alpine";
    @Unique private boolean animaniax$sheared;
    @Unique private int animaniax$woolRegrowthTimer;
    @Unique private int animaniax$faintTicks;
    @Unique private boolean animaniax$hadAi = true;

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$saveGoatBreed(CompoundTag tag, CallbackInfo ci) {
        if (((Mob) (Object) this).getType() == EntityType.GOAT) {
            tag.putString("AnimaniaxGoatBreed", animaniax$goatBreed);
            tag.putBoolean("AnimaniaxGoatSheared", animaniax$sheared);
            tag.putInt("AnimaniaxGoatWoolRegrowthTimer", animaniax$woolRegrowthTimer);
            tag.putInt("AnimaniaxGoatFaintTicks", animaniax$faintTicks);
            tag.putBoolean("AnimaniaxGoatHadAi", animaniax$hadAi);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$loadGoatBreed(CompoundTag tag, CallbackInfo ci) {
        if (((Mob) (Object) this).getType() == EntityType.GOAT) {
            if (tag.contains("AnimaniaxGoatBreed")) animaniax$setGoatBreed(tag.getString("AnimaniaxGoatBreed"));
            animaniax$sheared = tag.getBoolean("AnimaniaxGoatSheared");
            animaniax$syncSheared(animaniax$sheared);
            animaniax$woolRegrowthTimer = tag.contains("AnimaniaxGoatWoolRegrowthTimer")
                    ? Math.max(0, tag.getInt("AnimaniaxGoatWoolRegrowthTimer"))
                    : animaniax$sheared && "Angora".equals(animaniax$goatBreed)
                            ? animaniax$newWoolRegrowthTimer((Mob) (Object) this) : 0;
            animaniax$faintTicks = Math.max(0, Math.min(100, tag.getInt("AnimaniaxGoatFaintTicks")));
            animaniax$hadAi = !tag.contains("AnimaniaxGoatHadAi") || tag.getBoolean("AnimaniaxGoatHadAi");
            Mob mob = (Mob) (Object) this;
            animaniax$setSyncedFainting(mob, "Fainting".equals(animaniax$goatBreed) && animaniax$faintTicks > 0);
            if (animaniax$faintTicks > 0) mob.setNoAi(true);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void animaniax$handleGoatTraits(CallbackInfo ci) {
        Mob mob = (Mob) (Object) this;
        if (mob.getType() != EntityType.GOAT || mob.level().isClientSide) return;
        if (animaniax$sheared && "Angora".equals(animaniax$goatBreed)) {
            if (animaniax$woolRegrowthTimer > 0) animaniax$woolRegrowthTimer--;
            if (animaniax$woolRegrowthTimer == 0) animaniax$setSheared(false);
        }
        if (animaniax$faintTicks > 0 && --animaniax$faintTicks == 0) {
            mob.setNoAi(!animaniax$hadAi);
            animaniax$setSyncedFainting(mob, false);
        }
    }

    @Override @Unique public void animaniax$startFainting() {
        Mob mob = (Mob) (Object) this;
        if (mob.getType() != EntityType.GOAT || mob.level().isClientSide
                || !"Fainting".equals(animaniax$goatBreed) || animaniax$faintTicks > 0) return;
        animaniax$hadAi = !mob.isNoAi();
        animaniax$faintTicks = 100;
        animaniax$setSyncedFainting(mob, true);
        mob.setNoAi(true);
    }

    @Override @Unique public String animaniax$getGoatBreed() {
        Mob mob = (Mob) (Object) this;
        return mob.level().isClientSide && mob instanceof GoatBreedSyncedState state
                ? state.animaniax$getSyncedGoatBreed() : animaniax$goatBreed;
    }
    @Override @Unique public boolean animaniax$isSheared() {
        Mob mob = (Mob) (Object) this;
        return mob.level().isClientSide && mob instanceof GoatBreedSyncedState state
                ? state.animaniax$getSyncedGoatSheared() : animaniax$sheared;
    }
    @Override @Unique public void animaniax$setSheared(boolean sheared) {
        animaniax$sheared = sheared;
        animaniax$syncSheared(sheared);
        if (!sheared) animaniax$woolRegrowthTimer = 0;
        else if ("Angora".equals(animaniax$goatBreed))
            animaniax$woolRegrowthTimer = animaniax$newWoolRegrowthTimer((Mob) (Object) this);
    }

    @Unique private void animaniax$syncBreed(String breed) {
        Mob mob = (Mob) (Object) this;
        if (mob.getType() == EntityType.GOAT && mob instanceof GoatBreedSyncedState state)
            state.animaniax$setSyncedGoatBreed(breed);
    }

    @Unique private void animaniax$syncSheared(boolean sheared) {
        Mob mob = (Mob) (Object) this;
        if (mob.getType() == EntityType.GOAT && mob instanceof GoatBreedSyncedState state)
            state.animaniax$setSyncedGoatSheared(sheared);
    }

    @Unique private static int animaniax$newWoolRegrowthTimer(Mob mob) {
        return (int) Math.min(Integer.MAX_VALUE,
                (long) AnimalCareConfig.woolRegrowthTimer + mob.getRandom().nextInt(500));
    }

    @Unique private static void animaniax$setSyncedFainting(Mob mob, boolean fainting) {
        if (mob instanceof GoatFaintingState state) state.animaniax$setFainting(fainting);
    }

    @Override @Unique public void animaniax$setGoatBreed(String breed) {
        for (String validBreed : ANIMANIAX$GOAT_BREEDS) {
            if (validBreed.equals(breed)) {
                animaniax$goatBreed = validBreed;
                animaniax$syncBreed(validBreed);
                return;
            }
        }
        animaniax$goatBreed = "Alpine";
        animaniax$syncBreed("Alpine");
    }
}

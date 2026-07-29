package net.syrupstudios.animaniax.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.animal.Sheep;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.entity.SheepBreedState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Sheep.class)
abstract class SheepBreedMixin implements SheepBreedState {
    @Unique private static final EntityDataAccessor<String> ANIMANIAX$SHEEP_BREED =
            SynchedEntityData.defineId(Sheep.class, EntityDataSerializers.STRING);
    @Unique private static final EntityDataAccessor<Integer> ANIMANIAX$SHEEP_COLOR_NUMBER =
            SynchedEntityData.defineId(Sheep.class, EntityDataSerializers.INT);
    @Unique private static final String[] ANIMANIAX$BREEDS = {"Dorper", "Dorset", "Friesian", "Merino", "Jacob", "Suffolk"};
    @Unique private String animaniax$sheepBreed = "Friesian";
    @Unique private int animaniax$sheepColorNumber;
    @Unique private int animaniax$woolRegrowthTimer;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void animaniax$defineSheepData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(ANIMANIAX$SHEEP_BREED, "Friesian");
        builder.define(ANIMANIAX$SHEEP_COLOR_NUMBER, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$saveSheepBreed(CompoundTag tag, CallbackInfo ci) {
        tag.putString("AnimaniaxSheepBreed", animaniax$sheepBreed);
        tag.putInt("AnimaniaxSheepColorNumber", animaniax$sheepColorNumber);
        tag.putInt("AnimaniaxWoolRegrowthTimer", animaniax$woolRegrowthTimer);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$loadSheepBreed(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("AnimaniaxSheepBreed")) {
            animaniax$setSheepBreed(tag.getString("AnimaniaxSheepBreed"));
            if (tag.contains("AnimaniaxSheepColorNumber"))
                animaniax$setSheepColorNumber(tag.getInt("AnimaniaxSheepColorNumber"));
        }
        Sheep sheep = (Sheep) (Object) this;
        animaniax$woolRegrowthTimer = tag.contains("AnimaniaxWoolRegrowthTimer")
                ? Math.max(0, tag.getInt("AnimaniaxWoolRegrowthTimer"))
                : sheep.isSheared() ? animaniax$newWoolRegrowthTimer(sheep) : 0;
    }

    @Override @Unique
    public String animaniax$getSheepBreed() {
        Sheep sheep = (Sheep) (Object) this;
        return sheep.level().isClientSide
                ? sheep.getEntityData().get(ANIMANIAX$SHEEP_BREED) : animaniax$sheepBreed;
    }

    @Override @Unique
    public int animaniax$getSheepColorNumber() {
        Sheep sheep = (Sheep) (Object) this;
        return sheep.level().isClientSide
                ? sheep.getEntityData().get(ANIMANIAX$SHEEP_COLOR_NUMBER) : animaniax$sheepColorNumber;
    }

    @Override @Unique
    public void animaniax$setSheepColorNumber(int colorNumber) {
        int maximum = animaniax$maximumColorNumber();
        if (colorNumber >= 0 && colorNumber <= maximum) {
            animaniax$sheepColorNumber = colorNumber;
            ((Sheep) (Object) this).getEntityData().set(ANIMANIAX$SHEEP_COLOR_NUMBER, colorNumber);
        }
    }

    @Override @Unique
    public void animaniax$randomizeSheepColorNumber() {
        Sheep sheep = (Sheep) (Object) this;
        animaniax$sheepColorNumber = sheep.getRandom().nextInt(animaniax$maximumColorNumber() + 1);
        sheep.getEntityData().set(ANIMANIAX$SHEEP_COLOR_NUMBER, animaniax$sheepColorNumber);
    }

    @Override @Unique
    public int animaniax$getWoolRegrowthTimer() { return animaniax$woolRegrowthTimer; }

    @Override @Unique
    public void animaniax$setWoolRegrowthTimer(int ticks) { animaniax$woolRegrowthTimer = Math.max(0, ticks); }

    @Override @Unique
    public void animaniax$startWoolRegrowthTimer() {
        Sheep sheep = (Sheep) (Object) this;
        animaniax$setWoolRegrowthTimer(animaniax$newWoolRegrowthTimer(sheep));
    }

    @Unique
    private static int animaniax$newWoolRegrowthTimer(Sheep sheep) {
        return (int) Math.min(Integer.MAX_VALUE,
                (long) AnimalCareConfig.woolRegrowthTimer + sheep.getRandom().nextInt(500));
    }

    @Unique
    private int animaniax$maximumColorNumber() {
        return switch (animaniax$sheepBreed) {
            case "Friesian" -> 2;
            case "Dorset", "Merino", "Suffolk" -> 1;
            default -> 0;
        };
    }

    @Override @Unique
    public void animaniax$setSheepBreed(String breed) {
        for (String knownBreed : ANIMANIAX$BREEDS) {
            if (knownBreed.equals(breed)) {
                animaniax$sheepBreed = knownBreed;
                ((Sheep) (Object) this).getEntityData().set(ANIMANIAX$SHEEP_BREED, knownBreed);
                animaniax$randomizeSheepColorNumber();
                return;
            }
        }
    }
}

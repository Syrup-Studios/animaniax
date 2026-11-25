package net.syrupstudios.animaniax.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import java.util.UUID;
import net.syrupstudios.animaniax.entity.AnimalCareState;
import net.syrupstudios.animaniax.entity.AnimalProfile;
import net.syrupstudios.animaniax.entity.AnimalProfile.Capability;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.entity.DraftHorseContent;
import net.syrupstudios.animaniax.entity.AnimalReproductionState;
import net.syrupstudios.animaniax.entity.AnimalSterilizationState;
import net.syrupstudios.animaniax.entity.ChickenSexState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Animal.class)
abstract class AnimalReproductionMixin implements AnimalReproductionState, AnimalSterilizationState {
    @Unique private static final TagKey<EntityType<?>> animaniax$birthBreedInheritance = TagKey.create(
            Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("animaniax", "birth_breed_inheritance"));
    @Unique private boolean animaniax$sexSet;
    @Unique private boolean animaniax$female;
    @Unique private boolean animaniax$hasGivenBirth;
    @Unique private boolean animaniax$lactating;
    @Unique private java.util.UUID animaniax$motherUUID;
    @Unique private int animaniax$gestation;
    @Unique private int animaniax$fertilityCooldown;
    @Unique private boolean animaniax$lossChecked;
    @Unique private ListTag animaniax$babies = new ListTag();
    @Unique private String animaniax$mateId = "";
    @Unique private boolean animaniax$sterilized;
    @Unique private java.util.UUID animaniax$courtshipPartner;
    @Unique private java.util.UUID animaniax$matePartner;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void animaniax$initializeMilkability(EntityType<? extends Animal> type, net.minecraft.world.level.Level level, CallbackInfo ci) {
        if (AnimalProfile.has(type, Capability.MILKABLE_AT_SPAWN)) {
            animaniax$lactating = AnimalCareConfig.cowsMilkableAtSpawn;
        }
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void animaniax$tickGestation(CallbackInfo ci) {
        Animal animal = (Animal) (Object) this;
        if (animal.level().isClientSide || !AnimalProfile.has(animal.getType(), Capability.GESTATION)) return;
        if (animaniax$fertilityCooldown > 0) animaniax$fertilityCooldown--;
        if (!animaniax$isPregnant()) return;
        if (--animaniax$gestation < 200 && animal instanceof AnimalSleepState sleepState)
            sleepState.animaniax$setSleeping(false);
        if (animaniax$gestation > 0) return;
        AnimalCareState care = (AnimalCareState) animal;
        if (!animaniax$lossChecked) {
            animaniax$lossChecked = true;
            if (AnimalCareConfig.animalLossChance > 0.0
                && !care.animaniax$isFed() && !care.animaniax$isWatered()
                && animal.getRandom().nextDouble() < AnimalCareConfig.animalLossChance) {
                animaniax$babies = new ListTag();
                animaniax$mateId = "";
                animaniax$gestation = 0;
                animaniax$lactating = false;
                animaniax$startFertilityCooldown(animal);
                return;
            }
        }
        ServerLevel level = (ServerLevel) animal.level();
        CompoundTag babyData = animaniax$babies.getCompound(0);
        if (!babyData.hasUUID("UUID")) babyData.putUUID("UUID", java.util.UUID.randomUUID());
        babyData.putUUID("AnimaniaxMotherUUID", animal.getUUID());
        if (level.getEntity(babyData.getUUID("UUID")) != null) {
            animaniax$hasGivenBirth = true;
            animaniax$lactating = true;
            animaniax$babies.remove(0);
            if (animaniax$babies.isEmpty()) {
                animaniax$mateId = "";
                animaniax$gestation = 0;
                animaniax$startFertilityCooldown(animal);
            } else {
                animaniax$gestation = 1;
            }
            return;
        }
        CompoundTag entityData = babyData.copy();
        entityData.remove("AnimaniaxBreedResolved");
        Entity baby = EntityType.loadEntityRecursive(entityData, level, entity -> entity);
        if (baby == null) {
            animaniax$gestation = 1;
            return;
        }
        if (animaniax$hasPregnancyBreed(animal) && !babyData.getBoolean("AnimaniaxBreedResolved")) {
            Animal sire = animaniax$resolveSire(animal, level);
            AnimalProfile.inheritPregnancyBreed(baby, animal, sire == null ? animal : sire);
            babyData = baby.saveWithoutId(new CompoundTag());
            babyData.putString("id", EntityType.getKey(baby.getType()).toString());
            babyData.putUUID("UUID", baby.getUUID());
            babyData.putUUID("AnimaniaxMotherUUID", animal.getUUID());
            babyData.putBoolean("AnimaniaxBreedResolved", true);
            animaniax$babies.set(0, babyData);
        }
        baby.setPos(animal.getX(), animal.getY(), animal.getZ());
        baby.setDeltaMovement(0, 0, 0);
        if (baby instanceof AgeableMob ageable) ageable.setBaby(true);
        if (!level.addFreshEntity(baby)) {
            animaniax$gestation = 1;
            return;
        }
        animaniax$hasGivenBirth = true;
        animaniax$lactating = true;
        animaniax$babies.remove(0);
        if (animaniax$babies.isEmpty()) {
            animaniax$mateId = "";
            animaniax$gestation = 0;
            animaniax$startFertilityCooldown(animal);
        } else {
            animaniax$gestation = 1;
        }
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void animaniax$clearExpiredCourtship(CallbackInfo ci) {
        Animal animal = (Animal) (Object) this;
        if (!animal.isInLove()) animaniax$courtshipPartner = null;
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void animaniax$expireMatePartner(CallbackInfo ci) {
        Animal animal = (Animal) (Object) this;
        if (animal.level().isClientSide || animaniax$matePartner == null
                || !AnimalProfile.has(animal.getType(), Capability.GESTATION)
                || animal.getRandom().nextInt(200) != 0) return;
        AABB bounds = animal.getBoundingBox().inflate(30.0);
        boolean nearby = animal.level().getEntitiesOfClass(Animal.class, bounds,
                candidate -> candidate.isAlive() && candidate.getUUID().equals(animaniax$matePartner)).size() > 0;
        if (!nearby) animaniax$matePartner = null;
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void animaniax$clearLactationWhenOffspringMatures(CallbackInfo ci) {
        Animal child = (Animal) (Object) this;
        if (child.level().isClientSide || animaniax$motherUUID == null || child.isBaby()) return;

        if (child.level() instanceof ServerLevel level) {
            AABB bounds = new AABB(child.getX() - 15, child.getY() - 15, child.getZ() - 15,
                    child.getX() + 15, child.getY() + 15, child.getZ() + 15);
            for (Animal candidate : level.getEntitiesOfClass(Animal.class, bounds)) {
                if (candidate.getUUID().equals(animaniax$motherUUID)
                        && candidate instanceof AnimalReproductionState mother) {
                    mother.animaniax$setLactating(false);
                    break;
                }
            }
        }
        animaniax$motherUUID = null;
    }

    @Inject(method = "canMate", at = @At("RETURN"), cancellable = true)
    private void animaniax$requireOppositeSexAndCare(Animal other, CallbackInfoReturnable<Boolean> cir) {
        Animal animal = (Animal) (Object) this;
        if (!animaniax$canMateReserved(other)) {
            cir.setReturnValue(false);
            return;
        }
        if (AnimalProfile.has(animal.getType(), Capability.GESTATION)
                && !animaniax$canMateBonded(other)) {
            cir.setReturnValue(false);
            return;
        }
        if (AnimalProfile.has(animal.getType(), Capability.POULTRY_REPRODUCTION)) {
            cir.setReturnValue(false);
            return;
        }
        if (!AnimalProfile.has(animal.getType(), Capability.GESTATION)) {
            if (animal.getType() == DraftHorseContent.DRAFT_HORSE
                    || other.getType() == DraftHorseContent.DRAFT_HORSE) cir.setReturnValue(false);
            return;
        }
        if (!(other instanceof AnimalReproductionState state)
                || !AnimalProfile.has(other.getType(), Capability.GESTATION)
                || animaniax$isFemale() == state.animaniax$isFemale()
                || animaniax$isSterilized() || ((AnimalSterilizationState) other).animaniax$isSterilized()
                || animaniax$isPregnant()
                || animaniax$fertilityCooldown > 0
                || state.animaniax$isPregnant()
                || state.animaniax$fertilityCooldownRemaining() > 0
                || ((AnimalCareState) animal).animaniax$careBlocksBreeding()
                || ((AnimalCareState) other).animaniax$careBlocksBreeding()) {
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = "spawnChildFromBreeding", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
    private void animaniax$holdChildUntilBirth(ServerLevel level, Entity child, ServerLevel world, Animal mate) {
        Animal caller = (Animal) (Object) this;
        if (AnimalProfile.has(caller.getType(), Capability.GESTATION)
                && AnimalProfile.has(mate.getType(), Capability.GESTATION)) {
            Animal male = animaniax$isFemale() ? mate : caller;
            Animal female = animaniax$isFemale() ? caller : mate;
            ((AnimalReproductionState) male).animaniax$setMatePartner(female.getUUID());
            ((AnimalReproductionState) female).animaniax$setMatePartner(male.getUUID());
        }
        animaniax$courtshipPartner = null;
        ((AnimalReproductionState) mate).animaniax$setCourtshipPartner(null);
        ((AnimalCareState) caller).animaniax$clearHandFed();
        ((AnimalCareState) mate).animaniax$clearHandFed();
        AnimalProfile.inheritChildBreed(child, caller, mate);
        if (!AnimalProfile.has(caller.getType(), Capability.GESTATION) || !AnimalProfile.has(mate.getType(), Capability.GESTATION)
                || !(mate instanceof AnimalReproductionState mateState)
                || animaniax$isPregnant() || mateState.animaniax$isPregnant()) {
            level.addFreshEntityWithPassengers(child);
            return;
        }
        AnimalProfile.inheritPregnancyBreed(child, caller, mate);
        Animal mother = animaniax$isFemale() ? caller : mate;
        AnimalReproductionState motherState = (AnimalReproductionState) mother;
        if (!motherState.animaniax$isFemale()) {
            level.addFreshEntityWithPassengers(child);
            return;
        }
        ListTag babies = new ListTag();
        double chance = 1.0D;
        do {
            CompoundTag baby = child.saveWithoutId(new CompoundTag());
            baby.putString("id", EntityType.getKey(child.getType()).toString());
            baby.putUUID("UUID", child.getUUID());
            baby.putBoolean("AnimaniaxHasGivenBirth", false);
            babies.add(baby);
            chance *= AnimalCareConfig.birthMultipleChance;
            if (babies.size() >= 64 || chance <= 0.0D || caller.getRandom().nextDouble() > chance) break;
            child.setUUID(java.util.UUID.randomUUID());
            animaniax$inheritBreed(child, caller, mate);
        } while (true);
        motherState.animaniax$beginPregnancy(babies,
                (mother == caller ? mate : caller).getUUID().toString());
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$saveReproduction(CompoundTag tag, CallbackInfo ci) {
        Animal animal = (Animal) (Object) this;
        if (animaniax$courtshipPartner != null) tag.putUUID("AnimaniaxCourtshipPartner", animaniax$courtshipPartner);
        if (AnimalProfile.has(animal.getType(), Capability.POULTRY_REPRODUCTION)) {
            tag.putBoolean("AnimaniaxChickenSexSet", true);
            tag.putBoolean("AnimaniaxHen", ((ChickenSexState) animal).animaniax$isHen());
            tag.putBoolean("AnimaniaxSterilized", animaniax$sterilized);
            return;
        }
        if (!AnimalProfile.has(animal.getType(), Capability.GESTATION)) return;
        if (animaniax$matePartner != null) tag.putUUID("AnimaniaxMatePartner", animaniax$matePartner);
        tag.putBoolean("AnimaniaxSexSet", true);
        tag.putBoolean("AnimaniaxFemale", animaniax$isFemale());
        tag.putBoolean("AnimaniaxSterilized", animaniax$sterilized);
        tag.putBoolean("AnimaniaxHasGivenBirth", animaniax$hasGivenBirth);
        tag.putBoolean("AnimaniaxLactating", animaniax$lactating);
        if (animaniax$motherUUID != null) tag.putUUID("AnimaniaxMotherUUID", animaniax$motherUUID);
        if (animaniax$isPregnant()) {
            tag.putInt("AnimaniaxGestation", animaniax$gestation);
            tag.put("AnimaniaxBabies", animaniax$babies.copy());
            tag.putString("AnimaniaxMate", animaniax$mateId);
            tag.putBoolean("AnimaniaxLossChecked", animaniax$lossChecked);
        }
        tag.putInt("AnimaniaxFertilityCooldown", animaniax$fertilityCooldown);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$loadReproduction(CompoundTag tag, CallbackInfo ci) {
        Animal animal = (Animal) (Object) this;
        animaniax$courtshipPartner = tag.hasUUID("AnimaniaxCourtshipPartner")
                ? tag.getUUID("AnimaniaxCourtshipPartner") : null;
        if (!animal.isInLove()) animaniax$courtshipPartner = null;
        if (AnimalProfile.has(animal.getType(), Capability.POULTRY_REPRODUCTION)) {
            animaniax$sterilized = tag.getBoolean("AnimaniaxSterilized");
            if (tag.getBoolean("AnimaniaxChickenSexSet")) {
                ((ChickenSexState) animal).animaniax$setHen(tag.getBoolean("AnimaniaxHen"));
            }
            return;
        }
        if (!AnimalProfile.has(animal.getType(), Capability.GESTATION)) return;
        animaniax$matePartner = tag.hasUUID("AnimaniaxMatePartner")
                ? tag.getUUID("AnimaniaxMatePartner") : null;
        animaniax$sexSet = tag.getBoolean("AnimaniaxSexSet");
        animaniax$sterilized = tag.getBoolean("AnimaniaxSterilized");
        animaniax$female = tag.getBoolean("AnimaniaxFemale");
        if (!animal.level().isClientSide && animaniax$sexSet) {
            ((net.syrupstudios.animaniax.entity.AnimalVisualSyncState) animal).animaniax$setSyncedFemale(animaniax$female);
        }
        animaniax$hasGivenBirth = tag.contains("AnimaniaxHasGivenBirth")
                ? tag.getBoolean("AnimaniaxHasGivenBirth") : true;
        animaniax$lactating = tag.contains("AnimaniaxLactating")
                ? tag.getBoolean("AnimaniaxLactating") : animaniax$hasGivenBirth;
        animaniax$motherUUID = tag.hasUUID("AnimaniaxMotherUUID")
                ? tag.getUUID("AnimaniaxMotherUUID") : null;
        animaniax$gestation = Math.max(0, tag.getInt("AnimaniaxGestation"));
        animaniax$fertilityCooldown = Math.max(0, tag.getInt("AnimaniaxFertilityCooldown"));
        animaniax$babies = tag.contains("AnimaniaxBabies", 9) ? tag.getList("AnimaniaxBabies", 10).copy() : new ListTag();
        if (animaniax$babies.isEmpty() && tag.contains("AnimaniaxBaby", 10)) {
            animaniax$babies.add(tag.getCompound("AnimaniaxBaby").copy());
        }
        animaniax$mateId = tag.getString("AnimaniaxMate");
        animaniax$lossChecked = !animaniax$babies.isEmpty()
                && (!tag.contains("AnimaniaxLossChecked") || tag.getBoolean("AnimaniaxLossChecked"));
    }

    @Override public boolean animaniax$isFemale() {
        Animal animal = (Animal) (Object) this;
        if (animal.level().isClientSide) {
            return ((net.syrupstudios.animaniax.entity.AnimalVisualSyncState) animal).animaniax$isSyncedFemale();
        }
        return animaniax$sexSet ? animaniax$female
                : (animal.getUUID().getLeastSignificantBits() & 1L) != 0;
    }
    @Override public void animaniax$setFemale(boolean female) {
        animaniax$sexSet = true;
        animaniax$female = female;
        Animal animal = (Animal) (Object) this;
        if (!animal.level().isClientSide) {
            ((net.syrupstudios.animaniax.entity.AnimalVisualSyncState) animal).animaniax$setSyncedFemale(female);
        }
    }
    @Override public boolean animaniax$isPregnant() { return !animaniax$babies.isEmpty() && animaniax$gestation > 0; }
    @Override public int animaniax$fertilityCooldownRemaining() { return animaniax$fertilityCooldown; }
    @Override public boolean animaniax$hasGivenBirth() { return animaniax$hasGivenBirth; }
    @Override public void animaniax$setHasGivenBirth(boolean hasGivenBirth) { animaniax$hasGivenBirth = hasGivenBirth; }
    @Override public void animaniax$setMotherUUID(java.util.UUID motherUUID) { animaniax$motherUUID = motherUUID; }
    @Override public boolean animaniax$isLactating() { return animaniax$lactating; }
    @Override public void animaniax$setLactating(boolean lactating) { animaniax$lactating = lactating; }
    @Override public int animaniax$gestationRemaining() { return animaniax$gestation; }
    @Override public void animaniax$beginPregnancy(CompoundTag baby, String mateId) {
        ListTag babies = new ListTag();
        babies.add(baby.copy());
        animaniax$beginPregnancy(babies, mateId);
    }
    @Override public void animaniax$beginPregnancy(ListTag babies, String mateId) {
        animaniax$babies = babies.copy();
        animaniax$mateId = mateId;
        animaniax$lossChecked = false;
        animaniax$gestation = AnimalCareConfig.gestationTimer + ((Animal) (Object) this).getRandom().nextInt(200);
    }
    @Override public boolean animaniax$isSterilized() { return animaniax$sterilized; }
    @Override public void animaniax$sterilize() { animaniax$sterilized = true; }
    @Override public java.util.UUID animaniax$courtshipPartner() { return animaniax$courtshipPartner; }
    @Override public void animaniax$setCourtshipPartner(java.util.UUID partner) { animaniax$courtshipPartner = partner; }
    @Override public java.util.UUID animaniax$matePartner() { return animaniax$matePartner; }
    @Override public void animaniax$setMatePartner(java.util.UUID partner) { animaniax$matePartner = partner; }
    @Override public boolean animaniax$canMateBonded(Animal other) {
        Animal animal = (Animal) (Object) this;
        if (!AnimalProfile.has(animal.getType(), Capability.GESTATION)) return true;
        if (!AnimalProfile.has(other.getType(), Capability.GESTATION)
                || !(other instanceof AnimalReproductionState state)
                || animaniax$isFemale() == state.animaniax$isFemale()) return false;
        UUID selfId = animal.getUUID();
        UUID otherId = other.getUUID();
        if (animaniax$isFemale()) {
            return (animaniax$matePartner == null || animaniax$matePartner.equals(otherId))
                    && (state.animaniax$matePartner() == null || state.animaniax$matePartner().equals(selfId)
                    || AnimalCareConfig.malesMateMultipleFemales);
        }
        return (AnimalCareConfig.malesMateMultipleFemales || animaniax$matePartner == null
                || animaniax$matePartner.equals(otherId))
                && (state.animaniax$matePartner() == null || state.animaniax$matePartner().equals(selfId));
    }
    @Override public boolean animaniax$canMateReserved(Animal other) {
        java.util.UUID otherPartner = ((AnimalReproductionState) other).animaniax$courtshipPartner();
        boolean unreserved = animaniax$courtshipPartner == null && otherPartner == null;
        boolean reservedPair = other.getUUID().equals(animaniax$courtshipPartner)
                && ((Animal) (Object) this).getUUID().equals(otherPartner);
        return (unreserved || reservedPair)
                && !animaniax$isSleepingForBreeding((Animal) (Object) this)
                && !animaniax$isSleepingForBreeding(other);
    }

    @Unique private static boolean animaniax$isSleepingForBreeding(Animal animal) {
        return animal instanceof AnimalSleepState sleepState && sleepState.animaniax$isSleeping()
                || animal.isSleeping();
    }

    @Unique private static boolean animaniax$hasPregnancyBreed(Animal animal) {
        return animal.getType().is(animaniax$birthBreedInheritance);
    }

    @Unique private Animal animaniax$resolveSire(Animal mother, ServerLevel level) {
        UUID mateId;
        try {
            mateId = UUID.fromString(animaniax$mateId);
        } catch (IllegalArgumentException exception) {
            return null;
        }
        AABB bounds = mother.getBoundingBox().inflate(30.0);
        for (Animal candidate : level.getEntitiesOfClass(Animal.class, bounds,
                animal -> animal.isAlive() && animal.getType() == mother.getType()
                        && animal.getUUID().equals(mateId))) {
            return candidate;
        }
        return null;
    }

    @Unique private void animaniax$startFertilityCooldown(Animal animal) {
        animaniax$fertilityCooldown = Math.max(1, AnimalCareConfig.gestationTimer / 9)
                + animal.getRandom().nextInt(50);
    }

    @Unique private static void animaniax$inheritBreed(Entity child, Animal first, Animal second) {
        AnimalProfile.inheritExtraLitterBreed(child, first, second);
    }
}

package net.syrupstudios.animaniax.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.EntityType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.syrupstudios.animaniax.entity.AnimalCareState;
import net.syrupstudios.animaniax.entity.ChickenSexState;
import net.syrupstudios.animaniax.entity.ChickenBreedState;
import net.syrupstudios.animaniax.entity.ChickenFlockSpawner;
import net.syrupstudios.animaniax.entity.ChickenFlockState;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.entity.PeafowlMorphState;
import net.syrupstudios.animaniax.item.PeafowlContent;
import net.syrupstudios.animaniax.item.BrownEggContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.objectweb.asm.Opcodes;

@Mixin(Chicken.class)
abstract class ChickenProductionMixin implements ChickenSexState, ChickenBreedState, ChickenFlockState {
    @Unique private static final String[] ANIMANIAX$CHICKEN_BREEDS = {"Leghorn", "Orpington", "Plymouth Rock", "Rhode Island Red", "Wyandotte"};
    @Shadow public int eggTime;
    @Unique private int animaniax$pausedEggTime = -1;
    @Unique private boolean animaniax$sexSet;
    @Unique private boolean animaniax$hen;
    @Unique private int animaniax$nestLayCooldown = -1;
    @Unique private int animaniax$crowTicks = -1;
    @Unique private int animaniax$featherTicks = -1;
    @Unique private String animaniax$chickenBreed = "Leghorn";
    @Unique private boolean animaniax$pendingFlockSpawn;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void animaniax$setInitialEggTimer(EntityType<? extends Chicken> type, Level level, CallbackInfo ci) {
        if (type == EntityType.CHICKEN) eggTime = 6000;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$saveChickenBreed(CompoundTag tag, CallbackInfo ci) {
        tag.putString("AnimaniaxChickenBreed", animaniax$chickenBreed);
        tag.putInt("AnimaniaxNestLayCooldown", animaniax$nestLayCooldown);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$loadChickenBreed(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("AnimaniaxChickenBreed")) animaniax$setChickenBreed(tag.getString("AnimaniaxChickenBreed"));
        animaniax$pendingFlockSpawn = tag.getBoolean("AnimaniaxPendingFlockSpawn");
        animaniax$nestLayCooldown = tag.contains("AnimaniaxNestLayCooldown")
                ? Math.max(-1, tag.getInt("AnimaniaxNestLayCooldown")) : -1;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$savePendingFlockSpawn(CompoundTag tag, CallbackInfo ci) {
        if (animaniax$pendingFlockSpawn) tag.putBoolean("AnimaniaxPendingFlockSpawn", true);
    }

    @Override public String animaniax$getChickenBreed() { return animaniax$chickenBreed; }
    @Override public void animaniax$setChickenBreed(String breed) {
        for (String valid : ANIMANIAX$CHICKEN_BREEDS) if (valid.equals(breed)) { animaniax$chickenBreed = valid; return; }
    }

    @Override public boolean animaniax$isHen() {
        Chicken chicken = (Chicken) (Object) this;
        if (chicken.level().isClientSide) return false;
        if (!animaniax$sexSet) {
            animaniax$hen = (chicken.getUUID().getLeastSignificantBits() & 1L) != 0;
            animaniax$sexSet = true;
        }
        return animaniax$hen;
    }

    @Override public void animaniax$setHen(boolean hen) {
        animaniax$hen = hen;
        animaniax$sexSet = true;
    }

    @Override public void animaniax$setPendingFlockSpawn(boolean pending) { animaniax$pendingFlockSpawn = pending; }

    @Override public boolean animaniax$isNestLayReady() {
        return animaniax$nestLayCooldown == 0;
    }
    @Override public void animaniax$resetNestLayCooldown() {
        Chicken chicken = (Chicken) (Object) this;
        animaniax$nestLayCooldown = AnimalCareConfig.laidTimer + chicken.getRandom().nextInt(100);
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void animaniax$chickenAmbientBehavior(CallbackInfo ci) {
        Chicken chicken = (Chicken) (Object) this;
        if (animaniax$pendingFlockSpawn && chicken.level() instanceof ServerLevel server) {
            animaniax$pendingFlockSpawn = false;
            ChickenFlockSpawner.spawn(server, server.getCurrentDifficultyAt(chicken.blockPosition()), chicken);
        }
        if (chicken.level().isClientSide) return;
        if (chicken.isBaby() || chicken.isChickenJockey()) return;
        if (animaniax$nestLayCooldown < 0) animaniax$nestLayCooldown = AnimalCareConfig.laidTimer / 2 + chicken.getRandom().nextInt(100);
        if (animaniax$nestLayCooldown > 0) animaniax$nestLayCooldown--;

        if (chicken instanceof PeafowlMorphState peafowl) {
            if (AnimalCareConfig.birdsDropFeathers && !animaniax$isHen()) {
                if (animaniax$featherTicks < 0) animaniax$featherTicks = AnimalCareConfig.featherTimer;
                if (--animaniax$featherTicks <= 0) {
                    chicken.spawnAtLocation(PeafowlContent.feather(peafowl.animaniax$getMorph()));
                    animaniax$featherTicks = AnimalCareConfig.featherTimer;
                }
            }
            return;
        }
        long dayTime = Math.floorMod(chicken.level().getDayTime(), 24_000L);
        if (!animaniax$isHen()) {
            if (animaniax$crowTicks < 0) animaniax$crowTicks = 200 + chicken.getRandom().nextInt(200);
            else if (animaniax$crowTicks > 0) animaniax$crowTicks--;
            if (animaniax$crowTicks == 0 && (dayTime > 23_250 || dayTime < 500)) {
                chicken.playSound(SoundEvents.CHICKEN_AMBIENT, 1.0F, 0.75F);
                animaniax$crowTicks = 200 + chicken.getRandom().nextInt(200);
            }
        }

        if (AnimalCareConfig.birdsDropFeathers) {
            if (animaniax$featherTicks < 0) animaniax$featherTicks = AnimalCareConfig.featherTimer + chicken.getRandom().nextInt(1_000);
            if (--animaniax$featherTicks <= 0) {
                chicken.playSound(SoundEvents.CHICKEN_AMBIENT, 1.0F, 1.0F);
                chicken.spawnAtLocation(Items.FEATHER);
                animaniax$featherTicks = AnimalCareConfig.featherTimer + chicken.getRandom().nextInt(1_000);
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$saveChickenAmbient(CompoundTag tag, CallbackInfo ci) {
        tag.putInt("AnimaniaxCrowTicks", animaniax$crowTicks);
        tag.putInt("AnimaniaxFeatherTicks", animaniax$featherTicks);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$loadChickenAmbient(CompoundTag tag, CallbackInfo ci) {
        int crowTicks = tag.getInt("AnimaniaxCrowTicks");
        animaniax$crowTicks = tag.contains("AnimaniaxCrowTicks") ? Math.max(0, crowTicks) : -1;
        int ticks = tag.getInt("AnimaniaxFeatherTicks");
        animaniax$featherTicks = ticks > 0 ? ticks : -1;
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/Chicken;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private ItemEntity animaniax$layEgg(Chicken chicken, ItemLike item) {
        if (item != Items.EGG) return chicken.spawnAtLocation(item);
        if (!animaniax$isHen()) return null;
        if (chicken instanceof PeafowlMorphState) return null;
        if (!AnimalCareConfig.chickensDropEggs) return null;
        return chicken.spawnAtLocation(BrownEggContent.isBrownBreed(((ChickenBreedState) chicken).animaniax$getChickenBreed())
                ? BrownEggContent.BROWN_EGG : item);
    }

    @Redirect(method = "aiStep", at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/entity/animal/Chicken;eggTime:I", opcode = Opcodes.GETFIELD))
    private int animaniax$pauseEggTimerWhenUnhappy(Chicken chicken) {
        if (!((ChickenSexState) chicken).animaniax$isHen()) {
            return chicken.eggTime > 0 ? chicken.eggTime + 1 : 2;
        }
        if (((AnimalCareState) chicken).animaniax$isContent()) {
            if (animaniax$pausedEggTime >= 0) {
                chicken.eggTime = animaniax$pausedEggTime;
                animaniax$pausedEggTime = -1;
            }
            return chicken.eggTime;
        }
        if (animaniax$pausedEggTime < 0) animaniax$pausedEggTime = chicken.eggTime;
        return chicken.eggTime > 0 ? chicken.eggTime + 1 : 2;
    }
}

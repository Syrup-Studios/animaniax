package net.syrupstudios.animaniax.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.ServerLevelAccessor;
import net.syrupstudios.animaniax.entity.ChickenBreedState;
import net.syrupstudios.animaniax.entity.ChickenFlockSpawner;
import net.syrupstudios.animaniax.entity.ChickenFlockState;
import net.syrupstudios.animaniax.entity.ChickenSexState;
import net.syrupstudios.animaniax.entity.NaturalBreedGroups;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
abstract class ChickenNaturalBreedMixin {
    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void animaniax$chooseNaturalChickenBreed(ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType reason, @Nullable SpawnGroupData group, CallbackInfoReturnable<SpawnGroupData> cir) {
        if (reason != MobSpawnType.NATURAL && reason != MobSpawnType.CHUNK_GENERATION) return;
        Mob mob = (Mob) (Object) this;
        if (mob.getType() != EntityType.CHICKEN || !(mob instanceof ChickenBreedState chicken)) return;
        SpawnGroupData spawnData = cir.getReturnValue();
        String biome = level.getBiome(mob.blockPosition()).unwrapKey().map(ResourceKey::location)
                .map(key -> key.getPath()).orElse("");
        String breed = NaturalBreedGroups.getBreed(spawnData, "chicken");
        if (breed == null) breed = biome.contains("jungle") || biome.contains("swamp") ? "Orpington"
                : biome.contains("mountain") || biome.contains("hills") || biome.contains("peaks")
                        || biome.contains("windswept_forest") ? "Plymouth Rock"
                : biome.contains("forest") ? (mob.getRandom().nextBoolean() ? "Rhode Island Red" : "Wyandotte")
                : biome.contains("plains") ? "Leghorn"
                : new String[] {"Leghorn", "Orpington", "Plymouth Rock", "Rhode Island Red", "Wyandotte"}
                        [mob.getRandom().nextInt(5)];
        chicken.animaniax$setChickenBreed(breed);
        NaturalBreedGroups.rememberBreed(spawnData, "chicken", breed);

        if (!(mob instanceof Chicken spawned) || spawned.isBaby()) return;
        ((ChickenSexState) spawned).animaniax$setHen(true);
        spawned.eggTime = 6000;
        if (reason == MobSpawnType.CHUNK_GENERATION) {
            ((ChickenFlockState) spawned).animaniax$setPendingFlockSpawn(true);
        } else {
            ChickenFlockSpawner.spawn(level, difficulty, spawned);
        }
    }
}

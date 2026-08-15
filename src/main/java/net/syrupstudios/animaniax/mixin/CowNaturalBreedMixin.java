package net.syrupstudios.animaniax.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.syrupstudios.animaniax.entity.CowBreedState;
import net.syrupstudios.animaniax.entity.LivestockFamilySpawner;
import net.syrupstudios.animaniax.entity.NaturalBreedGroups;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
abstract class CowNaturalBreedMixin {
    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void animaniax$chooseNaturalBreed(ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType reason, @Nullable SpawnGroupData group,
            CallbackInfoReturnable<SpawnGroupData> cir) {
        if (reason != MobSpawnType.NATURAL && reason != MobSpawnType.CHUNK_GENERATION) return;
        Mob mob = (Mob) (Object) this;
        if (mob.getType() == EntityType.MOOSHROOM) {
            if (mob instanceof net.minecraft.world.entity.animal.Animal animal)
                LivestockFamilySpawner.spawn(level, difficulty, animal);
            return;
        }
        if (mob.getType() != EntityType.COW || !(mob instanceof CowBreedState cow)) return;
        SpawnGroupData spawnData = cir.getReturnValue();
        String biome = level.getBiome(mob.blockPosition()).unwrapKey().map(ResourceKey::location)
                .map(key -> key.getPath()).orElse("");
        String breed = NaturalBreedGroups.getBreed(spawnData, "cow");
        if (breed == null) breed = biome.contains("jungle") || biome.contains("badlands") ? "Angus"
                : biome.contains("swamp") ? (mob.getRandom().nextBoolean() ? "Angus" : "Jersey")
                : biome.contains("savanna") ? "Texas Longhorn"
                : biome.contains("forest_hills") || biome.contains("wooded_hills")
                        || biome.contains("windswept_forest") || biome.contains("mountain")
                        || biome.contains("hills") || biome.contains("peaks")
                        || biome.contains("windswept_hills") || biome.contains("grove")
                        ? (mob.getRandom().nextBoolean() ? "Highland" : "Hereford")
                : biome.contains("forest") || biome.contains("taiga") ? "Holstein"
                : biome.contains("plains") || biome.contains("meadow") ? "Friesian"
                : new String[]{"Angus", "Hereford", "Texas Longhorn", "Holstein", "Friesian", "Jersey"}
                        [mob.getRandom().nextInt(6)];
        cow.animaniax$setBreed(breed);
        NaturalBreedGroups.rememberBreed(spawnData, "cow", breed);
        if (mob instanceof net.minecraft.world.entity.animal.Animal animal)
            LivestockFamilySpawner.spawn(level, difficulty, animal);
    }
}

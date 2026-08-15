package net.syrupstudios.animaniax.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.syrupstudios.animaniax.entity.GoatBreedState;
import net.syrupstudios.animaniax.entity.LivestockFamilySpawner;
import net.syrupstudios.animaniax.entity.NaturalBreedGroups;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
abstract class GoatNaturalBreedMixin {
    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void animaniax$chooseNaturalGoatBreed(ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType reason, @Nullable SpawnGroupData group,
            CallbackInfoReturnable<SpawnGroupData> cir) {
        if (reason != MobSpawnType.NATURAL && reason != MobSpawnType.CHUNK_GENERATION) return;
        Mob mob = (Mob) (Object) this;
        if (mob.getType() != EntityType.GOAT || !(mob instanceof GoatBreedState goat)) return;
        SpawnGroupData spawnData = cir.getReturnValue();
        String biome = level.getBiome(mob.blockPosition()).unwrapKey().map(ResourceKey::location)
                .map(key -> key.getPath()).orElse("");
        String[] breeds = biome.contains("mountain") || biome.contains("hill")
                ? new String[]{"Alpine", "Kiko"}
                : biome.contains("plains") ? new String[]{"Angora", "Fainting"}
                : biome.contains("savanna") || biome.contains("badlands") ? new String[]{"Kinder", "Pygmy"}
                : biome.contains("desert") ? new String[]{"Nigerian Dwarf"} : null;
        String breed = NaturalBreedGroups.getBreed(spawnData, "goat");
        if (breed == null) breed = breeds == null ? goat.animaniax$getGoatBreed()
                : breeds[mob.getRandom().nextInt(breeds.length)];
        goat.animaniax$setGoatBreed(breed);
        NaturalBreedGroups.rememberBreed(spawnData, "goat", breed);
        LivestockFamilySpawner.spawn(level, difficulty, (net.minecraft.world.entity.animal.Animal) mob);
    }
}

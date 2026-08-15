package net.syrupstudios.animaniax.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.syrupstudios.animaniax.entity.SheepBreedState;
import net.syrupstudios.animaniax.entity.LivestockFamilySpawner;
import net.syrupstudios.animaniax.entity.NaturalBreedGroups;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
abstract class SheepNaturalBreedMixin {
    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void animaniax$chooseNaturalSheepBreed(ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType reason, @Nullable SpawnGroupData group,
            CallbackInfoReturnable<SpawnGroupData> cir) {
        Mob mob = (Mob) (Object) this;
        if (mob.getType() != EntityType.SHEEP || !(mob instanceof SheepBreedState sheep)) return;
        if (reason != MobSpawnType.NATURAL && reason != MobSpawnType.CHUNK_GENERATION) {
            sheep.animaniax$randomizeSheepColorNumber();
            return;
        }
        SpawnGroupData spawnData = cir.getReturnValue();
        String biome = level.getBiome(mob.blockPosition()).unwrapKey().map(ResourceKey::location)
                .map(key -> key.getPath()).orElse("");
        String breed = NaturalBreedGroups.getBreed(spawnData, "sheep");
        String[] breeds = biome.contains("badlands") || biome.contains("mesa") ? new String[]{"Suffolk", "Merino"}
                : biome.contains("savanna") ? new String[]{"Dorper", "Suffolk"}
                : biome.contains("plains") ? new String[]{"Dorper", "Dorset", "Friesian", "Merino"}
                : biome.contains("desert") ? new String[]{"Merino"}
                : biome.contains("hill") || biome.contains("peak") || biome.contains("mountain")
                        ? new String[]{"Dorset"}
                : biome.contains("forest") ? new String[]{"Jacob"}
                : new String[]{"Dorper", "Dorset", "Friesian", "Merino", "Jacob", "Suffolk"};
        if (breed == null) breed = breeds[mob.getRandom().nextInt(breeds.length)];
        sheep.animaniax$setSheepBreed(breed);
        NaturalBreedGroups.rememberBreed(spawnData, "sheep", breed);
        if (mob instanceof net.minecraft.world.entity.animal.Animal animal)
            LivestockFamilySpawner.spawn(level, difficulty, animal);
    }
}

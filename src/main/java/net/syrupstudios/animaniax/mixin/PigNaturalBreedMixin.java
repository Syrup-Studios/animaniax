package net.syrupstudios.animaniax.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.syrupstudios.animaniax.entity.PigBreedState;
import net.syrupstudios.animaniax.entity.LivestockFamilySpawner;
import net.syrupstudios.animaniax.entity.NaturalBreedGroups;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
abstract class PigNaturalBreedMixin {
    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void animaniax$chooseNaturalPigBreed(ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType reason, @Nullable SpawnGroupData group,
            CallbackInfoReturnable<SpawnGroupData> cir) {
        if (reason != MobSpawnType.NATURAL && reason != MobSpawnType.CHUNK_GENERATION) return;
        Mob mob = (Mob) (Object) this;
        if (mob.getType() != EntityType.PIG || !(mob instanceof PigBreedState pig)) return;
        SpawnGroupData spawnData = cir.getReturnValue();
        String biome = level.getBiome(mob.blockPosition()).unwrapKey().map(ResourceKey::location)
                .map(key -> key.getPath()).orElse("");
        String breed = NaturalBreedGroups.getBreed(spawnData, "pig");
        if (breed == null) breed = biome.contains("jungle") ? "Duroc"
                : biome.contains("mountain") || biome.contains("hills") || biome.contains("peaks")
                        || biome.contains("windswept_forest") ? "Hampshire"
                : biome.contains("swamp") || biome.contains("dark_forest") || biome.contains("old_growth") ? "Large Black"
                : biome.contains("forest") || biome.contains("taiga") ? (mob.getRandom().nextBoolean() ? "Large White" : "Old Spot")
                : biome.contains("plains") ? "Yorkshire"
                : PigBreedState.BREEDS.get(mob.getRandom().nextInt(PigBreedState.BREEDS.size()));
        pig.animaniax$setPigBreed(breed);
        NaturalBreedGroups.rememberBreed(spawnData, "pig", breed);
        if (mob instanceof net.minecraft.world.entity.animal.Animal animal)
            LivestockFamilySpawner.spawn(level, difficulty, animal);
    }
}

package net.syrupstudios.animaniax.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.ServerLevelAccessor;
import net.syrupstudios.animaniax.entity.LivestockFamilySpawner;
import net.syrupstudios.animaniax.entity.NaturalBreedGroups;
import net.syrupstudios.animaniax.entity.RabbitBreedState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
abstract class RabbitNaturalBreedMixin {
    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void animaniax$chooseNaturalRabbitBreed(ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType reason, @Nullable SpawnGroupData group, CallbackInfoReturnable<SpawnGroupData> cir) {
        if (reason != MobSpawnType.NATURAL && reason != MobSpawnType.CHUNK_GENERATION) return;
        Mob mob = (Mob) (Object) this;
        if (mob.getType() != EntityType.RABBIT || !(mob instanceof RabbitBreedState rabbit)) return;
        SpawnGroupData spawnData = cir.getReturnValue();
        String biome = level.getBiome(mob.blockPosition()).unwrapKey().map(ResourceKey::location)
                .map(key -> key.getPath()).orElse("");
        String[] breeds = biome.contains("mountain") || biome.contains("hill") || biome.contains("peak")
                ? new String[]{"Havana"}
                : biome.contains("desert") || biome.contains("badlands") || biome.contains("beach")
                        ? new String[]{"Jack"}
                : biome.contains("savanna") ? new String[]{"Chinchilla", "Jack", "Rex"}
                : biome.contains("forest") ? new String[]{"Cottontail", "New Zealand", "Lop"}
                : biome.contains("plains") ? new String[]{"Dutch", "Lop"}
                : new String[]{"Cottontail"};
        String breed = NaturalBreedGroups.getBreed(spawnData, "rabbit");
        if (breed == null) breed = breeds[mob.getRandom().nextInt(breeds.length)];
        rabbit.animaniax$setRabbitBreed(breed);
        NaturalBreedGroups.rememberBreed(spawnData, "rabbit", breed);
        LivestockFamilySpawner.spawn(level, difficulty, (Animal) mob);
    }
}

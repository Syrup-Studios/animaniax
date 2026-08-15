package net.syrupstudios.animaniax.entity;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.SpawnGroupData;

public final class NaturalBreedGroups {
    private static final Map<SpawnGroupData, Breed> BREEDS = Collections.synchronizedMap(new WeakHashMap<>());

    private NaturalBreedGroups() {}

    public static String getBreed(SpawnGroupData groupData, String family) {
        if (groupData == null || groupData.getClass() != AgeableMob.AgeableMobGroupData.class) return null;
        Breed breed = BREEDS.get(groupData);
        return breed != null && breed.family.equals(family) ? breed.name : null;
    }

    public static void rememberBreed(SpawnGroupData groupData, String family, String breed) {
        if (groupData == null || groupData.getClass() != AgeableMob.AgeableMobGroupData.class) return;
        BREEDS.putIfAbsent(groupData, new Breed(family, breed));
    }

    private record Breed(String family, String name) {}
}

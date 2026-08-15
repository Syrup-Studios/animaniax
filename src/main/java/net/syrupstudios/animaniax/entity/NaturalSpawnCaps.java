package net.syrupstudios.animaniax.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.syrupstudios.animaniax.config.AnimalCareConfig;

public final class NaturalSpawnCaps {
    private enum Group {
        COWS("cows"), PIGS("pigs"), CHICKENS("chickens"), SHEEP("sheep"),
        GOATS("goats"), HORSES("horses"), RABBITS("rabbits"),
        AMPHIBIANS("amphibians"), HAMSTERS("hamsters"), FERRETS("ferrets"),
        HEDGEHOGS("hedgehogs"), CATS("cats"), DOGS("dogs");

        private final TagKey<EntityType<?>> types;

        Group(String name) {
            this.types = TagKey.create(Registries.ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath("animaniax", "natural_spawn/" + name));
        }

        private int limit() {
            return switch (this) {
                case COWS -> AnimalCareConfig.naturalSpawnLimitCows;
                case PIGS -> AnimalCareConfig.naturalSpawnLimitPigs;
                case CHICKENS -> AnimalCareConfig.naturalSpawnLimitChickens;
                case SHEEP -> AnimalCareConfig.naturalSpawnLimitSheep;
                case GOATS -> AnimalCareConfig.naturalSpawnLimitGoats;
                case HORSES -> AnimalCareConfig.naturalSpawnLimitHorses;
                case RABBITS -> AnimalCareConfig.naturalSpawnLimitRabbits;
                case AMPHIBIANS -> AnimalCareConfig.naturalSpawnLimitAmphibians;
                case HAMSTERS -> AnimalCareConfig.naturalSpawnLimitHamsters;
                case FERRETS -> AnimalCareConfig.naturalSpawnLimitFerrets;
                case HEDGEHOGS -> AnimalCareConfig.naturalSpawnLimitHedgehogs;
                case CATS -> AnimalCareConfig.naturalSpawnLimitCats;
                case DOGS -> AnimalCareConfig.naturalSpawnLimitDogs;
            };
        }
    }

    private NaturalSpawnCaps() {}

    public static boolean isOverLimit(EntityType<?> type, ServerLevelAccessor level, BlockPos pos) {
        Group group = groupFor(type);
        if (group == null) return false;

        BlockPos center = pos.above();
        AABB bounds = new AABB(center.getX() - 100, center.getY() - 100, center.getZ() - 100,
                center.getX() + 100, center.getY() + 100, center.getZ() + 100);
        return level.getLevel().getEntitiesOfClass(Mob.class, bounds,
                mob -> mob.getType().is(group.types)).size() > group.limit();
    }

    private static Group groupFor(EntityType<?> type) {
        for (Group group : Group.values()) {
            if (type.is(group.types)) return group;
        }
        return null;
    }
}

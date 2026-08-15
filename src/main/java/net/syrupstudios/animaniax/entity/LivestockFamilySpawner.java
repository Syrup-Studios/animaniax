package net.syrupstudios.animaniax.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;

public final class LivestockFamilySpawner {
    private static final TagKey<EntityType<?>> FAMILY_GENERATORS = TagKey.create(
            Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("animaniax", "natural_family_generators"));
    private static final TagKey<EntityType<?>> COW_POPULATION = TagKey.create(
            Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("animaniax", "natural_spawn/cows"));

    private LivestockFamilySpawner() {}

    public static void spawn(ServerLevelAccessor level, DifficultyInstance difficulty, Animal mother) {
        if (level.getLevel().isClientSide || !mother.getType().is(FAMILY_GENERATORS)
                || mother.isBaby() || !(mother instanceof AnimalReproductionState motherState)) return;

        motherState.animaniax$setFemale(true);

        int choice = mother.getRandom().nextInt(3);
        boolean cowPopulation = mother.getType().is(COW_POPULATION);
        AABB bounds = new AABB(mother.getX() - 64.0, mother.getY() - 64.0, mother.getZ() - 64.0,
                mother.getX() + 64.0, mother.getY() + 64.0, mother.getZ() + 64.0);
        long nearby = level.getEntitiesOfClass(Animal.class, bounds,
                animal -> animal != mother && (cowPopulation
                        ? animal.getType().is(COW_POPULATION) : animal.getType() == mother.getType())).size();
        if (nearby > 8 || choice == 2) return;

        Entity created = mother.getType().create(level.getLevel());
        if (!(created instanceof Animal member)
                || !(member instanceof AnimalReproductionState memberState)) return;
        BlockPos spawnPosition = findSpawnPosition(level, mother, member);
        if (spawnPosition == null) return;

        member.setPos(spawnPosition.getX() + 0.5, spawnPosition.getY(), spawnPosition.getZ() + 0.5);
        member.finalizeSpawn(level, difficulty, MobSpawnType.TRIGGERED, null);
        animaniax$copyBreed(mother, member);
        if (choice == 0) {
            memberState.animaniax$setFemale(false);
            memberState.animaniax$setMatePartner(mother.getUUID());
        } else {
            member.setBaby(true);
            memberState.animaniax$setMotherUUID(mother.getUUID());
        }

        if (!level.addFreshEntity(member)) return;
        if (choice == 1) {
            motherState.animaniax$setHasGivenBirth(true);
            motherState.animaniax$setLactating(true);
        }
    }

    private static BlockPos findSpawnPosition(ServerLevelAccessor level, Animal mother, Animal member) {
        BlockPos origin = mother.blockPosition();
        int[][] offsets = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}, {2, 2}, {-2, 2}, {2, -2}, {-2, -2},
                {3, 1}, {-3, 1}, {3, -1}, {-3, -1}, {1, 3}, {-1, 3}, {1, -3}, {-1, -3}};
        int start = mother.getRandom().nextInt(offsets.length);
        for (int i = 0; i < offsets.length; i++) {
            int[] offset = offsets[(start + i) % offsets.length];
            BlockPos pos = origin.offset(offset[0], 0, offset[1]);
            if (!level.hasChunkAt(pos) || !level.hasChunkAt(pos.below())
                    || !level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                    || !level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()
                    || !level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) continue;
            member.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            if (!SpawnPlacements.checkSpawnRules(member.getType(), level, MobSpawnType.NATURAL, pos,
                    member.getRandom()) || !level.noCollision(member)
                    || !animaniax$hasEmptyFluidSpace(level, member.getBoundingBox())) continue;
            return pos;
        }
        return null;
    }

    private static boolean animaniax$hasEmptyFluidSpace(ServerLevelAccessor level, AABB bounds) {
        BlockPos min = BlockPos.containing(bounds.minX, bounds.minY, bounds.minZ);
        BlockPos max = BlockPos.containing(Math.nextDown(bounds.maxX), Math.nextDown(bounds.maxY),
                Math.nextDown(bounds.maxZ));
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!level.hasChunkAt(pos) || !level.getFluidState(pos).isEmpty()) return false;
                }
            }
        }
        return true;
    }

    private static void animaniax$copyBreed(Animal mother, Animal member) {
        if (mother instanceof CowBreedState parent && member instanceof CowBreedState child)
            child.animaniax$setBreed(parent.animaniax$getBreed());
        else if (mother instanceof PigBreedState parent && member instanceof PigBreedState child)
            child.animaniax$setPigBreed(parent.animaniax$getPigBreed());
        else if (mother instanceof SheepBreedState parent && member instanceof SheepBreedState child)
            child.animaniax$setSheepBreed(parent.animaniax$getSheepBreed());
        else if (mother instanceof GoatBreedState parent && member instanceof GoatBreedState child)
            child.animaniax$setGoatBreed(parent.animaniax$getGoatBreed());
        else if (mother instanceof RabbitBreedState parent && member instanceof RabbitBreedState child)
            child.animaniax$setRabbitBreed(parent.animaniax$getRabbitBreed());
    }
}

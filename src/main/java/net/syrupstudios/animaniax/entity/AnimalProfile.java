package net.syrupstudios.animaniax.entity;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.function.LongPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/** Internal capabilities for the animals that use the shared care and breeding rules. */
public final class AnimalProfile {
    public static final TagKey<Item> COW_FOODS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("animaniax", "cow_foods"));
    public static final TagKey<Item> GOAT_FOODS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("animaniax", "goat_foods"));
    public static final TagKey<Item> PIG_FOODS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("animaniax", "pig_slop_foods"));
    public static final TagKey<Item> HORSE_FOODS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("animaniax", "horse_foods"));
    public static final TagKey<Item> POULTRY_FOODS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("animaniax", "poultry_foods"));
    public static final TagKey<Item> RABBIT_FOODS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("animaniax", "rabbit_foods"));
    public static final TagKey<Item> SHEEP_FOODS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("animaniax", "sheep_foods"));
    public static final TagKey<Item> CAT_FOODS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("animaniax", "cat_foods"));
    public static final TagKey<Item> DOG_FOODS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("animaniax", "dog_foods"));
    public static final TagKey<Item> FLOWER_BREEDING_FOODS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("animaniax", "flower_breeding_foods"));

    public enum Capability {
        CARE, SLEEP, GROWTH, REPRODUCTIVE_INTERACTIONS, GESTATION, MILKABLE_AT_SPAWN,
        POULTRY_REPRODUCTION, SALT_LICK_GOAL, SEED_GOAL, GRASS_GOAL, SHOULDER_CARRY, FLOWER_BREEDING,
        PIG_ENRICHMENT_GOALS, PET_FOOD_GOAL, NESTING_GOAL, HERDING_GOAL, NATURAL_GRAZING_GOAL, TROUGH_SLOP,
        REMOVE_NATURAL_WATER
    }

    private record Diet(Set<Item> troughFeed, Set<Item> bowlFeed, int troughWaterCost) {}
    public record SleepBed(Block block, int scanOriginYOffset, int destinationYOffset) {}
    private record SleepPolicy(LongPredicate schedule, Supplier<List<SleepBed>> beds) {}
    private enum BreedStage { SPAWN_CHILD, PREGNANCY, EXTRA_LITTER }
    private record BreedAccess(Predicate<Entity> supports, Function<Entity, String> get, BiConsumer<Entity, String> set) {}
    @FunctionalInterface private interface BreedInheritance { void inherit(Entity child, Animal first, Animal second, BreedStage stage, boolean useFirst); }
    private static final class Profile {
        private final Set<Capability> capabilities;
        private final Supplier<Diet> diet;
        private final BreedInheritance inheritance;
        private final SleepPolicy sleep;
        private volatile Diet resolvedDiet;

        private Profile(Set<Capability> capabilities, Supplier<Diet> diet, BreedInheritance inheritance, SleepPolicy sleep) {
            this.capabilities = capabilities;
            this.diet = diet;
            this.inheritance = inheritance;
            this.sleep = sleep;
        }

        private Set<Capability> capabilities() { return capabilities; }
        private BreedInheritance inheritance() { return inheritance; }
        private SleepPolicy sleep() { return sleep; }
        private Diet food() {
            if (diet == null) return null;
            Diet resolved = resolvedDiet;
            if (resolved == null) resolvedDiet = resolved = diet.get();
            return resolved;
        }
    }

    private static final class Profiles {
        private static final BreedAccess COW_BREED = new BreedAccess(
                entity -> entity instanceof CowBreedState,
                entity -> ((CowBreedState) entity).animaniax$getBreed(),
                (entity, breed) -> ((CowBreedState) entity).animaniax$setBreed(breed));
        private static final BreedAccess PIG_BREED = new BreedAccess(
                entity -> entity instanceof PigBreedState,
                entity -> ((PigBreedState) entity).animaniax$getPigBreed(),
                (entity, breed) -> ((PigBreedState) entity).animaniax$setPigBreed(breed));
        private static final BreedAccess GOAT_BREED = new BreedAccess(
                entity -> entity instanceof GoatBreedState,
                entity -> ((GoatBreedState) entity).animaniax$getGoatBreed(),
                (entity, breed) -> ((GoatBreedState) entity).animaniax$setGoatBreed(breed));
        private static final BreedAccess RABBIT_BREED = new BreedAccess(
                entity -> entity instanceof RabbitBreedState,
                entity -> ((RabbitBreedState) entity).animaniax$getRabbitBreed(),
                (entity, breed) -> ((RabbitBreedState) entity).animaniax$setRabbitBreed(breed));
        private static final BreedAccess SHEEP_BREED = new BreedAccess(
                entity -> entity instanceof SheepBreedState,
                entity -> ((SheepBreedState) entity).animaniax$getSheepBreed(),
                (entity, breed) -> ((SheepBreedState) entity).animaniax$setSheepBreed(breed));

        private static final Map<EntityType<?>, Profile> BY_TYPE = Map.ofEntries(
                Map.entry(EntityType.COW, sleepProfile(caps(Capability.CARE, Capability.SLEEP, Capability.GROWTH, Capability.REPRODUCTIVE_INTERACTIONS, Capability.GESTATION, Capability.MILKABLE_AT_SPAWN, Capability.SALT_LICK_GOAL, Capability.GRASS_GOAL, Capability.NATURAL_GRAZING_GOAL, Capability.REMOVE_NATURAL_WATER), () -> troughDiet(Items.WHEAT), Profiles.stringBreed(EntityType.COW, Profiles.COW_BREED), Profiles::farmSleepBeds)),
                Map.entry(EntityType.MOOSHROOM, sleepProfile(caps(Capability.CARE, Capability.SLEEP, Capability.GROWTH, Capability.REPRODUCTIVE_INTERACTIONS, Capability.GESTATION, Capability.MILKABLE_AT_SPAWN, Capability.SALT_LICK_GOAL, Capability.GRASS_GOAL, Capability.NATURAL_GRAZING_GOAL, Capability.REMOVE_NATURAL_WATER), () -> troughDiet(Items.WHEAT), null, Profiles::farmSleepBeds)),
                Map.entry(EntityType.SHEEP, sleepProfile(caps(Capability.CARE, Capability.SLEEP, Capability.GROWTH, Capability.REPRODUCTIVE_INTERACTIONS, Capability.GESTATION, Capability.SALT_LICK_GOAL, Capability.GRASS_GOAL, Capability.HERDING_GOAL, Capability.NATURAL_GRAZING_GOAL, Capability.REMOVE_NATURAL_WATER), () -> troughDiet(Items.WHEAT), Profiles.stringBreed(EntityType.SHEEP, Profiles.SHEEP_BREED), Profiles::farmSleepBeds)),
                Map.entry(EntityType.PIG, sleepProfile(caps(Capability.CARE, Capability.SLEEP, Capability.GROWTH, Capability.REPRODUCTIVE_INTERACTIONS, Capability.GESTATION, Capability.SALT_LICK_GOAL, Capability.GRASS_GOAL, Capability.PIG_ENRICHMENT_GOALS, Capability.NATURAL_GRAZING_GOAL, Capability.TROUGH_SLOP, Capability.REMOVE_NATURAL_WATER), () -> troughDiet(Items.CARROT, Items.POTATO, Items.POISONOUS_POTATO, Items.BEETROOT, Items.BREAD), Profiles.stringBreed(EntityType.PIG, Profiles.PIG_BREED), Profiles::farmSleepBeds)),
                Map.entry(EntityType.CHICKEN, sleepProfile(caps(Capability.CARE, Capability.SLEEP, Capability.GROWTH, Capability.REPRODUCTIVE_INTERACTIONS, Capability.POULTRY_REPRODUCTION, Capability.SEED_GOAL, Capability.NESTING_GOAL), () -> troughDiet(50), Profiles::inheritChickenBreed, Profiles::farmSleepBeds)),
                Map.entry(EntityType.RABBIT, sleepProfile(caps(Capability.CARE, Capability.SLEEP, Capability.GROWTH, Capability.REPRODUCTIVE_INTERACTIONS, Capability.GESTATION, Capability.GRASS_GOAL), () -> troughDiet(Items.WHEAT, Items.CARROT, Items.BEETROOT, Items.APPLE), Profiles.stringBreed(EntityType.RABBIT, Profiles.RABBIT_BREED), time -> (time > 20_000L && time < 24_000L) || (time > 10_000L && time < 15_000L), Profiles::farmSleepBeds)),
                Map.entry(EntityType.GOAT, sleepProfile(caps(Capability.CARE, Capability.SLEEP, Capability.GROWTH, Capability.REPRODUCTIVE_INTERACTIONS, Capability.GESTATION, Capability.SALT_LICK_GOAL, Capability.GRASS_GOAL, Capability.HERDING_GOAL, Capability.NATURAL_GRAZING_GOAL, Capability.REMOVE_NATURAL_WATER), () -> troughDiet(Items.WHEAT, Items.APPLE), Profiles.stringBreed(EntityType.GOAT, Profiles.GOAT_BREED), Profiles::farmSleepBeds))
        );

        private static Set<Capability> caps(Capability... capabilities) { return Set.of(capabilities); }
        private static Profile profile(Set<Capability> capabilities, Supplier<Diet> diet) { return new Profile(capabilities, diet, null, null); }
        private static Profile profile(Set<Capability> capabilities, Supplier<Diet> diet, BreedInheritance inheritance) { return new Profile(capabilities, diet, inheritance, null); }
        private static Profile sleepProfile(Set<Capability> capabilities, Supplier<Diet> diet, BreedInheritance inheritance, Supplier<List<SleepBed>> beds) {
            return sleepProfile(capabilities, diet, inheritance, false, beds);
        }
        private static Profile sleepProfile(Set<Capability> capabilities, Supplier<Diet> diet, BreedInheritance inheritance, boolean daySleeper, Supplier<List<SleepBed>> beds) {
            return sleepProfile(capabilities, diet, inheritance,
                    daySleeper ? time -> time < 13_000L : time -> time >= 13_000L, beds);
        }
        private static Profile sleepProfile(Set<Capability> capabilities, Supplier<Diet> diet, BreedInheritance inheritance, LongPredicate schedule, Supplier<List<SleepBed>> beds) {
            return new Profile(capabilities, diet, inheritance, new SleepPolicy(schedule, beds));
        }
        private static List<SleepBed> farmSleepBeds() {
            return List.of(new SleepBed(Blocks.GRASS_BLOCK, -1, 1));
        }
        private static Diet troughDiet(Item... feed) { return troughDiet(100, feed); }
        private static Diet troughDiet(int waterCost, Item... feed) { return new Diet(Set.of(feed), Set.of(), waterCost); }
        private static Diet bowlDiet(Item... feed) { return new Diet(Set.of(), Set.of(feed), 100); }

        private static BreedInheritance stringBreed(EntityType<?> parentType, BreedAccess access) {
            return (child, first, second, stage, useFirst) -> {
                if (!access.supports().test(child) || !access.supports().test(first) || !access.supports().test(second)) return;
                if (stage == BreedStage.PREGNANCY && first.getType() == parentType && second.getType() == parentType) {
                    useFirst = first.getRandom().nextBoolean();
                } else if (stage != BreedStage.EXTRA_LITTER) {
                    return;
                }
                access.set().accept(child, access.get().apply(useFirst ? first : second));
            };
        }

        private static void inheritChickenBreed(Entity child, Animal first, Animal second, BreedStage stage, boolean useFirst) {
            if (stage == BreedStage.SPAWN_CHILD && first.getType() == EntityType.CHICKEN && second.getType() == EntityType.CHICKEN
                    && child instanceof ChickenBreedState c && first instanceof ChickenBreedState a && second instanceof ChickenBreedState b) {
                c.animaniax$setChickenBreed(first.getRandom().nextBoolean() ? a.animaniax$getChickenBreed() : b.animaniax$getChickenBreed());
            }
        }

    }

    private AnimalProfile() {}

    public static void inheritChildBreed(Entity child, Animal first, Animal second) {
        inheritBreed(child, first, second, BreedStage.SPAWN_CHILD, false);
    }

    public static void inheritPregnancyBreed(Entity child, Animal first, Animal second) {
        inheritBreed(child, first, second, BreedStage.PREGNANCY, false);
    }

    public static void inheritExtraLitterBreed(Entity child, Animal first, Animal second) {
        inheritBreed(child, first, second, BreedStage.EXTRA_LITTER, first.getRandom().nextBoolean());
    }

    private static void inheritBreed(Entity child, Animal first, Animal second, BreedStage stage, boolean useFirst) {
        Profile profile = child == null ? null : Profiles.BY_TYPE.get(child.getType());
        if (profile != null && profile.inheritance() != null) {
            profile.inheritance().inherit(child, first, second, stage, useFirst);
        }
    }

    public static boolean has(EntityType<?> type, Capability capability) {
        Profile profile = type == null ? null : Profiles.BY_TYPE.get(type);
        return profile != null && profile.capabilities().contains(capability);
    }

    public static boolean acceptsFlowerBreedingFood(EntityType<?> type, ItemStack stack) {
        return false;
    }

    public static boolean acceptsCowOrGoatFood(EntityType<?> type, ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (type == EntityType.COW || type == EntityType.MOOSHROOM) return stack.is(COW_FOODS);
        if (type == EntityType.GOAT) return stack.is(GOAT_FOODS);
        return false;
    }

    public static boolean acceptsTroughFeed(EntityType<?> type, ItemStack stack) {
        Profile profile = type == null ? null : Profiles.BY_TYPE.get(type);
        Diet diet = profile == null ? null : profile.food();
        return diet != null && (diet.troughFeed().contains(stack.getItem())
                || acceptsCowOrGoatFood(type, stack)
                || type == EntityType.PIG && stack.is(PIG_FOODS)
                || type == EntityType.RABBIT && stack.is(RABBIT_FOODS)
                || type == EntityType.SHEEP && stack.is(SHEEP_FOODS)
                || has(type, Capability.SEED_GOAL) && stack.is(POULTRY_FOODS));
    }

    public static boolean acceptsPetBowlFood(EntityType<?> type, ItemStack stack) {
        Profile profile = type == null ? null : Profiles.BY_TYPE.get(type);
        Diet diet = profile == null ? null : profile.food();
        return diet != null && diet.bowlFeed().contains(stack.getItem());
    }

    public static int troughWaterCost(EntityType<?> type) {
        Profile profile = type == null ? null : Profiles.BY_TYPE.get(type);
        Diet diet = profile == null ? null : profile.food();
        return diet == null ? 100 : diet.troughWaterCost();
    }

    public static boolean shouldSleepAt(EntityType<?> type, long time) {
        Profile profile = type == null ? null : Profiles.BY_TYPE.get(type);
        return profile != null && profile.sleep() != null && profile.sleep().schedule().test(time);
    }

    public static List<SleepBed> sleepBeds(EntityType<?> type) {
        Profile profile = type == null ? null : Profiles.BY_TYPE.get(type);
        return profile == null || profile.sleep() == null ? List.of() : profile.sleep().beds().get();
    }
}

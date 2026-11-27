package net.syrupstudios.animaniax.config;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.LinkedHashMap;
import java.util.Map;
import net.syrupstudios.syruplibrary.config.ConfigSpec;
import net.syrupstudios.syruplibrary.config.RegisteredConfig;
import net.syrupstudios.syruplibrary.config.SyrupConfigManager;
import net.syrupstudios.syruplibrary.config.value.ConfigConstraint;
import net.syrupstudios.syruplibrary.config.value.ConfigType;
import net.syrupstudios.syruplibrary.config.RestartRequirement;
import net.syrupstudios.syruplibrary.config.value.ConfigValue;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.syrupstudios.animaniax.item.BonusEffectFoodItem;
import net.syrupstudios.animaniax.item.MilkBottleItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AnimalCareConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("animaniax");
    private static final int MAX_TICKS = 2_147_483_000;
    private static volatile Map<ResourceLocation, FoodValueOverride> foodValueOverrides = Map.of();
    public static int gestationTimer = 20_000;
    public static double birthMultipleChance = 0.1;
    public static double animalLossChance = 0.0;
    public static int feedTimer = 12_000;
    public static int waterTimer = 12_000;
    public static int ticksBetweenAIFirings = 100;
    public static int aiBlockSearchRange = 16;
    public static int childGrowthTick = 200;
    public static boolean animalsStarve = false;
    public static boolean showUnhappyParticles = true;
    public static boolean animalsSleep = true;
    public static boolean animalsCanAttackOthers = true;
    public static boolean replaceVanillaWolves = true;
    public static boolean replaceVanillaOcelots = true;
    public static int starvationTimer = 400;
    public static int matingTimer = 100;
    public static boolean requireAnimalInteractionForAI = true;
    public static boolean ambianceMode = false;
    public static boolean feedToBreed = true;
    public static boolean malesMateMultipleFemales = false;
    public static int entityBreedingLimit = 15;
    public static int animalCapSearchRange = 80;
    public static int naturalSpawnLimitCows = 40;
    public static int naturalSpawnLimitPigs = 40;
    public static int naturalSpawnLimitChickens = 40;
    public static int naturalSpawnLimitSheep = 40;
    public static int naturalSpawnLimitGoats = 40;
    public static int naturalSpawnLimitHorses = 40;
    public static int naturalSpawnLimitRabbits = 40;
    public static int naturalSpawnLimitAmphibians = 40;
    public static int naturalSpawnLimitHamsters = 40;
    public static int naturalSpawnLimitFerrets = 40;
    public static int naturalSpawnLimitHedgehogs = 40;
    public static int naturalSpawnLimitCats = 20;
    public static int naturalSpawnLimitDogs = 20;
    public static boolean plantsRemovedAfterEating = true;
    public static boolean waterRemovedAfterDrinking = true;
    public static boolean hiveSpawning = true;
    public static int hiveSpawningFrequency = 3;
    public static int hiveWildHoneyRate = 700;
    public static int hivePlayermadeHoneyRate = 450;
    public static boolean allowSeedDispenserPlacement = true;
    public static boolean shiftSeedPlacement = false;
    public static boolean allowTroughAutomation = true;
    public static int saltLickTick = 8000;
    public static int saltLickMaxUses = 200;
    public static int laidTimer = 2000;
    public static int eggHatchChance = 2;
    public static int cheeseMaturityTime = 24_000;
    public static int saltCreationAmount = 16;
    public static int woolRegrowthTimer = 8_000;
    public static int hamsterWheelCapacity = 200_000;
    public static int hamsterWheelRFGeneration = 20;
    public static int hamsterWheelUseTime = 2_000;
    public static boolean allowEggThrowing = false;
    public static boolean chickensDropEggs = false;
    public static boolean birdsDropFeathers = true;
    public static int featherTimer = 12_000;
    public static boolean foodsGiveBonusEffects = true;
    public static boolean eatFoodAnytime = true;
    public static boolean cowsMilkableAtSpawn = false;
    public static boolean disableSaltCreation = false;

    private AnimalCareConfig() {}

    private record FoodValueOverride(int nutrition, float saturation) {}

    public static FoodProperties foodProperties(ItemStack stack) {
        return foodProperties(stack, stack.get(DataComponents.FOOD));
    }

    public static FoodProperties foodProperties(ItemStack stack, FoodProperties original) {
        if (original == null) return null;
        FoodValueOverride override = foodValueOverrides.get(BuiltInRegistries.ITEM.getKey(stack.getItem()));
        return override == null ? original : new FoodProperties(override.nutrition(), override.saturation(),
                original.canAlwaysEat(), original.eatSeconds(), original.usingConvertsTo(), original.effects());
    }

    private static Map<ResourceLocation, FoodValueOverride> readFoodValueOverrides(String value) {
        if (value == null || value.isBlank()) return Map.of();
        Map<ResourceLocation, FoodValueOverride> parsed = new LinkedHashMap<>();
        for (String entry : value.split(";", -1)) {
            try {
                String text = entry.trim();
                int open = text.indexOf('(');
                if (open <= 0 || !text.endsWith(")") || text.indexOf('(', open + 1) >= 0)
                    throw new IllegalArgumentException("expected id(nutrition,saturationMultiplier)");
                String idText = text.substring(0, open).trim();
                if (!idText.contains(":")) throw new IllegalArgumentException("item id must be namespaced");
                ResourceLocation id = ResourceLocation.tryParse(idText);
                if (id == null) throw new IllegalArgumentException("invalid item id");
                String[] values = text.substring(open + 1, text.length() - 1).split(",", -1);
                if (values.length != 2) throw new IllegalArgumentException("expected two food values");
                int nutrition = Integer.parseInt(values[0].trim());
                double multiplier = Double.parseDouble(values[1].trim());
                double saturationValue = nutrition * multiplier * 2.0;
                if (nutrition < 0 || nutrition > Integer.MAX_VALUE - 20 || !Double.isFinite(multiplier) || multiplier < 0.0
                        || !Double.isFinite(saturationValue) || saturationValue > Float.MAX_VALUE)
                    throw new IllegalArgumentException("food values are out of range");
                if (parsed.containsKey(id)) throw new IllegalArgumentException("duplicate item id");
                Item item = BuiltInRegistries.ITEM.getOptional(id).orElse(null);
                if (!(item instanceof BonusEffectFoodItem || item instanceof MilkBottleItem)
                        || item.components().get(DataComponents.FOOD) == null)
                    throw new IllegalArgumentException("item is not an Animaniax food item");
                parsed.put(id, new FoodValueOverride(nutrition, (float) saturationValue));
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Ignoring invalid foodValueOverrides entry '{}': {}", entry.trim(), e.getMessage());
            }
        }
        return Map.copyOf(parsed);
    }

    public static void load(Path legacyPath) {
        Path directory = legacyPath.getParent();
        boolean migrate = Files.notExists(directory.resolve("animaniax.json5")) && Files.exists(legacyPath);
        Properties legacyProperties = new Properties();
        if (migrate) {
            loadLegacy(legacyPath);
            try (Reader reader = Files.newBufferedReader(legacyPath)) {
                legacyProperties.load(reader);
            } catch (IOException e) {
                throw new IllegalStateException("Could not read legacy Animaniax config at " + legacyPath, e);
            }
        }
        ConfigSpec spec = ConfigSpec.builder("animaniax").header("Animaniax world settings.", "Restart the world after editing this file.").build();
        ConfigValue<Integer> gestationTimerValue = spec.integer("gestation_timer", 20_000, 1, MAX_TICKS, "Animal care and reproduction settings");
        ConfigValue<Double> birthMultipleChanceValue = spec.value("birth_multiple_chance", ConfigType.DOUBLE, 0.1, "Chance for each additional offspring; one offspring is always produced, up to 64 total.", RestartRequirement.NONE, List.of(ConfigConstraint.range(0.0, 1.0)));
        ConfigValue<Double> animalLossChanceValue = spec.value("animal_loss_chance", ConfigType.DOUBLE, 0.0, "Chance of losing the litter at term when both hunger and thirst are low.", RestartRequirement.NONE, List.of(ConfigConstraint.range(0.0, 1.0)));
        ConfigValue<Integer> feedTimerValue = spec.integer("feed_timer", 12_000, 1, MAX_TICKS, "Feed timer.");
        ConfigValue<Integer> waterTimerValue = spec.integer("water_timer", 12_000, 1, MAX_TICKS, "Water timer.");
        ConfigValue<Integer> ticksBetweenAIFiringsValue = spec.integer("ticks_between_ai_firings", 100, 1, MAX_TICKS, "Goal checks between food, water, and mud searches.");
        ConfigValue<Integer> aiBlockSearchRangeValue = spec.integer("ai_block_search_range", 16, 1, 64, "Range for food, water, and salt lick block searches.");
        ConfigValue<Integer> childGrowthTickValue = spec.integer("child_growth_tick", 200, 1, MAX_TICKS, "Growth interval in ticks. The timer advances during neglect and sleep, but growth applies only when cared for and awake.");
        ConfigValue<Boolean> animalsStarveValue = spec.bool("animals_starve", false, "Animals take starvation damage when both hunger and thirst are low.");
        ConfigValue<Boolean> showUnhappyParticlesValue = spec.bool("show_unhappy_particles", true, "Show smoke when an animal is hungry and thirsty.");
        ConfigValue<Boolean> animalsSleepValue = spec.bool("animals_sleep", true, "Animals sleep at night when they can reach straw or grass.");
        ConfigValue<Boolean> animalsCanAttackOthersValue = spec.bool("animals_can_attack_others", true, "Allows bull retaliation, sparring, and proactive animal hunting.");
        ConfigValue<Boolean> replaceVanillaWolvesValue = spec.bool("replace_vanilla_wolves", true, "Prevent natural vanilla wolf spawns.");
        ConfigValue<Boolean> replaceVanillaOcelotsValue = spec.bool("replace_vanilla_ocelots", true, "Prevent natural vanilla ocelot spawns.");
        ConfigValue<Integer> starvationTimerValue = spec.integer("starvation_timer", 400, 1, MAX_TICKS, "Ticks between starvation damage.");
        ConfigValue<Integer> matingTimerValue = spec.integer("mating_timer", 100, 1, MAX_TICKS, "Mating timer.");
        ConfigValue<Boolean> requireAnimalInteractionForAIValue = spec.bool("require_animal_interaction_for_ai", true, "Require animal interaction for AI.");
        ConfigValue<Boolean> ambianceModeValue = spec.bool("ambiance_mode", false, "Animals do not have to eat or drink.");
        ConfigValue<Boolean> feedToBreedValue = spec.bool("feed_to_breed", true, "Feed to breed.");
        ConfigValue<Boolean> malesMateMultipleFemalesValue = spec.bool("males_mate_multiple_females", false, "Males mate multiple females.");
        ConfigValue<Integer> entityBreedingLimitValue = spec.integer("entity_breeding_limit", 15, 1, MAX_TICKS, "Entity breeding limit.");
        ConfigValue<Integer> animalCapSearchRangeValue = spec.integer("animal_cap_search_range", 80, 1, MAX_TICKS, "Animal cap search range.");
        ConfigValue<Integer> naturalSpawnLimitCowsValue = spec.integer("natural_spawn_limit_cows", 40, 1, MAX_TICKS, "Threshold used to limit nearby natural spawns for each population group.");
        ConfigValue<Integer> naturalSpawnLimitPigsValue = spec.integer("natural_spawn_limit_pigs", 40, 1, MAX_TICKS, "Natural spawn limit pigs.");
        ConfigValue<Integer> naturalSpawnLimitChickensValue = spec.integer("natural_spawn_limit_chickens", 40, 1, MAX_TICKS, "Natural spawn limit chickens.");
        ConfigValue<Integer> naturalSpawnLimitSheepValue = spec.integer("natural_spawn_limit_sheep", 40, 1, MAX_TICKS, "Natural spawn limit sheep.");
        ConfigValue<Integer> naturalSpawnLimitGoatsValue = spec.integer("natural_spawn_limit_goats", 40, 1, MAX_TICKS, "Natural spawn limit goats.");
        ConfigValue<Integer> naturalSpawnLimitHorsesValue = spec.integer("natural_spawn_limit_horses", 40, 1, MAX_TICKS, "Natural spawn limit horses.");
        ConfigValue<Integer> naturalSpawnLimitRabbitsValue = spec.integer("natural_spawn_limit_rabbits", 40, 1, MAX_TICKS, "Natural spawn limit rabbits.");
        ConfigValue<Integer> naturalSpawnLimitAmphibiansValue = spec.integer("natural_spawn_limit_amphibians", 40, 1, MAX_TICKS, "Natural spawn limit amphibians.");
        ConfigValue<Integer> naturalSpawnLimitHamstersValue = spec.integer("natural_spawn_limit_hamsters", 40, 1, MAX_TICKS, "Natural spawn limit hamsters.");
        ConfigValue<Integer> naturalSpawnLimitFerretsValue = spec.integer("natural_spawn_limit_ferrets", 40, 1, MAX_TICKS, "Natural spawn limit ferrets.");
        ConfigValue<Integer> naturalSpawnLimitHedgehogsValue = spec.integer("natural_spawn_limit_hedgehogs", 40, 1, MAX_TICKS, "Natural spawn limit hedgehogs.");
        ConfigValue<Integer> naturalSpawnLimitCatsValue = spec.integer("natural_spawn_limit_cats", 20, 1, MAX_TICKS, "Natural spawn limit cats.");
        ConfigValue<Integer> naturalSpawnLimitDogsValue = spec.integer("natural_spawn_limit_dogs", 20, 1, MAX_TICKS, "Natural spawn limit dogs.");
        ConfigValue<Boolean> plantsRemovedAfterEatingValue = spec.bool("plants_removed_after_eating", true, "Plants removed after eating.");
        ConfigValue<Boolean> waterRemovedAfterDrinkingValue = spec.bool("water_removed_after_drinking", true, "Water removed after drinking.");
        ConfigValue<Boolean> hiveSpawningValue = spec.bool("hive_spawning", true, "Allow wild hives to spawn.");
        ConfigValue<Integer> hiveSpawningFrequencyValue = spec.integer("hive_spawning_frequency", 3, 0, 10, "Wild hive spawn chance per eligible chunk, from 0 to 10 out of 200.");
        ConfigValue<Integer> hiveWildHoneyRateValue = spec.integer("hive_wild_honey_rate", 700, 1, MAX_TICKS, "Honey production interval for wild hives, in ticks.");
        ConfigValue<Integer> hivePlayermadeHoneyRateValue = spec.integer("hive_playermade_honey_rate", 450, 1, MAX_TICKS, "Honey production interval for player-made hives, in ticks.");
        ConfigValue<Boolean> allowSeedDispenserPlacementValue = spec.bool("allow_seed_dispenser_placement", true, "Allow dispensers to place seed piles.");
        ConfigValue<Boolean> shiftSeedPlacementValue = spec.bool("shift_seed_placement", false, "Require sneaking to place seed piles with seeds.");
        ConfigValue<Boolean> allowTroughAutomationValue = spec.bool("allow_trough_automation", true, "Allow hoppers and pipes to insert or remove trough feed.");
        ConfigValue<Integer> saltLickTickValue = spec.integer("salt_lick_tick", 8000, 1, MAX_TICKS, "Salt lick tick.");
        ConfigValue<Integer> saltLickMaxUsesValue = spec.integer("salt_lick_max_uses", 200, 1, 1_000_000, "Maximum uses for a new salt lick.");
        ConfigValue<Integer> laidTimerValue = spec.integer("laid_timer", 2000, 1, MAX_TICKS, "Ticks between eggs laid in nests.");
        ConfigValue<Integer> eggHatchChanceValue = spec.integer("egg_hatch_chance", 2, 1, MAX_TICKS, "Chance of hatching an egg, as 1 in N.");
        ConfigValue<Integer> cheeseMaturityTimeValue = spec.integer("cheese_maturity_time", 24_000, 1, MAX_TICKS, "Ticks that cheese takes to mature.");
        ConfigValue<Integer> saltCreationAmountValue = spec.integer("salt_creation_amount", 16, 1, 64, "Salt items made by curing water in a cheese mold.");
        ConfigValue<Integer> woolRegrowthTimerValue = spec.integer("wool_regrowth_timer", 8_000, 1, MAX_TICKS, "Ticks before sheep wool regrows after shearing.");
        ConfigValue<Integer> hamsterWheelCapacityValue = spec.integer("hamster_wheel_capacity", 200_000, 1, MAX_TICKS, "RF capacity of the hamster wheel.");
        ConfigValue<Integer> hamsterWheelRFGenerationValue = spec.integer("hamster_wheel_r_f_generation", 20, 0, MAX_TICKS, "RF generated by a running hamster wheel each tick.");
        ConfigValue<Integer> hamsterWheelUseTimeValue = spec.integer("hamster_wheel_use_time", 2_000, 1, MAX_TICKS, "Ticks that a hamster stays in the wheel before it needs more food.");
        ConfigValue<Boolean> allowEggThrowingValue = spec.bool("allow_egg_throwing", false, "Allow egg throwing.");
        ConfigValue<Boolean> chickensDropEggsValue = spec.bool("chickens_drop_eggs", false, "Chickens can drop eggs without using a nest.");
        ConfigValue<Boolean> birdsDropFeathersValue = spec.bool("birds_drop_feathers", true, "Birds shed feathers over time.");
        ConfigValue<Integer> featherTimerValue = spec.integer("feather_timer", 12_000, 1, MAX_TICKS - 999, "Feather timer.");
        ConfigValue<Boolean> foodsGiveBonusEffectsValue = spec.bool("foods_give_bonus_effects", true, "Apply Animania bonus effects when food is consumed.");
        ConfigValue<Boolean> eatFoodAnytimeValue = spec.bool("eat_food_anytime", true, "Allow Animania foods to be eaten when hunger is full.");
        ConfigValue<Boolean> cowsMilkableAtSpawnValue = spec.bool("cows_milkable_at_spawn", false, "Cows milkable at spawn.");
        ConfigValue<Boolean> disableSaltCreationValue = spec.bool("disable_salt_creation", false, "Prevent players from making salt in cheese molds with water.");
        ConfigValue<String> foodOverridesValue = spec.string("food_value_overrides", "", "Item food values as item_id(nutrition,saturationMultiplier), separated by semicolons.");
        RegisteredConfig config = SyrupConfigManager.create(directory).register(spec);
        if (!config.initialResult().successful()) throw new IllegalStateException("Could not load Animaniax config at " + config.path());
        if (migrate) {
            Map<ConfigValue<?>, Object> values = new LinkedHashMap<>();
            values.put(gestationTimerValue, gestationTimer);
            values.put(birthMultipleChanceValue, birthMultipleChance);
            values.put(animalLossChanceValue, animalLossChance);
            values.put(feedTimerValue, feedTimer);
            values.put(waterTimerValue, waterTimer);
            values.put(ticksBetweenAIFiringsValue, ticksBetweenAIFirings);
            values.put(aiBlockSearchRangeValue, aiBlockSearchRange);
            values.put(childGrowthTickValue, childGrowthTick);
            values.put(animalsStarveValue, animalsStarve);
            values.put(showUnhappyParticlesValue, showUnhappyParticles);
            values.put(animalsSleepValue, animalsSleep);
            values.put(animalsCanAttackOthersValue, animalsCanAttackOthers);
            values.put(replaceVanillaWolvesValue, replaceVanillaWolves);
            values.put(replaceVanillaOcelotsValue, replaceVanillaOcelots);
            values.put(starvationTimerValue, starvationTimer);
            values.put(matingTimerValue, matingTimer);
            values.put(requireAnimalInteractionForAIValue, requireAnimalInteractionForAI);
            values.put(ambianceModeValue, ambianceMode);
            values.put(feedToBreedValue, feedToBreed);
            values.put(malesMateMultipleFemalesValue, malesMateMultipleFemales);
            values.put(entityBreedingLimitValue, entityBreedingLimit);
            values.put(animalCapSearchRangeValue, animalCapSearchRange);
            values.put(naturalSpawnLimitCowsValue, naturalSpawnLimitCows);
            values.put(naturalSpawnLimitPigsValue, naturalSpawnLimitPigs);
            values.put(naturalSpawnLimitChickensValue, naturalSpawnLimitChickens);
            values.put(naturalSpawnLimitSheepValue, naturalSpawnLimitSheep);
            values.put(naturalSpawnLimitGoatsValue, naturalSpawnLimitGoats);
            values.put(naturalSpawnLimitHorsesValue, naturalSpawnLimitHorses);
            values.put(naturalSpawnLimitRabbitsValue, naturalSpawnLimitRabbits);
            values.put(naturalSpawnLimitAmphibiansValue, naturalSpawnLimitAmphibians);
            values.put(naturalSpawnLimitHamstersValue, naturalSpawnLimitHamsters);
            values.put(naturalSpawnLimitFerretsValue, naturalSpawnLimitFerrets);
            values.put(naturalSpawnLimitHedgehogsValue, naturalSpawnLimitHedgehogs);
            values.put(naturalSpawnLimitCatsValue, naturalSpawnLimitCats);
            values.put(naturalSpawnLimitDogsValue, naturalSpawnLimitDogs);
            values.put(plantsRemovedAfterEatingValue, plantsRemovedAfterEating);
            values.put(waterRemovedAfterDrinkingValue, waterRemovedAfterDrinking);
            values.put(hiveSpawningValue, hiveSpawning);
            values.put(hiveSpawningFrequencyValue, hiveSpawningFrequency);
            values.put(hiveWildHoneyRateValue, hiveWildHoneyRate);
            values.put(hivePlayermadeHoneyRateValue, hivePlayermadeHoneyRate);
            values.put(allowSeedDispenserPlacementValue, allowSeedDispenserPlacement);
            values.put(shiftSeedPlacementValue, shiftSeedPlacement);
            values.put(allowTroughAutomationValue, allowTroughAutomation);
            values.put(saltLickTickValue, saltLickTick);
            values.put(saltLickMaxUsesValue, saltLickMaxUses);
            values.put(laidTimerValue, laidTimer);
            values.put(eggHatchChanceValue, eggHatchChance);
            values.put(cheeseMaturityTimeValue, cheeseMaturityTime);
            values.put(saltCreationAmountValue, saltCreationAmount);
            values.put(woolRegrowthTimerValue, woolRegrowthTimer);
            values.put(hamsterWheelCapacityValue, hamsterWheelCapacity);
            values.put(hamsterWheelRFGenerationValue, hamsterWheelRFGeneration);
            values.put(hamsterWheelUseTimeValue, hamsterWheelUseTime);
            values.put(allowEggThrowingValue, allowEggThrowing);
            values.put(chickensDropEggsValue, chickensDropEggs);
            values.put(birdsDropFeathersValue, birdsDropFeathers);
            values.put(featherTimerValue, featherTimer);
            values.put(foodsGiveBonusEffectsValue, foodsGiveBonusEffects);
            values.put(eatFoodAnytimeValue, eatFoodAnytime);
            values.put(cowsMilkableAtSpawnValue, cowsMilkableAtSpawn);
            values.put(disableSaltCreationValue, disableSaltCreation);
            values.put(foodOverridesValue, legacyProperties.getProperty("foodValueOverrides", ""));
            if (!config.updateAndSaveAll(values).successful()) {
                try { Files.deleteIfExists(config.path()); } catch (IOException e) { LOGGER.error("Could not remove incomplete config at {}", config.path(), e); }
                throw new IllegalStateException("Could not migrate Animaniax config at " + legacyPath);
            }
        }
        gestationTimer = gestationTimerValue.get();
        birthMultipleChance = birthMultipleChanceValue.get();
        animalLossChance = animalLossChanceValue.get();
        feedTimer = feedTimerValue.get();
        waterTimer = waterTimerValue.get();
        ticksBetweenAIFirings = ticksBetweenAIFiringsValue.get();
        aiBlockSearchRange = aiBlockSearchRangeValue.get();
        childGrowthTick = childGrowthTickValue.get();
        animalsStarve = animalsStarveValue.get();
        showUnhappyParticles = showUnhappyParticlesValue.get();
        animalsSleep = animalsSleepValue.get();
        animalsCanAttackOthers = animalsCanAttackOthersValue.get();
        replaceVanillaWolves = replaceVanillaWolvesValue.get();
        replaceVanillaOcelots = replaceVanillaOcelotsValue.get();
        starvationTimer = starvationTimerValue.get();
        matingTimer = matingTimerValue.get();
        requireAnimalInteractionForAI = requireAnimalInteractionForAIValue.get();
        ambianceMode = ambianceModeValue.get();
        feedToBreed = feedToBreedValue.get();
        malesMateMultipleFemales = malesMateMultipleFemalesValue.get();
        entityBreedingLimit = entityBreedingLimitValue.get();
        animalCapSearchRange = animalCapSearchRangeValue.get();
        naturalSpawnLimitCows = naturalSpawnLimitCowsValue.get();
        naturalSpawnLimitPigs = naturalSpawnLimitPigsValue.get();
        naturalSpawnLimitChickens = naturalSpawnLimitChickensValue.get();
        naturalSpawnLimitSheep = naturalSpawnLimitSheepValue.get();
        naturalSpawnLimitGoats = naturalSpawnLimitGoatsValue.get();
        naturalSpawnLimitHorses = naturalSpawnLimitHorsesValue.get();
        naturalSpawnLimitRabbits = naturalSpawnLimitRabbitsValue.get();
        naturalSpawnLimitAmphibians = naturalSpawnLimitAmphibiansValue.get();
        naturalSpawnLimitHamsters = naturalSpawnLimitHamstersValue.get();
        naturalSpawnLimitFerrets = naturalSpawnLimitFerretsValue.get();
        naturalSpawnLimitHedgehogs = naturalSpawnLimitHedgehogsValue.get();
        naturalSpawnLimitCats = naturalSpawnLimitCatsValue.get();
        naturalSpawnLimitDogs = naturalSpawnLimitDogsValue.get();
        plantsRemovedAfterEating = plantsRemovedAfterEatingValue.get();
        waterRemovedAfterDrinking = waterRemovedAfterDrinkingValue.get();
        hiveSpawning = hiveSpawningValue.get();
        hiveSpawningFrequency = hiveSpawningFrequencyValue.get();
        hiveWildHoneyRate = hiveWildHoneyRateValue.get();
        hivePlayermadeHoneyRate = hivePlayermadeHoneyRateValue.get();
        allowSeedDispenserPlacement = allowSeedDispenserPlacementValue.get();
        shiftSeedPlacement = shiftSeedPlacementValue.get();
        allowTroughAutomation = allowTroughAutomationValue.get();
        saltLickTick = saltLickTickValue.get();
        saltLickMaxUses = saltLickMaxUsesValue.get();
        laidTimer = laidTimerValue.get();
        eggHatchChance = eggHatchChanceValue.get();
        cheeseMaturityTime = cheeseMaturityTimeValue.get();
        saltCreationAmount = saltCreationAmountValue.get();
        woolRegrowthTimer = woolRegrowthTimerValue.get();
        hamsterWheelCapacity = hamsterWheelCapacityValue.get();
        hamsterWheelRFGeneration = hamsterWheelRFGenerationValue.get();
        hamsterWheelUseTime = hamsterWheelUseTimeValue.get();
        allowEggThrowing = allowEggThrowingValue.get();
        chickensDropEggs = chickensDropEggsValue.get();
        birdsDropFeathers = birdsDropFeathersValue.get();
        featherTimer = featherTimerValue.get();
        foodsGiveBonusEffects = foodsGiveBonusEffectsValue.get();
        eatFoodAnytime = eatFoodAnytimeValue.get();
        cowsMilkableAtSpawn = cowsMilkableAtSpawnValue.get();
        disableSaltCreation = disableSaltCreationValue.get();
        foodValueOverrides = readFoodValueOverrides(foodOverridesValue.get());
    }

    private static void loadLegacy(Path path) {
        try {
            Properties properties = new Properties();
            try (Reader reader = Files.newBufferedReader(path)) {
                properties.load(reader);
            }
            gestationTimer = readTicks(properties, "gestationTimer", 20_000);
            birthMultipleChance = readChance(properties, "birthMultipleChance", 0.1);
            animalLossChance = readChance(properties, "animalLossChance", 0.0);
            feedTimer = readTicks(properties, "feedTimer", 12_000);
            waterTimer = readTicks(properties, "waterTimer", 12_000);
            ticksBetweenAIFirings = readTicks(properties, "ticksBetweenAIFirings", 100);
            aiBlockSearchRange = readClampedInt(properties, "aiBlockSearchRange", 16, 1, 64);
            childGrowthTick = readTicks(properties, "childGrowthTick", 200);
            animalsStarve = readBoolean(properties, "animalsStarve", false);
            showUnhappyParticles = readBoolean(properties, "showUnhappyParticles", true);
            animalsSleep = readBoolean(properties, "animalsSleep", true);
            animalsCanAttackOthers = readBoolean(properties, "animalsCanAttackOthers", true);
            replaceVanillaWolves = readBoolean(properties, "replaceVanillaWolves", true);
            replaceVanillaOcelots = readBoolean(properties, "replaceVanillaOcelots", true);
            starvationTimer = readTicks(properties, "starvationTimer", 400);
            matingTimer = readTicks(properties, "matingTimer", 100);
            requireAnimalInteractionForAI = readBoolean(properties, "requireAnimalInteractionForAI", true);
            ambianceMode = readBoolean(properties, "ambianceMode", false);
            feedToBreed = readBoolean(properties, "feedToBreed", true);
            malesMateMultipleFemales = readBoolean(properties, "malesMateMultipleFemales", false);
            entityBreedingLimit = readTicks(properties, "entityBreedingLimit", 15);
            animalCapSearchRange = readTicks(properties, "animalCapSearchRange", 80);
            naturalSpawnLimitCows = readTicks(properties, "naturalSpawnLimitCows", 40);
            naturalSpawnLimitPigs = readTicks(properties, "naturalSpawnLimitPigs", 40);
            naturalSpawnLimitChickens = readTicks(properties, "naturalSpawnLimitChickens", 40);
            naturalSpawnLimitSheep = readTicks(properties, "naturalSpawnLimitSheep", 40);
            naturalSpawnLimitGoats = readTicks(properties, "naturalSpawnLimitGoats", 40);
            naturalSpawnLimitHorses = readTicks(properties, "naturalSpawnLimitHorses", 40);
            naturalSpawnLimitRabbits = readTicks(properties, "naturalSpawnLimitRabbits", 40);
            naturalSpawnLimitAmphibians = readTicks(properties, "naturalSpawnLimitAmphibians", 40);
            naturalSpawnLimitHamsters = readTicks(properties, "naturalSpawnLimitHamsters", 40);
            naturalSpawnLimitFerrets = readTicks(properties, "naturalSpawnLimitFerrets", 40);
            naturalSpawnLimitHedgehogs = readTicks(properties, "naturalSpawnLimitHedgehogs", 40);
            naturalSpawnLimitCats = readTicks(properties, "naturalSpawnLimitCats", 20);
            naturalSpawnLimitDogs = readTicks(properties, "naturalSpawnLimitDogs", 20);
            hiveSpawning = readBoolean(properties, "hiveSpawning", true);
            hiveSpawningFrequency = readClampedInt(properties, "hiveSpawningFrequency", 3, 0, 10);
            hiveWildHoneyRate = readTicks(properties, "hiveWildHoneyRate", 700);
            hivePlayermadeHoneyRate = readTicks(properties, "hivePlayermadeHoneyRate", 450);
            allowSeedDispenserPlacement = readBoolean(properties, "allowSeedDispenserPlacement", true);
            shiftSeedPlacement = readBoolean(properties, "shiftSeedPlacement", false);
            allowTroughAutomation = readBoolean(properties, "allowTroughAutomation", true);
            saltLickTick = readTicks(properties, "saltLickTick", 8000);
            saltLickMaxUses = readClampedInt(properties, "saltLickMaxUses", 200, 1, 1_000_000);
            laidTimer = readTicks(properties, "laidTimer", 2000);
            eggHatchChance = readTicks(properties, "eggHatchChance", 2);
            cheeseMaturityTime = readTicks(properties, "cheeseMaturityTime", 24_000);
            saltCreationAmount = readBoundedInt(properties, "saltCreationAmount", 16, 1, 64);
            woolRegrowthTimer = readTicks(properties, "woolRegrowthTimer", 8_000);
            hamsterWheelCapacity = readTicks(properties, "hamsterWheelCapacity", 200_000);
            hamsterWheelRFGeneration = readNonNegativeInt(properties, "hamsterWheelRFGeneration", 20);
            hamsterWheelUseTime = readTicks(properties, "hamsterWheelUseTime", 2_000);
            chickensDropEggs = readBoolean(properties, "chickensDropEggs", false);
            birdsDropFeathers = readBoolean(properties, "birdsDropFeathers", true);
            featherTimer = readFeatherTimer(properties, "featherTimer", 12_000);
            foodsGiveBonusEffects = readBoolean(properties, "foodsGiveBonusEffects", true);
            foodValueOverrides = readFoodValueOverrides(properties.getProperty("foodValueOverrides"));
            eatFoodAnytime = readBoolean(properties, "eatFoodAnytime", true);
            disableSaltCreation = readBoolean(properties, "disableSaltCreation", false);
            cowsMilkableAtSpawn = readBoolean(properties, "cowsMilkableAtSpawn", false);
            allowEggThrowing = readBoolean(properties, "allowEggThrowing", false);
            plantsRemovedAfterEating = readBoolean(properties, "plantsRemovedAfterEating", true);
            waterRemovedAfterDrinking = readBoolean(properties, "waterRemovedAfterDrinking", true);
        } catch (IOException | IllegalArgumentException e) {
            throw new IllegalStateException("Could not read legacy Animaniax config at " + path, e);
        }
    }

    private static int readTicks(Properties properties, String key, int fallback) {
        String value = properties.getProperty(key);
        if (value == null) return fallback;
        try {
            int ticks = Integer.parseInt(value.trim());
            if (ticks > 0 && ticks <= MAX_TICKS) return ticks;
        } catch (NumberFormatException ignored) {
        }
        LOGGER.warn("Ignoring invalid Animaniax config value {}={}; using {}", key, value, fallback);
        return fallback;
    }

    private static int readNonNegativeInt(Properties properties, String key, int fallback) {
        String value = properties.getProperty(key);
        if (value == null) return fallback;
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed >= 0 && parsed <= MAX_TICKS) return parsed;
        } catch (NumberFormatException ignored) {
        }
        LOGGER.warn("Ignoring invalid Animaniax config value {}={}; using {}", key, value, fallback);
        return fallback;
    }

    private static int readClampedInt(Properties properties, String key, int fallback, int min, int max) {
        String value = properties.getProperty(key);
        if (value == null) return fallback;
        try {
            return Math.max(min, Math.min(max, Integer.parseInt(value.trim())));
        } catch (NumberFormatException ignored) {
            LOGGER.warn("Ignoring invalid Animaniax config value {}={}; using {}", key, value, fallback);
            return fallback;
        }
    }

    private static int readBoundedInt(Properties properties, String key, int fallback, int min, int max) {
        String value = properties.getProperty(key);
        if (value == null) return fallback;
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed >= min && parsed <= max) return parsed;
        } catch (NumberFormatException ignored) {
        }
        LOGGER.warn("Ignoring invalid Animaniax config value {}={}; using {}", key, value, fallback);
        return fallback;
    }

    private static double readChance(Properties properties, String key, double fallback) {
        String value = properties.getProperty(key);
        if (value == null) return fallback;
        try {
            double chance = Double.parseDouble(value.trim());
            if (Double.isFinite(chance) && chance >= 0.0 && chance <= 1.0) return chance;
        } catch (NumberFormatException ignored) {
        }
        LOGGER.warn("Ignoring invalid Animaniax config value {}={}; using {}", key, value, fallback);
        return fallback;
    }

    private static int readFeatherTimer(Properties properties, String key, int fallback) {
        String value = properties.getProperty(key);
        if (value == null) return fallback;
        try {
            int ticks = Integer.parseInt(value.trim());
            if (ticks > 0 && ticks <= MAX_TICKS - 999) return ticks;
        } catch (NumberFormatException ignored) {
        }
        LOGGER.warn("Ignoring invalid Animaniax config value {}={}; using {}", key, value, fallback);
        return fallback;
    }

    private static boolean readBoolean(Properties properties, String key, boolean fallback) {
        String value = properties.getProperty(key);
        if (value == null) return fallback;
        if (value.trim().equalsIgnoreCase("true")) return true;
        if (value.trim().equalsIgnoreCase("false")) return false;
        LOGGER.warn("Ignoring invalid Animaniax config value {}={}; using {}", key, value, fallback);
        return fallback;
    }
}

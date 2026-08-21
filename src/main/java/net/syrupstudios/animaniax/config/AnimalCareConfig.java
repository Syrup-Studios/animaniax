package net.syrupstudios.animaniax.config;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AnimalCareConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("animaniax");
    private static final int MAX_TICKS = 2_147_483_000;
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
    public static boolean chickensDropEggs = false;
    public static boolean birdsDropFeathers = true;
    public static int featherTimer = 12_000;
    public static boolean foodsGiveBonusEffects = true;
    public static boolean cowsMilkableAtSpawn = false;
    public static boolean disableSaltCreation = false;
    public static boolean plantsRemovedAfterEating = true;
    public static boolean waterRemovedAfterDrinking = true;

    private AnimalCareConfig() {}

    public static void load(Path path) {
        try {
            Files.createDirectories(path.getParent());
            if (Files.notExists(path)) {
                Files.writeString(path, "# Animal care and reproduction settings\n"
                        + "gestationTimer=20000\n"
                        + "# Chance for each additional offspring; one offspring is always produced, up to 64 total.\n"
                        + "birthMultipleChance=0.1\n"
                        + "# Chance of losing the litter at term when both hunger and thirst are low.\n"
                        + "animalLossChance=0.0\n"
                        + "feedTimer=12000\n"
                        + "waterTimer=12000\n"
                        + "# Goal checks between food, water, and mud searches.\n"
                        + "ticksBetweenAIFirings=100\n"
                        + "# Range for food, water, and salt lick block searches. Clamped to 1..64.\n"
                        + "aiBlockSearchRange=16\n"
                        + "# Growth interval in ticks. The timer advances during neglect and sleep, but growth applies only when cared for and awake.\n"
                        + "childGrowthTick=200\n"
                        + "# Animals take starvation damage when both hunger and thirst are low.\n"
                        + "animalsStarve=false\n"
                        + "# Show smoke when an animal is hungry and thirsty.\n"
                        + "showUnhappyParticles=true\n"
                        + "# Animals sleep at night when they can reach straw or grass.\n"
                        + "animalsSleep=true\n"
                        + "# Allows bull retaliation, sparring, and proactive animal hunting.\n"
                        + "animalsCanAttackOthers=true\n"
                        + "# Prevent natural vanilla wolf spawns.\n"
                        + "replaceVanillaWolves=true\n"
                        + "# Prevent natural vanilla ocelot spawns.\n"
                        + "replaceVanillaOcelots=true\n"
                        + "# Ticks between starvation damage.\n"
                        + "starvationTimer=400\n"
                        + "matingTimer=100\n"
                        + "requireAnimalInteractionForAI=true\n"
                        + "# Animals do not have to eat or drink.\n"
                        + "ambianceMode=false\n"
                        + "feedToBreed=true\n"
                        + "malesMateMultipleFemales=false\n"
                        + "entityBreedingLimit=15\n"
                        + "animalCapSearchRange=80\n"
                        + "# Threshold used to limit nearby natural spawns for each population group.\n"
                        + "naturalSpawnLimitCows=40\n"
                        + "naturalSpawnLimitPigs=40\n"
                        + "naturalSpawnLimitChickens=40\n"
                        + "naturalSpawnLimitSheep=40\n"
                        + "naturalSpawnLimitGoats=40\n"
                        + "naturalSpawnLimitHorses=40\n"
                        + "naturalSpawnLimitRabbits=40\n"
                        + "naturalSpawnLimitAmphibians=40\n"
                        + "naturalSpawnLimitHamsters=40\n"
                        + "naturalSpawnLimitFerrets=40\n"
                        + "naturalSpawnLimitHedgehogs=40\n"
                        + "naturalSpawnLimitCats=20\n"
                        + "naturalSpawnLimitDogs=20\n"
                        + "# Allow dispensers to place seed piles.\n"
                        + "allowSeedDispenserPlacement=true\n"
                        + "# Require sneaking to place seed piles with seeds.\n"
                        + "shiftSeedPlacement=false\n"
                        + "# Allow hoppers and pipes to insert or remove trough feed.\n"
                        + "allowTroughAutomation=true\n"
                        + "saltLickTick=8000\n"
                        + "# Maximum uses for a new salt lick. Clamped to 1..1000000.\n"
                        + "saltLickMaxUses=200\n"
                        + "# Ticks between eggs laid in nests.\n"
                        + "laidTimer=2000\n"
                        + "# Chance of hatching an egg, as 1 in N.\n"
                        + "eggHatchChance=2\n"
                        + "# Ticks that cheese takes to mature.\n"
                        + "cheeseMaturityTime=24000\n"
                        + "# Salt items made by curing water in a cheese mold.\n"
                        + "saltCreationAmount=16\n"
                        + "# Ticks before sheep wool regrows after shearing.\n"
                        + "woolRegrowthTimer=8000\n"
                        + "# Chickens can drop eggs without using a nest.\n"
                        + "chickensDropEggs=false\n"
                        + "# Birds shed feathers over time.\n"
                        + "birdsDropFeathers=true\n"
                        + "featherTimer=12000\n"
                        + "# Apply Animania bonus effects when food is consumed.\n"
                        + "foodsGiveBonusEffects=true\n"
                        + "cowsMilkableAtSpawn=false\n"
                        + "# Prevent players from making salt in cheese molds with water.\n"
                        + "disableSaltCreation=false\n"
                        + "plantsRemovedAfterEating=true\n"
                        + "waterRemovedAfterDrinking=true\n",
                        StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            }
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
            chickensDropEggs = readBoolean(properties, "chickensDropEggs", false);
            birdsDropFeathers = readBoolean(properties, "birdsDropFeathers", true);
            featherTimer = readFeatherTimer(properties, "featherTimer", 12_000);
            foodsGiveBonusEffects = readBoolean(properties, "foodsGiveBonusEffects", true);
            cowsMilkableAtSpawn = readBoolean(properties, "cowsMilkableAtSpawn", false);
            disableSaltCreation = readBoolean(properties, "disableSaltCreation", false);
            plantsRemovedAfterEating = readBoolean(properties, "plantsRemovedAfterEating", true);
            waterRemovedAfterDrinking = readBoolean(properties, "waterRemovedAfterDrinking", true);
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.error("Could not read Animaniax config at {}; defaults will be used", path, e);
            gestationTimer = 20_000;
            birthMultipleChance = 0.1;
            animalLossChance = 0.0;
            feedTimer = 12_000;
            waterTimer = 12_000;
            ticksBetweenAIFirings = 100;
            aiBlockSearchRange = 16;
            childGrowthTick = 200;
            animalsStarve = false;
            showUnhappyParticles = true;
            animalsSleep = true;
            animalsCanAttackOthers = true;
            replaceVanillaWolves = true;
            replaceVanillaOcelots = true;
            starvationTimer = 400;
            matingTimer = 100;
            requireAnimalInteractionForAI = true;
            ambianceMode = false;
            feedToBreed = true;
            malesMateMultipleFemales = false;
            entityBreedingLimit = 15;
            animalCapSearchRange = 80;
            naturalSpawnLimitCows = 40;
            naturalSpawnLimitPigs = 40;
            naturalSpawnLimitChickens = 40;
            naturalSpawnLimitSheep = 40;
            naturalSpawnLimitGoats = 40;
            naturalSpawnLimitHorses = 40;
            naturalSpawnLimitRabbits = 40;
            naturalSpawnLimitAmphibians = 40;
            naturalSpawnLimitHamsters = 40;
            naturalSpawnLimitFerrets = 40;
            naturalSpawnLimitHedgehogs = 40;
            naturalSpawnLimitCats = 20;
            naturalSpawnLimitDogs = 20;
            allowSeedDispenserPlacement = true;
            shiftSeedPlacement = false;
            allowTroughAutomation = true;
            saltLickTick = 8000;
            saltLickMaxUses = 200;
            laidTimer = 2000;
            eggHatchChance = 2;
            cheeseMaturityTime = 24_000;
            saltCreationAmount = 16;
            woolRegrowthTimer = 8_000;
            chickensDropEggs = false;
            birdsDropFeathers = true;
            featherTimer = 12_000;
            foodsGiveBonusEffects = true;
            cowsMilkableAtSpawn = false;
            disableSaltCreation = false;
            plantsRemovedAfterEating = true;
            waterRemovedAfterDrinking = true;
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

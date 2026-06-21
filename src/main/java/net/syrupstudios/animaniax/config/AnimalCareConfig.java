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
    public static int starvationTimer = 400;
    public static int matingTimer = 100;
    public static boolean requireAnimalInteractionForAI = true;
    public static boolean ambianceMode = false;
    public static boolean feedToBreed = true;
    public static boolean malesMateMultipleFemales = false;
    public static int entityBreedingLimit = 15;
    public static int animalCapSearchRange = 80;
    public static boolean cowsMilkableAtSpawn = false;
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
                        + "cowsMilkableAtSpawn=false\n"
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
            starvationTimer = readTicks(properties, "starvationTimer", 400);
            matingTimer = readTicks(properties, "matingTimer", 100);
            requireAnimalInteractionForAI = readBoolean(properties, "requireAnimalInteractionForAI", true);
            ambianceMode = readBoolean(properties, "ambianceMode", false);
            feedToBreed = readBoolean(properties, "feedToBreed", true);
            malesMateMultipleFemales = readBoolean(properties, "malesMateMultipleFemales", false);
            entityBreedingLimit = readTicks(properties, "entityBreedingLimit", 15);
            animalCapSearchRange = readTicks(properties, "animalCapSearchRange", 80);
            cowsMilkableAtSpawn = readBoolean(properties, "cowsMilkableAtSpawn", false);
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
            starvationTimer = 400;
            matingTimer = 100;
            requireAnimalInteractionForAI = true;
            ambianceMode = false;
            feedToBreed = true;
            malesMateMultipleFemales = false;
            entityBreedingLimit = 15;
            animalCapSearchRange = 80;
            cowsMilkableAtSpawn = false;
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

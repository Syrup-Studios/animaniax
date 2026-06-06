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
    public static int feedTimer = 12_000;
    public static int waterTimer = 12_000;
    public static int ticksBetweenAIFirings = 100;
    public static int aiBlockSearchRange = 16;
    public static boolean animalsStarve = false;
    public static boolean showUnhappyParticles = true;
    public static int starvationTimer = 400;
    public static boolean requireAnimalInteractionForAI = true;
    public static boolean ambianceMode = false;
    public static boolean plantsRemovedAfterEating = true;
    public static boolean waterRemovedAfterDrinking = true;

    private AnimalCareConfig() {}

    public static void load(Path path) {
        try {
            Files.createDirectories(path.getParent());
            if (Files.notExists(path)) {
                Files.writeString(path, "# Animal care and reproduction settings\n"
                        + "feedTimer=12000\n"
                        + "waterTimer=12000\n"
                        + "# Goal checks between food, water, and mud searches.\n"
                        + "ticksBetweenAIFirings=100\n"
                        + "# Range for food, water, and salt lick block searches. Clamped to 1..64.\n"
                        + "aiBlockSearchRange=16\n"
                        + "# Animals take starvation damage when both hunger and thirst are low.\n"
                        + "animalsStarve=false\n"
                        + "# Show smoke when an animal is hungry and thirsty.\n"
                        + "showUnhappyParticles=true\n"
                        + "# Ticks between starvation damage.\n"
                        + "starvationTimer=400\n"
                        + "requireAnimalInteractionForAI=true\n"
                        + "# Animals do not have to eat or drink.\n"
                        + "ambianceMode=false\n"
                        + "plantsRemovedAfterEating=true\n"
                        + "waterRemovedAfterDrinking=true\n",
                        StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            }
            Properties properties = new Properties();
            try (Reader reader = Files.newBufferedReader(path)) {
                properties.load(reader);
            }
            feedTimer = readTicks(properties, "feedTimer", 12_000);
            waterTimer = readTicks(properties, "waterTimer", 12_000);
            ticksBetweenAIFirings = readTicks(properties, "ticksBetweenAIFirings", 100);
            aiBlockSearchRange = readClampedInt(properties, "aiBlockSearchRange", 16, 1, 64);
            animalsStarve = readBoolean(properties, "animalsStarve", false);
            showUnhappyParticles = readBoolean(properties, "showUnhappyParticles", true);
            starvationTimer = readTicks(properties, "starvationTimer", 400);
            requireAnimalInteractionForAI = readBoolean(properties, "requireAnimalInteractionForAI", true);
            ambianceMode = readBoolean(properties, "ambianceMode", false);
            plantsRemovedAfterEating = readBoolean(properties, "plantsRemovedAfterEating", true);
            waterRemovedAfterDrinking = readBoolean(properties, "waterRemovedAfterDrinking", true);
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.error("Could not read Animaniax config at {}; defaults will be used", path, e);
            feedTimer = 12_000;
            waterTimer = 12_000;
            ticksBetweenAIFirings = 100;
            aiBlockSearchRange = 16;
            animalsStarve = false;
            showUnhappyParticles = true;
            starvationTimer = 400;
            requireAnimalInteractionForAI = true;
            ambianceMode = false;
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

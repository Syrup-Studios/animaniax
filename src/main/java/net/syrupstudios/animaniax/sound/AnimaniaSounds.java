package net.syrupstudios.animaniax.sound;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.syrupstudios.animaniax.entity.AnimalReproductionState;

/**
 * Custom animal and vehicle sound events registered on both Fabric and NeoForge.
 */
public final class AnimaniaSounds {
    private static final Map<String, SoundEvent> ALL = new LinkedHashMap<>();

    public static final SoundEvent CHICKEN_AMBIENT = create("entity.chicken.ambient");
    public static final SoundEvent ROOSTER_CROW = create("entity.rooster.crow");
    public static final SoundEvent CHICKEN_HURT = create("entity.chicken.hurt");
    public static final SoundEvent CHICKEN_DEATH = create("entity.chicken.death");

    public static final SoundEvent COW_AMBIENT = create("entity.cow.ambient");
    public static final SoundEvent COW_HURT = create("entity.cow.hurt");
    public static final SoundEvent COW_DEATH = create("entity.cow.death");
    public static final SoundEvent COW_EAT = create("entity.cow.eat");
    public static final SoundEvent BULL_AMBIENT = create("entity.bull.ambient");
    public static final SoundEvent BULL_ANGRY = create("entity.bull.angry");
    public static final SoundEvent CALF_AMBIENT = create("entity.calf.ambient");
    public static final SoundEvent CALF_HURT = create("entity.calf.hurt");

    public static final SoundEvent GOAT_AMBIENT = create("entity.goat.ambient");
    public static final SoundEvent GOAT_HURT = create("entity.goat.hurt");
    public static final SoundEvent KID_AMBIENT = create("entity.kid.ambient");
    public static final SoundEvent KID_HURT = create("entity.kid.hurt");

    public static final SoundEvent DRAFT_HORSE_AMBIENT = create("entity.draft_horse.ambient");
    public static final SoundEvent DRAFT_HORSE_HURT = create("entity.draft_horse.hurt");

    public static final SoundEvent PIG_AMBIENT = create("entity.pig.ambient");
    public static final SoundEvent HOG_AMBIENT = create("entity.hog.ambient");
    public static final SoundEvent PIG_HURT = create("entity.pig.hurt");
    public static final SoundEvent PIGLET_AMBIENT = create("entity.piglet.ambient");
    public static final SoundEvent PIGLET_HURT = create("entity.piglet.hurt");

    public static final SoundEvent SHEEP_AMBIENT = create("entity.sheep.ambient");
    public static final SoundEvent SHEEP_HURT = create("entity.sheep.hurt");
    public static final SoundEvent LAMB_AMBIENT = create("entity.lamb.ambient");
    public static final SoundEvent LAMB_HURT = create("entity.lamb.hurt");

    public static final SoundEvent RABBIT_AMBIENT = create("entity.rabbit.ambient");
    public static final SoundEvent RABBIT_HURT = create("entity.rabbit.hurt");

    public static final SoundEvent PEAFOWL_AMBIENT = create("entity.peafowl.ambient");
    public static final SoundEvent PEAFOWL_HURT = create("entity.peafowl.hurt");

    public static final SoundEvent HAMSTER_AMBIENT = create("entity.hamster.ambient");
    public static final SoundEvent HAMSTER_EAT = create("entity.hamster.eat");
    public static final SoundEvent HAMSTER_HURT = create("entity.hamster.hurt");
    public static final SoundEvent HAMSTER_DEATH = create("entity.hamster.death");

    public static final SoundEvent FERRET_AMBIENT = create("entity.ferret.ambient");
    public static final SoundEvent FERRET_HURT = create("entity.ferret.hurt");

    public static final SoundEvent HEDGEHOG_AMBIENT = create("entity.hedgehog.ambient");
    public static final SoundEvent HEDGEHOG_HURT = create("entity.hedgehog.hurt");

    public static final SoundEvent COMMON_FROG_AMBIENT = create("entity.common_frog.ambient");
    public static final SoundEvent DART_FROG_AMBIENT = create("entity.dart_frog.ambient");
    public static final SoundEvent TOAD_AMBIENT = create("entity.toad.ambient");

    public static final SoundEvent VEHICLE_HITCH = create("entity.vehicle.hitch");
    public static final SoundEvent VEHICLE_UNHITCH = create("entity.vehicle.unhitch");

    private AnimaniaSounds() {}

    private static SoundEvent create(String path) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("animaniax", path);
        SoundEvent event = SoundEvent.createVariableRangeEvent(id);
        ALL.put(path, event);
        return event;
    }

    public static Map<String, SoundEvent> all() {
        return Collections.unmodifiableMap(ALL);
    }

    public static void registerFabric() {
        for (Map.Entry<String, SoundEvent> entry : ALL.entrySet()) {
            Registry.register(BuiltInRegistries.SOUND_EVENT,
                    ResourceLocation.fromNamespaceAndPath("animaniax", entry.getKey()), entry.getValue());
        }
    }

    /** Selects the baby or adult sound, or sex-specific adult sound when available. */
    public static SoundEvent pickBySex(Object entity, boolean baby, SoundEvent babySound,
            SoundEvent femaleSound, SoundEvent maleSound) {
        if (baby) return babySound;
        boolean female = !(entity instanceof AnimalReproductionState sex) || sex.animaniax$isFemale();
        return female ? femaleSound : maleSound;
    }
}

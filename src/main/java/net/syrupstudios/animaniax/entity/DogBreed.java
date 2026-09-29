package net.syrupstudios.animaniax.entity;

import java.util.Locale;

public enum DogBreed {
    BLOOD_HOUND, CHIHUAHUA, COLLIE, CORGI, DACHSHUND, FOX, GERMAN_SHEPHERD,
    GREAT_DANE, GREYHOUND, HUSKY, LABRADOR, POMERANIAN, POODLE, PUG, WOLF;

    public String id() { return name().toLowerCase(Locale.ROOT); }
    public static DogBreed byId(String id) {
        for (DogBreed breed : values()) if (breed.id().equals(id)) return breed;
        return WOLF;
    }
}

package net.syrupstudios.animaniax.entity;

import net.minecraft.util.RandomSource;

public enum CatBreed {
    AMERICAN_SHORTHAIR("american_shorthair"), ASIATIC("asiatic"), EXOTIC("exotic"),
    NORWEGIAN("norwegian"), OCELOT("ocelot"), RAGDOLL("ragdoll"), SIAMESE("siamese"), TABBY("tabby");

    private final String id;
    CatBreed(String id) { this.id = id; }
    public String id() { return id; }
    public static CatBreed byId(String id) {
        for (CatBreed breed : values()) if (breed.id.equals(id)) return breed;
        return TABBY;
    }
    public static CatBreed random(RandomSource random) { return values()[random.nextInt(values().length)]; }
}

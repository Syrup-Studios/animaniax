package net.syrupstudios.animaniax.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public final class AnimalSpawnEggContent {
    public static final AnimalSpawnEggItem ITEM = new AnimalSpawnEggItem();

    private AnimalSpawnEggContent() {}

    public static void registerFabric() {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", "animal_spawn_egg"), ITEM);
    }
}

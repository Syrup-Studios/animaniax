package net.syrupstudios.animaniax.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.Item;

public final class BrownEggContent {
    public static final Item BROWN_EGG = new EggItem(new Item.Properties().stacksTo(16));

    private BrownEggContent() {}

    public static boolean isBrownBreed(String breed) {
        return "Rhode Island Red".equals(breed) || "Wyandotte".equals(breed);
    }

    public static void registerFabric() {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", "brown_egg"), BROWN_EGG);
    }
}

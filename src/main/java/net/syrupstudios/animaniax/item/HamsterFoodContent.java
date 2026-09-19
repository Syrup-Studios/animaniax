package net.syrupstudios.animaniax.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class HamsterFoodContent {
    public static final Item HAMSTER_FOOD = new Item(new Item.Properties());

    private HamsterFoodContent() {}

    public static void registerFabric() {
        Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath("animaniax", "hamster_food"), HAMSTER_FOOD);
    }
}

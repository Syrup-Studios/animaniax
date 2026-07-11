package net.syrupstudios.animaniax.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class AnimalMilkContent {
    public static final Item SHEEP_MILK_BUCKET = new AnimalMilkBucketItem();
    public static final Item GOAT_MILK_BUCKET = new AnimalMilkBucketItem();
    public static final Item FRIESIAN_MILK_BUCKET = new AnimalMilkBucketItem();
    public static final Item HOLSTEIN_MILK_BUCKET = new AnimalMilkBucketItem();
    public static final Item JERSEY_MILK_BUCKET = new AnimalMilkBucketItem();
    public static final Item MILK_BOTTLE = new MilkBottleItem();

    private AnimalMilkContent() {}

    public static void registerFabric() {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", "sheep_milk_bucket"), SHEEP_MILK_BUCKET);
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", "goat_milk_bucket"), GOAT_MILK_BUCKET);
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", "friesian_milk_bucket"), FRIESIAN_MILK_BUCKET);
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", "holstein_milk_bucket"), HOLSTEIN_MILK_BUCKET);
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", "jersey_milk_bucket"), JERSEY_MILK_BUCKET);
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", "milk_bottle"), MILK_BOTTLE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
                ResourceLocation.fromNamespaceAndPath("animaniax", "milk_conversion"), MilkConversionRecipe.SERIALIZER);
    }
}

package net.syrupstudios.animaniax.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public final class SlopContent {
    public static final Item SLOP_BUCKET = new SlopBucketItem(new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET));

    private SlopContent() {}

    public static void registerFabric() {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", "slop_bucket"), SLOP_BUCKET);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath("animaniax", "slop"), SlopRecipe.SERIALIZER);
    }
}

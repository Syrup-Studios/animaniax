package net.syrupstudios.animaniax.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class ManualContent {
    public static final Item MANUAL = new Item(new Item.Properties().stacksTo(1));

    private ManualContent() {}

    public static void registerFabric() {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", "manual"), MANUAL);
    }
}

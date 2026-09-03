package net.syrupstudios.animaniax.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class RidingCropContent {
    public static final Item RIDING_CROP = new RidingCropItem(new Item.Properties().stacksTo(1).durability(100));

    private RidingCropContent() {}

    public static void registerFabric() {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", "riding_crop"), RIDING_CROP);
    }
}

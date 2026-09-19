package net.syrupstudios.animaniax.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class HamsterCarryContent {
    public static final Item CARRIED_HAMSTER = new HamsterCarryItem(new Item.Properties().stacksTo(1));

    private HamsterCarryContent() { }

    public static void registerFabric() {
        //? if fabric {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", "carried_hamster"), CARRIED_HAMSTER);
        //?}
    }
}

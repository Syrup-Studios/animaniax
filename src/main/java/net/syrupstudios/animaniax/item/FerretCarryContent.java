package net.syrupstudios.animaniax.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class FerretCarryContent {
    public static final Item CARRIED_FERRET = new FerretCarryItem(new Item.Properties().stacksTo(1));

    private FerretCarryContent() { }

    public static void registerFabric() {
        //? if fabric {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", "carried_ferret"), CARRIED_FERRET);
        //?}
    }
}

package net.syrupstudios.animaniax.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class HedgehogCarryContent {
    public static final Item CARRIED_HEDGEHOG = new HedgehogCarryItem(new Item.Properties().stacksTo(1));

    private HedgehogCarryContent() { }

    public static void registerFabric() {
        //? if fabric {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", "carried_hedgehog"), CARRIED_HEDGEHOG);
        //?}
    }
}

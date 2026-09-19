package net.syrupstudios.animaniax.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.DyeColor;

public final class HamsterBallContent {
    public static final Item CLEAR = new Item(new Item.Properties().stacksTo(1));
    public static final Item[] COLORED = new Item[DyeColor.values().length];

    static {
        for (DyeColor color : DyeColor.values()) COLORED[color.getId()] = new Item(new Item.Properties().stacksTo(1));
    }

    private HamsterBallContent() { }

    public static int colorOf(Item item) {
        if (item == CLEAR) return -1;
        for (DyeColor color : DyeColor.values()) if (COLORED[color.getId()] == item) return color.getId();
        return -2;
    }

    public static Item itemFor(int color) { return color < 0 ? CLEAR : COLORED[color]; }

    public static void registerFabric() {
        Registry.register(BuiltInRegistries.ITEM, id("hamster_ball"), CLEAR);
        for (DyeColor color : DyeColor.values()) Registry.register(BuiltInRegistries.ITEM, id("hamster_ball_" + color.getName()), COLORED[color.getId()]);
    }

    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("animaniax", path); }
}

package net.syrupstudios.animaniax.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.core.Holder;

public final class CheeseWedgeContent {
    public static final Item KNIFE = new KnifeItem(new Item.Properties().durability(100));
    public static final Item FRIESIAN_WEDGE = wedge(MobEffects.HEAL, 1, 1);
    public static final Item HOLSTEIN_WEDGE = wedge(MobEffects.HEAL, 1, 1);
    public static final Item JERSEY_WEDGE = wedge(MobEffects.HEAL, 1, 1);
    public static final Item GOAT_WEDGE = wedge(MobEffects.DAMAGE_RESISTANCE, 1200, 0);
    public static final Item SHEEP_WEDGE = wedge(MobEffects.HEAL, 1, 0);

    private static Item wedge(Holder<net.minecraft.world.effect.MobEffect> effect, int duration, int amplifier) {
        FoodProperties food = new FoodProperties.Builder().nutrition(3).saturationModifier(0.9F).build();
        return new BonusEffectFoodItem(new Item.Properties().food(food),
                new MobEffectInstance(effect, duration, amplifier));
    }

    private CheeseWedgeContent() {}

    public static void registerFabric() {
        register("carving_knife", KNIFE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath("animaniax", "friesian_cheese_cutting"), CheeseCuttingRecipe.FRIESIAN_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath("animaniax", "holstein_cheese_cutting"), CheeseCuttingRecipe.HOLSTEIN_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath("animaniax", "jersey_cheese_cutting"), CheeseCuttingRecipe.JERSEY_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath("animaniax", "goat_cheese_cutting"), CheeseCuttingRecipe.GOAT_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath("animaniax", "sheep_cheese_cutting"), CheeseCuttingRecipe.SHEEP_SERIALIZER);
        register("friesian_cheese_wedge", FRIESIAN_WEDGE);
        register("holstein_cheese_wedge", HOLSTEIN_WEDGE);
        register("jersey_cheese_wedge", JERSEY_WEDGE);
        register("goat_cheese_wedge", GOAT_WEDGE);
        register("sheep_cheese_wedge", SHEEP_WEDGE);
    }

    private static void register(String path, Item item) {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", path), item);
    }
}

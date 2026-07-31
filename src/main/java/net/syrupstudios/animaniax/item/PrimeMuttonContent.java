package net.syrupstudios.animaniax.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public final class PrimeMuttonContent {
    public static final Item RAW_PRIME_MUTTON = new BonusEffectFoodItem(new Item.Properties().food(
            new FoodProperties.Builder().nutrition(1).saturationModifier(1.0F)
                    .build()), new MobEffectInstance(MobEffects.CONFUSION, 200, 3));
    public static final Item COOKED_PRIME_MUTTON = new BonusEffectFoodItem(new Item.Properties().food(
            new FoodProperties.Builder().nutrition(12).saturationModifier(0.5F)
                    .build()), new MobEffectInstance(MobEffects.HEAL, 1, 0));

    private PrimeMuttonContent() {}

    public static void registerFabric() {
        register("raw_prime_mutton", RAW_PRIME_MUTTON);
        register("cooked_prime_mutton", COOKED_PRIME_MUTTON);
    }

    private static void register(String path, Item item) {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", path), item);
    }
}

package net.syrupstudios.animaniax.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public final class FrogLegContent {
    public static final Item RAW_FROG_LEGS = new BonusEffectFoodItem(new Item.Properties().food(
            new FoodProperties.Builder().nutrition(1).saturationModifier(1.0F)
                    .build()), new MobEffectInstance(MobEffects.CONFUSION, 200, 3));
    public static final Item COOKED_FROG_LEGS = new BonusEffectFoodItem(new Item.Properties().food(
            new FoodProperties.Builder().nutrition(7).saturationModifier(0.5F)
                    .build()), new MobEffectInstance(MobEffects.JUMP, 1200, 2));

    private FrogLegContent() {}

    public static void registerFabric() {
        register("raw_frog_legs", RAW_FROG_LEGS);
        register("cooked_frog_legs", COOKED_FROG_LEGS);
    }

    private static void register(String path, Item item) {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", path), item);
    }
}

package net.syrupstudios.animaniax.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public final class GoatMeatContent {
    public static final Item RAW_CHEVON = raw();
    public static final Item COOKED_CHEVON = cooked(5, 600, 0);
    public static final Item RAW_PRIME_CHEVON = raw();
    public static final Item COOKED_PRIME_CHEVON = cooked(10, 1200, 1);

    private GoatMeatContent() {}

    public static void registerFabric() {
        register("raw_chevon", RAW_CHEVON);
        register("cooked_chevon", COOKED_CHEVON);
        register("raw_prime_chevon", RAW_PRIME_CHEVON);
        register("cooked_prime_chevon", COOKED_PRIME_CHEVON);
    }

    private static Item raw() {
        return new BonusEffectFoodItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(1)
                .saturationModifier(1.0F).build()), new MobEffectInstance(MobEffects.CONFUSION, 200, 3));
    }

    private static Item cooked(int nutrition, int duration, int amplifier) {
        return new BonusEffectFoodItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(nutrition)
                .saturationModifier(0.5F).build()), new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, amplifier));
    }

    private static void register(String path, Item item) {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", path), item);
    }
}

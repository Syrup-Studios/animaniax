package net.syrupstudios.animaniax.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public final class PrimeMeatContent {
    public static final Item RAW_PRIME_BEEF = raw();
    public static final Item COOKED_PRIME_BEEF = food(12, 0.5F, MobEffects.HEAL, 1, 0);
    public static final Item RAW_PRIME_STEAK = raw();
    public static final Item COOKED_PRIME_STEAK = food(8, 0.5F, MobEffects.HEAL, 1, 0);
    public static final Item RAW_PRIME_PORK = raw();
    public static final Item COOKED_PRIME_PORK = food(12, 0.5F, MobEffects.ABSORPTION, 3000, 0);
    public static final Item RAW_PRIME_BACON = raw();
    public static final Item COOKED_PRIME_BACON = food(8, 0.5F, MobEffects.ABSORPTION, 1800, 0);
    public static final Item RAW_HORSE = raw();
    public static final Item COOKED_HORSE = food(12, 0.5F, MobEffects.DAMAGE_BOOST, 600, 0);

    private PrimeMeatContent() {}

    private static Item raw() { return food(1, 1.0F, MobEffects.CONFUSION, 200, 3); }
    private static Item food(int nutrition, float saturation, net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect, int duration, int amplifier) {
        return new BonusEffectFoodItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(nutrition)
                .saturationModifier(saturation).build()), new MobEffectInstance(effect, duration, amplifier));
    }
    public static void registerFabric() {
        register("raw_prime_beef", RAW_PRIME_BEEF); register("cooked_prime_beef", COOKED_PRIME_BEEF);
        register("raw_prime_steak", RAW_PRIME_STEAK); register("cooked_prime_steak", COOKED_PRIME_STEAK);
        register("raw_prime_pork", RAW_PRIME_PORK); register("cooked_prime_pork", COOKED_PRIME_PORK);
        register("raw_prime_bacon", RAW_PRIME_BACON); register("cooked_prime_bacon", COOKED_PRIME_BACON);
        register("raw_horse", RAW_HORSE); register("cooked_horse", COOKED_HORSE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath("animaniax", "prime_beef_cutting"), PrimeMeatCuttingRecipe.BEEF_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath("animaniax", "prime_pork_cutting"), PrimeMeatCuttingRecipe.PORK_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath("animaniax", "straw_cutting"), PrimeMeatCuttingRecipe.STRAW_SERIALIZER);
    }
    private static void register(String path, Item item) {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", path), item);
    }
}

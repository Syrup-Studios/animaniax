package net.syrupstudios.animaniax.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public final class OmeletteContent {
    public static final Item PLAIN = food(5, 0.6F);
    public static final Item CHEESE = food(5, 0.7F, new MobEffectInstance(MobEffects.HEAL, 1, 1));
    public static final Item BACON = food(5, 0.7F, new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 0));
    public static final Item TRUFFLE = food(5, 0.8F, new MobEffectInstance(MobEffects.REGENERATION, 600, 1));
    public static final Item SUPER = food(5, 0.9F,
            new MobEffectInstance(MobEffects.REGENERATION, 600, 1),
            new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 0),
            new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 1));

    private OmeletteContent() {}

    private static Item food(int nutrition, float saturation, MobEffectInstance... effects) {
        FoodProperties food = new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build();
        Item.Properties properties = new Item.Properties().food(food);
        return new BonusEffectFoodItem(properties, effects);
    }

    public static void registerFabric() {
        register("plain_omelette", PLAIN);
        register("cheese_omelette", CHEESE);
        register("bacon_omelette", BACON);
        register("truffle_omelette", TRUFFLE);
        register("super_omelette", SUPER);
    }

    private static void register(String path, Item item) {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", path), item);
    }
}

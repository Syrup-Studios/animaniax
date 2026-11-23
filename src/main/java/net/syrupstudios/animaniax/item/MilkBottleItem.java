package net.syrupstudios.animaniax.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public final class MilkBottleItem extends Item {
    public MilkBottleItem() {
        super(new Item.Properties().stacksTo(4).food(new net.minecraft.world.food.FoodProperties.Builder()
                .nutrition(4).saturationModifier(1.0F).alwaysEdible()
                .usingConvertsTo(Items.GLASS_BOTTLE).build()));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player) player.removeAllEffects();
        FoodProperties original = stack.get(DataComponents.FOOD);
        FoodProperties adjusted = AnimalCareConfig.foodProperties(stack);
        if (level.isClientSide || original == null || adjusted == original)
            return super.finishUsingItem(stack, level, entity);
        stack.set(DataComponents.FOOD, adjusted);
        try {
            return super.finishUsingItem(stack, level, entity);
        } finally {
            if (!stack.isEmpty()) stack.set(DataComponents.FOOD, original);
        }
    }
}

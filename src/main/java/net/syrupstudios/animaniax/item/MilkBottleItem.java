package net.syrupstudios.animaniax.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
        return super.finishUsingItem(stack, level, entity);
    }
}

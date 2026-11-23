package net.syrupstudios.animaniax.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.syrupstudios.animaniax.config.AnimalCareConfig;

public final class BonusEffectFoodItem extends Item {
    private final MobEffectInstance[] bonusEffects;

    public BonusEffectFoodItem(Properties properties, MobEffectInstance... bonusEffects) {
        super(properties);
        this.bonusEffects = bonusEffects.clone();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide && !AnimalCareConfig.eatFoodAnytime && !player.canEat(false)) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        FoodProperties original = stack.get(DataComponents.FOOD);
        FoodProperties adjusted = AnimalCareConfig.foodProperties(stack);
        ItemStack result;
        if (!level.isClientSide && original != null && adjusted != original) {
            stack.set(DataComponents.FOOD, adjusted);
            try {
                result = super.finishUsingItem(stack, level, entity);
            } finally {
                if (!stack.isEmpty()) stack.set(DataComponents.FOOD, original);
            }
        } else {
            result = super.finishUsingItem(stack, level, entity);
        }
        if (!level.isClientSide && entity instanceof Player player && AnimalCareConfig.foodsGiveBonusEffects) {
            for (MobEffectInstance effect : bonusEffects) {
                player.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration(),
                        effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
            }
        }
        return result;
    }
}

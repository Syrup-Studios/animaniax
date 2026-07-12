package net.syrupstudios.animaniax.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide && entity instanceof Player player && AnimalCareConfig.foodsGiveBonusEffects) {
            for (MobEffectInstance effect : bonusEffects) {
                player.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration(),
                        effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
            }
        }
        return result;
    }
}

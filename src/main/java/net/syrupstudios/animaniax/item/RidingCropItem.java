package net.syrupstudios.animaniax.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.syrupstudios.animaniax.entity.DraftHorseEntity;

public final class RidingCropItem extends Item {
    public RidingCropItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getVehicle() instanceof DraftHorseEntity horse && stack.getDamageValue() < stack.getMaxDamage()) {
            if (level.isClientSide) return InteractionResultHolder.sidedSuccess(stack, true);
            if (horse.animaniax$boost()) {
                if (!player.getAbilities().instabuild) {
                    stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND
                            ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                }
                return InteractionResultHolder.sidedSuccess(stack, false);
            }
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.pass(stack);
    }
}

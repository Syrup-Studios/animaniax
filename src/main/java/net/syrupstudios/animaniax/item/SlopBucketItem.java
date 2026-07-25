package net.syrupstudios.animaniax.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.syrupstudios.animaniax.entity.AnimalCareState;
import net.syrupstudios.animaniax.entity.AnimalReproductionState;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.AnimalSterilizationState;

public final class SlopBucketItem extends Item {
    public SlopBucketItem(Properties properties) { super(properties); }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof Pig pig)) return InteractionResult.PASS;
        if (pig.isSleeping() || ((AnimalSleepState) (Object) pig).animaniax$isSleeping())
            return InteractionResult.sidedSuccess(player.level().isClientSide);
        if (!player.level().isClientSide) {
            ((AnimalCareState) pig).animaniax$eatSlop();
            if (pig.canFallInLove()
                    && !((AnimalSterilizationState) pig).animaniax$isSterilized()
                    && !((AnimalReproductionState) pig).animaniax$isPregnant()) pig.setInLove(player);
            if (!player.getAbilities().instabuild) {
                ItemStack bucket = new ItemStack(net.minecraft.world.item.Items.BUCKET);
                if (stack.getCount() == 1) player.setItemInHand(hand, bucket);
                else {
                    stack.shrink(1);
                    if (!player.getInventory().add(bucket)) player.drop(bucket, false);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}

package net.syrupstudios.animaniax.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.syrupstudios.animaniax.block.BeeHiveContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BottleItem.class)
abstract class HoneyBottlePickupMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void animaniax$fillFromHoney(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> callback) {
        if (!level.getEntitiesOfClass(AreaEffectCloud.class, player.getBoundingBox().inflate(2.0),
                cloud -> cloud.isAlive() && cloud.getOwner() instanceof EnderDragon).isEmpty()) return;
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.calculateViewVector(player.getXRot(), player.getYRot())
                .scale(player.blockInteractionRange()));
        BlockHitResult hit = level.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE,
                ClipContext.Fluid.SOURCE_ONLY, player));
        if (hit.getType() != HitResult.Type.BLOCK) return;
        BlockPos pos = hit.getBlockPos();
        var fluid = level.getFluidState(pos);
        if (!fluid.isSource() || !fluid.getType().isSame(BeeHiveContent.HONEY_SOURCE)) return;
        if (!level.mayInteract(player, pos)) return;

        ItemStack bottle = ItemUtils.createFilledResult(player.getItemInHand(hand), player,
                new ItemStack(BeeHiveContent.HONEY_BOTTLE));
        player.awardStat(Stats.ITEM_USED.get((Item) (Object) this));
        level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL,
                SoundSource.NEUTRAL, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
        if (!level.isClientSide) level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        callback.setReturnValue(InteractionResultHolder.sidedSuccess(bottle, level.isClientSide));
    }
}

package net.syrupstudios.animaniax.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.syrupstudios.animaniax.entity.CartContent;
import net.syrupstudios.animaniax.entity.PullCartEntity;

public class CartItem extends Item {
    public CartItem(Properties properties) { super(properties); }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        PullCartEntity cart = entityType().create(level);
        if (cart == null) return InteractionResult.FAIL;
        cart.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.getYRot(), 0);
        if (!level.noCollision(cart)) return InteractionResult.FAIL;
        if (!level.isClientSide) {
            cart.setOwner(player.getUUID());
            if (!level.addFreshEntity(cart)) return InteractionResult.FAIL;
            if (!player.getAbilities().instabuild) context.getItemInHand().shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    protected net.minecraft.world.entity.EntityType<? extends PullCartEntity> entityType() { return CartContent.CART; }
}

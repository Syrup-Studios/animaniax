package net.syrupstudios.animaniax.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.syrupstudios.animaniax.entity.TillerContent;
import net.syrupstudios.animaniax.entity.TillerEntity;

public final class TillerItem extends Item {
    public TillerItem(Properties properties) { super(properties); }
    @Override public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel(); Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        TillerEntity tiller = TillerContent.TILLER.create(level);
        if (tiller == null) return InteractionResult.FAIL;
        tiller.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.getYRot(), 0);
        if (!level.noCollision(tiller)) return InteractionResult.FAIL;
        if (!level.isClientSide) {
            if (!level.addFreshEntity(tiller)) return InteractionResult.FAIL;
            if (!player.getAbilities().instabuild) context.getItemInHand().shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}

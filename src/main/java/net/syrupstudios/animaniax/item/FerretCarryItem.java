package net.syrupstudios.animaniax.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.syrupstudios.animaniax.entity.FerretContent;
import net.syrupstudios.animaniax.entity.FerretEntity;

public final class FerretCarryItem extends Item {
    private static final String FERRET_TAG = "Ferret";

    public FerretCarryItem(Properties properties) { super(properties); }

    public static CompoundTag getData(ItemStack stack) {
        CompoundTag root = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!root.contains(FERRET_TAG, CompoundTag.TAG_COMPOUND)) return null;
        CompoundTag data = root.getCompound(FERRET_TAG);
        String id = data.getString("id");
        return (id.equals(FerretContent.id("ferret_grey").toString())
                || id.equals(FerretContent.id("ferret_white").toString()))
                && data.hasUUID("UUID") && data.hasUUID("Owner") ? data.copy() : null;
    }

    public static void store(ItemStack stack, FerretEntity ferret) {
        CompoundTag data = new CompoundTag();
        ferret.saveWithoutId(data);
        data.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(ferret.getType()).toString());
        data.putUUID("UUID", ferret.getUUID());
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.put(FERRET_TAG, data));
    }

    @Override public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown()) return InteractionResult.PASS;
        ItemStack stack = context.getItemInHand();
        CompoundTag data = getData(stack);
        if (data == null || !data.getUUID("Owner").equals(player.getUUID())) return InteractionResult.PASS;
        Level level = context.getLevel();
        if (level.isClientSide) return InteractionResult.sidedSuccess(true);

        EntityType<FerretEntity> type = data.getString("id").equals(FerretContent.id("ferret_grey").toString())
                ? FerretContent.GREY : FerretContent.WHITE;
        FerretEntity ferret = type.create(level);
        if (ferret == null) return InteractionResult.PASS;
        ferret.load(data);
        Direction face = context.getClickedFace();
        BlockPos pos = context.getClickedPos().relative(face);
        ferret.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.getYRot(), 0);
        if (!level.noCollision(ferret) || !level.addFreshEntity(ferret)) {
            ferret.discard();
            return InteractionResult.PASS;
        }
        stack.shrink(1);
        return InteractionResult.sidedSuccess(false);
    }
}

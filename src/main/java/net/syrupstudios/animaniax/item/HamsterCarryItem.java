package net.syrupstudios.animaniax.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.syrupstudios.animaniax.entity.HamsterEntity;

public final class HamsterCarryItem extends Item {
    private static final String HAMSTER_TAG = "Hamster";

    public HamsterCarryItem(Properties properties) {
        super(properties);
    }

    public static CompoundTag getData(ItemStack stack) {
        CompoundTag root = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!root.contains(HAMSTER_TAG, CompoundTag.TAG_COMPOUND)) return null;
        CompoundTag data = root.getCompound(HAMSTER_TAG);
        return HamsterEntity.id("hamster").toString().equals(data.getString("id"))
                && data.hasUUID("UUID") && data.hasUUID("Owner") ? data.copy() : null;
    }

    public static void storeData(ItemStack stack, CompoundTag data) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.put(HAMSTER_TAG, data.copy()));
    }

    public static void store(ItemStack stack, HamsterEntity hamster) {
        CompoundTag data = new CompoundTag();
        hamster.saveWithoutId(data);
        data.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(HamsterEntity.TYPE).toString());
        data.putUUID("UUID", hamster.getUUID());
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.put(HAMSTER_TAG, data));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null || !context.getPlayer().isShiftKeyDown()) return InteractionResult.PASS;
        ItemStack stack = context.getItemInHand();
        CompoundTag data = getData(stack);
        if (data == null) return InteractionResult.PASS;

        Level level = context.getLevel();
        if (level.isClientSide) return InteractionResult.sidedSuccess(true);

        Direction face = context.getClickedFace();
        BlockPos pos = context.getClickedPos().relative(face);
        HamsterEntity hamster = HamsterEntity.TYPE.create(level);
        if (hamster == null) return InteractionResult.PASS;
        hamster.load(data);
        hamster.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, context.getPlayer() == null ? 0 : context.getPlayer().getYRot(), 0);
        if (!level.noCollision(hamster) || !level.addFreshEntity(hamster)) {
            hamster.discard();
            return InteractionResult.PASS;
        }

        stack.shrink(1);
        return InteractionResult.sidedSuccess(false);
    }
}

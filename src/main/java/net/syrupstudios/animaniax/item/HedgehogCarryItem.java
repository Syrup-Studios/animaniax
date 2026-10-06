package net.syrupstudios.animaniax.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.syrupstudios.animaniax.entity.HedgehogContent;
import net.syrupstudios.animaniax.entity.HedgehogEntity;

public final class HedgehogCarryItem extends Item {
    private static final String HEDGEHOG_TAG = "Hedgehog";

    public HedgehogCarryItem(Properties properties) { super(properties); }

    public static CompoundTag getData(ItemStack stack) {
        CompoundTag root = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!root.contains(HEDGEHOG_TAG, CompoundTag.TAG_COMPOUND)) return null;
        CompoundTag data = root.getCompound(HEDGEHOG_TAG);
        String id = data.getString("id");
        return (id.equals(HedgehogContent.id("hedgehog").toString())
                || id.equals(HedgehogContent.id("hedgehog_albino").toString()))
                && data.hasUUID("UUID") && data.hasUUID("Owner") ? data.copy() : null;
    }

    public static void store(ItemStack stack, HedgehogEntity hedgehog) {
        CompoundTag data = new CompoundTag();
        hedgehog.saveWithoutId(data);
        data.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(hedgehog.getType()).toString());
        data.putUUID("UUID", hedgehog.getUUID());
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.put(HEDGEHOG_TAG, data));
    }

    @Override public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown()) return InteractionResult.PASS;
        ItemStack stack = context.getItemInHand();
        CompoundTag data = getData(stack);
        if (data == null || !data.getUUID("Owner").equals(player.getUUID())) return InteractionResult.PASS;
        Level level = context.getLevel();
        if (level.isClientSide) return InteractionResult.sidedSuccess(true);

        EntityType<HedgehogEntity> type = data.getString("id").equals(HedgehogContent.id("hedgehog").toString())
                ? HedgehogContent.HEDGEHOG : HedgehogContent.HEDGEHOG_ALBINO;
        HedgehogEntity hedgehog = type.create(level);
        if (hedgehog == null) return InteractionResult.PASS;
        hedgehog.load(data);
        Direction face = context.getClickedFace();
        BlockPos pos = context.getClickedPos().relative(face);
        hedgehog.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.getYRot(), 0);
        if (!level.noCollision(hedgehog) || !level.addFreshEntity(hedgehog)) {
            hedgehog.discard();
            return InteractionResult.PASS;
        }
        stack.shrink(1);
        return InteractionResult.sidedSuccess(false);
    }
}

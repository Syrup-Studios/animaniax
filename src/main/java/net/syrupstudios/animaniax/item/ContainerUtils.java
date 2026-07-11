package net.syrupstudios.animaniax.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;

public final class ContainerUtils {
    private ContainerUtils() {}

    public static void giveOrDrop(Player player, ItemStack stack) {
        if (player == null || stack.isEmpty()) return;
        if (!player.getInventory().add(stack)) player.drop(stack, false);
    }

    public static void returnEmptyBucket(Player player, ItemStack held) {
        if (player == null || player.getAbilities().instabuild) return;
        held.shrink(1);
        giveOrDrop(player, new ItemStack(Items.BUCKET));
    }

    public static void swapInHand(Player player, InteractionHand hand, ItemStack held, ItemStack result) {
        if (player == null || player.getAbilities().instabuild) return;
        held.shrink(1);
        if (held.isEmpty()) player.setItemInHand(hand, result);
        else giveOrDrop(player, result);
    }

    public static void fillGlassBottle(Player player, InteractionHand hand, ItemStack held, ItemStack filled) {
        player.setItemInHand(hand, ItemUtils.createFilledResult(held, player, filled));
    }
}

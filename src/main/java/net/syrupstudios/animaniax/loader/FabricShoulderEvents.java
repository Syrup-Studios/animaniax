package net.syrupstudios.animaniax.loader;

//? if fabric {
public final class FabricShoulderEvents {
    private FabricShoulderEvents() {}

    public static void register() {
        net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (!player.isShiftKeyDown() || hand != net.minecraft.world.InteractionHand.MAIN_HAND
                    || !net.syrupstudios.animaniax.entity.ShoulderCarryState.has(player))
                return net.minecraft.world.InteractionResult.PASS;
            if (level.isClientSide) return net.minecraft.world.InteractionResult.SUCCESS;
            return net.syrupstudios.animaniax.entity.ShoulderCarryState.release(player, hit.getBlockPos())
                    ? net.minecraft.world.InteractionResult.SUCCESS : net.minecraft.world.InteractionResult.FAIL;
        });
    }
}
//?}

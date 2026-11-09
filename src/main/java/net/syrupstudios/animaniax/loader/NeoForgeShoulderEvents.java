package net.syrupstudios.animaniax.loader;

//? if neoforge {
/*
@net.neoforged.fml.common.EventBusSubscriber(modid = "animaniax")
public final class NeoForgeShoulderEvents {
    private NeoForgeShoulderEvents() {}

    @net.neoforged.bus.api.SubscribeEvent
    public static void release(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock event) {
        var player = event.getEntity();
        if (!player.isShiftKeyDown() || !net.syrupstudios.animaniax.entity.ShoulderCarryState.has(player)) return;
        event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
        event.setCanceled(true);
        if (!player.level().isClientSide)
            net.syrupstudios.animaniax.entity.ShoulderCarryState.release(player, event.getPos());
    }

    @net.neoforged.bus.api.SubscribeEvent
    public static void copy(net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone event) {
        net.syrupstudios.animaniax.entity.ShoulderCarryState.copy(event.getOriginal(), event.getEntity());
    }

}
*/
//?}

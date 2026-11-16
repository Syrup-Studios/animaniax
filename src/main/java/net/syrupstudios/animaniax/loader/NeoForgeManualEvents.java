package net.syrupstudios.animaniax.loader;

//? if neoforge {
/*
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.syrupstudios.animaniax.client.ManualScreen;
import net.syrupstudios.animaniax.item.ManualContent;

@EventBusSubscriber(modid = "animaniax", value = Dist.CLIENT)
public final class NeoForgeManualEvents {
    private NeoForgeManualEvents() {}

    @SubscribeEvent
    public static void openManual(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().is(ManualContent.MANUAL)) {
            Minecraft.getInstance().setScreen(new ManualScreen());
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }
}
*/
//?}

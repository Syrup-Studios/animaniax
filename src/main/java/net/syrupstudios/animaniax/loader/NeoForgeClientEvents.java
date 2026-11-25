package net.syrupstudios.animaniax.loader;

//? if neoforge {
/*
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.syrupstudios.animaniax.block.BeeHiveContent;
import net.syrupstudios.animaniax.block.CheeseMoldContent;
import net.syrupstudios.animaniax.block.HamsterWheelContent;
import net.syrupstudios.animaniax.block.NestContent;
import net.syrupstudios.animaniax.block.PetBowlContent;
import net.syrupstudios.animaniax.block.PropBlockEntityContent;
import net.syrupstudios.animaniax.block.SaltLickContent;
import net.syrupstudios.animaniax.block.TroughContent;
import net.syrupstudios.animaniax.client.AnimaniaAmphibianRenderer;
import net.syrupstudios.animaniax.client.AnimaniaCatRenderer;
import net.syrupstudios.animaniax.client.AnimaniaChickenRenderer;
import net.syrupstudios.animaniax.client.AnimaniaCowRenderer;
import net.syrupstudios.animaniax.client.AnimaniaDogRenderer;
import net.syrupstudios.animaniax.client.AnimaniaGoatRenderer;
import net.syrupstudios.animaniax.client.AnimaniaPigRenderer;
import net.syrupstudios.animaniax.client.AnimaniaRabbitRenderer;
import net.syrupstudios.animaniax.client.AnimaniaSheepRenderer;
import net.syrupstudios.animaniax.client.DraftHorseRenderer;
import net.syrupstudios.animaniax.client.FerretRenderer;
import net.syrupstudios.animaniax.client.HamsterRenderer;
import net.syrupstudios.animaniax.client.HedgehogRenderer;
import net.syrupstudios.animaniax.client.PeafowlRenderer;
import net.syrupstudios.animaniax.client.PullCartRenderer;
import net.syrupstudios.animaniax.client.TillerRenderer;
import net.syrupstudios.animaniax.client.blockentity.BeeHiveBlockEntityRenderer;
import net.syrupstudios.animaniax.client.blockentity.CheeseMoldBlockEntityRenderer;
import net.syrupstudios.animaniax.client.blockentity.HamsterWheelBlockEntityRenderer;
import net.syrupstudios.animaniax.client.blockentity.NestBlockEntityRenderer;
import net.syrupstudios.animaniax.client.blockentity.PetBowlBlockEntityRenderer;
import net.syrupstudios.animaniax.client.blockentity.PropBlockEntityRenderer;
import net.syrupstudios.animaniax.client.blockentity.SaltLickBlockEntityRenderer;
import net.syrupstudios.animaniax.client.blockentity.TroughBlockEntityRenderer;
import net.syrupstudios.animaniax.client.model.PlaceholderModelRegistry;
import net.syrupstudios.animaniax.entity.CartContent;
import net.syrupstudios.animaniax.entity.CatContent;
import net.syrupstudios.animaniax.entity.DogContent;
import net.syrupstudios.animaniax.entity.DraftHorseContent;
import net.syrupstudios.animaniax.entity.FerretContent;
import net.syrupstudios.animaniax.entity.FrogContent;
import net.syrupstudios.animaniax.entity.HamsterEntity;
import net.syrupstudios.animaniax.entity.HedgehogContent;
import net.syrupstudios.animaniax.entity.PeafowlEntity;
import net.syrupstudios.animaniax.entity.TillerContent;
import net.syrupstudios.animaniax.entity.WagonContent;
import net.syrupstudios.animaniax.item.AnimalSpawnEggContent;
import net.syrupstudios.animaniax.item.AnimalSpawnEggItem;

@EventBusSubscriber(modid = "animaniax", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class NeoForgeClientEvents {
    private NeoForgeClientEvents() {}

    @SubscribeEvent
    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(PlaceholderModelRegistry.INSTANCE);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityType.COW, AnimaniaCowRenderer::new);
        event.registerEntityRenderer(EntityType.MOOSHROOM, AnimaniaCowRenderer::new);
        event.registerEntityRenderer(EntityType.PIG, AnimaniaPigRenderer::new);
        event.registerEntityRenderer(EntityType.SHEEP, AnimaniaSheepRenderer::new);
        event.registerEntityRenderer(EntityType.GOAT, AnimaniaGoatRenderer::new);
        event.registerEntityRenderer(EntityType.CHICKEN, AnimaniaChickenRenderer::new);
        event.registerEntityRenderer(EntityType.RABBIT, AnimaniaRabbitRenderer::new);
        event.registerEntityRenderer(DogContent.TYPE, AnimaniaDogRenderer::new);
        event.registerEntityRenderer(CatContent.TYPE, AnimaniaCatRenderer::new);
        event.registerEntityRenderer(DraftHorseContent.DRAFT_HORSE, DraftHorseRenderer::new);
        event.registerEntityRenderer(CartContent.CART, PullCartRenderer::new);
        event.registerEntityRenderer(WagonContent.WAGON, PullCartRenderer::new);
        event.registerEntityRenderer(TillerContent.TILLER, TillerRenderer::new);
        event.registerEntityRenderer(PeafowlEntity.TYPE, PeafowlRenderer::new);
        event.registerEntityRenderer(HamsterEntity.TYPE, HamsterRenderer::new);
        event.registerEntityRenderer(FerretContent.GREY, FerretRenderer::new);
        event.registerEntityRenderer(FerretContent.WHITE, FerretRenderer::new);
        event.registerEntityRenderer(HedgehogContent.HEDGEHOG, HedgehogRenderer::new);
        event.registerEntityRenderer(HedgehogContent.HEDGEHOG_ALBINO, HedgehogRenderer::new);
        event.registerEntityRenderer(FrogContent.COMMON_FROG, AnimaniaAmphibianRenderer::new);
        event.registerEntityRenderer(FrogContent.DART_FROG, AnimaniaAmphibianRenderer::new);
        event.registerEntityRenderer(FrogContent.TOAD, AnimaniaAmphibianRenderer::new);

        event.registerBlockEntityRenderer(TroughContent.TROUGH_ENTITY, TroughBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(NestContent.NEST_ENTITY, NestBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(SaltLickContent.ENTITY, SaltLickBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(PetBowlContent.BLOCK_ENTITY, PetBowlBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(HamsterWheelContent.ENTITY, HamsterWheelBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BeeHiveContent.ENTITY, BeeHiveBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(PropBlockEntityContent.ENTITY, PropBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(CheeseMoldContent.MOLD_ENTITY, CheeseMoldBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(AnimalSpawnEggItem::color, AnimalSpawnEggContent.ITEM);
    }
}
*/
//?}

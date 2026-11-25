package net.syrupstudios.animaniax.loader;

//? if fabric {
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.syrupstudios.animaniax.block.AnimalMilkFluids;
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
import net.syrupstudios.animaniax.client.ManualScreen;
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
import net.syrupstudios.animaniax.item.ManualContent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class FabricClientEntrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.ITEM.register(AnimalSpawnEggItem::color, AnimalSpawnEggContent.ITEM);
        UseItemCallback.EVENT.register((player, world, hand) -> {
            var stack = player.getItemInHand(hand);
            if (stack.is(ManualContent.MANUAL)) {
                if (world.isClientSide) Minecraft.getInstance().setScreen(new ManualScreen());
                return InteractionResultHolder.sidedSuccess(stack, world.isClientSide);
            }
            return InteractionResultHolder.pass(stack);
        });

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(
                new IdentifiableResourceReloadListener() {
                    @Override
                    public ResourceLocation getFabricId() {
                        return PlaceholderModelRegistry.LISTENER_ID;
                    }

                    @Override
                    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager,
                            ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler,
                            Executor backgroundExecutor, Executor gameExecutor) {
                        return PlaceholderModelRegistry.INSTANCE.reload(barrier, manager,
                                prepareProfiler, applyProfiler, backgroundExecutor, gameExecutor);
                    }
                });

        FluidRenderHandlerRegistry.INSTANCE.register(BeeHiveContent.HONEY_SOURCE,
                BeeHiveContent.HONEY_FLOWING,
                new SimpleFluidRenderHandler(
                        ResourceLocation.fromNamespaceAndPath("animaniax", "block/fluid/animania_honey_still"),
                        ResourceLocation.fromNamespaceAndPath("animaniax", "block/fluid/animania_honey_flow"),
                        0xFFFFFFFF));
        registerMilk("friesian", AnimalMilkFluids.FRIESIAN_SOURCE, AnimalMilkFluids.FRIESIAN_FLOWING);
        registerMilk("holstein", AnimalMilkFluids.HOLSTEIN_SOURCE, AnimalMilkFluids.HOLSTEIN_FLOWING);
        registerMilk("jersey", AnimalMilkFluids.JERSEY_SOURCE, AnimalMilkFluids.JERSEY_FLOWING);
        registerMilk("goat", AnimalMilkFluids.GOAT_SOURCE, AnimalMilkFluids.GOAT_FLOWING);
        registerMilk("sheep", AnimalMilkFluids.SHEEP_SOURCE, AnimalMilkFluids.SHEEP_FLOWING);
        registerMilk("generic", AnimalMilkFluids.GENERIC_SOURCE, AnimalMilkFluids.GENERIC_FLOWING);

        EntityRendererRegistry.register(EntityType.COW, AnimaniaCowRenderer::new);
        EntityRendererRegistry.register(EntityType.MOOSHROOM, AnimaniaCowRenderer::new);
        EntityRendererRegistry.register(EntityType.PIG, AnimaniaPigRenderer::new);
        EntityRendererRegistry.register(EntityType.SHEEP, AnimaniaSheepRenderer::new);
        EntityRendererRegistry.register(EntityType.GOAT, AnimaniaGoatRenderer::new);
        EntityRendererRegistry.register(EntityType.CHICKEN, AnimaniaChickenRenderer::new);
        EntityRendererRegistry.register(EntityType.RABBIT, AnimaniaRabbitRenderer::new);
        EntityRendererRegistry.register(DogContent.TYPE, AnimaniaDogRenderer::new);
        EntityRendererRegistry.register(CatContent.TYPE, AnimaniaCatRenderer::new);
        EntityRendererRegistry.register(DraftHorseContent.DRAFT_HORSE, DraftHorseRenderer::new);
        EntityRendererRegistry.register(CartContent.CART, PullCartRenderer::new);
        EntityRendererRegistry.register(WagonContent.WAGON, PullCartRenderer::new);
        EntityRendererRegistry.register(TillerContent.TILLER, TillerRenderer::new);
        EntityRendererRegistry.register(PeafowlEntity.TYPE, PeafowlRenderer::new);
        EntityRendererRegistry.register(HamsterEntity.TYPE, HamsterRenderer::new);
        EntityRendererRegistry.register(FerretContent.GREY, FerretRenderer::new);
        EntityRendererRegistry.register(FerretContent.WHITE, FerretRenderer::new);
        EntityRendererRegistry.register(HedgehogContent.HEDGEHOG, HedgehogRenderer::new);
        EntityRendererRegistry.register(HedgehogContent.HEDGEHOG_ALBINO, HedgehogRenderer::new);
        EntityRendererRegistry.register(FrogContent.COMMON_FROG, AnimaniaAmphibianRenderer::new);
        EntityRendererRegistry.register(FrogContent.DART_FROG, AnimaniaAmphibianRenderer::new);
        EntityRendererRegistry.register(FrogContent.TOAD, AnimaniaAmphibianRenderer::new);

        BlockEntityRenderers.register(TroughContent.TROUGH_ENTITY, TroughBlockEntityRenderer::new);
        BlockEntityRenderers.register(NestContent.NEST_ENTITY, NestBlockEntityRenderer::new);
        BlockEntityRenderers.register(SaltLickContent.ENTITY, SaltLickBlockEntityRenderer::new);
        BlockEntityRenderers.register(PetBowlContent.BLOCK_ENTITY, PetBowlBlockEntityRenderer::new);
        BlockEntityRenderers.register(HamsterWheelContent.ENTITY, HamsterWheelBlockEntityRenderer::new);
        BlockEntityRenderers.register(BeeHiveContent.ENTITY, BeeHiveBlockEntityRenderer::new);
        BlockEntityRenderers.register(PropBlockEntityContent.ENTITY, PropBlockEntityRenderer::new);
        BlockEntityRenderers.register(CheeseMoldContent.MOLD_ENTITY, CheeseMoldBlockEntityRenderer::new);
    }

    private static void registerMilk(String kind, net.minecraft.world.level.material.Fluid source,
            net.minecraft.world.level.material.Fluid flowing) {
        String slug = "generic".equals(kind) ? "holstein" : kind;
        FluidRenderHandlerRegistry.INSTANCE.register(source, flowing,
                new SimpleFluidRenderHandler(
                        ResourceLocation.fromNamespaceAndPath("animaniax", "block/fluid/milk_" + slug + "_still"),
                        ResourceLocation.fromNamespaceAndPath("animaniax", "block/fluid/milk_" + slug + "_flow"),
                        0xFFFFFFFF));
    }
}
//?}

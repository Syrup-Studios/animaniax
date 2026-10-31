package net.syrupstudios.animaniax.loader;

//? if fabric {
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.syrupstudios.animaniax.entity.DogContent;
import net.syrupstudios.animaniax.entity.CatContent;
import net.minecraft.client.renderer.entity.CatRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.FrogRenderer;
import net.syrupstudios.animaniax.client.PeafowlRenderer;
import net.syrupstudios.animaniax.client.HamsterRenderer;
import net.syrupstudios.animaniax.client.FerretRenderer;
import net.syrupstudios.animaniax.entity.DraftHorseContent;
import net.syrupstudios.animaniax.entity.PeafowlEntity;
import net.syrupstudios.animaniax.entity.HamsterEntity;
import net.syrupstudios.animaniax.entity.FerretContent;
import net.syrupstudios.animaniax.entity.HedgehogContent;
import net.syrupstudios.animaniax.client.HedgehogRenderer;
import net.syrupstudios.animaniax.entity.FrogContent;
import net.syrupstudios.animaniax.item.AnimalSpawnEggContent;
import net.syrupstudios.animaniax.item.AnimalSpawnEggItem;

public final class FabricClientEntrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.ITEM.register(AnimalSpawnEggItem::color, AnimalSpawnEggContent.ITEM);
        FluidRenderHandlerRegistry.INSTANCE.register(net.syrupstudios.animaniax.block.BeeHiveContent.HONEY_SOURCE,
                net.syrupstudios.animaniax.block.BeeHiveContent.HONEY_FLOWING,
                new SimpleFluidRenderHandler(net.minecraft.resources.ResourceLocation.withDefaultNamespace("block/water_still"),
                        net.minecraft.resources.ResourceLocation.withDefaultNamespace("block/water_flow"), 0xFFFFC943));
        registerMilk("friesian", net.syrupstudios.animaniax.block.AnimalMilkFluids.FRIESIAN_SOURCE,
                net.syrupstudios.animaniax.block.AnimalMilkFluids.FRIESIAN_FLOWING);
        registerMilk("holstein", net.syrupstudios.animaniax.block.AnimalMilkFluids.HOLSTEIN_SOURCE,
                net.syrupstudios.animaniax.block.AnimalMilkFluids.HOLSTEIN_FLOWING);
        registerMilk("jersey", net.syrupstudios.animaniax.block.AnimalMilkFluids.JERSEY_SOURCE,
                net.syrupstudios.animaniax.block.AnimalMilkFluids.JERSEY_FLOWING);
        registerMilk("goat", net.syrupstudios.animaniax.block.AnimalMilkFluids.GOAT_SOURCE,
                net.syrupstudios.animaniax.block.AnimalMilkFluids.GOAT_FLOWING);
        registerMilk("sheep", net.syrupstudios.animaniax.block.AnimalMilkFluids.SHEEP_SOURCE,
                net.syrupstudios.animaniax.block.AnimalMilkFluids.SHEEP_FLOWING);
        registerMilk("generic", net.syrupstudios.animaniax.block.AnimalMilkFluids.GENERIC_SOURCE,
                net.syrupstudios.animaniax.block.AnimalMilkFluids.GENERIC_FLOWING);
        EntityRendererRegistry.register(DogContent.TYPE, WolfRenderer::new);
        EntityRendererRegistry.register(CatContent.TYPE, CatRenderer::new);
        EntityRendererRegistry.register(DraftHorseContent.DRAFT_HORSE, HorseRenderer::new);
        EntityRendererRegistry.register(PeafowlEntity.TYPE, PeafowlRenderer::new);
        EntityRendererRegistry.register(HamsterEntity.TYPE, HamsterRenderer::new);
        EntityRendererRegistry.register(FerretContent.GREY, FerretRenderer::new);
        EntityRendererRegistry.register(FerretContent.WHITE, FerretRenderer::new);
        EntityRendererRegistry.register(HedgehogContent.HEDGEHOG, HedgehogRenderer::new);
        EntityRendererRegistry.register(HedgehogContent.HEDGEHOG_ALBINO, HedgehogRenderer::new);
        EntityRendererRegistry.register(FrogContent.COMMON_FROG, FrogRenderer::new);
        EntityRendererRegistry.register(FrogContent.DART_FROG, FrogRenderer::new);
        EntityRendererRegistry.register(FrogContent.TOAD, FrogRenderer::new);
    }

    private static void registerMilk(String kind, net.minecraft.world.level.material.Fluid source,
            net.minecraft.world.level.material.Fluid flowing) {
        FluidRenderHandlerRegistry.INSTANCE.register(source, flowing,
                new SimpleFluidRenderHandler(net.minecraft.resources.ResourceLocation.withDefaultNamespace("block/water_still"),
                        net.minecraft.resources.ResourceLocation.withDefaultNamespace("block/water_flow"),
                        net.syrupstudios.animaniax.block.AnimalMilkFluids.tint(kind)));
    }
}
//?}

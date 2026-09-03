package net.syrupstudios.animaniax.loader;

//? if fabric {
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.syrupstudios.animaniax.entity.DraftHorseContent;

public final class FabricClientEntrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
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
        EntityRendererRegistry.register(DraftHorseContent.DRAFT_HORSE, HorseRenderer::new);
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

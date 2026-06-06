package net.syrupstudios.animaniax.loader;

//? if fabric {
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.world.level.storage.LevelResource;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FabricEntrypoint implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("animaniax");

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            AnimalCareConfig.load(server.getWorldPath(LevelResource.ROOT).resolve("serverconfig/animaniax.properties"));
        });
        LOGGER.info("Animaniax initialized on Fabric");
    }
}
//?}

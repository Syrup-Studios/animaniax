package net.syrupstudios.animaniax.loader;

//? if fabric {
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FabricEntrypoint implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("animaniax");

    @Override
    public void onInitialize() {
        LOGGER.info("Animaniax initialized on Fabric");
    }
}
//?}

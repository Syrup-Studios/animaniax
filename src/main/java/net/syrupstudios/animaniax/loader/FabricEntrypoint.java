package net.syrupstudios.animaniax.loader;

//? if fabric {
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.SpawnEggItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.level.storage.LevelResource;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.block.NestContent;
import net.syrupstudios.animaniax.block.TroughContent;
import net.syrupstudios.animaniax.block.SeedPileContent;
import net.syrupstudios.animaniax.block.StrawContent;
import net.syrupstudios.animaniax.block.MudContent;
import net.syrupstudios.animaniax.block.SaltLickContent;
import net.syrupstudios.animaniax.item.BrownEggContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FabricEntrypoint implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("animaniax");

    @Override
    public void onInitialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries ->
                BuiltInRegistries.ITEM.stream().filter(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals("animaniax")
                        && !(item instanceof SpawnEggItem)).forEach(entries::accept));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries ->
                BuiltInRegistries.ITEM.stream().filter(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals("animaniax")
                        && item instanceof SpawnEggItem).forEach(entries::accept));
        TroughContent.registerFabric();
        SeedPileContent.registerFabric();
        StrawContent.registerFabric();
        MudContent.registerFabric();
        SaltLickContent.registerFabric();
        NestContent.registerFabric();
        BrownEggContent.registerFabric();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            AnimalCareConfig.load(server.getWorldPath(LevelResource.ROOT).resolve("serverconfig/animaniax.properties"));
            TroughContent.registerWaterBucketBehavior();
            SeedPileContent.registerDispenserBehaviors();
        });
        LOGGER.info("Animaniax initialized on Fabric");
    }
}
//?}

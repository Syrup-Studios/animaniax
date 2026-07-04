package net.syrupstudios.animaniax.loader;

//? if neoforge {
/*
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraft.world.level.storage.LevelResource;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.syrupstudios.animaniax.block.NestContent;
import net.syrupstudios.animaniax.block.TroughContent;
import net.syrupstudios.animaniax.block.SeedPileContent;
import net.syrupstudios.animaniax.block.StrawContent;
import net.syrupstudios.animaniax.block.MudContent;
import net.syrupstudios.animaniax.block.SaltLickContent;
import net.syrupstudios.animaniax.item.BrownEggContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("animaniax")
public final class NeoForgeEntrypoint {
    private static final Logger LOGGER = LoggerFactory.getLogger("animaniax");
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, "animaniax");
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, "animaniax");
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "animaniax");

    public NeoForgeEntrypoint(IEventBus modBus) {
        modBus.addListener(this::addCreativeItems);
        BLOCKS.register("salt_lick", () -> SaltLickContent.BLOCK);
        ITEMS.register("salt_lick", () -> SaltLickContent.ITEM);
        BLOCK_ENTITIES.register("salt_lick", () -> SaltLickContent.ENTITY);
        BLOCKS.register("trough", () -> TroughContent.TROUGH);
        BLOCKS.register("wheat_seed_pile", () -> SeedPileContent.WHEAT);
        BLOCKS.register("pumpkin_seed_pile", () -> SeedPileContent.PUMPKIN);
        BLOCKS.register("melon_seed_pile", () -> SeedPileContent.MELON);
        BLOCKS.register("beetroot_seed_pile", () -> SeedPileContent.BEETROOT);
        BLOCKS.register("straw", () -> StrawContent.BLOCK);
        BLOCKS.register("mud", () -> MudContent.BLOCK);
        ITEMS.register("trough", () -> TroughContent.TROUGH_ITEM);
        ITEMS.register("wheat_seed_pile", () -> SeedPileContent.WHEAT_ITEM);
        ITEMS.register("pumpkin_seed_pile", () -> SeedPileContent.PUMPKIN_ITEM);
        ITEMS.register("melon_seed_pile", () -> SeedPileContent.MELON_ITEM);
        ITEMS.register("beetroot_seed_pile", () -> SeedPileContent.BEETROOT_ITEM);
        ITEMS.register("straw", () -> StrawContent.ITEM);
        ITEMS.register("mud", () -> MudContent.ITEM);
        BLOCK_ENTITIES.register("trough", () -> TroughContent.TROUGH_ENTITY);
        BLOCK_ENTITIES.register("nest", () -> NestContent.NEST_ENTITY);
        BLOCKS.register("nest", () -> NestContent.NEST);
        ITEMS.register("nest", () -> NestContent.NEST_ITEM);
        ITEMS.register("brown_egg", () -> BrownEggContent.BROWN_EGG);
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event) -> {
            AnimalCareConfig.load(event.getServer().getWorldPath(LevelResource.ROOT).resolve("serverconfig/animaniax.properties"));
        });
        NeoForge.EVENT_BUS.addListener((ServerStartingEvent event) -> {
            TroughContent.registerWaterBucketBehavior();
            SeedPileContent.registerDispenserBehaviors();
        });
        LOGGER.info("Animaniax initialized on NeoForge");
    }

    private void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        boolean spawnEggs = event.getTabKey().equals(CreativeModeTabs.SPAWN_EGGS);
        if (!spawnEggs && !event.getTabKey().equals(CreativeModeTabs.INGREDIENTS)) return;
        BuiltInRegistries.ITEM.stream()
                .filter(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals("animaniax")
                        && (item instanceof SpawnEggItem) == spawnEggs)
                .forEach(event::accept);
    }
}
*/
//?}

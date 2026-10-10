package net.syrupstudios.animaniax.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public final class FrogContent {
    public static final EntityType<CommonFrogEntity> COMMON_FROG = EntityType.Builder
            .of(CommonFrogEntity::new, MobCategory.CREATURE).sized(0.5F, 0.5F).clientTrackingRange(8).build("animaniax:common_frog");
    public static final EntityType<DartFrogEntity> DART_FROG = EntityType.Builder
            .of(DartFrogEntity::new, MobCategory.CREATURE).sized(0.5F, 0.5F).clientTrackingRange(8).build("animaniax:dart_frog");
    public static final EntityType<ToadEntity> TOAD = EntityType.Builder
            .of(ToadEntity::new, MobCategory.CREATURE).sized(0.7F, 0.65F).clientTrackingRange(8).build("animaniax:toad");
    public static final Item COMMON_FROG_SPAWN_EGG = new SpawnEggItem(COMMON_FROG, 0x596B3B, 0xA7A55D, new Item.Properties());
    public static final Item DART_FROG_SPAWN_EGG = new SpawnEggItem(DART_FROG, 0xD5A52A, 0xD83D32, new Item.Properties());
    public static final Item TOAD_SPAWN_EGG = new SpawnEggItem(TOAD, 0x76543B, 0xB59A70, new Item.Properties());

    private FrogContent() {}

    public static void registerFabric() {
        //? if fabric {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("common_frog"), COMMON_FROG);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("dart_frog"), DART_FROG);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("toad"), TOAD);
        Registry.register(BuiltInRegistries.ITEM, id("common_frog_spawn_egg"), COMMON_FROG_SPAWN_EGG);
        Registry.register(BuiltInRegistries.ITEM, id("dart_frog_spawn_egg"), DART_FROG_SPAWN_EGG);
        Registry.register(BuiltInRegistries.ITEM, id("toad_spawn_egg"), TOAD_SPAWN_EGG);
        net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(COMMON_FROG, net.minecraft.world.entity.animal.frog.Frog.createAttributes());
        net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(DART_FROG, net.minecraft.world.entity.animal.frog.Frog.createAttributes());
        net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(TOAD, ToadEntity.createAttributes());
        //?}
    }

    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("animaniax", path); }
}

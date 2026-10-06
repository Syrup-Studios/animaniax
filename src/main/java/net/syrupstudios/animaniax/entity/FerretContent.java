package net.syrupstudios.animaniax.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public final class FerretContent {
    public static final EntityType<FerretEntity> GREY = EntityType.Builder.of(FerretEntity::new, MobCategory.CREATURE)
            .sized(0.45F, 0.35F).clientTrackingRange(8).build("animaniax:ferret_grey");
    public static final EntityType<FerretEntity> WHITE = EntityType.Builder.of(FerretEntity::new, MobCategory.CREATURE)
            .sized(0.45F, 0.35F).clientTrackingRange(8).build("animaniax:ferret_white");
    public static final Item GREY_SPAWN_EGG = new SpawnEggItem(GREY, 0x777777, 0xD5D0C8, new Item.Properties());
    public static final Item WHITE_SPAWN_EGG = new SpawnEggItem(WHITE, 0xEEEEEE, 0x333333, new Item.Properties());

    private FerretContent() {}
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("animaniax", path); }
    public static void registerFabric() {
        //? if fabric {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("ferret_grey"), GREY);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("ferret_white"), WHITE);
        Registry.register(BuiltInRegistries.ITEM, id("ferret_grey_spawn_egg"), GREY_SPAWN_EGG);
        Registry.register(BuiltInRegistries.ITEM, id("ferret_white_spawn_egg"), WHITE_SPAWN_EGG);
        net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(GREY, FerretEntity.createAttributes());
        net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(WHITE, FerretEntity.createAttributes());
        //?}
    }
}

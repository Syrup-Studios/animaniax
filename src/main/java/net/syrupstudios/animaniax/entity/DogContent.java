package net.syrupstudios.animaniax.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public final class DogContent {
    public static final EntityType<DogEntity> TYPE = EntityType.Builder.of(DogEntity::new, MobCategory.CREATURE)
            .sized(0.6F, 0.85F).clientTrackingRange(10).build("animaniax:dog");
    public static final Item SPAWN_EGG = new SpawnEggItem(TYPE, 0x8C6749, 0xD5C3A2, new Item.Properties());

    private DogContent() {}
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("animaniax", path); }
    public static void registerFabric() {
        //? if fabric {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("dog"), TYPE);
        Registry.register(BuiltInRegistries.ITEM, id("dog_spawn_egg"), SPAWN_EGG);
        net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(TYPE, DogEntity.createAttributes());
        //?}
    }
}

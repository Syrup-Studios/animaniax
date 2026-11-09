package net.syrupstudios.animaniax.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.syrupstudios.animaniax.item.TillerItem;

public final class TillerContent {
    public static final EntityType<TillerEntity> TILLER = EntityType.Builder.of(TillerEntity::new, MobCategory.MISC)
            .sized(1.25F, 0.8F).clientTrackingRange(8).updateInterval(2).build("animaniax:tiller");
    public static final Item TILLER_ITEM = new TillerItem(new Item.Properties().stacksTo(1));
    private TillerContent() { }
    public static void registerFabric() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("tiller"), TILLER);
        Registry.register(BuiltInRegistries.ITEM, id("tiller"), TILLER_ITEM);
    }
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("animaniax", path); }
}

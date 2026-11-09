package net.syrupstudios.animaniax.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.syrupstudios.animaniax.item.WagonItem;

public final class WagonContent {
    public static final EntityType<WagonEntity> WAGON = EntityType.Builder
            .<WagonEntity>of(WagonEntity::new, MobCategory.MISC)
            .sized(1.25F, 0.8F)
            .clientTrackingRange(8)
            .updateInterval(2)
            .build("animaniax:wagon");
    public static final Item WAGON_ITEM = new WagonItem(new Item.Properties().stacksTo(1));

    private WagonContent() { }

    public static void registerFabric() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, CartContent.id("wagon"), WAGON);
        Registry.register(BuiltInRegistries.ITEM, CartContent.id("wagon"), WAGON_ITEM);
    }
}

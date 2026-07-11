package net.syrupstudios.animaniax.item;

import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.item.Items;

public final class AnimalMilkBucketItem extends MilkBucketItem {
    public AnimalMilkBucketItem() {
        super(new Properties().stacksTo(1).craftRemainder(Items.BUCKET));
    }
}

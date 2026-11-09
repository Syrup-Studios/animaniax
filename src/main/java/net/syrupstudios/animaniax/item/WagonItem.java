package net.syrupstudios.animaniax.item;

import net.minecraft.world.entity.EntityType;
import net.syrupstudios.animaniax.entity.PullCartEntity;
import net.syrupstudios.animaniax.entity.WagonContent;

public final class WagonItem extends CartItem {
    public WagonItem(Properties properties) { super(properties); }

    @Override
    protected EntityType<? extends PullCartEntity> entityType() { return WagonContent.WAGON; }
}

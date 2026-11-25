package net.syrupstudios.animaniax.block;

import net.minecraft.world.level.block.entity.BlockEntityType;
//? if fabric {
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
//?}

/**
 * Shared block entity registration for decorative props and pet beds.
 */
public final class PropBlockEntityContent {
    public static final BlockEntityType<PropBlockEntity> ENTITY = BlockEntityType.Builder.of(
            PropBlockEntity::new,
            PetBedContent.CAT_BED_1,
            PetBedContent.CAT_BED_2,
            PetBedContent.DOG_PILLOW,
            DecorativePropContent.CAT_TOWER,
            DecorativePropContent.DOG_HOUSE,
            DecorativePropContent.LITTER_BOX
    ).build(null);

    private PropBlockEntityContent() {}

    //? if fabric {
    public static void registerFabric() {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath("animaniax", "prop"), ENTITY);
    }
    //?}
}

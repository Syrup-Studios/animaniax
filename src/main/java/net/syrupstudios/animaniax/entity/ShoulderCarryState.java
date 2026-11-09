package net.syrupstudios.animaniax.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.syrupstudios.animaniax.block.HamsterWheelBlockEntity;

public final class ShoulderCarryState {
    private static final String ID = "id";

    private ShoulderCarryState() {}

    public static CompoundTag get(Player player) {
        return ((ShoulderCarrier) player).animaniax$getShoulderData().copy();
    }

    public static boolean has(Player player) {
        return player.level().isClientSide ? !visualType(player).isEmpty()
                : !((ShoulderCarrier) player).animaniax$getShoulderData().isEmpty();
    }

    public static void copy(Player from, Player to) {
        ((ShoulderCarrier) to).animaniax$setShoulderData(get(from));
    }

    public static boolean set(Player player, Entity animal) {
        if (has(player) || !supported(animal.getType())) return false;
        CompoundTag data = new CompoundTag();
        animal.saveWithoutId(data);
        data.putString(ID, net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(animal.getType()).toString());
        data.putUUID("UUID", animal.getUUID());
        if (!data.hasUUID("Owner")) return false;
        ((ShoulderCarrier) player).animaniax$setShoulderData(data);
        return true;
    }

    public static boolean release(Player player, BlockPos clicked) {
        if (!(player.level() instanceof ServerLevel level) || !has(player)) return false;
        CompoundTag data = get(player);
        Entity entity = create(level, data);
        if (entity == null) return false;
        for (BlockPos pos : releasePositions(clicked)) {
            if (!level.hasChunkAt(pos) || !level.hasChunkAt(pos.below())) continue;
            BlockState floor = level.getBlockState(pos.below());
            if (!floor.isFaceSturdy(level, pos.below(), Direction.UP)) continue;
            entity.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.getYRot(), 0);
            if (level.noCollision(entity) && level.addFreshEntity(entity)) {
                ((ShoulderCarrier) player).animaniax$setShoulderData(new CompoundTag());
                return true;
            }
        }
        entity.discard();
        return false;
    }

    public static boolean insertHamster(Player player, HamsterWheelBlockEntity wheel) {
        if (player.level().isClientSide || wheel.occupied() || !has(player)) return false;
        CompoundTag data = get(player);
        if (!HamsterEntity.id("hamster").toString().equals(data.getString(ID))
                || !data.hasUUID("UUID") || !data.hasUUID("Owner")) return false;
        if (data.getBoolean("InBall") || data.getInt("StoredFood") <= 0) return false;
        if (!wheel.insertHamster(data)) return false;
        ((ShoulderCarrier) player).animaniax$setShoulderData(new CompoundTag());
        return true;
    }

    public static Entity create(Level level, CompoundTag data) {
        var id = net.minecraft.resources.ResourceLocation.tryParse(data.getString(ID));
        if (id == null) return null;
        EntityType<?> type = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
        if (type == null || !supported(type) || !data.hasUUID("UUID") || !data.hasUUID("Owner")) return null;
        Entity entity = type.create(level);
        if (entity == null) return null;
        try {
            entity.load(data.copy());
        } catch (RuntimeException invalidData) {
            return null;
        }
        return entity;
    }

    public static String visualType(Player player) {
        return ((ShoulderCarrier) player).animaniax$getVisualType();
    }

    public static Entity createVisual(Level level, String typeId) {
        var id = net.minecraft.resources.ResourceLocation.tryParse(typeId);
        if (id == null) return null;
        var type = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
        return type != null && supported(type) ? type.create(level) : null;
    }

    private static Iterable<BlockPos> releasePositions(BlockPos clicked) {
        return java.util.List.of(clicked.above(), clicked.north().above(), clicked.south().above(),
                clicked.east().above(), clicked.west().above());
    }

    private static boolean supported(EntityType<?> type) {
        return AnimalProfile.has(type, AnimalProfile.Capability.SHOULDER_CARRY);
    }

    public interface ShoulderCarrier {
        CompoundTag animaniax$getShoulderData();
        void animaniax$setShoulderData(CompoundTag data);
        String animaniax$getVisualType();
        Entity animaniax$getRenderPet();
        String animaniax$getRenderPetType();
        void animaniax$setRenderPet(String type, Entity pet);
    }
}

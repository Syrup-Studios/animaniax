package net.syrupstudios.animaniax.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.syrupstudios.animaniax.sound.AnimaniaSounds;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Inventory;

import java.util.UUID;

public class PullCartEntity extends Entity {
    private static final EntityDataAccessor<Boolean> HAS_CHEST = SynchedEntityData.defineId(PullCartEntity.class, EntityDataSerializers.BOOLEAN);
    protected final SimpleContainer storage;
    private int viewers;
    private boolean breakPending;
    private boolean suppressCartDrop;
    private UUID owner;
    private UUID pullerId;
    private Entity puller;
    private float damage;
    private double verticalVelocity;

    public PullCartEntity(EntityType<? extends PullCartEntity> type, Level level) {
        this(type, level, 36);
    }

    protected PullCartEntity(EntityType<? extends PullCartEntity> type, Level level, int storageSize) {
        super(type, level);
        noCulling = true;
        storage = new SimpleContainer(storageSize) {
            @Override public void startOpen(Player player) { viewers++; }
            @Override public void stopOpen(Player player) { viewers = Math.max(0, viewers - 1); }
        };
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(HAS_CHEST, false);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (breakPending) return InteractionResult.PASS;
        if (!level().isClientSide && puller == null && pullerId != null
                && level() instanceof ServerLevel server) {
            puller = server.getEntity(pullerId);
        }
        if (!level().isClientSide && held.is(Items.LEAD) && pullerId != null && puller == null) {
            clearPuller();
            player.displayClientMessage(Component.literal("Cart puller cleared."), true);
            return InteractionResult.SUCCESS;
        }
        if (held.is(Items.CHEST) && !hasChest() && !isVehicle()) {
            if (!level().isClientSide) {
                setHasChest(true);
                if (!player.getAbilities().instabuild) held.shrink(1);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }

        if (!player.isSecondaryUseActive() && !level().isClientSide) {
            if (held.isEmpty() && puller instanceof Mob mob && (mob instanceof AbstractHorse || mob instanceof Pig)) {
                clearPuller();
                return InteractionResult.SUCCESS;
            }
            if ((held.isEmpty() || held.is(Items.LEAD)) && pullerId == null) {
                Mob leashed = level().getEntitiesOfClass(Mob.class, getBoundingBox().inflate(3), mob ->
                        (mob instanceof AbstractHorse || mob instanceof Pig) && mob.isLeashed() && mob.getLeashHolder() == player)
                        .stream().findFirst().orElse(null);
                if (leashed != null) {
                    leashed.dropLeash(true, false);
                    puller = leashed;
                    pullerId = leashed.getUUID();
                    setOwnerIfMissing(player);
                    playHitchSound(true);
                    if (!player.getAbilities().instabuild) {
                        ItemStack lead = new ItemStack(Items.LEAD);
                        if (!player.getInventory().add(lead)) player.drop(lead, false);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        if (player.isSecondaryUseActive()) {
            if (hasChest()) {
                if (!level().isClientSide) player.openMenu(new StorageMenu());
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
            if (!hasChest() && held.isEmpty()) {
                if (!level().isClientSide) {
                    if (player.startRiding(this)) setOwnerIfMissing(player);
                }
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
            return InteractionResult.PASS;
        }

        if (!held.isEmpty()) return InteractionResult.PASS;
        if (!level().isClientSide) {
            Entity candidate = player.getVehicle();
            if (!(candidate instanceof AbstractHorse) && !(candidate instanceof Pig)) candidate = player;
            if (pullerId != null && (puller == candidate || pullerId.equals(candidate.getUUID()))) {
                clearPuller();
            } else if (pullerId == null && candidate != this) {
                puller = candidate;
                pullerId = candidate.getUUID();
                setOwnerIfMissing(player);
                playHitchSound(true);
            }
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    public boolean hasChest() { return entityData.get(HAS_CHEST); }

    protected void setHasChest(boolean value) { entityData.set(HAS_CHEST, value); }

    protected ItemStack getCartDrop() { return new ItemStack(CartContent.CART_ITEM); }

    protected boolean hasBuiltInChest() { return false; }

    protected boolean isBreakPending() { return breakPending; }

    protected void stopPulling(Player player) {
        if (player.getUUID().equals(pullerId)) {
            clearPuller();
        }
    }

    private void clearPuller() {
        if (puller != null && !level().isClientSide) playHitchSound(false);
        puller = null;
        pullerId = null;
    }

    private void playHitchSound(boolean hitched) {
        level().playSound(null, getX(), getY(), getZ(),
                hitched ? AnimaniaSounds.VEHICLE_HITCH : AnimaniaSounds.VEHICLE_UNHITCH,
                SoundSource.PLAYERS, 0.7F, 1.5F);
    }

    protected boolean shouldDropChest() { return hasChest(); }

    protected Component getContainerName() { return Component.translatable("container.animaniax.cart"); }

    public void setOwner(UUID owner) { this.owner = owner; }

    protected void setOwnerIfMissing(Player player) {
        if (owner == null) owner = player.getUUID();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (breakPending && viewers == 0) { destroyCart(); return; }
        if (!breakPending && damage > 0) damage = Math.max(0, damage - 0.1F);
        resolvePuller((ServerLevel) level());

        if (puller != null) {
            double yaw = Math.toRadians(puller.getYRot());
            double targetX = puller.getX() + Math.sin(yaw) * 2.5;
            double targetZ = puller.getZ() - Math.cos(yaw) * 2.5;
            double dx = targetX - getX();
            double dz = targetZ - getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);
            double step = Math.min(0.18, Math.max(0, distance - 1.15) * 0.12);
            if (!onGround()) verticalVelocity -= 0.04;
            else verticalVelocity = 0;
            Vec3 movement = new Vec3(distance > 0.001 ? dx / distance * step : 0, verticalVelocity,
                    distance > 0.001 ? dz / distance * step : 0);
            if (movement.lengthSqr() > 0) beforeCartMovement();
            move(MoverType.SELF, movement);
            setYRot((float) Math.toDegrees(Math.atan2(-dx, dz)));
        } else {
            if (!onGround()) verticalVelocity -= 0.04;
            else verticalVelocity = 0;
            if (verticalVelocity != 0) beforeCartMovement();
            move(MoverType.SELF, new Vec3(0, verticalVelocity, 0));
        }
    }

    protected void beforeCartMovement() {}

    private void resolvePuller(ServerLevel server) {
        if (puller != null) {
            Entity.RemovalReason reason = puller.getRemovalReason();
            if (reason == Entity.RemovalReason.UNLOADED_TO_CHUNK
                    || reason == Entity.RemovalReason.UNLOADED_WITH_PLAYER) {
                puller = null;
                return;
            } else if (!puller.isAlive() || reason != null || puller.level() != level()
                    || Math.abs(puller.getX() - getX()) > 12 || Math.abs(puller.getZ() - getZ()) > 12) {
                clearPuller();
            }
        }

        if (puller == null && pullerId != null) {
            puller = server.getEntity(pullerId);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        owner = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
        pullerId = tag.hasUUID("Puller") ? tag.getUUID("Puller") : null;
        setHasChest(tag.contains("HasChest") ? tag.getBoolean("HasChest") : hasBuiltInChest());
        storage.clearContent();
        ListTag items = tag.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < items.size(); i++) {
            CompoundTag item = items.getCompound(i);
            int slot = item.getByte("Slot") & 255;
            if (slot < storage.getContainerSize()) storage.setItem(slot, ItemStack.parse(registryAccess(), item).orElse(ItemStack.EMPTY));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (owner != null) tag.putUUID("Owner", owner);
        if (pullerId != null) tag.putUUID("Puller", pullerId);
        tag.putBoolean("HasChest", hasChest());
        ListTag items = new ListTag();
        for (int slot = 0; slot < storage.getContainerSize(); slot++) {
            ItemStack stack = storage.getItem(slot);
            if (!stack.isEmpty()) {
                CompoundTag item = new CompoundTag();
                item.putByte("Slot", (byte) slot);
                items.add(stack.save(registryAccess(), item));
            }
        }
        tag.put("Items", items);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide) return true;
        if (source.getDirectEntity() != null && source.getEntity() != null
                && source.getDirectEntity() != source.getEntity()) return false;
        if (isInvulnerableTo(source)) return false;
        ServerLevel level = (ServerLevel) level();
        if (breakPending) return true;
        boolean creativeHit = source.getEntity() instanceof Player player && player.getAbilities().instabuild;
        damage += amount;
        if (creativeHit || damage > 4) {
            if (viewers > 0) {
                breakPending = true;
                suppressCartDrop = creativeHit;
                puller = null;
                pullerId = null;
            } else {
                suppressCartDrop = creativeHit;
                destroyCart();
            }
        }
        return true;
    }

    private void destroyCart() {
        ServerLevel level = (ServerLevel) level();
        if (level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            if (!suppressCartDrop) spawnAtLocation(getCartDrop());
            if (shouldDropChest()) spawnAtLocation(Items.CHEST);
            for (int i = 0; i < storage.getContainerSize(); i++) {
                ItemStack stack = storage.getItem(i);
                if (!stack.isEmpty()) {
                    spawnAtLocation(stack);
                    storage.setItem(i, ItemStack.EMPTY);
                }
            }
        }
        discard();
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        passenger.setPos(getX(), getY() + 0.85, getZ());
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) { return getPassengers().isEmpty(); }

    @Override
    public void positionRider(Entity passenger, MoveFunction moveFunction) {
        moveFunction.accept(passenger, getX(), getY() + 0.85, getZ());
    }

    @Override
    public boolean isPickable() { return isAlive(); }

    @Override
    public boolean isPushable() { return true; }

    @Override
    public EntityDimensions getDimensions(Pose pose) { return EntityDimensions.scalable(1.25F, 0.8F); }

    @Override
    public boolean shouldBeSaved() { return true; }

    private final class StorageMenu implements MenuProvider {
        @Override public Component getDisplayName() { return getContainerName(); }
        @Override public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
            if (storage.getContainerSize() == 54) return ChestMenu.sixRows(id, inventory, storage);
            if (storage.getContainerSize() == 36)
                return new ChestMenu(net.minecraft.world.inventory.MenuType.GENERIC_9x4, id, inventory, storage, 4);
            return ChestMenu.threeRows(id, inventory, storage);
        }
    }
}

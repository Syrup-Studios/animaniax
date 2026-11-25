package net.syrupstudios.animaniax.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.PitcherCropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public final class TillerEntity extends Entity {
    private static final TagKey<net.minecraft.world.item.Item> TILLER_SEEDS =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "seeds"));
    private final SimpleContainer seeds = new SimpleContainer(27) {
        @Override public void startOpen(Player player) { viewers++; }
        @Override public void stopOpen(Player player) { viewers = Math.max(0, viewers - 1); }
    };
    private UUID pullerId;
    private Entity puller;
    private float damage;
    private double verticalVelocity;
    private int viewers;
    private boolean breakPending;
    private boolean suppressTillerDrop;

    public TillerEntity(EntityType<? extends TillerEntity> type, Level level) { super(type, level); noCulling = true; }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) { }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (breakPending) return InteractionResult.PASS;
        ItemStack held = player.getItemInHand(hand);
        if (player.isSecondaryUseActive()) {
            if (!level().isClientSide) player.openMenu(new MenuProvider() {
                public Component getDisplayName() { return Component.translatable("container.animaniax.tiller"); }
                public AbstractContainerMenu createMenu(int id, Inventory inventory, Player user) { return ChestMenu.threeRows(id, inventory, seeds); }
            });
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (!level().isClientSide && held.isEmpty()) {
            if (pullerId != null) {
                if (puller != null) {
                    level().playSound(null, getX(), getY(), getZ(), AnimaniaSounds.VEHICLE_UNHITCH,
                            SoundSource.PLAYERS, 0.7F, 1.5F);
                }
                puller = null; pullerId = null; return InteractionResult.SUCCESS;
            }
            Mob animal = level().getEntitiesOfClass(Mob.class, getBoundingBox().inflate(3), mob ->
                    (mob instanceof AbstractHorse || mob instanceof net.minecraft.world.entity.animal.Pig)
                            && mob.isLeashed() && mob.getLeashHolder() == player).stream().findFirst().orElse(null);
            if (animal != null) {
                animal.dropLeash(true, true);
                puller = animal;
                pullerId = animal.getUUID();
            } else {
                Entity candidate = player.getVehicle();
                if (!(candidate instanceof AbstractHorse) && !(candidate instanceof net.minecraft.world.entity.animal.Pig)) candidate = player;
                puller = candidate;
                pullerId = candidate.getUUID();
            }
            level().playSound(null, getX(), getY(), getZ(), AnimaniaSounds.VEHICLE_HITCH,
                    SoundSource.PLAYERS, 0.7F, 1.5F);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (breakPending && viewers == 0) { destroyTiller(suppressTillerDrop); return; }
        if (!breakPending && damage > 0) damage = Math.max(0, damage - 0.1F);
        if (puller == null && pullerId != null && level() instanceof ServerLevel server) puller = server.getEntity(pullerId);
        if (puller != null && (!puller.isAlive() || puller.level() != level()
                || Math.abs(puller.getX() - getX()) > 12 || Math.abs(puller.getZ() - getZ()) > 12)) {
            level().playSound(null, getX(), getY(), getZ(), AnimaniaSounds.VEHICLE_UNHITCH,
                    SoundSource.PLAYERS, 0.7F, 1.5F);
            puller = null; pullerId = null;
        }
        if (puller != null) {
            double oldX = getX(), oldZ = getZ();
            double yaw = Math.toRadians(puller.getYRot());
            double tx = puller.getX() + Math.sin(yaw) * 2.5, tz = puller.getZ() - Math.cos(yaw) * 2.5;
            double dx = tx - getX(), dz = tz - getZ(), distance = Math.sqrt(dx * dx + dz * dz);
            verticalVelocity = onGround() ? 0 : verticalVelocity - 0.04;
            double step = Math.min(0.18, Math.max(0, distance - 1.15) * 0.12);
            move(MoverType.SELF, new Vec3(distance > 0.001 ? dx / distance * step : 0, verticalVelocity, distance > 0.001 ? dz / distance * step : 0));
            setYRot((float)Math.toDegrees(Math.atan2(-dx, dz)));
            if (Math.abs(getX() - oldX) + Math.abs(getZ() - oldZ) > 1.0E-6) tillBehind();
        } else {
            verticalVelocity = onGround() ? 0 : verticalVelocity - 0.04;
            move(MoverType.SELF, new Vec3(0, verticalVelocity, 0));
        }
    }

    private void tillBehind() {
        BlockPos center = BlockPos.containing(getX(), getBoundingBox().minY - 1.0E-3, getZ());
        for (int offset = -1; offset <= 1; offset++) {
            BlockPos pos = center.relative(net.minecraft.core.Direction.fromYRot(getYRot()).getClockWise(), offset);
            if (!level().getBlockState(pos.above()).isAir()) continue;
            BlockState state = level().getBlockState(pos);
            if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT)) {
                if (!level().setBlock(pos, Blocks.FARMLAND.defaultBlockState(), 3)) continue;
            } else if (!state.is(Blocks.FARMLAND)) continue;
            for (int i = 0; i < seeds.getContainerSize(); i++) {
                ItemStack stack = seeds.getItem(i);
                BlockState crop = seedCrop(stack);
                if (crop != null && crop.canSurvive(level(), pos.above())) {
                    if (level().setBlock(pos.above(), crop, 3)) stack.shrink(1);
                    break;
                }
            }
        }
    }

    private BlockState seedCrop(ItemStack stack) {
        if (!stack.is(TILLER_SEEDS) || !(stack.getItem() instanceof BlockItem item)) return null;
        if (item.getBlock() instanceof CropBlock crop) return crop.getStateForAge(0);
        if (item.getBlock() instanceof StemBlock stem)
            return stem.defaultBlockState().setValue(StemBlock.AGE, 0);
        if (item.getBlock() instanceof PitcherCropBlock pitcher)
            return pitcher.defaultBlockState().setValue(PitcherCropBlock.AGE, 0)
                    .setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
        return null;
    }

    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        pullerId = tag.hasUUID("Puller") ? tag.getUUID("Puller") : null;
        seeds.clearContent();
        ListTag items = tag.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < items.size(); i++) {
            CompoundTag item = items.getCompound(i); int slot = item.getByte("Slot") & 255;
            if (slot < seeds.getContainerSize()) seeds.setItem(slot, ItemStack.parse(registryAccess(), item).orElse(ItemStack.EMPTY));
        }
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        if (pullerId != null) tag.putUUID("Puller", pullerId);
        ListTag items = new ListTag();
        for (int slot = 0; slot < seeds.getContainerSize(); slot++) if (!seeds.getItem(slot).isEmpty()) {
            CompoundTag item = new CompoundTag(); item.putByte("Slot", (byte)slot);
            items.add(seeds.getItem(slot).save(registryAccess(), item));
        }
        tag.put("Items", items);
    }
    @Override public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide) return true;
        if (isInvulnerableTo(source)) return false;
        if (breakPending) return true;
        boolean creativeHit = source.getEntity() instanceof Player player && player.getAbilities().instabuild;
        damage += amount;
        if (creativeHit || damage > 4) {
            suppressTillerDrop = creativeHit;
            puller = null;
            pullerId = null;
            if (viewers > 0) breakPending = true;
            else destroyTiller(suppressTillerDrop);
        }
        return true;
    }
    private void destroyTiller(boolean creative) {
        if (level() instanceof ServerLevel server && server.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            if (!creative) spawnAtLocation(TillerContent.TILLER_ITEM);
            for (int i = 0; i < seeds.getContainerSize(); i++) {
                ItemStack stack = seeds.getItem(i);
                if (!stack.isEmpty()) { spawnAtLocation(stack.copy()); seeds.setItem(i, ItemStack.EMPTY); }
            }
        }
        discard();
    }
    @Override protected void addPassenger(Entity passenger) { super.addPassenger(passenger); passenger.setPos(getX(), getY() + 0.85, getZ()); }
    @Override protected boolean canAddPassenger(Entity passenger) { return getPassengers().isEmpty(); }
    @Override public void positionRider(Entity passenger, MoveFunction moveFunction) { moveFunction.accept(passenger, getX(), getY() + 0.85, getZ()); }
    @Override public boolean isPickable() { return isAlive(); }
    @Override public boolean isPushable() { return true; }
    @Override public net.minecraft.world.entity.EntityDimensions getDimensions(net.minecraft.world.entity.Pose pose) { return net.minecraft.world.entity.EntityDimensions.scalable(1.25F, 0.8F); }
}

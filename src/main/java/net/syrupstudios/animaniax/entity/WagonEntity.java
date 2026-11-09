package net.syrupstudios.animaniax.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.network.chat.Component;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.stats.Stats;
//? if neoforge {
/*
import com.mojang.datafixers.util.Either;
import net.minecraft.util.Unit;
*///?}
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class WagonEntity extends PullCartEntity {
    private static final EntityDataAccessor<Integer> SLEEP_LIGHT_TICKS =
            SynchedEntityData.defineId(WagonEntity.class, EntityDataSerializers.INT);

    public WagonEntity(EntityType<? extends WagonEntity> type, Level level) {
        super(type, level, 54);
        setHasChest(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SLEEP_LIGHT_TICKS, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && getSleepLightTicks() > 0) {
            entityData.set(SLEEP_LIGHT_TICKS, getSleepLightTicks() - 1);
        }
    }

    public int getSleepLightTicks() { return entityData.get(SLEEP_LIGHT_TICKS); }

    public static boolean supportsSleepAt(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return false;
        return player.getSleepingPos().map(pos -> !serverLevel.getEntitiesOfClass(WagonEntity.class,
                new AABB(pos).inflate(1.0), wagon -> wagon.blockPosition().equals(pos)).isEmpty()).orElse(false);
    }

    private void wakeSleepingPlayers() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        BlockPos pos = blockPosition();
        for (ServerPlayer player : serverLevel.players()) {
            if (player.getSleepingPos().filter(pos::equals).isPresent()) player.stopSleepInBed(true, true);
        }
    }

    @Override
    protected void beforeCartMovement() { wakeSleepingPlayers(); }

    @Override
    public void remove(RemovalReason reason) {
        wakeSleepingPlayers();
        super.remove(reason);
    }

    @Override
    protected ItemStack getCartDrop() { return new ItemStack(WagonContent.WAGON_ITEM); }

    @Override
    protected boolean hasBuiltInChest() { return true; }

    @Override
    public InteractionResult interactAt(Player player, Vec3 hit, InteractionHand hand) {
        if (isBreakPending()) return InteractionResult.PASS;
        if (player.getVehicle() == this) return InteractionResult.sidedSuccess(level().isClientSide);
        if (hit.y > 0.65 && player.getItemInHand(hand).isEmpty() && !player.isSecondaryUseActive()
                && !player.isPassenger()) {
            if (level().isClientSide) return InteractionResult.sidedSuccess(true);
            if (player instanceof ServerPlayer serverPlayer && level() instanceof ServerLevel serverLevel) {
                if (!serverLevel.dimensionType().bedWorks()) {
                    player.displayClientMessage(Component.translatable("block.minecraft.bed.not_possible_here"), true);
                } else if (serverLevel.isDay() && !serverLevel.isThundering()) {
                    player.displayClientMessage(Component.translatable("block.minecraft.bed.no_sleep"), true);
                } else if (!serverPlayer.getAbilities().instabuild && serverLevel.getEntitiesOfClass(Monster.class,
                        new AABB(player.blockPosition()).inflate(8.0, 5.0, 8.0),
                        monster -> monster.isPreventingPlayerRest(player)).size() > 0) {
                    player.displayClientMessage(Component.translatable("block.minecraft.bed.not_safe"), true);
                } else if (player.isSleeping() || !player.isAlive()) {
                    return InteractionResult.PASS;
                } else {
                    BlockPos sleepPos = blockPosition();
                    //? if neoforge {
                    /*
                    var sleepResult = net.neoforged.neoforge.event.EventHooks.canPlayerStartSleeping(
                            serverPlayer, sleepPos, Either.right(Unit.INSTANCE));
                    if (sleepResult.left().isPresent()) {
                        Component problem = sleepResult.left().get().getMessage();
                        if (problem != null) player.displayClientMessage(problem, true);
                        return InteractionResult.CONSUME;
                    }
                    *///?}
                    serverPlayer.startSleeping(sleepPos);
                    serverPlayer.awardStat(Stats.SLEEP_IN_BED);
                    CriteriaTriggers.SLEPT_IN_BED.trigger(serverPlayer);
                    serverLevel.updateSleepingPlayerList();
                    entityData.set(SLEEP_LIGHT_TICKS, 120);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.CONSUME;
            }
        }
        if (!player.getItemInHand(hand).isEmpty() || player.isSecondaryUseActive()
                || player.getVehicle() instanceof net.minecraft.world.entity.animal.horse.AbstractHorse
                || player.getVehicle() instanceof net.minecraft.world.entity.animal.Pig) return InteractionResult.PASS;
        double yaw = Math.toRadians(getYRot());
        double forward = -Math.sin(yaw) * hit.x + Math.cos(yaw) * hit.z;
        if (forward <= 0.28) return InteractionResult.PASS;
        if (level().isClientSide) return InteractionResult.sidedSuccess(true);
        if (!player.startRiding(this)) return InteractionResult.PASS;
        setOwnerIfMissing(player);
        stopPulling(player);
        return InteractionResult.sidedSuccess(false);
    }

    @Override
    protected boolean shouldDropChest() { return false; }

    @Override
    protected Component getContainerName() { return Component.translatable("container.animaniax.wagon"); }
}

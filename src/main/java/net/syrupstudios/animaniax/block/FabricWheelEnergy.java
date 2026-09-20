//? if fabric {
package net.syrupstudios.animaniax.block;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

public final class FabricWheelEnergy {
    private FabricWheelEnergy() {}

    public static void register() {
        EnergyStorage.SIDED.registerForBlockEntity((wheel, direction) -> wheel.fabricEnergy, HamsterWheelContent.ENTITY);
    }

    public static void push(HamsterWheelBlockEntity wheel) {
        if (!(wheel.getLevel() instanceof ServerLevel level) || wheel.getEnergy() == 0) return;
        Storage source = wheel.fabricEnergy;
        BlockPos pos = wheel.getBlockPos();
        for (Direction direction : Direction.values()) {
            if (source.getAmount() == 0) break;
            BlockPos neighborPos = pos.relative(direction);
            if (!level.hasChunkAt(neighborPos) || level.getBlockEntity(neighborPos) == wheel) continue;
            EnergyStorage target = EnergyStorage.SIDED.find(level, neighborPos, direction.getOpposite());
            if (target != null && target.supportsInsertion()) {
                EnergyStorageUtil.move(source, target, Long.MAX_VALUE, null);
            }
        }
    }

    static final class Storage extends SnapshotParticipant<Integer> implements EnergyStorage {
        private final HamsterWheelBlockEntity wheel;

        Storage(HamsterWheelBlockEntity wheel) {
            this.wheel = wheel;
        }

        @Override
        protected Integer createSnapshot() {
            return wheel.getEnergy();
        }

        @Override
        protected void readSnapshot(Integer snapshot) {
            wheel.setEnergyFromTransfer(snapshot);
        }

        @Override
        protected void onFinalCommit() {
            wheel.markEnergyChanged();
        }

        @Override
        public boolean supportsInsertion() {
            return false;
        }

        @Override
        public long insert(long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notNegative(maxAmount);
            return 0;
        }

        @Override
        public boolean supportsExtraction() {
            return true;
        }

        @Override
        public long extract(long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notNegative(maxAmount);
            int extracted = (int) Math.min(maxAmount, wheel.getEnergy());
            if (extracted > 0) {
                updateSnapshots(transaction);
                wheel.setEnergyFromTransfer(wheel.getEnergy() - extracted);
            }
            return extracted;
        }

        @Override
        public long getAmount() {
            return wheel.getEnergy();
        }

        @Override
        public long getCapacity() {
            return wheel.getEnergyCapacity();
        }
    }
}
//?}

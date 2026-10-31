//? if fabric {
package net.syrupstudios.animaniax.block;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public final class BeeHiveFluidStorage extends SnapshotParticipant<Integer> implements SingleSlotStorage<FluidVariant> {
    private static final long DROPLETS_PER_MB = FluidConstants.BUCKET / 1000;
    private final BeeHiveBlockEntity hive;
    private final FluidVariant honey = FluidVariant.of(BeeHiveContent.HONEY_SOURCE);

    public BeeHiveFluidStorage(BeeHiveBlockEntity hive) {
        this.hive = hive;
    }

    public static void register() {
        FluidStorage.SIDED.registerForBlockEntity((hive, side) -> hive.getFluidStorage(), BeeHiveContent.ENTITY);
    }

    @Override public FluidVariant getResource() { return hive.getHoney() == 0 ? FluidVariant.blank() : honey; }
    @Override public boolean isResourceBlank() { return hive.getHoney() == 0; }
    @Override public long getAmount() { return hive.getHoney() * DROPLETS_PER_MB; }
    @Override public long getCapacity() { return BeeHiveBlockEntity.CAPACITY * DROPLETS_PER_MB; }

    @Override public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        if (!honey.equals(resource)) return 0;
        int inserted = (int) Math.min(maxAmount / DROPLETS_PER_MB,
                BeeHiveBlockEntity.CAPACITY - hive.getHoney());
        if (inserted > 0) {
            updateSnapshots(transaction);
            hive.setHoneyRaw(hive.getHoney() + inserted);
        }
        return inserted * DROPLETS_PER_MB;
    }

    @Override public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        if (!honey.equals(resource)) return 0;
        int extracted = (int) Math.min(maxAmount / DROPLETS_PER_MB, hive.getHoney());
        if (extracted > 0) {
            updateSnapshots(transaction);
            hive.setHoneyRaw(hive.getHoney() - extracted);
        }
        return extracted * DROPLETS_PER_MB;
    }

    @Override protected Integer createSnapshot() { return hive.getHoney(); }
    @Override protected void readSnapshot(Integer snapshot) { hive.setHoneyRaw(snapshot); }
    @Override protected void onFinalCommit() { hive.setChanged(); }
}
//?}

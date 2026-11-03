package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.syrupstudios.animaniax.config.AnimalCareConfig;

public final class BeeHiveBlockEntity extends BlockEntity {
    public static final int CAPACITY = 5000;
    public static final int BOTTLE_AMOUNT = 1000;
    private static final ResourceKey<DamageType> BEE_STING = ResourceKey.create(Registries.DAMAGE_TYPE,
            BeeHiveContent.id("bee_sting"));
    private static final TagKey<Biome> HIVE_BIOMES = TagKey.create(Registries.BIOME,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("animaniax", "hive_spawns"));
    private int honey;
    private int nextHoney = 400;
    //? if fabric {
    private final BeeHiveFluidStorage fluidStorage = new BeeHiveFluidStorage(this);
    public BeeHiveFluidStorage getFluidStorage() { return fluidStorage; }
    //?}

    public BeeHiveBlockEntity(BlockPos pos, BlockState state) { super(BeeHiveContent.ENTITY, pos, state); }

    public int getHoney() { return honey; }

    void setHoney(int honey) {
        setHoneyRaw(honey);
        setChanged();
    }

    void setHoneyRaw(int honey) { this.honey = Math.max(0, Math.min(CAPACITY, honey)); }

    public void removeHoney(int amount) {
        honey = Math.max(0, honey - amount);
        setChanged();
    }

    @Override public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Honey", honey);
        return tag;
    }

    @Override public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static void serverTick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, BeeHiveBlockEntity hive) {
        if (level.isClientSide) return;
        if (hive.nextHoney > 0) hive.nextHoney--;
        if (hive.nextHoney <= 0) {
            if (isValidBiome(level, pos)) {
                int previous = hive.honey;
                hive.honey = Math.min(CAPACITY, hive.honey + 25);
                if (hive.honey != previous) hive.setChanged();
                int interval = state.getBlock() == BeeHiveContent.WILD
                        ? AnimalCareConfig.hiveWildHoneyRate
                        : AnimalCareConfig.hivePlayermadeHoneyRate;
                hive.nextHoney = interval + level.random.nextInt(100);
            }
        }
        if (state.getBlock() == BeeHiveContent.WILD && level.random.nextInt(10) == 0) {
            for (Player player : level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(2)))
                if (level.random.nextInt(3) == 0) player.hurt(level.damageSources().source(BEE_STING), 2.5F);
        }
    }

    private static boolean isValidBiome(net.minecraft.world.level.Level level, BlockPos pos) {
        var biome = level.getBiome(pos);
        return biome.is(HIVE_BIOMES);
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        honey = Math.max(0, Math.min(CAPACITY, tag.getInt("Honey")));
        nextHoney = Math.max(1, tag.contains("NextHoney") ? tag.getInt("NextHoney") : 400);
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Honey", honey);
        tag.putInt("NextHoney", nextHoney);
    }
}

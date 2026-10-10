package net.syrupstudios.animaniax.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public final class DartFrogEntity extends Frog {
    private static final EntityDataAccessor<Integer> POISON_COOLDOWN = SynchedEntityData.defineId(DartFrogEntity.class, EntityDataSerializers.INT);
    private static final int ARROW_COOLDOWN = 800;

    public DartFrogEntity(EntityType<? extends Frog> type, Level level) { super(type, level); }

    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType reason, SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        setVariant(BuiltInRegistries.FROG_VARIANT.getHolderOrThrow(
                switch (getRandom().nextInt(3)) {
                    case 0 -> FrogVariant.TEMPERATE;
                    case 1 -> FrogVariant.WARM;
                    default -> FrogVariant.COLD;
                }));
        return result;
    }

    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(2, new AmphibianAvoidPlayerGoal(this));
        goalSelector.addGoal(5, new AmphibianAvoidPredatorGoal(this));
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(POISON_COOLDOWN, 2);
    }

    @Override public void tick() {
        super.tick();
        if (!level().isClientSide && getPoisonCooldown() > 0) setPoisonCooldown(getPoisonCooldown() - 1);
        if (!level().isClientSide) {
            for (Player player : level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(0.1))) {
                if (player.isAlive() && player.getBoundingBox().intersects(getBoundingBox())) player.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1));
            }
        }
    }

    @Override public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack arrow = player.getItemInHand(hand);
        if (arrow.is(Items.ARROW) && getPoisonCooldown() <= 1) {
            if (level().isClientSide) return InteractionResult.SUCCESS;
            player.playSound(SoundEvents.MAGMA_CUBE_SQUISH, 0.2F, 1.8F);
            ItemStack tipped = PotionContents.createItemStack(Items.TIPPED_ARROW, Potions.POISON);
            if (!player.getAbilities().instabuild) arrow.shrink(1);
            if (!player.addItem(tipped)) player.drop(tipped, false);
            setPoisonCooldown(ARROW_COOLDOWN);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    public int getPoisonCooldown() { return entityData.get(POISON_COOLDOWN); }
    private void setPoisonCooldown(int value) { entityData.set(POISON_COOLDOWN, value); }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("PoisonCooldown", getPoisonCooldown());
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setPoisonCooldown(Math.max(0, tag.getInt("PoisonCooldown")));
    }

    @Override public boolean isFood(ItemStack stack) { return false; }
    @Override public boolean canFallInLove() { return false; }
    @Override public boolean canMate(net.minecraft.world.entity.animal.Animal other) { return false; }
    @Override public Frog getBreedOffspring(ServerLevel level, AgeableMob mate) { return null; }
}

package net.syrupstudios.animaniax.entity;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.sound.AnimaniaSounds;
import org.jetbrains.annotations.Nullable;

public final class PeafowlEntity extends Chicken implements PeafowlMorphState {
    private static final EntityDataAccessor<Boolean> DATA_HEN = SynchedEntityData.defineId(
            PeafowlEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> DATA_MORPH = SynchedEntityData.defineId(
            PeafowlEntity.class, EntityDataSerializers.STRING);
    public static final List<String> MORPHS = List.of("Blue", "White", "Taupe", "Purple", "Peach", "Opal", "Charcoal");
    public static final EntityType<PeafowlEntity> TYPE = EntityType.Builder.of(PeafowlEntity::new, MobCategory.CREATURE)
            .sized(0.4F, 0.7F).clientTrackingRange(8).build("animaniax:peafowl");
    public static final Item SPAWN_EGG = new SpawnEggItem(TYPE, 0x315B9A, 0xD5C6A5, new Item.Properties());
    private boolean animaniax$lastBabyState;

    public PeafowlEntity(EntityType<? extends Chicken> type, Level level) {
        super(type, level);
        animaniax$lastBabyState = isBaby();
        if (!level.isClientSide) animaniax$setMorph(MORPHS.get(getRandom().nextInt(MORPHS.size())));
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HEN, false);
        builder.define(DATA_MORPH, MORPHS.get(0));
    }

    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        boolean baby = isBaby();
        if (key == DATA_HEN || baby != animaniax$lastBabyState) refreshDimensions();
        animaniax$lastBabyState = baby;
    }

    @Override public void setAge(int age) {
        boolean wasBaby = isBaby();
        super.setAge(age);
        animaniax$lastBabyState = isBaby();
        if (wasBaby != animaniax$lastBabyState) refreshDimensions();
    }

    @Override public EntityDimensions getDefaultDimensions(Pose pose) {
        if (isBaby()) return super.getDefaultDimensions(pose);
        return EntityDimensions.scalable(animaniax$isSyncedHen() ? 0.6F : 0.8F,
                animaniax$isSyncedHen() ? 1.2F : 1.6F);
    }

    public boolean animaniax$isSyncedHen() { return entityData.get(DATA_HEN); }
    public void animaniax$setSyncedHen(boolean hen) { entityData.set(DATA_HEN, hen); }

    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType reason, @Nullable SpawnGroupData groupData) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, groupData);
        if (reason != MobSpawnType.NATURAL && reason != MobSpawnType.CHUNK_GENERATION) {
            ((ChickenSexState) (Object) this).animaniax$isHen();
            return result;
        }

        PeafowlSpawnGroupData data;
        if (result instanceof PeafowlSpawnGroupData peafowlData) {
            data = peafowlData;
        } else {
            data = PeafowlSpawnGroupData.create(getRandom());
            if (result != null && result.getClass() == AgeableMob.AgeableMobGroupData.class)
                result = data.withGroupData((AgeableMob.AgeableMobGroupData) result);
        }
        animaniax$setMorph(data.morph);
        setBaby(data.chick);
        if (!data.chick) ((ChickenSexState) (Object) this).animaniax$setHen(data.hen);
        ((ChickenSexState) (Object) this).animaniax$isHen();
        return result;
    }

    @Override protected void registerGoals() {
        super.registerGoals();
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
        goalSelector.addGoal(8, new LeapAtTargetGoal(this, 0.2F) {
            @Override public boolean canUse() {
                return AnimalCareConfig.animalsCanAttackOthers && super.canUse();
            }

            @Override public boolean canContinueToUse() {
                return AnimalCareConfig.animalsCanAttackOthers && super.canContinueToUse();
            }
        });
        goalSelector.addGoal(9, new MeleeAttackGoal(this, 1.0D, true) {
            @Override public boolean canUse() {
                return AnimalCareConfig.animalsCanAttackOthers && super.canUse();
            }

            @Override public boolean canContinueToUse() {
                return AnimalCareConfig.animalsCanAttackOthers && super.canContinueToUse();
            }
        });
    }

    @Override public boolean doHurtTarget(Entity target) {
        var source = damageSources().mobAttack(this);
        boolean hit = target.hurt(source, 2.0F);
        if (hit && level() instanceof ServerLevel serverLevel)
            EnchantmentHelper.doPostAttackEffects(serverLevel, target, source);
        if (target instanceof CommonFrogEntity || target instanceof DartFrogEntity || target instanceof ToadEntity)
            ((AnimalCareState) (Object) this).animaniax$graze();
        if (target instanceof Player player)
            player.knockback(1.0D, getX() - target.getX(), getZ() - target.getZ());
        return hit;
    }

    @Override public String animaniax$getMorph() { return entityData.get(DATA_MORPH); }
    @Override public void animaniax$setMorph(String value) {
        if (MORPHS.contains(value)) entityData.set(DATA_MORPH, value);
    }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("AnimaniaxPeafowlMorph", animaniax$getMorph());
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        animaniax$setMorph(tag.getString("AnimaniaxPeafowlMorph"));
    }

    @Override protected SoundEvent getAmbientSound() {
        return AnimaniaSounds.PEAFOWL_AMBIENT;
    }

    @Override protected SoundEvent getHurtSound(DamageSource source) {
        return AnimaniaSounds.PEAFOWL_HURT;
    }

    @Override protected SoundEvent getDeathSound() {
        return AnimaniaSounds.PEAFOWL_HURT;
    }

    @Override protected float getSoundVolume() {
        return isBaby() ? 0.3F : 0.45F;
    }

    @Override public Chicken getBreedOffspring(ServerLevel level, AgeableMob mate) {
        return TYPE.create(level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Chicken.createAttributes().add(Attributes.ATTACK_DAMAGE, 1.5D);
    }

    private static final class PeafowlSpawnGroupData extends AgeableMob.AgeableMobGroupData {
        private final String morph;
        private final boolean hen;
        private final boolean chick;

        private PeafowlSpawnGroupData(String morph, boolean hen, boolean chick) {
            super(false);
            this.morph = morph;
            this.hen = hen;
            this.chick = chick;
        }

        private PeafowlSpawnGroupData(float babySpawnChance, int groupSize, String morph, boolean hen, boolean chick) {
            super(babySpawnChance);
            this.morph = morph;
            this.hen = hen;
            this.chick = chick;
            copyGroupSize(groupSize);
        }

        private PeafowlSpawnGroupData(int groupSize, String morph, boolean hen, boolean chick) {
            super(false);
            this.morph = morph;
            this.hen = hen;
            this.chick = chick;
            copyGroupSize(groupSize);
        }

        private PeafowlSpawnGroupData withGroupData(AgeableMob.AgeableMobGroupData groupData) {
            return groupData.isShouldSpawnBaby()
                    ? new PeafowlSpawnGroupData(groupData.getBabySpawnChance(), groupData.getGroupSize(), morph, hen, chick)
                    : new PeafowlSpawnGroupData(groupData.getGroupSize(), morph, hen, chick);
        }

        private static PeafowlSpawnGroupData create(net.minecraft.util.RandomSource random) {
            int form = random.nextInt(5);
            return new PeafowlSpawnGroupData(MORPHS.get(random.nextInt(MORPHS.size())), form == 2 || form == 3,
                    form == 4);
        }

        private void copyGroupSize(int groupSize) {
            for (int i = 0; i < groupSize; i++) increaseGroupSizeByOne();
        }
    }

    public static void registerFabric() {
        //? if fabric {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("peafowl"), TYPE);
        Registry.register(BuiltInRegistries.ITEM, id("peafowl_spawn_egg"), SPAWN_EGG);
        net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(TYPE, createAttributes());
        //?}
    }

    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("animaniax", path); }
}

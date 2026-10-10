package net.syrupstudios.animaniax.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.CatVariant;
import net.syrupstudios.animaniax.entity.ai.JuvenilePlayGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.syrupstudios.animaniax.config.AnimalCareConfig;

public class CompanionCatEntity extends Cat {
    private JuvenilePlayGoal playGoal;
    private static final EntityDataAccessor<String> BREED = SynchedEntityData.defineId(CompanionCatEntity.class, EntityDataSerializers.STRING);

    public CompanionCatEntity(EntityType<? extends Cat> type, Level level) { super(type, level); }

    @Override protected void registerGoals() {
        super.registerGoals();
        targetSelector.removeAllGoals(goal -> goal instanceof NonTameRandomTargetGoal<?>);
        playGoal = new JuvenilePlayGoal(this, CompanionCatEntity.class);
        goalSelector.addGoal(7, playGoal);
        targetSelector.addGoal(2, new NonTameRandomTargetGoal<LivingEntity>(this, LivingEntity.class, true,
                target -> target instanceof CommonFrogEntity || target instanceof ToadEntity
                        || target instanceof PeafowlEntity peafowl && peafowl.isBaby()
                        || target.getType() == EntityType.CHICKEN && target.isBaby()
                        || target instanceof HamsterEntity || target instanceof FerretEntity
                        || target instanceof net.minecraft.world.entity.monster.Silverfish
                        || target instanceof HedgehogEntity) {
            @Override public boolean canUse() {
                return animaniax$canHunt() && super.canUse();
            }

            @Override public boolean canContinueToUse() {
                return animaniax$canHunt() && super.canContinueToUse();
            }
        });
    }

    private boolean animaniax$canHunt() {
        return AnimalCareConfig.animalsCanAttackOthers && !isOrderedToSit() && !isSleeping()
                && !((AnimalSleepState) (Object) this).animaniax$isSleeping();
    }

    public JuvenilePlayGoal animaniax$getPlayGoal() { return playGoal; }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BREED, CatBreed.TABBY.id());
    }

    public CatBreed getBreed() { return CatBreed.byId(entityData.get(BREED)); }
    public void setBreed(CatBreed breed) {
        entityData.set(BREED, breed.id());
        setVariant(BuiltInRegistries.CAT_VARIANT.getHolderOrThrow(switch (breed) {
            case AMERICAN_SHORTHAIR -> CatVariant.BRITISH_SHORTHAIR;
            case ASIATIC -> CatVariant.CALICO;
            case EXOTIC -> CatVariant.PERSIAN;
            case NORWEGIAN -> CatVariant.WHITE;
            case OCELOT -> CatVariant.RED;
            case RAGDOLL -> CatVariant.RAGDOLL;
            case SIAMESE -> CatVariant.SIAMESE;
            case TABBY -> CatVariant.TABBY;
        }));
    }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("AnimaniaxBreed", getBreed().id());
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setBreed(CatBreed.byId(tag.getString("AnimaniaxBreed")));
    }

    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
            net.minecraft.world.entity.MobSpawnType reason, SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        boolean natural = reason == net.minecraft.world.entity.MobSpawnType.NATURAL;
        boolean spawnEgg = reason == net.minecraft.world.entity.MobSpawnType.SPAWN_EGG;
        setBreed(natural ? CatBreed.OCELOT : spawnEgg ? CatBreed.random(getRandom()) : CatBreed.TABBY);
        if (natural || spawnEgg) ((AnimalReproductionState) this).animaniax$setFemale(natural || getRandom().nextBoolean());
        return result;
    }

    @Override public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        boolean sleeping = ((AnimalSleepState) (Object) this).animaniax$isSleeping() || isSleeping();
        if (isFood(held) && sleeping)
            return InteractionResult.SUCCESS;
        if (sleeping && held.is(net.minecraft.world.item.Items.WATER_BUCKET))
            return InteractionResult.SUCCESS;
        if (isFood(held)) {
            if (!level().isClientSide) {
                if (!isTame()) {
                    setOwnerUUID(player.getUUID());
                    setTame(true, true);
                }
                ItemStack feed = held.copy();
                ((AnimalCareState) this).animaniax$feedForCare();
                AnimalReproductionState reproduction = (AnimalReproductionState) this;
                AnimalSterilizationState sterilization = (AnimalSterilizationState) this;
                if (!isBaby() && !isInLove() && canFallInLove()
                        && !reproduction.animaniax$isPregnant() && !sterilization.animaniax$isSterilized()) setInLove(player);
                if (!player.getAbilities().instabuild) held.shrink(1);
                if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)
                    net.syrupstudios.animaniax.advancement.FeedAnimalTrigger.INSTANCE.trigger(serverPlayer, this, feed);
                ((AnimalCareState) this).animaniax$markPlayerInteraction(true);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        boolean waterContainer = !sleeping && (level().isClientSide
                ? held.is(net.minecraft.world.item.Items.WATER_BUCKET)
                : giveWaterBucket(player, hand));
        if (waterContainer) {
            if (!level().isClientSide) {
                ((AnimalCareState) this).animaniax$waterForCare();
                AnimalReproductionState reproduction = (AnimalReproductionState) this;
                if (!isBaby() && !isInLove() && canFallInLove() && !reproduction.animaniax$isPregnant()
                        && !((AnimalSterilizationState) this).animaniax$isSterilized()) setInLove(player);
                ((AnimalCareState) this).animaniax$markPlayerInteraction(false);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    private boolean giveWaterBucket(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (!held.is(net.minecraft.world.item.Items.WATER_BUCKET)) return false;
        if (!player.getAbilities().instabuild) {
            held.shrink(1);
            ItemStack bucket = new ItemStack(net.minecraft.world.item.Items.BUCKET);
            if (held.isEmpty()) player.setItemInHand(hand, bucket);
            else if (!player.getInventory().add(bucket)) player.drop(bucket, false);
        }
        return true;
    }

    @Override public boolean isFood(ItemStack stack) { return AnimalProfile.acceptsPetBowlFood(getType(), stack); }

    @Override public Cat getBreedOffspring(ServerLevel level, AgeableMob mate) {
        CompanionCatEntity child = new CompanionCatEntity(CatContent.TYPE, level);
        if (mate instanceof CompanionCatEntity other) {
            child.setBreed(getRandom().nextBoolean() ? getBreed() : other.getBreed());
            if (isTame() && other.isTame() && getOwnerUUID() != null && getOwnerUUID().equals(other.getOwnerUUID())) {
                child.setOwnerUUID(getOwnerUUID());
                child.setTame(true, true);
            }
        }
        return child;
    }

    @Override public boolean canMate(Animal other) {
        if (!((AnimalReproductionState) this).animaniax$canMateReserved(other)
                || !(other instanceof CompanionCatEntity) || other.getType() != getType() || !super.canMate(other)
                || !(this instanceof AnimalReproductionState first) || !(other instanceof AnimalReproductionState second)
                || !(this instanceof AnimalSterilizationState firstSterilization)
                || !(other instanceof AnimalSterilizationState secondSterilization)
                || !(this instanceof AnimalCareState firstCare) || !(other instanceof AnimalCareState secondCare)) return false;
        return first.animaniax$isFemale() != second.animaniax$isFemale()
                && !first.animaniax$isPregnant() && !second.animaniax$isPregnant()
                && first.animaniax$fertilityCooldownRemaining() == 0
                && second.animaniax$fertilityCooldownRemaining() == 0
                && !firstSterilization.animaniax$isSterilized() && !secondSterilization.animaniax$isSterilized()
                && !firstCare.animaniax$careBlocksBreeding() && !secondCare.animaniax$careBlocksBreeding();
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return Cat.createAttributes()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 18.0D)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 0.3D)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, 2.5D);
    }
}

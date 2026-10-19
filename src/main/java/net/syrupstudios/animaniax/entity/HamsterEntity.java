package net.syrupstudios.animaniax.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.material.Fluids;
import net.syrupstudios.animaniax.block.PetBowlBlockEntity;
import net.syrupstudios.animaniax.block.PetBowlContent;
import net.syrupstudios.animaniax.block.TroughBlock;
import net.syrupstudios.animaniax.block.TroughContent;
import net.syrupstudios.animaniax.item.HamsterBallContent;
import net.syrupstudios.animaniax.advancement.FeedAnimalTrigger;
import net.syrupstudios.animaniax.config.AnimalCareConfig;

import java.util.EnumSet;

public final class HamsterEntity extends TamableAnimal {
    public static final EntityType<HamsterEntity> TYPE = EntityType.Builder.of(HamsterEntity::new, MobCategory.CREATURE)
            .sized(0.35F, 0.3F).clientTrackingRange(8).build("animaniax:hamster");
    public static final Item SPAWN_EGG = new SpawnEggItem(TYPE, 0xB88958, 0xE5D6C2, new Item.Properties());
    private static final TagKey<Item> HAMSTER_FOODS = TagKey.create(Registries.ITEM, id("hamster_foods"));
    private static final EntityDataAccessor<Boolean> IN_BALL = net.minecraft.network.syncher.SynchedEntityData.defineId(HamsterEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BALL_COLOR = net.minecraft.network.syncher.SynchedEntityData.defineId(HamsterEntity.class, EntityDataSerializers.INT);
    private static final String STORED_FOOD_TAG = "StoredFood";
    private static final String FED_TICKS_TAG = "FedTicks";
    private static final String WATERED_TICKS_TAG = "WateredTicks";
    private static final String CARE_INTERACTED_TAG = "CareInteracted";
    private static final String IDLE_FOOD_TICKS_TAG = "IdleFoodTicks";
    private int storedFood;
    private int idleFoodTicks = 5000;
    private int fedTicks;
    private int wateredTicks;
    private int starvationTimer;
    private boolean careInteracted;

    public HamsterEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        setHealth(6.0F);
        fedTicks = foodTicks();
        wateredTicks = waterTicks(4, 200);
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IN_BALL, false);
        builder.define(BALL_COLOR, -1);
    }

    public boolean isInBall() { return entityData.get(IN_BALL); }
    public int getBallColor() { return entityData.get(BALL_COLOR); }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.2, 5.0F, 2.0F));
        goalSelector.addGoal(3, new FleeSunGoal(this, 1.0));
        goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new HamsterWaterGoal());
        goalSelector.addGoal(2, new HamsterBowlFoodGoal());
        goalSelector.addGoal(3, new TemptGoal(this, 1.2, net.minecraft.world.item.crafting.Ingredient.of(HAMSTER_FOODS), false));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            boolean careActive = careActive();
            if (careActive) {
                if (fedTicks > 0) fedTicks--;
                if (wateredTicks > 0) wateredTicks--;
            }
            starvationTimer = PetCarePenalty.tick(this, fedTicks == 0, wateredTicks == 0, careActive,
                    ((AnimalSleepState) (Object) this).animaniax$isSleeping(), starvationTimer);
        }
        if (!level().isClientSide) {
            if (getHealth() < getMaxHealth()) {
                if (storedFood > 0) {
                    storedFood--;
                    heal(1.0F);
                }
                idleFoodTicks = 5000;
            } else if (storedFood > 0) {
                if (idleFoodTicks == 0) {
                    if (getRandom().nextInt(30) == 0 && getRandom().nextInt(30) == 0) {
                        storedFood--;
                        heal(1.0F);
                        idleFoodTicks = 5000;
                    }
                } else {
                    idleFoodTicks--;
                }
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        int ballColor = HamsterBallContent.colorOf(held.getItem());
        boolean sleeping = ((AnimalSleepState) (Object) this).animaniax$isSleeping();
        if (sleeping && (isFood(held)
                || net.syrupstudios.animaniax.item.WaterContainerTransfer.isWaterContainer(player, hand)))
            return InteractionResult.sidedSuccess(level().isClientSide);
        if (!sleeping && !isInBall() && ballColor != -2) {
            if (!level().isClientSide) {
                setOrderedToSit(false);
                entityData.set(BALL_COLOR, ballColor);
                entityData.set(IN_BALL, true);
                getNavigation().stop();
                if (!player.getAbilities().instabuild) held.shrink(1);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (!sleeping && isInBall() && held.isEmpty()) {
            if (!level().isClientSide) {
                if (!player.getAbilities().instabuild) {
                    ItemStack ball = new ItemStack(HamsterBallContent.itemFor(getBallColor()));
                    if (!player.getInventory().add(ball)) player.drop(ball, false);
                }
                entityData.set(IN_BALL, false);
                entityData.set(BALL_COLOR, -1);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        boolean waterContainer = !sleeping && (level().isClientSide
                ? net.syrupstudios.animaniax.item.WaterContainerTransfer.isWaterContainer(player, hand)
                : net.syrupstudios.animaniax.item.WaterContainerTransfer.water(player, hand));
        if (waterContainer) {
            if (!level().isClientSide) {
                careInteracted = true;
                wateredTicks = waterTicks(1, 100);
                player.displayClientMessage(net.minecraft.network.chat.Component.literal("Hamster drank water."), true);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (!sleeping && isFood(held)) {
            if (!level().isClientSide) {
                if (!isTame()) tame(player);
                careInteracted = true;
                fedTicks = foodTicks();
                ItemStack feedItem = held.copy();
                if (storedFood < 5) storedFood++;
                else heal(1.0F);
                if (!player.getAbilities().instabuild) held.shrink(1);
                if (player instanceof ServerPlayer serverPlayer) FeedAnimalTrigger.INSTANCE.trigger(serverPlayer, this, feedItem);
                player.displayClientMessage(net.minecraft.network.chat.Component.literal("Hamster food: " + storedFood + "/5."), true);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (isTame() && isOwnedBy(player) && held.isEmpty()) {
            if (!level().isClientSide) {
                setOrderedToSit(!isOrderedToSit());
                careInteracted = true;
                player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                        wateredTicks > 0 ? "Hamster is watered. Food: " + storedFood + "/5."
                                : "Hamster needs water. Food: " + storedFood + "/5."), true);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(HAMSTER_FOODS);
    }

    @Override
    public boolean canFallInLove() { return false; }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mate) { return null; }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(STORED_FOOD_TAG, storedFood);
        tag.putInt(IDLE_FOOD_TICKS_TAG, idleFoodTicks);
        tag.putBoolean("InBall", isInBall());
        tag.putInt("BallColor", getBallColor());
        tag.putInt(FED_TICKS_TAG, fedTicks);
        tag.putInt(WATERED_TICKS_TAG, wateredTicks);
        tag.putInt("PetStarvationTimer", starvationTimer);
        tag.putBoolean(CARE_INTERACTED_TAG, careInteracted);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        storedFood = Math.max(0, Math.min(5, tag.getInt(STORED_FOOD_TAG)));
        idleFoodTicks = tag.contains(IDLE_FOOD_TICKS_TAG)
                ? Math.max(0, Math.min(5000, tag.getInt(IDLE_FOOD_TICKS_TAG))) : 5000;
        entityData.set(IN_BALL, tag.getBoolean("InBall"));
        int color = tag.getInt("BallColor");
        entityData.set(BALL_COLOR, color >= -1 && color < net.minecraft.world.item.DyeColor.values().length ? color : -1);
        fedTicks = tag.contains(FED_TICKS_TAG)
                ? Math.max(0, Math.min(PetCarePenalty.maxFedTicks(), tag.getInt(FED_TICKS_TAG)))
                : foodTicks();
        wateredTicks = tag.contains(WATERED_TICKS_TAG)
                ? Math.max(0, tag.getInt(WATERED_TICKS_TAG))
                : waterTicks(4, 200);
        starvationTimer = Math.max(0, Math.min(AnimalCareConfig.starvationTimer, tag.getInt("PetStarvationTimer")));
        careInteracted = tag.contains(CARE_INTERACTED_TAG)
                ? tag.getBoolean(CARE_INTERACTED_TAG) : isTame();
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        if (isInBall() && level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))
            spawnAtLocation(new ItemStack(HamsterBallContent.itemFor(getBallColor())));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    private int waterTicks(int multiplier, int randomBound) {
        long configuredTicks = Math.max(0L, (long) AnimalCareConfig.waterTimer * multiplier);
        int maxBase = Integer.MAX_VALUE - (randomBound - 1);
        return (int) Math.min(configuredTicks, maxBase) + getRandom().nextInt(randomBound);
    }

    private int foodTicks() {
        return Math.max(0, AnimalCareConfig.feedTimer) + getRandom().nextInt(100);
    }

    private boolean careActive() {
        return !AnimalCareConfig.ambianceMode
                && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI);
    }

    private boolean isSleepingCare() {
        return ((AnimalSleepState) (Object) this).animaniax$isSleeping();
    }

    public static void registerFabric() {
        //? if fabric {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("hamster"), TYPE);
        Registry.register(BuiltInRegistries.ITEM, id("hamster_spawn_egg"), SPAWN_EGG);
        net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(TYPE, createAttributes());
        //?}
    }

    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("animaniax", path); }

    private final class HamsterBowlFoodGoal extends Goal {
        private BlockPos approach;
        private BlockPos bowlPos;
        private int nextScan;

        HamsterBowlFoodGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }

        @Override public boolean canUse() {
            if (!careActive() || fedTicks > 0 || isSleepingCare() || isOrderedToSit() || isInBall()
                    || tickCount < nextScan) return false;
            approach = bowlPos = null;
            BlockPos origin = blockPosition();
            ProviderSearch.Candidate result = ProviderSearch.findNearest(HamsterEntity.this, origin, 16, (pos, canReach) -> {
                if (!level().getBlockState(pos).is(PetBowlContent.BLOCK)
                        || !(level().getBlockEntity(pos) instanceof PetBowlBlockEntity bowl)
                        || !bowl.canFeed(TYPE)) return null;
                boolean inReach = distanceToSqr(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5) <= 2.25;
                BlockPos nearestApproach = null;
                double nearestDistance = Double.MAX_VALUE;
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    BlockPos candidate = pos.relative(dir);
                    if (!level().hasChunkAt(candidate) || !level().getBlockState(candidate).isAir()
                            || !level().getBlockState(candidate.below()).isFaceSturdy(level(), candidate.below(), Direction.UP)
                            || !inReach && !canReach.test(candidate)) continue;
                    double distance = distanceToSqr(candidate.getX() + 0.5, candidate.getY(), candidate.getZ() + 0.5);
                    if (distance < nearestDistance) { nearestDistance = distance; nearestApproach = candidate; }
                }
                return nearestApproach == null ? null : new ProviderSearch.Candidate(pos, nearestApproach);
            }, candidate -> distanceToSqr(candidate.getX() + 0.5, candidate.getY(), candidate.getZ() + 0.5));
            if (result != null) { bowlPos = result.provider(); approach = result.approach(); }
            nextScan = tickCount + 40;
            return approach != null;
        }

        @Override public boolean canContinueToUse() {
            return approach != null && careActive() && fedTicks == 0 && !isSleepingCare()
                    && !isOrderedToSit() && !isInBall();
        }

        @Override public void start() { getNavigation().moveTo(approach.getX() + 0.5, approach.getY(), approach.getZ() + 0.5, 1.0); }

        @Override public void tick() {
            if (distanceToSqr(bowlPos.getX() + 0.5, bowlPos.getY(), bowlPos.getZ() + 0.5) <= 2.25) {
                if (level().getBlockEntity(bowlPos) instanceof PetBowlBlockEntity bowl && bowl.consumeFood(TYPE)) {
                    fedTicks = foodTicks();
                    careInteracted = true;
                }
                getNavigation().stop();
                approach = bowlPos = null;
            } else if (getNavigation().isDone()) { approach = bowlPos = null; nextScan = tickCount + 100; }
        }

        @Override public void stop() { getNavigation().stop(); approach = bowlPos = null; }
    }

    private final class HamsterWaterGoal extends Goal {
        private BlockPos approach;
        private BlockPos source;
        private int nextScan;

        HamsterWaterGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }

        @Override public boolean canUse() {
            if (!careActive() || wateredTicks > 0 || isOrderedToSit() || isInBall() || tickCount < nextScan) return false;
            approach = source = null;
            BlockPos origin = blockPosition();
            ProviderSearch.Candidate result = ProviderSearch.findNearest(HamsterEntity.this, origin, 16, (water, canReach) -> {
                boolean bowl = level().getBlockState(water).is(PetBowlContent.BLOCK)
                        && level().getBlockEntity(water) instanceof PetBowlBlockEntity petBowl
                        && petBowl.getWaterAmount() >= 50;
                boolean trough = level().getBlockState(water).is(TroughContent.TROUGH)
                        && level().getBlockState(water).getValue(TroughBlock.CONTENT) == 1
                        && level().getBlockState(water).getValue(TroughBlock.LEVEL) > 0;
                if (!bowl && !trough && (!level().getFluidState(water).is(Fluids.WATER)
                        || level().getBiome(water).is(BiomeTags.IS_OCEAN)
                        || level().getBiome(water).is(BiomeTags.IS_BEACH))) return null;
                boolean inReach = distanceToSqr(water.getX() + 0.5, water.getY(), water.getZ() + 0.5) <= 2.25;
                BlockPos nearestApproach = null;
                double nearestDistance = Double.MAX_VALUE;
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    BlockPos candidate = trough || bowl ? water.relative(dir) : water.relative(dir).above();
                    if (!level().hasChunkAt(candidate) || !level().getBlockState(candidate).isAir()
                            || !level().getBlockState(candidate.below()).isFaceSturdy(level(), candidate.below(), Direction.UP)
                            || !inReach && !canReach.test(candidate)) continue;
                    double distance = distanceToSqr(candidate.getX() + 0.5, candidate.getY(), candidate.getZ() + 0.5);
                    if (distance < nearestDistance) { nearestDistance = distance; nearestApproach = candidate; }
                }
                return nearestApproach == null ? null : new ProviderSearch.Candidate(water, nearestApproach);
            }, candidate -> distanceToSqr(candidate.getX() + 0.5, candidate.getY(), candidate.getZ() + 0.5));
            if (result != null) { source = result.provider(); approach = result.approach(); }
            nextScan = tickCount + 40;
            return approach != null;
        }

        @Override public boolean canContinueToUse() { return approach != null && careActive() && wateredTicks == 0 && !isOrderedToSit() && !isInBall(); }
        @Override public void start() { getNavigation().moveTo(approach.getX() + 0.5, approach.getY(), approach.getZ() + 0.5, 1.0); }
        @Override public void tick() {
            if (distanceToSqr(source.getX() + 0.5, source.getY(), source.getZ() + 0.5) <= 2.25) {
                if (level().getBlockState(source).is(PetBowlContent.BLOCK)
                        && level().getBlockEntity(source) instanceof PetBowlBlockEntity bowl) {
                    if (bowl.consumeWater(50)) {
                        wateredTicks = waterTicks(1, 100);
                        careInteracted = true;
                    }
                } else if (level().getFluidState(source).is(Fluids.WATER)
                        || TroughBlock.consumeWater(level(), source, getType())) {
                    wateredTicks = waterTicks(1, 100);
                    if (level().getBlockState(source).is(TroughContent.TROUGH)) careInteracted = true;
                }
                getNavigation().stop();
                approach = source = null;
            } else if (getNavigation().isDone()) { approach = source = null; nextScan = tickCount + 100; }
        }
        @Override public void stop() { getNavigation().stop(); approach = source = null; }
    }
}

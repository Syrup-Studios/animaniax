package net.syrupstudios.animaniax.entity;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.material.Fluids;
import net.syrupstudios.animaniax.block.TroughBlock;
import net.syrupstudios.animaniax.block.TroughContent;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.syrupstudios.animaniax.block.NestBlock;
import net.syrupstudios.animaniax.block.NestBlockEntity;
import net.syrupstudios.animaniax.block.NestContent;
import net.syrupstudios.animaniax.advancement.FeedAnimalTrigger;
import net.syrupstudios.animaniax.config.AnimalCareConfig;

public final class HedgehogEntity extends TamableAnimal {
    private static final TagKey<Item> HEDGEHOG_FOODS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("animaniax", "hedgehog_foods"));
    private int fedTicks;
    private int wateredTicks;
    private int starvationTimer;
    private int nestForageCooldown;
    private int grassEatTimer;
    private boolean careInteracted;

    public HedgehogEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        fedTicks = AnimalCareConfig.feedTimer + getRandom().nextInt(100);
        wateredTicks = (int) Math.min(Integer.MAX_VALUE, (long) AnimalCareConfig.waterTimer * 2 + getRandom().nextInt(200));
    }

    @Override public void tick() {
        super.tick();
        if (grassEatTimer > 0) grassEatTimer--;
        if (!level().isClientSide) {
            if (nestForageCooldown > 0) nestForageCooldown--;
            boolean careActive = !AnimalCareConfig.ambianceMode && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI);
            if (careActive) {
                if (!AnimalCareConfig.ambianceMode && fedTicks > 0) fedTicks--;
                if (wateredTicks > 0) wateredTicks--;
            }
            if (careActive) {
                if (fedTicks == 0 && wateredTicks == 0) {
                    addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, 2, 1, false, false));
                    if (AnimalCareConfig.animalsStarve && !(((AnimalSleepState) (Object) this).animaniax$isSleeping() || isSleeping())) {
                        if (starvationTimer >= AnimalCareConfig.starvationTimer) {
                            hurt(damageSources().starve(), 4.0F);
                            starvationTimer = 0;
                        }
                        starvationTimer++;
                    }
                } else if (fedTicks == 0 || wateredTicks == 0) {
                    addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, 2, 0, false, false));
                }
            }
        }
    }

    @Override protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
        goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.2, 5.0F, 2.0F));
        goalSelector.addGoal(4, new HedgehogWaterGoal());
        goalSelector.addGoal(5, new NestForageGoal());
        goalSelector.addGoal(6, new TroughForageGoal());
        goalSelector.addGoal(8, new GrassSearchGoal());
        goalSelector.addGoal(7, new FleeSunGoal(this, 1.0));
        goalSelector.addGoal(9, new RandomStrollGoal(this, 0.8));
        goalSelector.addGoal(7, new TemptGoal(this, 1.2, Ingredient.of(HEDGEHOG_FOODS), false));
        goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(this, Silverfish.class, true) {
            @Override public boolean canUse() { return AnimalCareConfig.animalsCanAttackOthers && super.canUse(); }
            @Override public boolean canContinueToUse() { return AnimalCareConfig.animalsCanAttackOthers && super.canContinueToUse(); }
        });
        targetSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(this, net.minecraft.world.entity.animal.frog.Frog.class,
                10, true, false, frog -> frog instanceof CommonFrogEntity || frog instanceof ToadEntity) {
            @Override public boolean canUse() { return AnimalCareConfig.animalsCanAttackOthers && super.canUse(); }
            @Override public boolean canContinueToUse() { return AnimalCareConfig.animalsCanAttackOthers && super.canContinueToUse(); }
        });
    }

    @Override public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean sleeping = ((AnimalSleepState) (Object) this).animaniax$isSleeping() || isSleeping();
        if (sleeping && (isFood(stack)
                || stack.is(net.minecraft.world.item.Items.WATER_BUCKET)))
            return InteractionResult.sidedSuccess(level().isClientSide);
        boolean waterContainer = !sleeping && (level().isClientSide
                ? stack.is(net.minecraft.world.item.Items.WATER_BUCKET)
                : giveWaterBucket(player, hand));
        if (waterContainer) {
            if (!level().isClientSide) {
                careInteracted = true;
                wateredTicks = AnimalCareConfig.waterTimer + getRandom().nextInt(100);
                player.displayClientMessage(net.minecraft.network.chat.Component.literal("Hedgehog drank water."), true);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (isFood(stack)) {
            if (!level().isClientSide) {
                if (!isTame()) tame(player);
                careInteracted = true;
                fedTicks = AnimalCareConfig.feedTimer + getRandom().nextInt(100);
                ItemStack feedItem = stack.copy();
                if (!player.getAbilities().instabuild) stack.shrink(1);
                if (player instanceof ServerPlayer serverPlayer) FeedAnimalTrigger.INSTANCE.trigger(serverPlayer, this, feedItem);
                if (isOwnedBy(player)) setOrderedToSit(false);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (isTame() && isOwnedBy(player) && stack.isEmpty()) {
            if (!level().isClientSide) {
                setOrderedToSit(!isOrderedToSit());
                careInteracted = true;
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    private boolean giveWaterBucket(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(net.minecraft.world.item.Items.WATER_BUCKET)) return false;
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
            ItemStack bucket = new ItemStack(net.minecraft.world.item.Items.BUCKET);
            if (stack.isEmpty()) player.setItemInHand(hand, bucket);
            else if (!player.getInventory().add(bucket)) player.drop(bucket, false);
        }
        return true;
    }

    @Override public boolean isFood(ItemStack stack) {
        return stack.is(HEDGEHOG_FOODS);
    }

    @Override public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("FedTicks", fedTicks);
        tag.putInt("WateredTicks", wateredTicks);
        tag.putInt("PetStarvationTimer", starvationTimer);
        tag.putInt("NestForageCooldown", nestForageCooldown);
        tag.putBoolean("CareInteracted", careInteracted);
    }

    @Override public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        fedTicks = tag.contains("FedTicks")
                ? Math.max(0, Math.min(AnimalCareConfig.feedTimer + 99, tag.getInt("FedTicks")))
                : AnimalCareConfig.feedTimer + getRandom().nextInt(100);
        wateredTicks = tag.contains("WateredTicks")
                ? Math.max(0, Math.min((int) Math.min(Integer.MAX_VALUE, (long) AnimalCareConfig.waterTimer * 2 + 199), tag.getInt("WateredTicks")))
                : (int) Math.min(Integer.MAX_VALUE, (long) AnimalCareConfig.waterTimer * 2 + getRandom().nextInt(200));
        starvationTimer = Math.max(0, Math.min(AnimalCareConfig.starvationTimer, tag.getInt("PetStarvationTimer")));
        nestForageCooldown = Math.max(0, Math.min(12000, tag.getInt("NestForageCooldown")));
        careInteracted = tag.contains("CareInteracted") ? tag.getBoolean("CareInteracted") : isTame();
    }

    @Override public boolean canFallInLove() { return false; }
    @Override public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mate) { return null; }

    @Override public void handleEntityEvent(byte id) {
        if (id == 61) grassEatTimer = 160;
        else if (id == 62) grassEatTimer = 0;
        else super.handleEntityEvent(id);
    }

    public int grassEatTimer() { return grassEatTimer; }

    private final class HedgehogWaterGoal extends Goal {
        private BlockPos approach;
        private BlockPos source;
        private int nextScan;

        HedgehogWaterGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }

        @Override public boolean canUse() {
            if (!(!AnimalCareConfig.ambianceMode && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI)) || wateredTicks > 0 || isOrderedToSit() || tickCount < nextScan) return false;
            approach = source = null;
            BlockPos origin = blockPosition();
            ProviderSearch.Candidate result = ProviderSearch.findNearest(HedgehogEntity.this, origin, AnimalCareConfig.aiBlockSearchRange, (water, canReach) -> {
                boolean trough = level().getBlockState(water).is(TroughContent.TROUGH)
                        && level().getBlockState(water).getValue(TroughBlock.CONTENT) == 1
                        && level().getBlockState(water).getValue(TroughBlock.LEVEL) > 0;
                if (!trough && (!level().getFluidState(water).is(Fluids.WATER)
                        || level().getBiome(water).is(BiomeTags.IS_OCEAN)
                        || level().getBiome(water).is(BiomeTags.IS_BEACH))) return null;
                boolean inReach = distanceToSqr(water.getX() + 0.5, water.getY(), water.getZ() + 0.5) <= 2.25;
                BlockPos nearestApproach = null;
                double nearestDistance = Double.MAX_VALUE;
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    BlockPos candidate = trough ? water.relative(dir) : water.relative(dir).above();
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

        @Override public boolean canContinueToUse() { return approach != null && (!AnimalCareConfig.ambianceMode && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI)) && wateredTicks == 0 && !isOrderedToSit(); }
        @Override public void start() { getNavigation().moveTo(approach.getX() + 0.5, approach.getY(), approach.getZ() + 0.5, 1.0); }
        @Override public void tick() {
            if (distanceToSqr(source.getX() + 0.5, source.getY(), source.getZ() + 0.5) <= 2.25) {
                if (level().getFluidState(source).is(Fluids.WATER)
                        || TroughBlock.consumeWater(level(), source, getType()))
                    wateredTicks = AnimalCareConfig.waterTimer + getRandom().nextInt(100);
                getNavigation().stop();
                approach = source = null;
            } else if (getNavigation().isDone()) { approach = source = null; nextScan = tickCount + 100; }
        }
        @Override public void stop() { getNavigation().stop(); approach = source = null; }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes().add(Attributes.MAX_HEALTH, 8.0).add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 2.0);
    }

    private final class NestForageGoal extends Goal {
        private BlockPos nest;
        private boolean crop;
        private int nextSearch;

        NestForageGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }

        @Override public boolean canUse() {
            if (((!AnimalCareConfig.ambianceMode && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI)) ? fedTicks > 0 : nestForageCooldown > 0) || isOrderedToSit() || tickCount < nextSearch) return false;
            nest = findNest((!AnimalCareConfig.ambianceMode && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI)));
            nextSearch = tickCount + 100;
            return nest != null;
        }

        @Override public boolean canContinueToUse() {
            if (nest == null || isOrderedToSit()) return false;
            return crop ? (!AnimalCareConfig.ambianceMode && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI)) && fedTicks == 0 && isCrop(level().getBlockState(nest))
                    : canForageNest() && level().getBlockState(nest).is(NestContent.NEST)
                    && level().getBlockState(nest).getValue(NestBlock.EGGS) > 0;
        }

        @Override public void start() { moveToNest(); }
        @Override public void tick() {
            if (nest == null) return;
            if (distanceToSqr(nest.getX() + 0.5, nest.getY(), nest.getZ() + 0.5) <= 2.25) {
                BlockState state = level().getBlockState(nest);
                if (crop && isCrop(state)) {
                    fedTicks = AnimalCareConfig.feedTimer + getRandom().nextInt(100);
                    if (AnimalCareConfig.plantsRemovedAfterEating) level().destroyBlock(nest, false);
                } else if (state.is(NestContent.NEST) && state.getValue(NestBlock.EGGS) > 0
                        && level().getBlockEntity(nest) instanceof NestBlockEntity blockEntity) {
                    int eggs = state.getValue(NestBlock.EGGS);
                    blockEntity.syncEggMetadata(eggs);
                    blockEntity.removeEggs(1);
                    level().setBlock(nest, state.setValue(NestBlock.EGGS, eggs - 1), 3);
                    nestForageCooldown = 12000;
                    if ((!AnimalCareConfig.ambianceMode && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI))) {
                        fedTicks = AnimalCareConfig.feedTimer + getRandom().nextInt(100);
                        wateredTicks = AnimalCareConfig.waterTimer + getRandom().nextInt(100);
                    }
                }
                getNavigation().stop();
                nest = null;
                crop = false;
            } else if (getNavigation().isDone()) moveToNest();
        }
        @Override public void stop() { getNavigation().stop(); nest = null; }

        private BlockPos findNest(boolean includeCrops) {
            BlockPos origin = blockPosition();
            BlockPos nearest = null;
            double distance = Double.MAX_VALUE;
            for (int y = -3; y <= 2; y++) for (int x = -16; x < 16; x++) for (int z = -16; z < 16; z++) {
                BlockPos pos = origin.offset(x, y, z);
                if (x * x + z * z > 256 || !level().hasChunkAt(pos)) continue;
                BlockState state = level().getBlockState(pos);
                boolean candidateCrop = isCrop(state);
                if (candidateCrop && !includeCrops) continue;
                if (!candidateCrop && (!state.is(NestContent.NEST) || state.getValue(NestBlock.EGGS) == 0)) continue;
                double d = distanceToSqr(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                if (d < distance) { nearest = pos; distance = d; crop = candidateCrop; }
            }
            return nearest;
        }
        private boolean isCrop(BlockState state) {
            return state.is(Blocks.CARROTS) || state.is(Blocks.BEETROOTS) || state.is(Blocks.POTATOES);
        }
        private void moveToNest() {
            if (nest != null) getNavigation().moveTo(nest.getX() + 0.5, nest.getY(), nest.getZ() + 0.5, 1.0);
        }
    }

    private boolean canForageNest() {
        return (!AnimalCareConfig.ambianceMode && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI)) ? fedTicks == 0 : nestForageCooldown == 0;
    }

    private final class TroughForageGoal extends Goal {
        private BlockPos trough;
        private BlockPos approach;
        private int nextSearch;

        TroughForageGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }

        @Override public boolean canUse() {
            if (!(!AnimalCareConfig.ambianceMode && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI)) || fedTicks > 0 || isOrderedToSit() || tickCount < nextSearch) return false;
            trough = approach = null;
            BlockPos origin = blockPosition();
            ProviderSearch.Candidate result = ProviderSearch.findNearest(HedgehogEntity.this, origin, AnimalCareConfig.aiBlockSearchRange, (pos, canReach) -> {
                if (!level().getBlockState(pos).is(TroughContent.TROUGH)
                        || !(level().getBlockEntity(pos) instanceof net.syrupstudios.animaniax.block.TroughBlockEntity entity)
                        || !entity.canFeed(getType())) return null;
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
            if (result != null) { trough = result.provider(); approach = result.approach(); }
            nextSearch = tickCount + 40;
            return trough != null;
        }

        @Override public boolean canContinueToUse() {
            return trough != null && (!AnimalCareConfig.ambianceMode && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI)) && fedTicks == 0 && !isOrderedToSit()
                    && level().getBlockState(trough).is(TroughContent.TROUGH)
                    && level().getBlockEntity(trough) instanceof net.syrupstudios.animaniax.block.TroughBlockEntity entity
                    && entity.canFeed(getType());
        }

        @Override public void start() { moveToTrough(); }
        @Override public void tick() {
            if (trough == null) return;
            if (distanceToSqr(trough.getX() + 0.5, trough.getY(), trough.getZ() + 0.5) <= 2.25) {
                if (TroughBlock.consumeFeed(level(), trough, getType())) {
                    fedTicks = AnimalCareConfig.feedTimer + getRandom().nextInt(100);
                }
                getNavigation().stop();
                trough = approach = null;
            } else if (getNavigation().isDone()) moveToTrough();
        }
        @Override public void stop() { getNavigation().stop(); trough = approach = null; }

        private void moveToTrough() {
            if (approach != null) getNavigation().moveTo(approach.getX() + 0.5, approach.getY(), approach.getZ() + 0.5, 1.0);
        }
    }

    private final class GrassSearchGoal extends Goal {
        private int nextScan;
        private int eatingTicks;

        GrassSearchGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP)); }

        private boolean isEligible() {
            return !isOrderedToSit() && fedTicks == 0 && (!AnimalCareConfig.ambianceMode && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI))
                    && !isSleeping() && !((AnimalSleepState) (Object) HedgehogEntity.this).animaniax$isSleeping();
        }

        @Override public boolean canUse() {
            if (!isEligible() || tickCount < nextScan || getRandom().nextInt(120) != 0) return false;
            boolean grassNearby = hasGrassNearby();
            nextScan = tickCount + 40;
            return grassNearby;
        }

        @Override public boolean canContinueToUse() { return isEligible() && eatingTicks > 0; }
        @Override public boolean requiresUpdateEveryTick() { return true; }
        @Override public void start() {
            getNavigation().stop();
            eatingTicks = 160;
            level().broadcastEntityEvent(HedgehogEntity.this, (byte) 61);
        }

        @Override public void tick() {
            if (eatingTicks > 0) eatingTicks--;
        }

        @Override public void stop() {
            getNavigation().stop();
            if (eatingTicks > 0) level().broadcastEntityEvent(HedgehogEntity.this, (byte) 62);
            eatingTicks = 0;
        }

        private boolean hasGrassNearby() {
            BlockPos origin = blockPosition();
            for (int y = -5; y <= 3; y++) for (int x = -7; x <= 7; x++) for (int z = -7; z <= 7; z++) {
                BlockPos grass = origin.offset(x, y, z);
                if (level().hasChunkAt(grass) && level().getBlockState(grass).is(Blocks.GRASS_BLOCK)) return true;
            }
            return false;
        }
    }
}

package net.syrupstudios.animaniax.mixin;

import java.util.function.Consumer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.syrupstudios.animaniax.entity.AnimalCareState;
import net.syrupstudios.animaniax.entity.CowBreedState;
import net.syrupstudios.animaniax.entity.PigBreedState;
import net.syrupstudios.animaniax.entity.ChickenBreedState;
import net.syrupstudios.animaniax.item.PrimeChickenContent;
import net.syrupstudios.animaniax.entity.SheepBreedState;
import net.syrupstudios.animaniax.entity.GoatBreedState;
import net.syrupstudios.animaniax.entity.RabbitBreedState;
import net.syrupstudios.animaniax.item.PrimeMuttonContent;
import net.syrupstudios.animaniax.item.PrimeRabbitContent;
import net.syrupstudios.animaniax.item.PrimeMeatContent;
import net.syrupstudios.animaniax.item.GoatMeatContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
abstract class PrimeChickenLootMixin {
    @Redirect(method = "dropFromLootTable", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;JLjava/util/function/Consumer;)V"))
    private void animaniax$replacePrimeChickenLoot(LootTable table, LootParams params, long seed,
            Consumer<ItemStack> drop) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Goat goat) {
            table.getRandomItems(params, seed, drop);
            if (goat.isBaby()) return;
            String breed = ((GoatBreedState) goat).animaniax$getGoatBreed();
            boolean primeBreed = breed.equals("Alpine") || breed.equals("Kiko") || breed.equals("Pygmy");
            AnimalCareState care = (AnimalCareState) goat;
            int count = 1 + (goat.getRandom().nextBoolean() ? 1 : 0);
            if (care.animaniax$isFed() && goat.getRandom().nextBoolean()) count++;
            if (care.animaniax$isWatered() && goat.getRandom().nextBoolean()) count++;
            int looting = 0;
            if (params.getParamOrNull(LootContextParams.ATTACKING_ENTITY) instanceof LivingEntity killer) {
                looting = EnchantmentHelper.getEnchantmentLevel(
                        killer.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING),
                        killer);
            }
            count += Math.round(looting * goat.getRandom().nextFloat());
            ItemStack meat = new ItemStack(primeBreed
                    ? goat.isOnFire() ? GoatMeatContent.COOKED_PRIME_CHEVON : GoatMeatContent.RAW_PRIME_CHEVON
                    : goat.isOnFire() ? GoatMeatContent.COOKED_CHEVON : GoatMeatContent.RAW_CHEVON, count);
            drop.accept(meat);
            return;
        }
        if (entity.getType() == EntityType.COW && entity instanceof Cow cow) {
            boolean primeBreed = switch (((CowBreedState) cow).animaniax$getBreed()) {
                case "Angus", "Hereford", "Texas Longhorn", "Jersey", "Highland" -> true;
                default -> false;
            };
            int careBonus = cow.isBaby() ? 0 : animaniax$careMeatBonus(cow);
            int[] bonusRemaining = {careBonus};
            boolean[] meatDropped = {false};
            table.getRandomItems(params, seed, stack -> {
                if (stack.is(Items.BEEF) || stack.is(Items.COOKED_BEEF)) {
                    if (cow.isBaby()) return;
                    meatDropped[0] = true;
                    stack.grow(bonusRemaining[0]);
                    bonusRemaining[0] = 0;
                    drop.accept(primeBreed ? new ItemStack(stack.is(Items.COOKED_BEEF) || cow.isOnFire()
                            ? PrimeMeatContent.COOKED_PRIME_BEEF : PrimeMeatContent.RAW_PRIME_BEEF, stack.getCount()) : stack);
                } else drop.accept(stack);
            });
            if (!cow.isBaby() && !meatDropped[0] && careBonus > 0) {
                drop.accept(new ItemStack(primeBreed
                        ? cow.isOnFire() ? PrimeMeatContent.COOKED_PRIME_BEEF : PrimeMeatContent.RAW_PRIME_BEEF
                        : cow.isOnFire() ? Items.COOKED_BEEF : Items.BEEF, careBonus));
            }
            return;
        }
        if (entity.getType() == EntityType.PIG && entity instanceof Pig pig) {
            String breed = ((PigBreedState) pig).animaniax$getPigBreed();
            boolean primeBreed = breed.equals("Duroc") || breed.equals("Hampshire") || breed.equals("Large Black") || breed.equals("Old Spot");
            int careBonus = pig.isBaby() ? 0 : animaniax$careMeatBonus(pig);
            int[] bonusRemaining = {careBonus};
            boolean[] meatDropped = {false};
            table.getRandomItems(params, seed, stack -> {
                if (stack.is(Items.PORKCHOP) || stack.is(Items.COOKED_PORKCHOP)) {
                    if (pig.isBaby()) return;
                    meatDropped[0] = true;
                    stack.grow(bonusRemaining[0]);
                    bonusRemaining[0] = 0;
                    drop.accept(primeBreed ? new ItemStack(stack.is(Items.COOKED_PORKCHOP) || pig.isOnFire()
                            ? PrimeMeatContent.COOKED_PRIME_PORK : PrimeMeatContent.RAW_PRIME_PORK, stack.getCount()) : stack);
                } else drop.accept(stack);
            });
            if (!pig.isBaby() && !meatDropped[0] && careBonus > 0) {
                drop.accept(new ItemStack(primeBreed
                        ? pig.isOnFire() ? PrimeMeatContent.COOKED_PRIME_PORK : PrimeMeatContent.RAW_PRIME_PORK
                        : pig.isOnFire() ? Items.COOKED_PORKCHOP : Items.PORKCHOP, careBonus));
            }
            return;
        }
        if (!(entity instanceof Chicken chicken)) {
            if (entity instanceof Rabbit rabbit) {
                String breed = ((RabbitBreedState) rabbit).animaniax$getRabbitBreed();
                boolean primeBreed = breed.equals("Rex") || breed.equals("New Zealand") || breed.equals("Chinchilla");
                AnimalCareState care = (AnimalCareState) rabbit;
                int meatBonus = !rabbit.isBaby() && care.animaniax$isFed()
                        && rabbit.getRandom().nextBoolean() ? 1 : 0;
                int hideBonus = !rabbit.isBaby() && care.animaniax$isWatered()
                        && rabbit.getRandom().nextBoolean() ? 1 : 0;
                int[] meatBonusRemaining = {meatBonus};
                int[] hideBonusRemaining = {hideBonus};
                boolean[] meatDropped = {false};
                boolean[] hideDropped = {false};
                table.getRandomItems(params, seed, stack -> {
                    if (stack.is(Items.RABBIT) || stack.is(Items.COOKED_RABBIT)) {
                        if (rabbit.isBaby()) return;
                        meatDropped[0] = true;
                        stack.grow(meatBonusRemaining[0]);
                        meatBonusRemaining[0] = 0;
                        if (primeBreed) {
                            if (!stack.isEmpty()) drop.accept(new ItemStack(stack.is(Items.COOKED_RABBIT)
                                    ? PrimeRabbitContent.COOKED_PRIME_RABBIT : PrimeRabbitContent.RAW_PRIME_RABBIT, stack.getCount()));
                        } else {
                            if (!stack.isEmpty()) drop.accept(stack);
                        }
                    } else if (stack.is(Items.RABBIT_HIDE)) {
                        if (!rabbit.isBaby() && hideBonusRemaining[0] > 0) {
                            stack.grow(hideBonusRemaining[0]);
                            hideBonusRemaining[0] = 0;
                        }
                        if (!rabbit.isBaby() && !stack.isEmpty()) hideDropped[0] = true;
                        if (!stack.isEmpty()) drop.accept(stack);
                    } else {
                        drop.accept(stack);
                    }
                });
                if (!rabbit.isBaby() && !meatDropped[0] && meatBonusRemaining[0] > 0) {
                    drop.accept(new ItemStack(primeBreed
                            ? rabbit.isOnFire() ? PrimeRabbitContent.COOKED_PRIME_RABBIT : PrimeRabbitContent.RAW_PRIME_RABBIT
                            : rabbit.isOnFire() ? Items.COOKED_RABBIT : Items.RABBIT, meatBonusRemaining[0]));
                }
                if (!rabbit.isBaby() && !hideDropped[0] && hideBonusRemaining[0] > 0)
                    drop.accept(new ItemStack(Items.RABBIT_HIDE, hideBonusRemaining[0]));
                return;
            }
            if (entity instanceof Sheep sheep) {
                String breed = ((SheepBreedState) sheep).animaniax$getSheepBreed();
                boolean primeBreed = breed.equals("Dorper") || breed.equals("Dorset") || breed.equals("Suffolk");
                int careBonus = sheep.isBaby() ? 0 : animaniax$careMeatBonus(sheep);
                int[] bonusRemaining = {careBonus};
                boolean[] meatDropped = {false};
                table.getRandomItems(params, seed, stack -> {
                    if (stack.is(Items.MUTTON) || stack.is(Items.COOKED_MUTTON)) {
                        if (sheep.isBaby()) return;
                        meatDropped[0] = true;
                        stack.grow(bonusRemaining[0]);
                        bonusRemaining[0] = 0;
                        if (primeBreed) {
                            if (!stack.isEmpty()) drop.accept(new ItemStack(stack.is(Items.COOKED_MUTTON)
                                    ? PrimeMuttonContent.COOKED_PRIME_MUTTON : PrimeMuttonContent.RAW_PRIME_MUTTON, stack.getCount()));
                        } else {
                            if (!stack.isEmpty()) drop.accept(stack);
                        }
                    } else {
                        drop.accept(stack);
                    }
                });
                if (!sheep.isBaby() && !meatDropped[0] && careBonus > 0) {
                    drop.accept(new ItemStack(primeBreed
                            ? sheep.isOnFire() ? PrimeMuttonContent.COOKED_PRIME_MUTTON : PrimeMuttonContent.RAW_PRIME_MUTTON
                            : sheep.isOnFire() ? Items.COOKED_MUTTON : Items.MUTTON, careBonus));
                }
                return;
            }
            table.getRandomItems(params, seed, drop);
            return;
        }
        boolean primeBreed = !"Leghorn".equals(((ChickenBreedState) chicken).animaniax$getChickenBreed());
        boolean[] meatDropped = {false};
        table.getRandomItems(params, seed, stack -> {
            if (primeBreed && (stack.is(Items.CHICKEN) || stack.is(Items.COOKED_CHICKEN))) {
                meatDropped[0] = true;
                ItemStack prime = new ItemStack(stack.is(Items.COOKED_CHICKEN)
                        || stack.is(Items.CHICKEN) && chicken.isOnFire()
                        ? PrimeChickenContent.COOKED_PRIME_CHICKEN : PrimeChickenContent.RAW_PRIME_CHICKEN,
                        stack.getCount());
                drop.accept(prime);
            } else {
                drop.accept(stack);
            }
        });
        if (primeBreed && !chicken.isBaby() && !meatDropped[0]) {
            chicken.spawnAtLocation(chicken.isOnFire() ? PrimeChickenContent.COOKED_PRIME_CHICKEN
                    : PrimeChickenContent.RAW_PRIME_CHICKEN);
        }
        if (!chicken.isBaby()) {
            AnimalCareState care = (AnimalCareState) chicken;
            if (care.animaniax$isFed() && chicken.getRandom().nextBoolean()) chicken.spawnAtLocation(Items.FEATHER);
            if (care.animaniax$isWatered() && chicken.getRandom().nextBoolean()) chicken.spawnAtLocation(Items.FEATHER);
        }
    }

    private static int animaniax$careMeatBonus(LivingEntity animal) {
        AnimalCareState care = (AnimalCareState) animal;
        int bonus = 0;
        if (care.animaniax$isFed() && animal.getRandom().nextBoolean()) bonus++;
        if (care.animaniax$isWatered() && animal.getRandom().nextBoolean()) bonus++;
        return bonus;
    }
}

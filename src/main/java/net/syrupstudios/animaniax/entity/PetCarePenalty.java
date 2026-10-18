package net.syrupstudios.animaniax.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.util.RandomSource;
import net.syrupstudios.animaniax.config.AnimalCareConfig;

final class PetCarePenalty {
    private PetCarePenalty() {}

    static boolean isCareActive(boolean interacted) {
        return interacted || !AnimalCareConfig.requireAnimalInteractionForAI;
    }

    static boolean isCareAIActive(boolean interacted) {
        return !AnimalCareConfig.ambianceMode && isCareActive(interacted);
    }

    static int initialFedTicks(RandomSource random) {
        return AnimalCareConfig.feedTimer + random.nextInt(100);
    }

    static int initialWaterTicks(RandomSource random) {
        return (int) Math.min(Integer.MAX_VALUE, (long) AnimalCareConfig.waterTimer * 2 + random.nextInt(200));
    }

    static int maxFedTicks() { return AnimalCareConfig.feedTimer + 99; }

    static int maxWaterTicks() {
        return (int) Math.min(Integer.MAX_VALUE, (long) AnimalCareConfig.waterTimer * 2 + 199);
    }

    static int tick(LivingEntity pet, boolean hungry, boolean thirsty, boolean interacted,
            boolean sleeping, int starvationTimer) {
        if (!isCareActive(interacted) || AnimalCareConfig.ambianceMode) return starvationTimer;
        if (hungry && thirsty) {
            pet.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 2, 1, false, false));
            if (AnimalCareConfig.animalsStarve && !sleeping) {
                if (starvationTimer >= AnimalCareConfig.starvationTimer) {
                    pet.hurt(pet.damageSources().starve(), 4.0F);
                    starvationTimer = 0;
                }
                starvationTimer++;
            }
        } else if (hungry || thirsty) {
            pet.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 2, 0, false, false));
        }
        return starvationTimer;
    }
}

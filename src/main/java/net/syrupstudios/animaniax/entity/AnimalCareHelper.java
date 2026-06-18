package net.syrupstudios.animaniax.entity;

import net.minecraft.world.entity.Mob;
import net.syrupstudios.animaniax.config.AnimalCareConfig;

public final class AnimalCareHelper {
    private AnimalCareHelper() {}

    public static boolean canStartCare(Mob mob, boolean needsCare, boolean sleeping) {
        if (!needsCare || mob == null || sleeping) return false;
        return !mob.isVehicle() && !mob.isPassenger() && !mob.isSleeping();
    }

    public static boolean isSleepBlocked(Mob mob) {
        if (mob == null || !AnimalCareConfig.animalsSleep) return true;
        return mob.isOnFire() || mob.isLeashed() || mob.isVehicle()
                || mob.isPassenger() || mob.isInWater() || mob.getTarget() != null;
    }

    public static int nextScanTick(Mob mob, int baseDelay) {
        int interval = Math.max(10, Math.min(baseDelay, Math.max(20, AnimalCareConfig.ticksBetweenAIFirings)));
        return mob.tickCount + interval;
    }

    public static int searchRange() {
        return Math.max(1, Math.min(64, AnimalCareConfig.aiBlockSearchRange));
    }
}

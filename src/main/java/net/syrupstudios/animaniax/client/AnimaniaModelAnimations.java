package net.syrupstudios.animaniax.client;

import net.minecraft.util.Mth;
import net.syrupstudios.animaniax.client.model.PlaceholderModel;
import net.syrupstudios.animaniax.client.model.PlaceholderModel.PartOverride;

import java.util.Map;

/**
 * Shared procedural pose delta helpers for extracted Animania entity models.
 * Always writes additive deltas so rest-pose rotations in the JSON models stay intact.
 */
public final class AnimaniaModelAnimations {
    private static final float DEG_TO_RAD = (float) (Math.PI / 180.0D);

    private AnimaniaModelAnimations() {}

    public static void addRotX(PlaceholderModel model, Map<String, PartOverride> out, String part, float rx) {
        if (part != null && model.getPart(part) != null && rx != 0.0F) {
            out.computeIfAbsent(part, k -> new PartOverride()).rotX += rx;
        }
    }

    public static void addRotY(PlaceholderModel model, Map<String, PartOverride> out, String part, float ry) {
        if (part != null && model.getPart(part) != null && ry != 0.0F) {
            out.computeIfAbsent(part, k -> new PartOverride()).rotY += ry;
        }
    }

    public static void addRotZ(PlaceholderModel model, Map<String, PartOverride> out, String part, float rz) {
        if (part != null && model.getPart(part) != null && rz != 0.0F) {
            out.computeIfAbsent(part, k -> new PartOverride()).rotZ += rz;
        }
    }

    public static void addLook(PlaceholderModel model, Map<String, PartOverride> out, float netHeadYaw, float headPitch, String... candidates) {
        float pitchRad = Mth.clamp(headPitch, -45.0F, 45.0F) * DEG_TO_RAD;
        float yawRad = Mth.clamp(netHeadYaw, -60.0F, 60.0F) * DEG_TO_RAD;
        for (String name : candidates) {
            if (model.getPart(name) != null) {
                addRotX(model, out, name, pitchRad);
                addRotY(model, out, name, yawRad);
                return;
            }
        }
    }

    public static void animateQuadruped(
            PlaceholderModel model,
            Map<String, PartOverride> out,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            boolean sleeping,
            String headPart,
            String tailPart,
            String fl,
            String fr,
            String bl,
            String br) {
        if (!sleeping) {
            addLook(model, out, netHeadYaw, headPitch, headPart);
            float swingA = Mth.cos(limbSwing * 0.6662F) * 1.1F * limbSwingAmount;
            float swingB = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.1F * limbSwingAmount;
            addRotX(model, out, fl, swingA);
            addRotX(model, out, br, swingA);
            addRotX(model, out, fr, swingB);
            addRotX(model, out, bl, swingB);
            addRotY(model, out, tailPart, Mth.sin(ageInTicks * 0.12F) * 0.18F);
        } else {
            addRotY(model, out, headPart, 0.35F);
            addRotX(model, out, fl, -1.2F);
            addRotX(model, out, fr, -1.2F);
            addRotX(model, out, bl, 1.2F);
            addRotX(model, out, br, 1.2F);
        }
    }

    public static void animateQuadrupedExtra(
            PlaceholderModel model,
            Map<String, PartOverride> out,
            float limbSwing,
            float limbSwingAmount,
            boolean sleeping,
            String flExtra,
            String frExtra,
            String blExtra,
            String brExtra) {
        if (!sleeping) {
            float swingA = Mth.cos(limbSwing * 0.6662F) * 1.1F * limbSwingAmount;
            float swingB = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.1F * limbSwingAmount;
            addRotX(model, out, flExtra, swingA);
            addRotX(model, out, brExtra, swingA);
            addRotX(model, out, frExtra, swingB);
            addRotX(model, out, blExtra, swingB);
        } else {
            addRotX(model, out, flExtra, -1.2F);
            addRotX(model, out, frExtra, -1.2F);
            addRotX(model, out, blExtra, 1.2F);
            addRotX(model, out, brExtra, 1.2F);
        }
    }

    public static void animateBird(
            PlaceholderModel model,
            Map<String, PartOverride> out,
            float limbSwing,
            float limbSwingAmount,
            float wingBob,
            float netHeadYaw,
            float headPitch,
            boolean sleeping,
            boolean displayFan) {
        if (!sleeping) {
            addLook(model, out, netHeadYaw, headPitch, "Neck", "Head");
            float legA = Mth.cos(limbSwing * 0.6662F) * 1.25F * limbSwingAmount;
            float legB = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.25F * limbSwingAmount;
            addRotX(model, out, "leg1Pivot", legA);
            addRotX(model, out, "Leg1Pivot", legA);
            addRotX(model, out, "leg1Top", legA);
            addRotX(model, out, "leg2Pivot", legB);
            addRotX(model, out, "Leg2Pivot", legB);
            addRotX(model, out, "leg2Top", legB);

            addRotZ(model, out, "Wing1", wingBob);
            addRotZ(model, out, "Wing2", -wingBob);
            addRotZ(model, out, "wing1", wingBob);
            addRotZ(model, out, "wing2", -wingBob);
            addRotZ(model, out, "wing3", wingBob);
            addRotZ(model, out, "wing4", -wingBob);
        } else {
            addRotY(model, out, "Neck", 0.3F);
            addRotX(model, out, "FanNodeA", -0.6F);
            addRotX(model, out, "FanNodeB", -0.6F);
            addRotX(model, out, "FanNodeC", -0.6F);
            addRotX(model, out, "FanNodeD", -0.6F);
            return;
        }
        if (displayFan) {
            float fanIdle = 0.04F * Mth.sin(wingBob * 0.25F);
            addRotX(model, out, "FanNodeA", fanIdle);
            addRotX(model, out, "FanNodeB", fanIdle);
            addRotX(model, out, "FanNodeC", fanIdle);
            addRotX(model, out, "FanNodeD", fanIdle);
        }
    }

    public static void animateRabbit(
            PlaceholderModel model,
            Map<String, PartOverride> out,
            float limbSwing,
            float limbSwingAmount,
            float netHeadYaw,
            float headPitch,
            boolean sleeping) {
        if (!sleeping) {
            addLook(model, out, netHeadYaw, headPitch, "Neck1", "HeadBase");
            float hop = Mth.sin(limbSwing * 1.0F) * limbSwingAmount;
            addRotX(model, out, "BackLegL1", hop * 1.1F);
            addRotX(model, out, "BackLegR1", hop * 1.1F);
            addRotX(model, out, "LegL1", -hop * 0.9F);
            addRotX(model, out, "LegR1", -hop * 0.9F);
        } else {
            addRotY(model, out, "Neck1", 0.25F);
        }
    }

    public static void animateCatOrDog(
            PlaceholderModel model,
            Map<String, PartOverride> out,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            boolean sleeping,
            boolean sitting,
            boolean tailWag) {
        if (sleeping && model.hasPose("sleeping")) {
            return;
        }
        if (sitting && model.hasPose("sitting")) {
            if (tailWag) {
                addRotY(model, out, "tail", Mth.sin(ageInTicks * 0.35F) * 0.35F);
            }
            return;
        }
        addLook(model, out, netHeadYaw, headPitch, "neck1", "neck", "neck_base", "head", "head_base", "pug_head");
        float swingA = Mth.cos(limbSwing * 0.6662F) * 1.15F * limbSwingAmount;
        float swingB = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.15F * limbSwingAmount;

        // Standard dog / cat rig
        if (model.getPart("leg_l1") != null && model.getPart("back_leg_r1") != null) {
            addRotX(model, out, "leg_l1", swingA);
            addRotX(model, out, "back_leg_r1", swingA);
            addRotX(model, out, "leg_r1", swingB);
            addRotX(model, out, "back_leg_l1", swingB);
        } else if (model.getPart("leg_l1") != null && model.getPart("leg_r2") != null) {
            // cat_ocelot variant
            addRotX(model, out, "leg_l1", swingA);
            addRotX(model, out, "back_leg_r1", swingA);
            addRotX(model, out, "leg_r2", swingB);
            addRotX(model, out, "back_leg_l1", swingB);
        } else if (model.getPart("leg_r1") != null && model.getPart("leg_r11") != null) {
            // corgi
            addRotX(model, out, "leg_r11", swingA);
            addRotX(model, out, "leg_r2", swingA);
            addRotX(model, out, "leg_r1", swingB);
            addRotX(model, out, "leg_r21", swingB);
        } else if (model.getPart("front_left") != null) {
            // chihuahua, pomeranian, pug
            addRotX(model, out, "front_left", swingA);
            addRotX(model, out, "back_right", swingA);
            addRotX(model, out, "front_right", swingB);
            addRotX(model, out, "back_left", swingB);
        } else if (model.getPart("leg1") != null) {
            // dachshund
            addRotX(model, out, "leg1", swingA);
            addRotX(model, out, "leg4", swingA);
            addRotX(model, out, "leg2", swingB);
            addRotX(model, out, "leg3", swingB);
        }

        if (tailWag) {
            addRotY(model, out, "tail", Mth.sin(ageInTicks * 0.35F) * 0.35F);
        } else {
            addRotY(model, out, "tail", Mth.sin(ageInTicks * 0.12F) * 0.12F);
        }
    }

    public static void animateFerret(
            PlaceholderModel model,
            Map<String, PartOverride> out,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            boolean sleeping) {
        if (!sleeping) {
            addLook(model, out, netHeadYaw, headPitch, "Head", "Neck");
            float swingA = Mth.cos(limbSwing * 0.6662F) * 1.2F * limbSwingAmount;
            float swingB = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.2F * limbSwingAmount;
            addRotX(model, out, "PawLF", swingA);
            addRotX(model, out, "PawRB", swingA);
            addRotX(model, out, "PawRF", swingB);
            addRotX(model, out, "PawLB", swingB);
            addRotY(model, out, "Tail", Mth.sin(ageInTicks * 0.14F) * 0.18F);
        } else {
            addRotY(model, out, "Head", 0.3F);
        }
    }

    public static void animateHedgehog(
            PlaceholderModel model,
            Map<String, PartOverride> out,
            float limbSwing,
            float limbSwingAmount,
            float netHeadYaw,
            float headPitch,
            boolean sleeping) {
        if (!sleeping) {
            addLook(model, out, netHeadYaw, headPitch, "Head");
            float swingA = Mth.cos(limbSwing * 0.6662F) * 1.1F * limbSwingAmount;
            float swingB = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.1F * limbSwingAmount;
            addRotX(model, out, "LegFrontLeft", swingA);
            addRotX(model, out, "LegFrontLeftFoot", swingA);
            addRotX(model, out, "LegBackRight", swingA);
            addRotX(model, out, "LegFrontLeftFoot2", swingA);
            addRotX(model, out, "LegFrontRight", swingB);
            addRotX(model, out, "LegFrontLeftFoot3", swingB);
            addRotX(model, out, "LegBackLeft", swingB);
            addRotX(model, out, "LegFrontLeftFoot1", swingB);
        } else {
            addRotY(model, out, "Head", 0.25F);
        }
    }

    public static void animateAmphibian(
            PlaceholderModel model,
            Map<String, PartOverride> out,
            float limbSwing,
            float limbSwingAmount,
            float netHeadYaw,
            float headPitch) {
        addLook(model, out, netHeadYaw, headPitch, "Head", "Neck");
        float hop = Mth.sin(limbSwing * 1.1F) * limbSwingAmount;
        addRotX(model, out, "HindLegL", -hop * 0.9F);
        addRotX(model, out, "HindLegR", -hop * 0.9F);
        addRotX(model, out, "FrontLegLTop", hop * 0.7F);
        addRotX(model, out, "FrontLegRTop", hop * 0.7F);
    }
}

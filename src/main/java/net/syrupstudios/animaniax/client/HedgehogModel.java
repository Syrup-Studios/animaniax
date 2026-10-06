package net.syrupstudios.animaniax.client;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.syrupstudios.animaniax.entity.HedgehogEntity;

public final class HedgehogModel extends HierarchicalModel<HedgehogEntity> {
    private final ModelPart root;

    public HedgehogModel(ModelPart root) { this.root = root; }
    @Override public ModelPart root() { return root; }
    @Override public void setupAnim(HedgehogEntity hedgehog, float limbSwing, float limbSwingAmount, float ageInTicks,
            float netHeadYaw, float headPitch) {
        ModelPart head = root.getChild("head");
        ModelPart rightEar = root.getChild("right_ear");
        ModelPart leftEar = root.getChild("left_ear");
        ModelPart nose = root.getChild("nose");
        float yaw = netHeadYaw * ((float) Math.PI / 180F);
        head.yRot = yaw;
        nose.yRot = yaw;
        rightEar.yRot = yaw - 0.2617994F;
        leftEar.yRot = yaw + 0.2617994F;
        int eatTimer = hedgehog.grassEatTimer();
        float pitch;
        if (eatTimer > 4 && eatTimer <= 76) {
            pitch = (float) Math.toRadians(36.0 + 8.4 * Math.sin((eatTimer - 4) * (28.7 / 24.0)));
        } else {
            pitch = eatTimer > 0 ? (float) Math.PI / 5F : headPitch * ((float) Math.PI / 180F);
        }
        head.xRot = pitch;
        rightEar.xRot = pitch;
        leftEar.xRot = pitch;
        nose.xRot = pitch;
    }
}

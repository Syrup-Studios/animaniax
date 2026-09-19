package net.syrupstudios.animaniax.client;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.syrupstudios.animaniax.entity.HamsterEntity;

public final class HamsterModel extends HierarchicalModel<HamsterEntity> {
    private final ModelPart root;

    public HamsterModel(ModelPart root) {
        this.root = root;
    }

    @Override
    public ModelPart root() { return root; }

    @Override
    public void setupAnim(HamsterEntity hamster, float limbSwing, float limbSwingAmount, float ageInTicks,
            float netHeadYaw, float headPitch) {}
}

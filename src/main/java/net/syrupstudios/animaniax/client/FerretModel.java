package net.syrupstudios.animaniax.client;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.syrupstudios.animaniax.entity.FerretEntity;

public final class FerretModel extends HierarchicalModel<FerretEntity> {
    private final ModelPart root;

    public FerretModel(ModelPart root) { this.root = root; }
    @Override public ModelPart root() { return root; }
    @Override public void setupAnim(FerretEntity ferret, float limbSwing, float limbSwingAmount, float ageInTicks,
            float netHeadYaw, float headPitch) {}
}

package net.syrupstudios.animaniax.client;

import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.PeafowlEntity;

public final class PeafowlRenderer extends MobRenderer<PeafowlEntity, ChickenModel<PeafowlEntity>> {
    private static final ResourceLocation BODY_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/chicken.png");
    private static final ResourceLocation FAN_TEXTURE = ResourceLocation.withDefaultNamespace("textures/block/white_wool.png");

    public PeafowlRenderer(EntityRendererProvider.Context context) {
        super(context, new ChickenModel<>(context.bakeLayer(ModelLayers.CHICKEN)), 0.3F);
        addLayer(new FanLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(PeafowlEntity peafowl) { return BODY_TEXTURE; }

    @Override
    public float getBob(PeafowlEntity peafowl, float partialTick) {
        return (Mth.sin(Mth.lerp(partialTick, peafowl.oFlap, peafowl.flap)) + 1.0F)
                * Mth.lerp(partialTick, peafowl.oFlapSpeed, peafowl.flapSpeed);
    }

    private static final class FanLayer extends RenderLayer<PeafowlEntity, ChickenModel<PeafowlEntity>> {
        private final ModelPart[] feathers = new ModelPart[9];
        private static final int[][] COLORS = {
                {0xFF173F58, 0xFF176A70, 0xFF1B8A82, 0xFF245E8B, 0xFF317C9A, 0xFF245E8B, 0xFF1B8A82, 0xFF176A70, 0xFF173F58},
                {0xFFB7C4C7, 0xFFD9E0DF, 0xFFEAF0ED, 0xFFF7F7F2, 0xFFFFFFFF, 0xFFF7F7F2, 0xFFEAF0ED, 0xFFD9E0DF, 0xFFB7C4C7},
                {0xFF60574B, 0xFF827666, 0xFFA0927C, 0xFFB8A88E, 0xFFC8B99F, 0xFFB8A88E, 0xFFA0927C, 0xFF827666, 0xFF60574B},
                {0xFF45275F, 0xFF633A83, 0xFF8152A4, 0xFF9D6ABD, 0xFFB587D0, 0xFF9D6ABD, 0xFF8152A4, 0xFF633A83, 0xFF45275F},
                {0xFF9B5944, 0xFFC27A5D, 0xFFD89470, 0xFFE7B18C, 0xFFF1C9A5, 0xFFE7B18C, 0xFFD89470, 0xFFC27A5D, 0xFF9B5944},
                {0xFF476D76, 0xFF648E8D, 0xFF8CB39E, 0xFFC1C99A, 0xFFE1D9AA, 0xFFC1C99A, 0xFF8CB39E, 0xFF648E8D, 0xFF476D76},
                {0xFF292C32, 0xFF41454C, 0xFF5A6068, 0xFF747B82, 0xFF90969B, 0xFF747B82, 0xFF5A6068, 0xFF41454C, 0xFF292C32}
        };

        private FanLayer(RenderLayerParent<PeafowlEntity, ChickenModel<PeafowlEntity>> parent) {
            super(parent);
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition root = mesh.getRoot();
            for (int i = 0; i < feathers.length; i++) {
                float spread = (i - 4) * 0.13F;
                root.addOrReplaceChild("feather_" + i,
                        CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -10.0F, -0.5F, 2.0F, 10.0F, 1.0F),
                        PartPose.offsetAndRotation((i - 4) * 1.25F, -4.0F, 2.0F, 0.0F, 0.0F, spread));
            }
            ModelPart baked = LayerDefinition.create(mesh, 64, 32).bakeRoot();
            for (int i = 0; i < feathers.length; i++) feathers[i] = baked.getChild("feather_" + i);
        }

        @Override
        public void render(PoseStack pose, net.minecraft.client.renderer.MultiBufferSource buffers, int light,
                PeafowlEntity peafowl, float limbSwing, float limbSwingAmount, float partialTick,
                float ageInTicks, float netHeadYaw, float headPitch) {
            if (peafowl.isInvisible() || peafowl.isBaby() || peafowl.animaniax$isSyncedHen()) return;
            boolean sleeping = ((AnimalSleepState) (Object) peafowl).animaniax$isSleeping();
            pose.pushPose();
            VertexConsumer consumer = buffers.getBuffer(net.minecraft.client.renderer.RenderType.entityCutoutNoCull(FAN_TEXTURE));
            int[] colors = colors(peafowl.animaniax$getMorph());
            for (int i = 0; i < feathers.length; i++) {
                ModelPart feather = feathers[i];
                feather.xRot = sleeping ? 0.9F : -0.3F;
                feather.zRot = (i - 4) * (sleeping ? 0.08F : 0.13F);
                feather.render(pose, consumer, light, OverlayTexture.NO_OVERLAY, colors[i]);
            }
            pose.popPose();
        }

        private static int[] colors(String morph) {
            return switch (morph) {
                case "White" -> COLORS[1];
                case "Taupe" -> COLORS[2];
                case "Purple" -> COLORS[3];
                case "Peach" -> COLORS[4];
                case "Opal" -> COLORS[5];
                case "Charcoal" -> COLORS[6];
                default -> COLORS[0];
            };
        }
    }
}

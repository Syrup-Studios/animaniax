package net.syrupstudios.animaniax.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * Dynamic EntityModel adapter rendering placeholder JSON geometry with procedural animations and poses.
 */
public final class PlaceholderEntityModel<T extends Entity> extends EntityModel<T> {
    @FunctionalInterface
    public interface Animator<T extends Entity> {
        void animate(
                T entity,
                PlaceholderModel model,
                float limbSwing,
                float limbSwingAmount,
                float ageInTicks,
                float netHeadYaw,
                float headPitch,
                Map<String, PlaceholderModel.PartOverride> overrides);
    }

    private final Function<T, String> modelKeySupplier;
    private final Function<T, String> poseSupplier;
    private final Function<T, Set<String>> extraRootsSupplier;
    private final ToIntFunction<T> coloredTintSupplier;
    private final Animator<T> animator;

    private final Map<String, PlaceholderModel.PartOverride> overrides = new HashMap<>();
    @Nullable private PlaceholderModel activeModel;
    @Nullable private String activePose;
    @Nullable private Set<String> activeExtraRoots;
    private int activeColoredTint = 0xFFFFFFFF;

    public PlaceholderEntityModel(
            Function<ResourceLocation, RenderType> renderType,
            Function<T, String> modelKeySupplier,
            Animator<T> animator,
            @Nullable Function<T, String> poseSupplier,
            @Nullable Function<T, Set<String>> extraRootsSupplier,
            @Nullable ToIntFunction<T> coloredTintSupplier) {
        super(renderType);
        this.modelKeySupplier = modelKeySupplier;
        this.animator = animator;
        this.poseSupplier = poseSupplier != null ? poseSupplier : entity -> null;
        this.extraRootsSupplier = extraRootsSupplier != null ? extraRootsSupplier : entity -> null;
        this.coloredTintSupplier = coloredTintSupplier != null ? coloredTintSupplier : entity -> 0xFFFFFFFF;
    }

    public PlaceholderEntityModel(Function<T, String> modelKeySupplier, Animator<T> animator) {
        this(RenderType::entityCutoutNoCull, modelKeySupplier, animator, null, null, null);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        overrides.clear();
        String key = modelKeySupplier.apply(entity);
        activeModel = key != null ? PlaceholderModelRegistry.INSTANCE.entity(key) : null;
        activePose = poseSupplier.apply(entity);
        activeExtraRoots = extraRootsSupplier.apply(entity);
        activeColoredTint = coloredTintSupplier.applyAsInt(entity);
        if (activeModel != null && animator != null) {
            animator.animate(entity, activeModel, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, overrides);
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay, int color) {
        if (activeModel == null) return;
        int tinted = multiplyArgb(color, activeColoredTint);
        activeModel.render(poseStack, consumer, packedLight, packedOverlay, color, tinted, activePose, overrides, activeExtraRoots);
    }

    private static int multiplyArgb(int a, int b) {
        if (b == 0xFFFFFFFF) return a;
        if (a == 0xFFFFFFFF) return b;
        int aa = ((a >>> 24) * (b >>> 24)) / 255;
        int rr = (((a >>> 16) & 0xFF) * ((b >>> 16) & 0xFF)) / 255;
        int gg = (((a >>> 8) & 0xFF) * ((b >>> 8) & 0xFF)) / 255;
        int bb = ((a & 0xFF) * (b & 0xFF)) / 255;
        return (aa << 24) | (rr << 16) | (gg << 8) | bb;
    }
}

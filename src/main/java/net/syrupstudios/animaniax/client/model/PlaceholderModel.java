package net.syrupstudios.animaniax.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import net.syrupstudios.animaniax.client.model.PlaceholderModelDefinition.BoxDef;
import net.syrupstudios.animaniax.client.model.PlaceholderModelDefinition.PartDef;
import net.syrupstudios.animaniax.client.model.PlaceholderModelDefinition.PoseDef;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Compiled hierarchical placeholder model backed by external JSON geometry definitions.
 */
public final class PlaceholderModel {
    private static final float RAD_TO_DEG = (float) (180.0D / Math.PI);
    private static final EnumSet<Direction> ALL_FACES = EnumSet.allOf(Direction.class);

    public static final class PartOverride {
        public float rotX;
        public float rotY;
        public float rotZ;
        public float pivotX;
        public float pivotY;
        public float pivotZ;
        public boolean absoluteRot;
        public boolean hasPivotOverride;

        public PartOverride() {}

        public static PartOverride addRot(float rx, float ry, float rz) {
            PartOverride o = new PartOverride();
            o.rotX = rx;
            o.rotY = ry;
            o.rotZ = rz;
            return o;
        }

        public static PartOverride setRot(float rx, float ry, float rz) {
            PartOverride o = new PartOverride();
            o.rotX = rx;
            o.rotY = ry;
            o.rotZ = rz;
            o.absoluteRot = true;
            return o;
        }
    }

    public static final class CompiledPart {
        public final String name;
        public final float pivotX;
        public final float pivotY;
        public final float pivotZ;
        public final float offsetX;
        public final float offsetY;
        public final float offsetZ;
        public final float rotX;
        public final float rotY;
        public final float rotZ;
        public final boolean animaniaQuat;
        public final boolean colored;
        public final float renderScale;
        public final List<ModelPart.Cube> cubes;
        public final List<CompiledPart> children = new ArrayList<>();

        CompiledPart(String name, PartDef def, int defaultTexW, int defaultTexH) {
            this.name = name;
            this.pivotX = def.pivot[0];
            this.pivotY = def.pivot[1];
            this.pivotZ = def.pivot[2];
            this.offsetX = def.offset[0];
            this.offsetY = def.offset[1];
            this.offsetZ = def.offset[2];
            this.rotX = def.rotation[0];
            this.rotY = def.rotation[1];
            this.rotZ = def.rotation[2];
            this.animaniaQuat = def.animaniaQuat;
            this.colored = def.colored;
            this.renderScale = def.renderScale != null ? def.renderScale : 1.0F;
            float tw = def.texWidth != null ? def.texWidth : defaultTexW;
            float th = def.texHeight != null ? def.texHeight : defaultTexH;
            int u = def.texOffset[0];
            int v = def.texOffset[1];
            List<ModelPart.Cube> built = new ArrayList<>(def.boxes.size());
            for (BoxDef b : def.boxes) {
                built.add(new ModelPart.Cube(
                        u, v, b.x, b.y, b.z,
                        b.dx, b.dy, b.dz,
                        b.inflate, b.inflate, b.inflate,
                        b.mirror, tw, th, ALL_FACES));
            }
            this.cubes = Collections.unmodifiableList(built);
        }
    }

    private final boolean renderWithRotation;
    private final Map<String, CompiledPart> partsByName;
    private final List<CompiledPart> rootParts;
    private final Map<String, Map<String, PoseDef>> poses;

    public PlaceholderModel(PlaceholderModelDefinition def) {
        this.renderWithRotation = def.renderWithRotation;
        this.poses = def.poses != null ? def.poses : Collections.emptyMap();

        Map<String, CompiledPart> built = new LinkedHashMap<>();
        for (Map.Entry<String, PartDef> entry : def.parts.entrySet()) {
            built.put(entry.getKey(), new CompiledPart(entry.getKey(), entry.getValue(), def.textureWidth, def.textureHeight));
        }
        for (Map.Entry<String, PartDef> entry : def.parts.entrySet()) {
            CompiledPart parent = built.get(entry.getKey());
            for (String childName : entry.getValue().children) {
                CompiledPart child = built.get(childName);
                if (child != null) parent.children.add(child);
            }
        }
        this.partsByName = Collections.unmodifiableMap(built);

        List<CompiledPart> roots = new ArrayList<>(def.roots.size());
        for (String r : def.roots) {
            CompiledPart part = built.get(r);
            if (part != null) roots.add(part);
        }
        this.rootParts = Collections.unmodifiableList(roots);
    }

    public boolean hasPose(String poseName) {
        return poses.containsKey(poseName);
    }

    @Nullable
    public CompiledPart getPart(String name) {
        return partsByName.get(name);
    }

    public Map<String, CompiledPart> getParts() {
        return partsByName;
    }

    public void render(PoseStack poseStack, VertexConsumer consumer, int light, int overlay, int baseArgb, int coloredArgb) {
        render(poseStack, consumer, light, overlay, baseArgb, coloredArgb, null, null, null);
    }

    public void render(
            PoseStack poseStack,
            VertexConsumer consumer,
            int light,
            int overlay,
            int baseArgb,
            int coloredArgb,
            @Nullable String poseName,
            @Nullable Map<String, PartOverride> overrides,
            @Nullable Set<String> extraRoots) {
        Map<String, PoseDef> poseMap = poseName != null ? poses.get(poseName) : null;
        for (CompiledPart root : rootParts) {
            renderNode(root, poseStack, consumer, light, overlay, baseArgb, coloredArgb, poseMap, overrides);
        }
        if (extraRoots != null) {
            for (String extra : extraRoots) {
                CompiledPart part = partsByName.get(extra);
                if (part != null) {
                    renderNode(part, poseStack, consumer, light, overlay, baseArgb, coloredArgb, poseMap, overrides);
                }
            }
        }
    }

    public void renderPart(
            String partName,
            PoseStack poseStack,
            VertexConsumer consumer,
            int light,
            int overlay,
            int baseArgb,
            int coloredArgb,
            @Nullable Map<String, PartOverride> overrides) {
        CompiledPart part = partsByName.get(partName);
        if (part != null) {
            renderNode(part, poseStack, consumer, light, overlay, baseArgb, coloredArgb, null, overrides);
        }
    }

    private void renderNode(
            CompiledPart part,
            PoseStack poseStack,
            VertexConsumer consumer,
            int light,
            int overlay,
            int baseArgb,
            int coloredArgb,
            @Nullable Map<String, PoseDef> poseMap,
            @Nullable Map<String, PartOverride> overrides) {
        PoseDef pose = poseMap != null ? poseMap.get(part.name) : null;
        PartOverride over = overrides != null ? overrides.get(part.name) : null;

        float px = pose != null && pose.pivot != null ? pose.pivot[0] : part.pivotX;
        float py = pose != null && pose.pivot != null ? pose.pivot[1] : part.pivotY;
        float pz = pose != null && pose.pivot != null ? pose.pivot[2] : part.pivotZ;
        if (over != null && over.hasPivotOverride) {
            px = over.pivotX;
            py = over.pivotY;
            pz = over.pivotZ;
        }

        float ox = pose != null && pose.offset != null ? pose.offset[0] : part.offsetX;
        float oy = pose != null && pose.offset != null ? pose.offset[1] : part.offsetY;
        float oz = pose != null && pose.offset != null ? pose.offset[2] : part.offsetZ;

        float rx = pose != null && pose.rotation != null ? pose.rotation[0] : part.rotX;
        float ry = pose != null && pose.rotation != null ? pose.rotation[1] : part.rotY;
        float rz = pose != null && pose.rotation != null ? pose.rotation[2] : part.rotZ;
        if (over != null) {
            if (over.absoluteRot) {
                rx = over.rotX;
                ry = over.rotY;
                rz = over.rotZ;
            } else {
                rx += over.rotX;
                ry += over.rotY;
                rz += over.rotZ;
            }
        }

        poseStack.pushPose();
        if (part.renderScale != 1.0F) {
            poseStack.scale(part.renderScale, part.renderScale, part.renderScale);
        }
        poseStack.translate(px / 16.0F, py / 16.0F, pz / 16.0F);
        if (part.animaniaQuat) {
            poseStack.mulPose(PlaceholderModelDefinition.quatFromEulerDeg(rx * RAD_TO_DEG, ry * RAD_TO_DEG, rz * RAD_TO_DEG));
        } else if (renderWithRotation) {
            if (ry != 0.0F) poseStack.mulPose(Axis.YP.rotation(ry));
            if (rx != 0.0F) poseStack.mulPose(Axis.XP.rotation(rx));
            if (rz != 0.0F) poseStack.mulPose(Axis.ZP.rotation(rz));
        } else {
            if (rz != 0.0F) poseStack.mulPose(Axis.ZP.rotation(rz));
            if (ry != 0.0F) poseStack.mulPose(Axis.YP.rotation(ry));
            if (rx != 0.0F) poseStack.mulPose(Axis.XP.rotation(rx));
        }
        if (ox != 0.0F || oy != 0.0F || oz != 0.0F) {
            poseStack.translate(ox / 16.0F, oy / 16.0F, oz / 16.0F);
        }

        int color = part.colored ? coloredArgb : baseArgb;
        PoseStack.Pose poseEntry = poseStack.last();
        for (ModelPart.Cube cube : part.cubes) {
            cube.compile(poseEntry, consumer, light, overlay, color);
        }

        for (CompiledPart child : part.children) {
            renderNode(child, poseStack, consumer, light, overlay, baseArgb, coloredArgb, poseMap, overrides);
        }
        poseStack.popPose();
    }
}

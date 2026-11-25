package net.syrupstudios.animaniax.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

/**
 * Hierarchical CraftStudio model runtime reproducing .csjsmodel geometry and transforms.
 */
public final class CraftStudioModel {
    public static final class Quad {
        final Vector3f[] corners;
        final int u1;
        final int v1;
        final int u2;
        final int v2;
        final Vector3f normal;

        Quad(Vector3f[] corners, int u1, int v1, int u2, int v2) {
            this.corners = corners;
            this.u1 = u1;
            this.v1 = v1;
            this.u2 = u2;
            this.v2 = v2;
            Vector3f e1 = new Vector3f(corners[2]).sub(corners[1]);
            Vector3f e2 = new Vector3f(corners[0]).sub(corners[1]);
            Vector3f n = e1.cross(e2, new Vector3f());
            if (n.lengthSquared() > 1.0E-8F) {
                n.normalize();
            } else {
                n.set(0.0F, 1.0F, 0.0F);
            }
            this.normal = n;
        }
    }

    public static final class Node {
        public final String name;
        public final float pivotX;
        public final float pivotY;
        public final float pivotZ;
        public final float offsetX;
        public final float offsetY;
        public final float offsetZ;
        public final float rotXDeg;
        public final float rotYDeg;
        public final float rotZDeg;
        public final float stretchX;
        public final float stretchY;
        public final float stretchZ;
        public final Quad[] quads;
        public final List<Node> children;

        Node(
                String name,
                float pivotX, float pivotY, float pivotZ,
                float offsetX, float offsetY, float offsetZ,
                float rotXDeg, float rotYDeg, float rotZDeg,
                float stretchX, float stretchY, float stretchZ,
                Quad[] quads,
                List<Node> children) {
            this.name = name;
            this.pivotX = pivotX;
            this.pivotY = pivotY;
            this.pivotZ = pivotZ;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.rotXDeg = rotXDeg;
            this.rotYDeg = rotYDeg;
            this.rotZDeg = rotZDeg;
            this.stretchX = stretchX;
            this.stretchY = stretchY;
            this.stretchZ = stretchZ;
            this.quads = quads;
            this.children = children;
        }
    }

    private final List<Node> roots;

    public CraftStudioModel(List<Node> roots) {
        this.roots = roots;
    }

    public void render(
            PoseStack poseStack,
            VertexConsumer consumer,
            int light,
            int overlay,
            int argb,
            float texWidth,
            float texHeight,
            @Nullable Map<String, Vector3f> extraRotDegByName) {
        for (Node root : roots) {
            renderNode(root, poseStack, consumer, light, overlay, argb, texWidth, texHeight, extraRotDegByName);
        }
    }

    private void renderNode(
            Node node,
            PoseStack poseStack,
            VertexConsumer consumer,
            int light,
            int overlay,
            int argb,
            float texWidth,
            float texHeight,
            @Nullable Map<String, Vector3f> extraRotDegByName) {
        poseStack.pushPose();
        poseStack.translate(node.pivotX / 16.0F, node.pivotY / 16.0F, node.pivotZ / 16.0F);
        float rx = node.rotXDeg;
        float ry = node.rotYDeg;
        float rz = node.rotZDeg;
        if (extraRotDegByName != null) {
            Vector3f extra = extraRotDegByName.get(node.name);
            if (extra != null) {
                rx += extra.x;
                ry += extra.y;
                rz += extra.z;
            }
        }
        poseStack.mulPose(PlaceholderModelDefinition.quatFromEulerDeg(rx, ry, rz));
        if (node.offsetX != 0.0F || node.offsetY != 0.0F || node.offsetZ != 0.0F) {
            poseStack.translate(node.offsetX / 16.0F, node.offsetY / 16.0F, node.offsetZ / 16.0F);
        }

        if (node.quads.length > 0) {
            poseStack.pushPose();
            if (node.stretchX != 1.0F || node.stretchY != 1.0F || node.stretchZ != 1.0F) {
                poseStack.scale(node.stretchX, node.stretchY, node.stretchZ);
            }
            PoseStack.Pose poseEntry = poseStack.last();
            Matrix4f mat = poseEntry.pose();
            Vector3f posVec = new Vector3f();
            Vector3f normVec = new Vector3f();
            for (Quad q : node.quads) {
                poseEntry.transformNormal(q.normal, normVec);
                float uStart = q.u1 / texWidth;
                float vStart = q.v1 / texHeight;
                float uEnd = q.u2 / texWidth;
                float vEnd = q.v2 / texHeight;

                emitVertex(consumer, mat, posVec, q.corners[0], argb, uEnd, vStart, overlay, light, normVec);
                emitVertex(consumer, mat, posVec, q.corners[1], argb, uStart, vStart, overlay, light, normVec);
                emitVertex(consumer, mat, posVec, q.corners[2], argb, uStart, vEnd, overlay, light, normVec);
                emitVertex(consumer, mat, posVec, q.corners[3], argb, uEnd, vEnd, overlay, light, normVec);
            }
            poseStack.popPose();
        }

        for (Node child : node.children) {
            renderNode(child, poseStack, consumer, light, overlay, argb, texWidth, texHeight, extraRotDegByName);
        }
        poseStack.popPose();
    }

    private static void emitVertex(
            VertexConsumer consumer,
            Matrix4f mat,
            Vector3f scratch,
            Vector3f corner,
            int argb,
            float u,
            float v,
            int overlay,
            int light,
            Vector3f normal) {
        mat.transformPosition(corner.x / 16.0F, corner.y / 16.0F, corner.z / 16.0F, scratch);
        consumer.addVertex(scratch.x, scratch.y, scratch.z, argb, u, v, overlay, light, normal.x, normal.y, normal.z);
    }
}

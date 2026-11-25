package net.syrupstudios.animaniax.client.model;

import org.joml.Quaternionf;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Immutable JSON schema for converted Animania Java entity and block entity models.
 */
public final class PlaceholderModelDefinition {
    public int textureWidth = 64;
    public int textureHeight = 32;
    public boolean renderWithRotation = false;
    public List<String> roots = Collections.emptyList();
    public List<String> optionalParts = Collections.emptyList();
    public Map<String, PartDef> parts = Collections.emptyMap();
    public Map<String, Map<String, PoseDef>> poses = Collections.emptyMap();

    public static final class PartDef {
        public int[] texOffset = {0, 0};
        public float[] pivot = {0.0F, 0.0F, 0.0F};
        public float[] offset = {0.0F, 0.0F, 0.0F};
        public float[] rotation = {0.0F, 0.0F, 0.0F};
        public boolean mirror = false;
        public boolean animaniaQuat = false;
        public boolean colored = false;
        public Integer texWidth = null;
        public Integer texHeight = null;
        public Float renderScale = null;
        public List<BoxDef> boxes = Collections.emptyList();
        public List<String> children = Collections.emptyList();
    }

    public static final class BoxDef {
        public float x;
        public float y;
        public float z;
        public int dx;
        public int dy;
        public int dz;
        public float inflate = 0.0F;
        public boolean mirror = false;
    }

    public static final class PoseDef {
        public float[] pivot = null;
        public float[] offset = null;
        public float[] rotation = null;
    }

    /**
     * Converts CraftStudio / ModelRendererAnimania Euler angles (degrees) into a quaternion.
     */
    public static Quaternionf quatFromEulerDeg(float rxDeg, float ryDeg, float rzDeg) {
        double hx = Math.toRadians(rxDeg) * 0.5D;
        double hy = Math.toRadians(ryDeg) * 0.5D;
        double hz = Math.toRadians(rzDeg) * 0.5D;
        float cx = (float) Math.cos(hx);
        float cy = (float) Math.cos(hy);
        float cz = (float) Math.cos(hz);
        float sx = (float) Math.sin(hx);
        float sy = (float) Math.sin(hy);
        float sz = (float) Math.sin(hz);
        float w = cx * cy * cz + sx * sy * sz;
        float x = sx * cy * cz + cx * sy * sz;
        float y = cx * sy * cz - sx * cy * sz;
        float z = cx * cy * sz - sx * sy * cz;
        return new Quaternionf(x, y, z, w);
    }
}

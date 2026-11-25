package net.syrupstudios.animaniax.client.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser converting CraftStudio .csjsmodel JSON trees into {@link CraftStudioModel}.
 */
public final class CraftStudioModelLoader {
    private static final int[] VERTEX_REMAP = {3, 2, 1, 0, 6, 7, 4, 5};
    private static final int[][] QUAD_CORNERS = {
            {5, 1, 2, 6},
            {0, 4, 7, 3},
            {5, 4, 0, 1},
            {2, 3, 7, 6},
            {1, 0, 3, 2},
            {4, 5, 6, 7}
    };

    private CraftStudioModelLoader() {}

    public static CraftStudioModel parse(JsonObject root) {
        JsonArray tree = root.getAsJsonArray("tree");
        List<CraftStudioModel.Node> roots = new ArrayList<>(tree.size());
        for (JsonElement elem : tree) {
            roots.add(parseNode(elem.getAsJsonObject(), true));
        }
        return new CraftStudioModel(roots);
    }

    private static CraftStudioModel.Node parseNode(JsonObject obj, boolean isRoot) {
        String name = obj.get("name").getAsString().trim().replace(' ', '_');
        JsonArray sizeArr = obj.getAsJsonArray("size");
        float sxRaw = sizeArr.get(0).getAsFloat();
        float syRaw = sizeArr.get(1).getAsFloat();
        float szRaw = sizeArr.get(2).getAsFloat();

        JsonArray posArr = obj.getAsJsonArray("position");
        float px = posArr.get(0).getAsFloat();
        float py = -posArr.get(1).getAsFloat() + (isRoot ? 24.0F : 0.0F);
        float pz = -posArr.get(2).getAsFloat();

        JsonArray rotArr = obj.getAsJsonArray("rotation");
        float rx = rotArr.get(0).getAsFloat();
        float ry = -rotArr.get(1).getAsFloat();
        float rz = -rotArr.get(2).getAsFloat();

        JsonArray offArr = obj.getAsJsonArray("offsetFromPivot");
        float ox = offArr.get(0).getAsFloat();
        float oy = -offArr.get(1).getAsFloat();
        float oz = -offArr.get(2).getAsFloat();

        JsonArray texArr = obj.getAsJsonArray("texOffset");
        int u = texArr.get(0).getAsInt();
        int v = texArr.get(1).getAsInt();

        Vector3f[] verts;
        float stretchX = 1.0F;
        float stretchY = 1.0F;
        float stretchZ = 1.0F;

        JsonElement vcElem = obj.get("vertexCoords");
        if (vcElem != null && vcElem.isJsonArray()) {
            JsonArray vc = vcElem.getAsJsonArray();
            verts = new Vector3f[8];
            for (int i = 0; i < 8; i++) {
                JsonArray c = vc.get(VERTEX_REMAP[i]).getAsJsonArray();
                verts[i] = new Vector3f(c.get(0).getAsFloat(), -c.get(1).getAsFloat(), -c.get(2).getAsFloat());
            }
            if (sxRaw != 0.0F) {
                float s = Math.abs(verts[1].x - verts[0].x) / sxRaw;
                if (s > 1.0E-6F) stretchX = s;
            }
            if (syRaw != 0.0F) {
                float s = Math.abs(verts[3].y - verts[0].y) / syRaw;
                if (s > 1.0E-6F) stretchY = s;
            }
            if (szRaw != 0.0F) {
                float s = Math.abs(verts[4].z - verts[0].z) / szRaw;
                if (s > 1.0E-6F) stretchZ = s;
            }
            for (int i = 0; i < 8; i++) {
                verts[i].set(verts[i].x / stretchX, verts[i].y / stretchY, verts[i].z / stretchZ);
            }
        } else {
            float sx = sxRaw;
            float sy = -syRaw;
            float sz = -szRaw;
            float x0 = -sx / 2.0F;
            float y0 = -sy / 2.0F;
            float z0 = -sz / 2.0F;
            float x1 = x0 + sx;
            float y1 = y0 + sy;
            float z1 = z0 + sz;
            verts = new Vector3f[] {
                    new Vector3f(x0, y0, z0),
                    new Vector3f(x1, y0, z0),
                    new Vector3f(x1, y1, z0),
                    new Vector3f(x0, y1, z0),
                    new Vector3f(x0, y0, z1),
                    new Vector3f(x1, y0, z1),
                    new Vector3f(x1, y1, z1),
                    new Vector3f(x0, y1, z1)
            };
        }

        CraftStudioModel.Quad[] quads;
        if (sxRaw == 0.0F && syRaw == 0.0F && szRaw == 0.0F && (vcElem == null || !vcElem.isJsonArray())) {
            quads = new CraftStudioModel.Quad[0];
        } else {
            quads = buildQuads(verts, u, v, sxRaw, syRaw, szRaw);
        }

        List<CraftStudioModel.Node> children = new ArrayList<>();
        JsonArray childrenArr = obj.getAsJsonArray("children");
        if (childrenArr != null) {
            for (JsonElement childElem : childrenArr) {
                children.add(parseNode(childElem.getAsJsonObject(), false));
            }
        }

        return new CraftStudioModel.Node(
                name,
                px, py, pz,
                ox, oy, oz,
                rx, ry, rz,
                stretchX, stretchY, stretchZ,
                quads,
                children);
    }

    private static CraftStudioModel.Quad[] buildQuads(Vector3f[] verts, int u, int v, float dx, float dy, float dz) {
        Vector3f[][] rawFaces = new Vector3f[6][4];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                rawFaces[i][j] = new Vector3f(verts[QUAD_CORNERS[i][j]]);
            }
        }

        // Reproduce CSModelBox.checkBlockForShadow vertex order flips
        Vector3f v10 = rawFaces[1][0];
        boolean flipX = (rawFaces[0][1].x - v10.x) < 0.0F;
        boolean flipY = (rawFaces[1][3].y - v10.y) > 0.0F;
        boolean flipZ = (rawFaces[1][1].z - v10.z) > 0.0F;
        boolean reverse = flipX ^ flipY ^ flipZ;

        int[][] uvs = getTextureUVsForRect(u, v, dx, dy, dz);
        CraftStudioModel.Quad[] result = new CraftStudioModel.Quad[6];
        for (int i = 0; i < 6; i++) {
            Vector3f[] f = rawFaces[i];
            if (reverse) {
                f = new Vector3f[] {f[3], f[2], f[1], f[0]};
            }
            int[] uv = uvs[i];
            result[i] = new CraftStudioModel.Quad(f, uv[0], uv[1], uv[2], uv[3]);
        }
        return result;
    }

    private static int[][] getTextureUVsForRect(int u, int v, float dx, float dy, float dz) {
        return new int[][] {
                {(int) (u + dz + dx + dz), (int) (v + dz + dy), (int) (u + dz + dx), (int) (v + dz)},
                {(int) (u + dz), (int) (v + dz + dy), u, (int) (v + dz)},
                {(int) (u + dz + dx), v, (int) (u + dz + dx + dx), (int) (v + dz)},
                {(int) (u + dz), v, (int) (u + dz + dx), (int) (v + dz)},
                {(int) (u + dz + dx + dz + dx), (int) (v + dz + dy), (int) (u + dz + dx + dz), (int) (v + dz)},
                {(int) (u + dz + dx), (int) (v + dz + dy), (int) (u + dz), (int) (v + dz)}
        };
    }
}

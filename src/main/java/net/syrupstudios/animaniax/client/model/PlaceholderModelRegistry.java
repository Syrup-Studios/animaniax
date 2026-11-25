package net.syrupstudios.animaniax.client.model;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Reloads resource-pack-overridable placeholder entity, blockentity, and CraftStudio models.
 */
public final class PlaceholderModelRegistry extends SimplePreparableReloadListener<PlaceholderModelRegistry.LoadedAssets> {
    public static final ResourceLocation LISTENER_ID = ResourceLocation.fromNamespaceAndPath("animaniax", "placeholder_models");
    public static final PlaceholderModelRegistry INSTANCE = new PlaceholderModelRegistry();

    private static final Logger LOGGER = LoggerFactory.getLogger("animaniax");
    private static final Gson GSON = new Gson();
    private static final String ENTITY_PREFIX = "models/placeholder/entity/";
    private static final String BLOCK_ENTITY_PREFIX = "models/placeholder/blockentity/";
    private static final String CRAFTSTUDIO_PREFIX = "models/placeholder/craftstudio/";

    public record LoadedAssets(
            Map<String, PlaceholderModel> entityModels,
            Map<String, PlaceholderModel> blockEntityModels,
            Map<String, CraftStudioModel> craftStudioModels) {}

    private volatile Map<String, PlaceholderModel> entityModels = Collections.emptyMap();
    private volatile Map<String, PlaceholderModel> blockEntityModels = Collections.emptyMap();
    private volatile Map<String, CraftStudioModel> craftStudioModels = Collections.emptyMap();

    private PlaceholderModelRegistry() {}

    @Override
    protected LoadedAssets prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<String, PlaceholderModel> entities = loadJsonModels(resourceManager, ENTITY_PREFIX);
        Map<String, PlaceholderModel> blockEntities = loadJsonModels(resourceManager, BLOCK_ENTITY_PREFIX);
        Map<String, CraftStudioModel> craftStudios = loadCraftStudioModels(resourceManager);
        return new LoadedAssets(entities, blockEntities, craftStudios);
    }

    @Override
    protected void apply(LoadedAssets prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        this.entityModels = prepared.entityModels();
        this.blockEntityModels = prepared.blockEntityModels();
        this.craftStudioModels = prepared.craftStudioModels();
    }

    @Nullable
    public PlaceholderModel entity(String name) {
        return entityModels.get(name);
    }

    @Nullable
    public PlaceholderModel blockEntity(String name) {
        return blockEntityModels.get(name);
    }

    @Nullable
    public CraftStudioModel craftStudio(String name) {
        return craftStudioModels.get(name);
    }

    private static Map<String, PlaceholderModel> loadJsonModels(ResourceManager manager, String prefix) {
        Map<String, PlaceholderModel> result = new HashMap<>();
        String folder = prefix.substring(0, prefix.length() - 1);
        Map<ResourceLocation, Resource> found = manager.listResources(folder, id -> id.getNamespace().equals("animaniax") && id.getPath().endsWith(".json"));
        for (Map.Entry<ResourceLocation, Resource> entry : found.entrySet()) {
            String path = entry.getKey().getPath();
            String key = path.substring(prefix.length(), path.length() - ".json".length());
            try (Reader reader = entry.getValue().openAsReader()) {
                PlaceholderModelDefinition def = GSON.fromJson(reader, PlaceholderModelDefinition.class);
                if (def != null) {
                    result.put(key, new PlaceholderModel(def));
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load placeholder model {}: {}", entry.getKey(), e.getMessage());
            }
        }
        return Collections.unmodifiableMap(result);
    }

    private static Map<String, CraftStudioModel> loadCraftStudioModels(ResourceManager manager) {
        Map<String, CraftStudioModel> result = new HashMap<>();
        String folder = CRAFTSTUDIO_PREFIX.substring(0, CRAFTSTUDIO_PREFIX.length() - 1);
        Map<ResourceLocation, Resource> found = manager.listResources(folder, id -> id.getNamespace().equals("animaniax") && id.getPath().endsWith(".csjsmodel"));
        for (Map.Entry<ResourceLocation, Resource> entry : found.entrySet()) {
            String path = entry.getKey().getPath();
            String key = path.substring(CRAFTSTUDIO_PREFIX.length(), path.length() - ".csjsmodel".length());
            try (var stream = entry.getValue().open();
                 var reader = new java.io.InputStreamReader(stream, StandardCharsets.UTF_8)) {
                var obj = JsonParser.parseReader(reader).getAsJsonObject();
                result.put(key, CraftStudioModelLoader.parse(obj));
            } catch (Exception e) {
                LOGGER.error("Failed to load CraftStudio model {}: {}", entry.getKey(), e.getMessage());
            }
        }
        return Collections.unmodifiableMap(result);
    }
}

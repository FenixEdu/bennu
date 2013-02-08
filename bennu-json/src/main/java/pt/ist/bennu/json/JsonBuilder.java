package pt.ist.bennu.json;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class JsonBuilder {
    private final Map<Class<?>, Object> adapters = new ConcurrentHashMap<>();
    private final TableRegistry<JsonCreator> creators = new TableRegistry<>(adapters);
    private final TableRegistry<JsonUpdater> updaters = new TableRegistry<>(adapters);
    private final TableRegistry<JsonViewer> viewers = new TableRegistry<>(adapters);

    public void setDefault(Class<?> objectClass, Class<?> registeeClass) {
        if (JsonCreator.class.isAssignableFrom(registeeClass)) {
            creators.setDefault(objectClass, (Class<? extends JsonCreator>) registeeClass);
        }
        if (JsonViewer.class.isAssignableFrom(registeeClass)) {
            viewers.setDefault(objectClass, (Class<? extends JsonViewer>) registeeClass);
        }
        if (JsonUpdater.class.isAssignableFrom(registeeClass)) {
            updaters.setDefault(objectClass, (Class<? extends JsonUpdater>) registeeClass);
        }
    }

    public JsonElement view(Object obj) {
        return view(obj, null);
    }

    public JsonElement view(Object obj, Class<? extends JsonViewer> jsonViewerClass) {
        if (obj instanceof Collection) {
            return views((Collection) obj, jsonViewerClass);
        }
        JsonViewer jsonViewer = viewers.get(obj.getClass(), jsonViewerClass);
        return jsonViewer.view(obj, this);
    }

    private JsonArray views(Collection coll, Class<? extends JsonViewer> jsonViewerClass) {
        JsonArray json = new JsonArray();
        for (final Object el : coll) {
            json.add(view(el, jsonViewerClass));
        }
        return json;
    }

    public <T> T create(String jsonData, Class<T> clazz) {
        return create(jsonData, clazz, null);
    }

    public <T> T create(String jsonData, Class<T> clazz, Class<? extends JsonCreator> jsonCreatorClass) {
        final JsonCreator jsonCreator = creators.get(clazz, jsonCreatorClass);
        return (T) jsonCreator.create(jsonData, this);
    }

    public <T> T update(String jsonData, T object) {
        return update(jsonData, object, null);
    }

    public <T> T update(String jsonData, T object, Class<? extends JsonUpdater> jsonUpdaterClass) {
        final JsonUpdater jsonUpdater = updaters.get(object.getClass(), jsonUpdaterClass);
        return (T) jsonUpdater.update(jsonData, object, this);
    }
}

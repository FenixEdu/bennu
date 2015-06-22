package org.fenixedu.bennu.core.rest;

import java.util.List;
import java.util.Map.Entry;

import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonCreator;
import org.fenixedu.bennu.core.json.JsonUpdater;
import org.fenixedu.bennu.core.json.JsonViewer;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class JsonAwareResource {

    private static final Logger LOG = LoggerFactory.getLogger("RestAdapters");
    private static final JsonBuilder BUILDER;
    private static final JsonParser PARSER;
    private static final Gson GSON;
    static {
        PARSER = new JsonParser();
        BUILDER = new JsonBuilder();
        if (CoreConfiguration.getConfiguration().developmentMode()) {
            GSON = new GsonBuilder().setPrettyPrinting().create();
        } else {
            GSON = new GsonBuilder().create();
        }
    }

    public static final JsonBuilder getBuilder() {
        return BUILDER;
    }

    public static final void setDefault(Class<?> objectClass, Class<?> registeeClass) {
        BUILDER.setDefault(objectClass, registeeClass);
    }

    public final JsonElement view(Object object) {
        return view(object, (Class<? extends JsonViewer<?>>) null);
    }

    public final JsonElement view(Object object, Class<? extends JsonViewer<?>> viewerClass) {
        if (object == null) {
            return view(object, (Class<?>) null, viewerClass);
        }
        return view(object, object.getClass(), viewerClass);
    }

    public final JsonElement view(Object object, Class<?> objectClass, Class<? extends JsonViewer<?>> viewerClass) {
        return BUILDER.view(object, objectClass, viewerClass);
    }

    public final JsonElement view(Object object, String collectionKey) {
        return view(object, collectionKey, null);
    }

    public final JsonElement view(Object object, String collectionKey, Class<? extends JsonViewer<?>> viewerClass) {
        if (object == null) {
            return view(object, (Class<?>) null, collectionKey, viewerClass);
        }
        return view(object, object.getClass(), collectionKey, viewerClass);
    }

    public final JsonElement view(Object object, Class<?> objectClass, String collectionKey,
            Class<? extends JsonViewer<?>> viewerClass) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(collectionKey, BUILDER.view(object, objectClass, viewerClass));
        return jsonObject;
    }

    public final JsonElement viewPaginated(List<?> object, String collectionKey, int skip, int pageSize) {
        return viewPaginated(object, collectionKey, null, skip, pageSize);
    }

    public final JsonElement viewPaginated(List<?> object, String collectionKey, Class<? extends JsonViewer<?>> viewerClass,
            int skip, int pageSize) {
        if (object == null) {
            return viewPaginated(null, (Class<?>) null, collectionKey, viewerClass, skip, pageSize);
        }
        return viewPaginated(object, object.getClass(), collectionKey, viewerClass, skip, pageSize);
    }

    public final JsonElement viewPaginated(List<?> object, Class<?> objectClass, String collectionKey,
            Class<? extends JsonViewer<?>> viewerClass, int skip, int pageSize) {
        JsonObject jsonObject = new JsonObject();
        if (object == null) {
            jsonObject.add(collectionKey, new JsonArray());
            jsonObject.addProperty("total", 0);
        } else {
            jsonObject.add(collectionKey, BUILDER.view(
                    object.subList(Math.min(object.size(), skip), Math.min(object.size(), skip + pageSize)), objectClass,
                    viewerClass));
            jsonObject.addProperty("total", object.size());
        }
        return jsonObject;
    }

    public <T> T create(JsonElement jsonData, Class<T> clazz) {
        return create(jsonData, clazz, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T create(JsonElement jsonData, Class<T> clazz, Class<? extends JsonCreator<? extends T>> jsonCreatorClass) {
        LOG.trace("Create instance of {} with data {}", clazz.getSimpleName(), jsonData);
        return (T) innerCreate(jsonData, clazz, jsonCreatorClass);
    }

    @Atomic(mode = TxMode.WRITE)
    private Object innerCreate(JsonElement jsonData, Class<?> clazz, Class<? extends JsonCreator<?>> jsonCreatorClass) {
        return BUILDER.create(jsonData, clazz, jsonCreatorClass);
    }

    public <T> T update(JsonElement jsonData, T object) {
        return update(jsonData, object, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T update(JsonElement jsonData, T object, Class<? extends JsonUpdater<? extends T>> jsonUpdaterClass) {
        LOG.trace("Update instance {} with data {}", object.toString(), jsonData);
        return (T) innerUpdate(jsonData, object, jsonUpdaterClass);
    }

    @Atomic(mode = TxMode.WRITE)
    private Object innerUpdate(JsonElement jsonData, Object object, Class<? extends JsonUpdater<?>> jsonUpdaterClass) {
        return BUILDER.update(jsonData, object, jsonUpdaterClass);
    }

    protected JsonElement parse(String jsonString) {
        try {
            if (Strings.isNullOrEmpty(jsonString)) {
                return new JsonObject();
            }
            return PARSER.parse(jsonString);
        } catch (JsonParseException | IllegalStateException e) {
            throw BennuCoreDomainException.parseError();
        }
    }

    public static String toJson(JsonElement el) {
        return GSON.toJson(el);
    }

    /**
     * merges the source within target
     * 
     * @param target json where to merge the source
     * @param source the json source to merge
     */
    public void merge(JsonObject target, JsonObject source) {
        for (final Entry<String, JsonElement> entry : source.entrySet()) {
            target.add(entry.getKey(), entry.getValue());
        }
    }
}

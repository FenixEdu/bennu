package pt.ist.bennu.core.rest.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.domain.exceptions.BennuCoreDomainException;
import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.json.JsonCreator;
import pt.ist.bennu.json.JsonUpdater;
import pt.ist.bennu.json.JsonViewer;
import pt.ist.bennu.service.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        GSON = new GsonBuilder().setPrettyPrinting().create();
    }

    public static final JsonBuilder getBuilder() {
        return BUILDER;
    }

    public static final void setDefault(Class<?> objectClass, Class<?> registeeClass) {
        BUILDER.setDefault(objectClass, registeeClass);
    }

    public final String view(Object object) {
        return view(object, (Class<? extends JsonViewer<?>>) null);
    }

    public final String view(Object object, Class<? extends JsonViewer<?>> viewerClass) {
        if (object == null) {
            return view(object, (Class<?>) null, viewerClass);
        }
        return view(object, object.getClass(), viewerClass);
    }

    public final String view(Object object, Class<?> objectClass, Class<? extends JsonViewer<?>> viewerClass) {
        return GSON.toJson(BUILDER.view(object, objectClass, viewerClass));
    }

    public final String view(Object object, String collectionKey) {
        return view(object, collectionKey, null);
    }

    public final String view(Object object, String collectionKey, Class<? extends JsonViewer<?>> viewerClass) {
        if (object == null) {
            return view(object, (Class<?>) null, collectionKey, viewerClass);
        }
        return view(object, object.getClass(), collectionKey, viewerClass);
    }

    public final String view(Object object, Class<?> objectClass, String collectionKey, Class<? extends JsonViewer<?>> viewerClass) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(collectionKey, BUILDER.view(object, objectClass, viewerClass));
        return GSON.toJson(jsonObject);
    }

    public <T> T create(String jsonData, Class<T> clazz) {
        return create(jsonData, clazz, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T create(String jsonData, Class<T> clazz, Class<? extends JsonCreator<? extends T>> jsonCreatorClass) {
        return (T) innerCreate(jsonData, clazz, jsonCreatorClass);
    }

    @Service
    private Object innerCreate(String jsonData, Class<?> clazz, Class<? extends JsonCreator<?>> jsonCreatorClass) {
        LOG.info("Create instance of {} with data {}", clazz.getSimpleName(), jsonData);
        return BUILDER.create(parse(jsonData), clazz, jsonCreatorClass);
    }

    public <T> T update(String jsonData, T object) {
        return update(jsonData, object, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T update(String jsonData, T object, Class<? extends JsonUpdater<? extends T>> jsonUpdaterClass) {
        return (T) innerUpdate(jsonData, object, jsonUpdaterClass);
    }

    @Service
    private Object innerUpdate(String jsonData, Object object, Class<? extends JsonUpdater<?>> jsonUpdaterClass) {
        LOG.info("Update instance {} with data {}", object.toString(), jsonData);
        return BUILDER.update(parse(jsonData), object, jsonUpdaterClass);
    }

    private JsonObject parse(String jsonString) {
        try {
            return PARSER.parse(jsonString).getAsJsonObject();
        } catch (JsonParseException | IllegalStateException e) {
            throw BennuCoreDomainException.parseError();
        }
    }

}

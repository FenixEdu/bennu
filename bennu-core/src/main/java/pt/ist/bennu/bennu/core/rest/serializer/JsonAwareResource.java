package pt.ist.bennu.bennu.core.rest.serializer;

import org.joda.time.DateTime;

import pt.ist.bennu.bennu.core.rest.BennuRestResource.CasConfigContext;
import pt.ist.bennu.bennu.core.rest.mapper.BennuRestError;
import pt.ist.bennu.bennu.core.rest.mapper.CasConfigContextSerializer;
import pt.ist.bennu.bennu.core.rest.mapper.DateTimeViewer;
import pt.ist.bennu.bennu.core.rest.mapper.RestException;
import pt.ist.bennu.bennu.core.rest.mapper.UserViewer;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.json.JsonCreator;
import pt.ist.bennu.json.JsonUpdater;
import pt.ist.bennu.json.JsonViewer;
import pt.ist.bennu.service.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class JsonAwareResource {

    private static final JsonBuilder BUILDER;
    private static final JsonParser PARSER;
    static {
        PARSER = new JsonParser();
        BUILDER = new JsonBuilder();
        BUILDER.setDefault(User.class, UserViewer.class);
        BUILDER.setDefault(CasConfigContext.class, CasConfigContextSerializer.class);
        BUILDER.setDefault(DateTime.class, DateTimeViewer.class);
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
        return BUILDER.view(object, viewerClass).toString();
    }

    public final String view(Object object, String collectionKey) {
        return view(object, collectionKey, null);
    }

    public final String view(Object object, String collectionKey, Class<? extends JsonViewer<?>> viewerClass) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(collectionKey, BUILDER.view(object, viewerClass));
        return jsonObject.toString();
    }

    public <T> T create(String jsonData, Class<T> clazz) {
        return create(jsonData, clazz, null);
    }

    public <T> T create(String jsonData, Class<T> clazz, Class<? extends JsonCreator<? extends T>> jsonCreatorClass) {
        return (T) innerCreate(jsonData, clazz, jsonCreatorClass);
    }

    @Service
    private Object innerCreate(String jsonData, Class<?> clazz, Class<? extends JsonCreator<?>> jsonCreatorClass) {
        return BUILDER.create(parse(jsonData), clazz, jsonCreatorClass);
    }

    public <T> T update(String jsonData, T object) {
        return update(jsonData, object, null);
    }

    @Service
    private Object innerUpdate(String jsonData, Object object, Class<? extends JsonUpdater<?>> jsonUpdaterClass) {
        return BUILDER.update(parse(jsonData), object, jsonUpdaterClass);
    }

    public <T> T update(String jsonData, T object, Class<? extends JsonUpdater<? extends T>> jsonUpdaterClass) {
        return (T) innerUpdate(jsonData, object, jsonUpdaterClass);
    }

    private JsonObject parse(String jsonString) {
        try {
            return PARSER.parse(jsonString).getAsJsonObject();
        } catch (JsonParseException e) {
            throw new RestException(BennuRestError.BAD_REQUEST);
        } catch (IllegalStateException e) {
            throw new RestException(BennuRestError.BAD_REQUEST);
        }
    }

}

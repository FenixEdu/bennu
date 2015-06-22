package org.fenixedu.bennu.core.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.fenixedu.bennu.core.util.CoreConfiguration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;

/***
 * 
 * Reads/writes {@link JsonElement} objects from/to messages when using {@link Consumes} {@link Produces} on JAX-RS methods.
 * 
 * @author Sérgio Silva (sergio.silva@tecnico.ulisboa.pt)
 * @see Gson
 * @param <T> subclass of {@link JsonElement}
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class JsonBodyReaderWriter<T extends JsonElement> implements MessageBodyReader<T>, MessageBodyWriter<T> {

    private final Gson gson;

    public JsonBodyReaderWriter() {
        GsonBuilder builder = new GsonBuilder();
        if (CoreConfiguration.getConfiguration().developmentMode()) {
            builder = builder.setPrettyPrinting();
        }
        gson = builder.create();
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // ignore media type parameters
        return MediaType.APPLICATION_JSON_TYPE.equals(new MediaType(mediaType.getType(), mediaType.getSubtype()));
    }

    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        try (InputStreamReader reader = new InputStreamReader(entityStream)) {
            return gson.fromJson(reader, genericType);
        } catch (Throwable e) {
            throw new WebApplicationException(e.getMessage(), Status.BAD_REQUEST);
        }
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isReadable(type, genericType, annotations, mediaType);
    }

    @Override
    public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(entityStream))) {
            gson.toJson(t, writer);
        } catch (Throwable e) {
            throw new WebApplicationException(e.getMessage(), Status.BAD_REQUEST);
        }
    }

}

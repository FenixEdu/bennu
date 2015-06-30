package org.fenixedu.bennu.core.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/***
 * Parameter Converter for {@link JsonElement}
 * 
 * Converts JSON String to a {@link JsonElement} using {@link Gson}
 * 
 * @author Sérgio Silva (sergio.silva@tecnico.ulisboa.pt)
 * 
 * @see JsonBodyReaderWriter
 *
 */
@Provider
public class JsonParamConverterProvider implements ParamConverterProvider {

    @Context
    Providers providers;

    @Override
    public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType, final Annotation[] annotations) {

        if (!JsonElement.class.isAssignableFrom(rawType)) {
            return null;
        }

        final MessageBodyReader<T> reader =
                providers.getMessageBodyReader(rawType, genericType, annotations, MediaType.APPLICATION_JSON_TYPE);

        final MessageBodyWriter<T> writer =
                providers.getMessageBodyWriter(rawType, genericType, annotations, MediaType.APPLICATION_JSON_TYPE);

        if (reader == null || !reader.isReadable(rawType, genericType, annotations, MediaType.APPLICATION_JSON_TYPE)) {
            return null;
        }

        return new ParamConverter<T>() {

            @Override
            public T fromString(String value) {
                try {
                    return reader.readFrom(rawType, genericType, annotations, MediaType.APPLICATION_JSON_TYPE, null,
                            new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8)));
                } catch (WebApplicationException | IOException e) {
                    throw new WebApplicationException(e, Status.BAD_REQUEST);
                }
            }

            @Override
            public String toString(T value) {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    writer.writeTo(value, rawType, genericType, annotations, MediaType.APPLICATION_JSON_TYPE, null, baos);
                    baos.flush();
                    return baos.toString(StandardCharsets.UTF_8.name());
                } catch (IOException e) {
                    throw new WebApplicationException(e, Status.BAD_REQUEST);
                }
            }
        };
    }
}

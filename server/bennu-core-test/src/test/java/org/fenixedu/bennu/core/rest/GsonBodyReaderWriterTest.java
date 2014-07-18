package org.fenixedu.bennu.core.rest;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/***
 * Tests for reading and writing body message as well as param convertion from/to String/JsonElement.
 * 
 * @author Sérgio Silva (sergio.silva@tecnico.ulisboa.pt)
 * 
 * @see JsonBodyReaderWriter
 * @see JsonParamConverterProvider
 * @see JerseyTest
 *
 */
public class GsonBodyReaderWriterTest extends JerseyTest {

    @Path("resource")
    public static class TestResource {
        @Path("/bodyreadertest")
        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        public String getJsonNameProperty(JsonElement json) {
            return json.getAsJsonObject().get("name").getAsString();
        }

        @Path("/bodywritertest")
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public JsonElement getJsonElement(@QueryParam("name") String name) {
            JsonObject json = new JsonObject();
            json.addProperty("name", name);
            return json;
        }

        @Path("/readerwritertest")
        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public JsonArray map(@QueryParam("data") JsonObject data, JsonArray elements) {
            JsonArray array = new JsonArray();
            array.add(new JsonPrimitive("Doing map for " + data.get("name").getAsString()));
            for (JsonElement el : elements) {
                final int newValue = el.getAsJsonObject().get("value").getAsInt() * 2;
                el.getAsJsonObject().addProperty("value", newValue);
                array.add(el);
            }

            return array;
        }

    }

    @Override
    protected void configureClient(ClientConfig config) {
        super.configureClient(config);
        config.register(JsonBodyReaderWriter.class);
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(TestResource.class, JsonParamConverterProvider.class, JsonBodyReaderWriter.class);
    }

    @Test
    public void testBodyReader() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "John Doe");
        final String johnDoe =
                target("resource").path("bodyreadertest").request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(json.toString(), MediaType.APPLICATION_JSON), String.class);
        assertEquals("John Doe", johnDoe);
    }

    @Test
    public void testBodyWriter() {
        final JsonObject json =
                target("resource").path("bodywritertest").queryParam("name", "John Doe").request(MediaType.APPLICATION_JSON)
                        .get(JsonObject.class);

        JsonObject expected = new JsonObject();
        expected.addProperty("name", "John Doe");

        assertEquals(expected, json);
    }

    @Test
    public void testBoth() throws UnsupportedEncodingException {
        JsonObject payload = new JsonObject();
        payload.addProperty("name", "testBoth");

        String array = "[{'value' : 1}, {'value' : 2}, {'value' : 3} , {'value' : 4}]";

        final JsonArray mapResult =
                target("resource").path("readerwritertest").queryParam("data", URLEncoder.encode(payload.toString(), "UTF-8"))
                        .request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(array, MediaType.APPLICATION_JSON), JsonArray.class);

        assertEquals("Doing map for testBoth", mapResult.get(0).getAsString());
        assertEquals(2, mapResult.get(1).getAsJsonObject().get("value").getAsInt());
        assertEquals(4, mapResult.get(2).getAsJsonObject().get("value").getAsInt());
        assertEquals(6, mapResult.get(3).getAsJsonObject().get("value").getAsInt());
        assertEquals(8, mapResult.get(4).getAsJsonObject().get("value").getAsInt());
    }

}

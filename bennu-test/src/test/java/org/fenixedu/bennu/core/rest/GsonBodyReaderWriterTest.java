package org.fenixedu.bennu.core.rest;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.Bennu;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import pt.ist.fenixframework.test.core.FenixFrameworkRunner;

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
@RunWith(FenixFrameworkRunner.class)
public class GsonBodyReaderWriterTest extends JerseyTest {

    private static String urlEncode(String payload) {
        try {
            return URLEncoder.encode(payload, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new Error(e); //will never happen
        }
    }

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

        @Path("/readerwritertest")
        @GET
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public JsonObject map(@QueryParam("data") JsonObject data) {
            return data;
        }

        @Path("/readerwritertestparams")
        @POST
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @Produces(MediaType.APPLICATION_JSON)
        public JsonArray readerwritertestparams(@QueryParam("bennu") Bennu bennu, @QueryParam("query") JsonObject data,
                @FormParam("body") JsonElement body) {
            JsonArray array = new JsonArray();
            array.add(new JsonPrimitive("Doing map for " + data.get("name").getAsString()));
            array.add(new JsonPrimitive(bennu.getExternalId()));
            for (JsonElement el : body.getAsJsonArray()) {
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
        return new ResourceConfig(TestResource.class, JsonParamConverterProvider.class, JsonBodyReaderWriter.class,
                DomainObjectParamConverter.class);
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
    public void testJsonObjectReaderWriter() throws UnsupportedEncodingException {
        JsonObject payload = new JsonObject();
        payload.addProperty("value", 1);

        JsonObject response =
                target("resource").path("readerwritertest").queryParam("data", urlEncode(payload.toString()))
                        .request(MediaType.APPLICATION_JSON).get(JsonObject.class);

        assertEquals(payload.toString(), response.toString());
    }

    @Test
    public void testMalformedJson() throws UnsupportedEncodingException {

        String payload = "{'xpto': '}";

        Response response =
                target("resource").path("readerwritertest").queryParam("data", urlEncode(payload))
                        .request(MediaType.APPLICATION_JSON).get();

        assertEquals(Status.BAD_REQUEST, response.getStatusInfo());
    }

    @Test
    public void testBoth() throws UnsupportedEncodingException {
        JsonObject payload = new JsonObject();
        payload.addProperty("name", "testBoth");

        String values = "[{\"value\" : 1}, {\"value\" : 2}, {\"value\" : 3} , {\"value\" : 4}]";

        final JsonArray mapResult =
                target("resource").path("readerwritertest").queryParam("data", urlEncode(payload.toString()))
                        .request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(values, MediaType.APPLICATION_JSON), JsonArray.class);

        assertEquals("Doing map for testBoth", mapResult.get(0).getAsString());
        assertEquals(2, mapResult.get(1).getAsJsonObject().get("value").getAsInt());
        assertEquals(4, mapResult.get(2).getAsJsonObject().get("value").getAsInt());
        assertEquals(6, mapResult.get(3).getAsJsonObject().get("value").getAsInt());
        assertEquals(8, mapResult.get(4).getAsJsonObject().get("value").getAsInt());
    }

    @Test
    public void testWithBodyAndParams() throws UnsupportedEncodingException {
        JsonObject payload = new JsonObject();
        final String bennuId = Bennu.getInstance().getExternalId();

        payload.addProperty("name", "testBoth");

        String values = "[{\"value\" : 1}, {\"value\" : 2}, {\"value\" : 3} , {\"value\" : 4}]";

        Form form = new Form();
        form.param("body", values);

        final JsonArray mapResult =
                target("resource").path("readerwritertestparams").queryParam("bennu", bennuId)
                        .queryParam("query", urlEncode(payload.toString())).request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), JsonArray.class);

        assertEquals("Doing map for testBoth", mapResult.get(0).getAsString());
        assertEquals(bennuId, mapResult.get(1).getAsString());
        assertEquals(2, mapResult.get(2).getAsJsonObject().get("value").getAsInt());
        assertEquals(4, mapResult.get(3).getAsJsonObject().get("value").getAsInt());
        assertEquals(6, mapResult.get(4).getAsJsonObject().get("value").getAsInt());
        assertEquals(8, mapResult.get(5).getAsJsonObject().get("value").getAsInt());
    }
}
